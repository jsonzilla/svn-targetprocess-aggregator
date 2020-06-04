package dao

import conf.{RepoConf, TargetConf}
import models._
import scm._
import tasks._
import javax.inject.Inject
import org.tmatesoft.svn.core.SVNLogEntry
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

class UpdateDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) {

  private def loadSvnRepository() = {
    lazy val taskConf = RepoConf.taskParser()
    lazy val scmConf = RepoConf.scm()

    implicit val parser: TaskParser = TaskParserOctothorpe(taskConf.pattern, taskConf.split, taskConf.separator)

    lazy val rep = new SvnConnectorFactory {}
    lazy val repository: Future[SvnConnector] = rep.connect(scmConf.url, scmConf.user, scmConf.pass)
    val taskConnector: TaskConnector = TargetConnector(TargetConf.auth.url, TargetConf.auth.user, TargetConf.auth.pass)

    val extractor = new SvnExtractor(parser)
    val taskProcessor = ProcessTargetConnector(taskConnector)

    repository.flatMap { repo =>
      Future.successful(new ScmRepositoryData[SVNLogEntry](dbConfigProvider, repo, extractor, taskProcessor))
    }
  }

  private def updateRepositoryAuto() = {
    val repository = loadSvnRepository()
    repository.flatMap(rep => rep.updateAuto())
  }

  private def updateRepositoryRange(from: Long, to: Long) = {
    val repository = loadSvnRepository()
    repository.flatMap(rep => rep.updateRange(FixedRange(from, to)))
  }

  def update(from: Option[Long], to: Option[Long]): Future[Seq[Int]] = {
    updateRepositoryRange(from.getOrElse(-1), to.getOrElse(-1))
  }

  def updateAll(): Future[Seq[Int]] = {
    updateRepositoryAuto()
  }

  def updateCustomFields(field: String, from: Option[Long], to: Option[Long]): Future[Seq[Int]] = {
    val repository = loadSvnRepository()
    repository.flatMap(rep => rep.updateCustomFields(field, from.getOrElse(-1), to.getOrElse(-1)))
  }

}

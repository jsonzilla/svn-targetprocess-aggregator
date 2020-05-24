package dao

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait CommitTaskComponent extends CommitComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import profile.api._

  class CommitTasksTable(tag: Tag) extends Table[CommitTasks](tag,"COMMITTASKS") {
    def taskId: Rep[Long] = column[Long]("task_id")
    def commitId: Rep[Long] = column[Long]("commit_id")
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * = (taskId, commitId, id) <> ((CommitTasks.apply _).tupled, CommitTasks.unapply)
    def commit = foreignKey("commit_fk", commitId, TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag)))(_.id, onDelete = ForeignKeyAction.Cascade)
  }
}

@Singleton
class CommitTaskDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends CommitTaskComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val commits = TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag))
  val commitTasks = TableQuery[CommitTasksTable]((tag: Tag) => new CommitTasksTable(tag))

  def insert(entries: Seq[CommitTasks]): Future[Seq[Int]] = db.run {
    def updateInsert(ct: CommitTasks, entryId: Option[Long], commitId: Option[Long]) =
      (entryId, commitId) match {
        case (Some(id), Some(coId)) => commitTasks.insertOrUpdate(ct.copy(commitId = coId, id = id))
        case (_, Some(coId)) => commitTasks += ct.copy(commitId = coId)
        case _ => commitTasks += ct.copy(commitId = -1L) //HIRO
      }

    def swapRevisionByTableId(revision: Long) = {
      commits.filter(_.revision === revision).map(_.id).result.headOption
    }

    def tryInsert(commitTask: CommitTasks) = for {
      commitId <- swapRevisionByTableId(commitTask.commitId)
      id <- commitTasks.filter(_.id === commitTask.id).map(_.id).result.headOption
      u <- updateInsert(commitTask, id, commitId)
    } yield u

    DBIO.sequence(entries.map(tryInsert)).transactionally
  }

  def list(): Future[Seq[CommitTasks]] = db run {
    commitTasks.sortBy(_.id).result
  }

  def info(id: Long): Future[Seq[CommitTasks]] = db run {
    commitTasks.filter(_.id === id).sortBy(_.id).result
  }

  def drop() = db.run {
    commitTasks.delete
  }
}

package dao

import javax.inject.Inject

import models._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

trait CommitTaskComponent extends CommitComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import profile.api._

  class CommitTasksTable(tag: Tag, suffix: DatabeSuffix) extends Table[CommitTasks](tag, suffix.suffix + "COMMITTASKS") {
    def taskId: Rep[Long] = column[Long]("task_id")
    def commitId: Rep[Long] = column[Long]("commit_id")
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * = (taskId, commitId, id) <> ((CommitTasks.apply _).tupled, CommitTasks.unapply)
    def commit = foreignKey("commit_fk", commitId, TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag, suffix)))(_.id, onDelete = ForeignKeyAction.Cascade)
  }
}

class CommitTaskDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends CommitTaskComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insert(entries: Seq[CommitTasks], suffix: DatabeSuffix): Future[Seq[Int]] = db.run {
    val commits = TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag, suffix))
    val commitTasks = TableQuery[CommitTasksTable]((tag: Tag) => new CommitTasksTable(tag, suffix))
    def updateInsert(ct: CommitTasks, id: Option[Long], commitId: Option[Long]) = {
      if (id.isEmpty)
        commitTasks += ct.copy(commitId = commitId.head)
      else
        commitTasks.insertOrUpdate(ct.copy(commitId = commitId.head, id = commitId.head))
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

  def list(suffix: DatabeSuffix): Future[Seq[CommitTasks]] = db run {
    val commitTasks = TableQuery[CommitTasksTable]((tag: Tag) => new CommitTasksTable(tag, suffix))
    commitTasks.sortBy(_.id).result
  }

  def info(suffix: DatabeSuffix, id: Long): Future[Seq[CommitTasks]] = db run {
    val commitTasks = TableQuery[CommitTasksTable]((tag: Tag) => new CommitTasksTable(tag, suffix))
    commitTasks.filter(_.id === id).sortBy(_.id).result
  }
}

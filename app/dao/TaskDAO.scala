package dao

import javax.inject.{Inject}
import models.Task
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait TaskComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import profile.api._


  class TaskTable(tag: Tag) extends Table[Task](tag, "TASK") {
    def typeTask: Rep[Option[String]] = column[Option[String]]("type_task")
    def typeTaskId: Rep[Option[Long]] = column[Option[Long]]("type_task_id")
    def userStoryId: Rep[Option[Long]] = column[Option[Long]]("user_story")
    def timeSpend: Rep[Option[Double]] = column[Option[Double]]("time_spend")
    def parentId: Rep[Option[Long]] = column[Option[Long]]("parent_id")
    def taskId: Rep[Long] = column[Long]("task_id", O.Unique)
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * = (typeTask, typeTaskId, userStoryId, timeSpend, parentId, taskId, id) <> ((Task.apply _).tupled, Task.unapply)
  }
}

class TaskDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends TaskComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  val tasks = TableQuery[TaskTable]((tag: Tag) => new TaskTable(tag))

  def insert(ts: Seq[Task]): Future[Seq[Int]] = db.run {
   def updateInsert(task: Task, taskId: Option[Long]) =
      taskId match {
        case None => tasks += task
        case Some(id) => tasks.insertOrUpdate(task.copy(id = id))
      }

    def taskQuery(task: Task) = {
      for {
        taskId <- tasks.filter(_.taskId === task.taskId).map(_.id).result.headOption
        u <- updateInsert(task, taskId)
      } yield u
    }

    DBIO.sequence(ts.map(taskQuery)).transactionally
  }

  def list(): Future[Seq[Task]] = db.run {
    tasks.sortBy(_.id).result
  }

  def info(id: Long): Future[Seq[Task]] = db.run {
    tasks.filter(_.id === id).result
  }

  def infoTaskId(taskId: Long): Future[Seq[Task]] = db.run {
    tasks.filter(_.taskId === taskId).result
  }

  def infoParentId(parentId: Long): Future[Seq[Task]] = db.run {
    tasks.filter(_.parentId === parentId).result
  }

  def drop() = db.run {
    tasks.delete
  }
}
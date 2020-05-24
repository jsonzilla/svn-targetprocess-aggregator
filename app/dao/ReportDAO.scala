package dao

import java.sql.Timestamp

import javax.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class ReportDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends AuthorComponent
  with CommitComponent
  with CommitEntryFileComponent
  with CommitTaskComponent
  with CustomFieldsComponent
  with TaskComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val authors = TableQuery[AuthorsTable]((tag: Tag) => new AuthorsTable(tag))
  val commitTasks = TableQuery[CommitTasksTable]((tag: Tag) => new CommitTasksTable(tag))
  val tasks = TableQuery[TaskTable]((tag: Tag) => new TaskTable(tag))
  val files = TableQuery[EntryFilesTable]((tag: Tag) => new EntryFilesTable(tag))
  val commits = TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag))
  val commitsFiles = TableQuery[CommitEntryFileTable]((tag: Tag) => new CommitEntryFileTable(tag))
  val customFields = TableQuery[CustomFieldsTable]((tag: Tag) => new CustomFieldsTable(tag))

  def filesBugsCounter(): Future[Seq[(String, Int)]] = db.run {
    val bugs = for {
      co <- commits
      cf <- commitsFiles if cf.revisionId === co.id
      ct <- commitTasks if ct.commitId === co.id
      tk <- tasks if tk.taskId === ct.taskId && tk.typeTask === "Bug"
      fi <- files if fi.id === cf.pathId
    } yield fi
    val countBugs = bugs
      .groupBy(result => result.path)
      .map {
        case (path, group) =>
          (path, group.length)
      }
    countBugs.result.transactionally
  }

  def fileAuthorCommitsCounter(author: String): Future[Seq[(String, Int)]] = db.run {
    val commitsCounts = for {
      ai <- authors if ai.author === author
      co <- commits if co.authorId === ai.id
      cf <- commitsFiles if cf.revisionId === co.id
      fi <- files if fi.id === cf.pathId
    } yield fi
    val countCommits = commitsCounts
      .groupBy(result => result.path)
      .map {
        case (path, group) =>
          (path, group.length)
      }
    countCommits.result.transactionally
  }

  def fileAuthorCommitsBugsCounter(author: String): Future[Seq[(String, Int)]] = db.run {
    val commitsBugs = for {
      ai <- authors if ai.author === author
      co <- commits if co.authorId === ai.id
      cf <- commitsFiles if cf.revisionId === co.id
      ct <- commitTasks if ct.commitId === co.id
      tk <- tasks if tk.taskId === ct.taskId && tk.typeTask === "Bug"
      fi <- files if fi.id === cf.pathId
    } yield fi
    val countCommits = commitsBugs
      .groupBy(result => result.path)
      .map {
        case (path, group) =>
          (path, group.length)
      }
    countCommits.result.transactionally
  }

  def filterMovedFiles(revisionId: Long): Future[Seq[CommitEntryFile]] = db.run {
    val commitsFiles = TableQuery[CommitEntryFileTable]((tag: Tag) => new CommitEntryFileTable(tag))
    val files = for {
      cf <- commitsFiles if cf.revisionId === revisionId && cf.copyPathId >= 1L
    } yield cf
    files.result.transactionally
  }

  def authorsNames(): Future[Seq[String]] = db.run {
    val authors = TableQuery[AuthorsTable]((tag: Tag) => new AuthorsTable(tag))
    authors.map(_.author).result
  }

  private def bugTasks(fieldValue: String) = {

    val query = for {
      cs <- customFields if cs.field_value === fieldValue
      taskParents <- tasks if cs.taskId === taskParents.taskId
      tsk <- tasks if tsk.taskId === taskParents.taskId ||
        tsk.parentId === taskParents.taskId ||
        (tsk.userStoryId.isEmpty && tsk.typeTask === "Bug")
    } yield tsk.taskId
    query.distinct
  }

  private def tasksByField(fieldValue: String) = {
    val query = for {
      cs <- customFields if cs.field_value === fieldValue
      taskParents <- tasks if cs.taskId === taskParents.taskId
      tsk <- tasks if tsk.taskId === taskParents.taskId ||
        tsk.parentId === taskParents.taskId
    } yield tsk.taskId
    query.distinct
  }

  private def commitDateRange(initialTime: Timestamp, finalTime: Timestamp) = {
    for {
      co <- commits if co.timestamp >= initialTime && co.timestamp <= finalTime
    } yield co.id
  }

  private def commitFiles(id : Rep[Long]) = {
    for {
      cf <- commitsFiles if cf.revisionId === id
      fi <- files if fi.id === cf.pathId
    } yield fi
  }

  private def commitsByCustomFiled(fieldValue: String, initialTime: Timestamp, finalTime: Timestamp) = {
    val query = for {
      taskId <- if (fieldValue.contains("Issue")) bugTasks(fieldValue) else tasksByField(fieldValue)
      commitId <- commitDateRange(initialTime, finalTime)
      ct <- commitTasks if ct.commitId === commitId && ct.taskId === taskId
    } yield ct.commitId
    query.distinct
  }

  private def filesByCustomFiled(fieldValue: String, initialTime: Timestamp, finalTime: Timestamp) = {
    for {
      ct <- commitsByCustomFiled(fieldValue, initialTime, finalTime)
      fi <- commitFiles(ct)
    } yield fi
  }

  def countCommitByCustomField(fieldValue: String, initialTime: Timestamp, finalTime: Timestamp) : Future[Seq[(String, Int)]] = db.run {
    val countFiles = filesByCustomFiled(fieldValue, initialTime, finalTime)
      .groupBy(result => result.path)
      .map { case (path, group) => (path, group.length) }
      .sortBy{ case (_, length) => length }
    countFiles.result.transactionally
  }

  def dump(initialTime: Timestamp, finalTime: Timestamp): Future[Seq[(Long, String, Long, Option[String], Option[Timestamp], Long, String, Option[Int], Long, Option[String], Option[Long], Option[Double])]] = db.run {
    val dumpData = for {
      ai <- authors
      co <- commits if co.authorId === ai.id && co.timestamp >= initialTime && co.timestamp <= finalTime
      cf <- commitsFiles if cf.revisionId === co.id
      ct <- commitTasks if ct.commitId === co.id
      tk <- tasks if tk.taskId === ct.taskId
      fi <- files if fi.id === cf.pathId
    } yield (ai.id, ai.author, co.revision, co.message, co.timestamp, fi.id, fi.path, cf.typeModification, tk.taskId, tk.typeTask, tk.userStoryId, tk.timeSpend)
    dumpData.result.transactionally
  }
}

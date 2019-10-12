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
  with LocFileComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def filesBugsCounter(suffix: DatabaseSuffix): Future[Seq[(String, Int)]] = db.run {
    val commitTasks = TableQuery[CommitTasksTable]((tag: Tag) => new CommitTasksTable(tag, suffix))
    val tasks = TableQuery[TaskTable]((tag: Tag) => new TaskTable(tag, suffix))
    val files = TableQuery[EntryFilesTable]((tag: Tag) => new EntryFilesTable(tag, suffix))
    val commits = TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag, suffix))
    val commitsFiles = TableQuery[CommitEntryFileTable]((tag: Tag) => new CommitEntryFileTable(tag, suffix))

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

  def fileAuthorCommitsCounter(author: String, suffix: DatabaseSuffix): Future[Seq[(String, Int)]] = db.run {
    val files = TableQuery[EntryFilesTable]((tag: Tag) => new EntryFilesTable(tag, suffix))
    val authors = TableQuery[AuthorsTable]((tag: Tag) => new AuthorsTable(tag, suffix))
    val commits = TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag, suffix))
    val commitsFiles = TableQuery[CommitEntryFileTable]((tag: Tag) => new CommitEntryFileTable(tag, suffix))

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

  def fileAuthorCommitsBugsCounter(author: String, suffix: DatabaseSuffix): Future[Seq[(String, Int)]] = db.run {
    val commitTasks = TableQuery[CommitTasksTable]((tag: Tag) => new CommitTasksTable(tag, suffix))
    val tasks = TableQuery[TaskTable]((tag: Tag) => new TaskTable(tag, suffix))
    val files = TableQuery[EntryFilesTable]((tag: Tag) => new EntryFilesTable(tag, suffix))
    val authors = TableQuery[AuthorsTable]((tag: Tag) => new AuthorsTable(tag, suffix))
    val commits = TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag, suffix))
    val commitsFiles = TableQuery[CommitEntryFileTable]((tag: Tag) => new CommitEntryFileTable(tag, suffix))

    val cmts = for {
      ai <- authors if ai.author === author
      co <- commits if co.authorId === ai.id
      cf <- commitsFiles if cf.revisionId === co.id
      ct <- commitTasks if ct.commitId === co.id
      tk <- tasks if tk.taskId === ct.taskId && tk.typeTask === "Bug"
      fi <- files if fi.id === cf.pathId
    } yield fi
    val countCommits = cmts
      .groupBy(result => result.path)
      .map {
        case (path, group) =>
          (path, group.length)
      }
    countCommits.result.transactionally
  }

  def filterMovedFiles(revisionId: Long, suffix: DatabaseSuffix): Future[Seq[CommitEntryFile]] = db.run {
    val commitsFiles = TableQuery[CommitEntryFileTable]((tag: Tag) => new CommitEntryFileTable(tag, suffix))
    val files = for {
      cf <- commitsFiles if cf.revisionId === revisionId && cf.copyPathId >= 1L
    } yield cf
    files.result.transactionally
  }

  def authorsNames(suffix: DatabaseSuffix): Future[Seq[String]] = db.run {
    val authors = TableQuery[AuthorsTable]((tag: Tag) => new AuthorsTable(tag, suffix))
    authors.map(_.author).result
  }

  private def bugTasks(suffix: DatabaseSuffix, fieldValue: String) = {
    val customFields = TableQuery[CustomFieldsTable]((tag: Tag) => new CustomFieldsTable(tag, suffix))
    val tasks = TableQuery[TaskTable]((tag: Tag) => new TaskTable(tag, suffix))
    for {
      cs <- customFields if cs.field_value === fieldValue
      taskParents <- tasks if cs.taskId === taskParents.taskId
      tsk <- tasks if tsk.taskId === taskParents.taskId ||
        tsk.parentId === taskParents.taskId ||
        (tsk.userStoryId.isEmpty && tsk.typeTask === "Bug")
    } yield tsk.taskId
  }

  private def tasksByField(suffix: DatabaseSuffix, fieldValue: String) = {
    val customFields = TableQuery[CustomFieldsTable]((tag: Tag) => new CustomFieldsTable(tag, suffix))
    val tasks = TableQuery[TaskTable]((tag: Tag) => new TaskTable(tag, suffix))
    for {
      cs <- customFields if cs.field_value === fieldValue
      taskParents <- tasks if cs.taskId === taskParents.taskId
      tsk <- tasks if tsk.taskId === taskParents.taskId ||
        tsk.parentId === taskParents.taskId
    } yield tsk.taskId
  }

  private def commitDateRange(suffix: DatabaseSuffix, initialTime: Timestamp, finalTime: Timestamp) = {
    val commits = TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag, suffix))
    for {
      co <- commits if co.timestamp >= initialTime && co.timestamp <= finalTime
    } yield co.id
  }

  private def commitFiles(suffix: DatabaseSuffix, id : Rep[Long]) = {
    val files = TableQuery[EntryFilesTable]((tag: Tag) => new EntryFilesTable(tag, suffix))
    val commitsFiles = TableQuery[CommitEntryFileTable]((tag: Tag) => new CommitEntryFileTable(tag, suffix))
    for {
      cf <- commitsFiles if cf.revisionId === id
      fi <- files if fi.id === cf.pathId
    } yield fi
  }

  def countCommitByCustomField(suffix: DatabaseSuffix, fieldValue: String, initialTime: Timestamp, finalTime: Timestamp) : Future[Seq[(String, Int)]] = db.run {
    val commitTasks = TableQuery[CommitTasksTable]((tag: Tag) => new CommitTasksTable(tag, suffix))
    def countCommitTask()= for {
      taskId <- if (fieldValue.contains("Issue")) bugTasks(suffix, fieldValue).distinct else tasksByField(suffix, fieldValue)
      commitId <- commitDateRange(suffix, initialTime, finalTime)
      ct <- commitTasks if ct.commitId === commitId && ct.taskId === taskId
      fi <- commitFiles(suffix, ct.commitId)
    } yield fi
    val countCommits = countCommitTask
      .groupBy(result => result.path)
      .map { case (path, group) => (path, group.length) }
      .sortBy(groupedResult => groupedResult._2)
    countCommits.result.transactionally
  }

  def countCommitLocByCustomField(suffix: DatabaseSuffix, fieldValue: String, initialTime: Timestamp, finalTime: Timestamp): Future[Seq[((String, Long), Int)]] = db.run {
    val commitTasks = TableQuery[CommitTasksTable]((tag: Tag) => new CommitTasksTable(tag, suffix))
    val fileLocs = TableQuery[LocFilesTable]((tag: Tag) => new LocFilesTable(tag, suffix))

    def countCommitTask() = for {
      taskId <- if (fieldValue.contains("Issue")) bugTasks(suffix, fieldValue).distinct else tasksByField(suffix, fieldValue)
      commitId <- commitDateRange(suffix, initialTime, finalTime)
      ct <- commitTasks if ct.commitId === commitId && ct.taskId === taskId
      fi <- commitFiles(suffix, ct.commitId)
      lc <- fileLocs if lc.fileRef === fi.id
    } yield (fi.path, lc.count)

    val countCommits = countCommitTask
      .groupBy(result => result)
      .map{ case (path, group) => (path, group.length) }
      .sortBy(groupedResult => groupedResult._2)

    countCommits.result.transactionally
  }

}

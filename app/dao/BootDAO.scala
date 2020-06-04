package dao

import conf.RepoConf

import scala.concurrent.duration._
import javax.inject.{Inject, Singleton}
import models.ApiKey
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import telemetrics.HandLogger

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class BootDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends AuthorComponent
  with CommitComponent
  with CommitEntryFileComponent
  with CommitTaskComponent
  with ApiKeyComponent
  with ApiTokenComponent
  with ApiLogComponent
  with UserComponent
  with CustomFieldsComponent
  with TaskComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val apiLog = TableQuery[ApiLogTable]((tag: Tag) => new ApiLogTable(tag))
  val apiToken = TableQuery[ApiTokenTable]((tag: Tag) => new ApiTokenTable(tag))
  val apiKey = TableQuery[ApiKeyTable]((tag: Tag) => new ApiKeyTable(tag))
  val user = TableQuery[UserTable]((tag: Tag) => new UserTable(tag))
  val commitTasks = TableQuery[CommitTasksTable]((tag: Tag) => new CommitTasksTable(tag))
  val tasks = TableQuery[TaskTable]((tag: Tag) => new TaskTable(tag))
  val files = TableQuery[EntryFilesTable]((tag: Tag) => new EntryFilesTable(tag))
  val authors = TableQuery[AuthorsTable]((tag: Tag) => new AuthorsTable(tag))
  val commits = TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag))
  val commitsFiles = TableQuery[CommitEntryFileTable]((tag: Tag) => new CommitEntryFileTable(tag))
  val customFields = TableQuery[CustomFieldsTable]((tag: Tag) => new CustomFieldsTable(tag))

  def boot(): Unit = {
    HandLogger.info("init boot")
    exec(createSchemas()) onComplete {
      case Success(_) => HandLogger.info("correct create default tables")
        exec(createFirstApiKey()) onComplete {
          case Success(_) => HandLogger.info("correct create default apikey")
          case Failure(e) =>
            HandLogger.error("error in create tables " + e.getMessage)
        }
      case Failure(e) => HandLogger.error("error insert first apikey " + e.getMessage)
    }
  }

  private def createSchemas() = {
    apiLog.schema.create.asTry andThen
      apiToken.schema.create.asTry andThen
      apiKey.schema.create.asTry andThen
      user.schema.create.asTry andThen
      tasks.schema.create.asTry andThen
      authors.schema.create.asTry andThen
      commits.schema.create.asTry andThen
      files.schema.create.asTry andThen
      commitsFiles.schema.create.asTry andThen
      commitTasks.schema.create.asTry andThen
      customFields.schema.create.asTry
  }

  private def createFirstApiKey() = {
    lazy val key = ApiKey(apiKey = RepoConf.first_api_key(), name = "boot-server-app", active = true, 1L)
    apiKey.insertOrUpdate(key)
  }
  
  private def exec[T](program: DBIO[T]): Future[T] =
    Await.ready(db.run(program), 2 minutes)
}

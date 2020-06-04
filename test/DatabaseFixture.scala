import dao._
import javax.inject.Inject
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

class DatabaseFixture @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                                (implicit executionContext: ExecutionContext,app: Application)
  extends ApiKeyComponent
  with ApiTokenComponent
  with ApiLogComponent
  with UserComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  val daoApiKeys : ApiKeyDAO = Application.instanceCache[ApiKeyDAO].apply(app)
  val daoUsers : UserDAO = Application.instanceCache[UserDAO].apply(app)
  val daoTasks: TaskDAO = Application.instanceCache[TaskDAO].apply(app)
  val daoAuthors: AuthorDAO = Application.instanceCache[AuthorDAO].apply(app)
  val daoCommits: CommitDAO = Application.instanceCache[CommitDAO].apply(app)
  val daoFiles: EntryFileDAO = Application.instanceCache[EntryFileDAO].apply(app)
  val daoCommitFiles: CommitEntryFileDAO = Application.instanceCache[CommitEntryFileDAO].apply(app)
  val daoCommitTasks: CommitTaskDAO = Application.instanceCache[CommitTaskDAO].apply(app)
  val daoCustomFields: CustomFieldsDAO = Application.instanceCache[CustomFieldsDAO].apply(app)

  var once = true

  def populate(): Seq[Int] = {
    if (once) {
      once = false

      val daoBootstrap: BootDAO = Application.instanceCache[BootDAO].apply(app)
      daoBootstrap.boot()
      val insertAll = for {
        _ <- daoUsers.insert(ExtractorFixture.users)
        _ <- daoTasks.insert(ExtractorFixture.extractTasks)
        _ <- daoCustomFields.insert(ExtractorFixture.customFields)
        _ <- daoAuthors.insert(ExtractorFixture.extractAuthors)
        _ <- daoCommits.insert(ExtractorFixture.extractCommits)
        _ <- daoFiles.insert(ExtractorFixture.extractFiles)
        _ <- daoCommitTasks.insert(ExtractorFixture.extractCommitsTasks)
        c <- daoCommitFiles.insert(ExtractorFixture.extractCommitsFiles)
      } yield c

      Await.result(insertAll, 2 seconds)
    }

    Seq(0)
  }

}

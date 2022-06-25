import models._
import org.specs2.concurrent.ExecutionEnv

class UpdateDaoSpec(implicit ee: ExecutionEnv) extends ApiSpecification {
  ApplicationFixture.initializeWithData()
  private val fixture = ApplicationFixture.fixture

  "After populate db with fixture data" should {
    "list task table must have five tasks" in {
      val tasks = fixture.daoTasks.list()
      tasks must haveSize[Seq[Task]](5).await
    }
    "list author table must have three authors" in {
      val counter = fixture.daoAuthors.list()
      counter must haveSize[Seq[Author]](3).await
    }
    "list commit table must have three commits" in {
      val counter = fixture.daoCommits.list()
      counter must haveSize[Seq[CommitEntry]](3).await
    }
    "list commit entry table with a revision three must return one" in {
      val counter = fixture.daoCommits.infoRevision( 3)
      counter must haveSize[Seq[CommitEntry]](1).await
    }
    "list commit entry table with a revision nine must return none" in {
      val counter = fixture.daoCommits.infoRevision( 9)
      counter must haveSize[Seq[CommitEntry]](0).await
    }
    "list commit entry table with an id return one" in {
      val counter = fixture.daoCommits.info( 1)
      counter must haveSize[Seq[CommitEntry]](1).await
    }
    "list table files must have three files" in {
      val counter = fixture.daoFiles.list()
      counter must haveSize[Seq[EntryFile]](3).await
    }
    "list table commit entry file must have six commits files" in {
      val counter = fixture.daoCommitFiles.list()
      counter must haveSize[Seq[CommitEntryFile]](6).await
    }
    "list table commit tasks must have three commits tasks" in {
      val counter = fixture.daoTasks.list()
      counter must haveSize[Seq[Task]](5).await
    }
    "last commit revision id must be three" in {
      val last = fixture.daoCommits.actionLatestRevision()
      last must beEqualTo[Option[Int]](Some(3)).await
    }
    "repeat insert a commitTask update record" in {
      val insert = fixture.daoCommitTasks.insert(ExtractorFixture.commitTaskChange)
      insert must beEqualTo[Seq[Int]](Seq(1)).await
    }
  }
}

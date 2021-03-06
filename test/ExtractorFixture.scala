import java.sql.Timestamp
import java.text.SimpleDateFormat

import models._

import scala.util.{Failure, Success, Try}

object ExtractorFixture {

  private def parseTimestamp(s: String): Option[Timestamp] = {
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    Try {
      Some(new Timestamp(format.parse(s).getTime()))
    } match {
      case Success(value) => value
      case Failure(_) => None
    }
  }

  val extractTasks = Seq(
    Task(Some("Task"), Some(5L), None, Some(20), None, 1),
    Task(Some("Task"), Some(5L), None, Some(20), None, 2),
    Task(Some("Bug"), Some(8L), None, Some(20), Some(1), 3),
    Task(Some("Bug"), Some(8L), None, Some(20), Some(2), 4),
    Task(Some("Bug"), Some(8L), None, Some(20), Some(2), 5))

  val customFields = Seq(
    CustomFields(Some("internal"), "Bug", 1L, 1L)
  )

  val extractAuthors =
    Seq(Author("john"), Author("philips"), Author("thomas"))

  private val commitOne = CommitEntry(Some("Task #1"), parseTimestamp("2015-09-06 10:11:00"), 1, 0, 1)
  private val commitTwo = CommitEntry(Some("Task #2"), parseTimestamp("2015-10-06 10:11:00"), 2, 0, 2)
  private val commitThree = CommitEntry(Some("Bug #3"), parseTimestamp("2015-11-0 T10:11:00"), 2, 0, 3)
  private val commitFour = CommitEntry(Some("Bug #4"), parseTimestamp("2015-12-05 10:11:00"), 2, 0, 4)
  private val commitFive = CommitEntry(Some("Bug #5"), parseTimestamp("2016-01-06 10:11:00"), 3, 0, 5)

  val extractCommits =
    Seq((commitOne, "john"), (commitTwo, "philips"), (commitThree, "philips"),
      (commitFour, "philips"), (commitFive, "thomas"), (commitOne, "john"))

  val file1 = "/trunk/zip"
  val file2 = "/trunk/zap"
  val file3 = "/trunk/zop"

  val extractFiles: Seq[EntryFile] =
    Seq(EntryFile(file2), EntryFile(file2), EntryFile(file3), EntryFile(file1))

  private val commitFilesOne = Seq(
    CommitEntryWriter(CommitEntryFile(Some('A'), None, Some(1), 0, 0), file1, ""),
    CommitEntryWriter(CommitEntryFile(Some('A'), None, Some(1), 0, 0), file2, ""),
    CommitEntryWriter(CommitEntryFile(Some('A'), None, Some(1), 0, 0), file3, ""))

  private val commitFilesTwo = Seq(
    CommitEntryWriter(CommitEntryFile(Some('M'), None, Some(2), 0, 0), file1, ""),
    CommitEntryWriter(CommitEntryFile(Some('M'), None, Some(2), 0, 0), file2, ""))

  private val commitFilesThree =
    Seq(CommitEntryWriter(CommitEntryFile(Some('M'), None, Some(3), 0, 0), file1, ""))

  private val commitFilesFour =
    Seq(CommitEntryWriter(CommitEntryFile(Some('M'), None, Some(4), 0, 0), file1, ""))

  private val commitFilesFive =
    Seq(CommitEntryWriter(CommitEntryFile(Some('D'), None, Some(5), 0, 0), file1, ""))

  val extractCommitsFiles: Seq[(Seq[CommitEntryWriter], Long)] =
    Seq((commitFilesOne, 1L), (commitFilesTwo, 2L), (commitFilesThree, 2L),
      (commitFilesFour, 2L), (commitFilesFive, 3L))

  val extractCommitsTasks =
    Seq(CommitTasks(1, 1, 1), CommitTasks(2, 3, 2), CommitTasks(3, 2, 3), CommitTasks(5, 3))

  val commitTaskChange = Seq(CommitTasks(1, 2, 1))

  val users: Seq[User] = Seq(
    User("user1@mail.com", "123456", "User 1", emailConfirmed = true, active = true, 1L),
    User("user2@mail.com", "123456", "User 2", emailConfirmed = true, active = true, 2L),
    User("user3@mail.com", "123456", "User 3", emailConfirmed = true, active = true, 3L))
}

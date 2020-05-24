package dao

import java.sql.Timestamp

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait CommitComponent extends AuthorComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import profile.api._

  class CommitTable(tag: Tag) extends Table[CommitEntry](tag,"COMMITS") {
    def message: Rep[Option[String]] = column[Option[String]]("message")
    def timestamp: Rep[Option[Timestamp]] = column[Option[Timestamp]]("timestamp")
    def revision: Rep[Long] = column[Long]("revision", O.Unique)
    def authorId: Rep[Long] = column[Long]("author")
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * = (message, timestamp, revision, authorId, id) <> ((CommitEntry.apply _).tupled, CommitEntry.unapply)
    def author = foreignKey("author_fk", authorId, TableQuery[AuthorsTable]((tag: Tag) => new AuthorsTable(tag)))(_.id, onDelete = ForeignKeyAction.SetNull)
  }
}

@Singleton
class CommitDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends CommitComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  val commits = TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag))
  val authors = TableQuery[AuthorsTable]((tag: Tag) => new AuthorsTable(tag))

  def list(): Future[Seq[CommitEntry]] = db.run {
    commits.sortBy(_.id).result
  }

  def info(id: Long): Future[Seq[CommitEntry]] = db.run {
    commits.filter(_.id === id).result
  }

  def infoRevision(revision: Long): Future[Seq[CommitEntry]] = db.run {
    commits.filter(_.revision === revision).result
  }

  def infoDate(from: Timestamp, to: Timestamp): Future[Seq[CommitEntry]] = db.run {
    commits.filter(_.timestamp >= from).filter(_.timestamp <= to).result
  }

  def insert(cs: Seq[(CommitEntry, String)]): Future[Seq[Int]] = db.run {
    def updateInsert(commit: CommitEntry, commitId: Option[Long], authorId: Option[Long]) =
      (commitId, authorId) match {
        case (Some(id), Some(authorId)) => commits.insertOrUpdate(commit.copy(authorId = authorId, id = id))
        case (None, Some(authorId)) => commits += commit.copy(authorId = authorId)
        case _ => commits += commit.copy(authorId = 1L)
      }

    def commitQuery(entry: (CommitEntry, String)) = {
      val (commit, authorName) = entry
      for {
        authorId <- authors.filter(_.author === authorName).map(_.id).result.headOption
        commitId <- commits.filter(_.revision === commit.revision).map(_.id).result.headOption
        u <- updateInsert(commit, commitId, authorId)
      } yield u
    }

    DBIO.sequence(cs.map(commitQuery)).transactionally
  }

  def actionLatestRevision(): Future[Option[Long]] = {
    db.run(commits.map(_.revision).max.result)
  }

  def drop() = db.run {
    commits.delete
  }
}
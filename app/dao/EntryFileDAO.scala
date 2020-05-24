package dao

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait EntryFileComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import profile.api._

  class EntryFilesTable(tag: Tag) extends Table[EntryFile](tag,"FILES") {
    def path: Rep[String] = column[String]("path", O.Unique)
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * = (path, id) <> ((EntryFile.apply _).tupled, EntryFile.unapply)
  }
}

@Singleton
class EntryFileDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends EntryFileComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val files = TableQuery[EntryFilesTable]((tag: Tag) => new EntryFilesTable(tag))

  def insert(fs: Seq[EntryFile]): Future[Seq[Int]] = db.run {
    def updateInsert(file: EntryFile, entryId: Option[Long]) =
      entryId match {
      case Some(id) => files.insertOrUpdate(file.copy(id = id))
      case None => files += file
    }

    def fileQuery(file: EntryFile) = {
      for {
        fileId <- files.filter(_.path === file.path).map(_.id).result.headOption
        u <- updateInsert(file, fileId)
      } yield u
    }

    DBIO.sequence(fs.map(fileQuery)).transactionally
  }

  def list(): Future[Seq[EntryFile]] = db.run {
    files.sortBy(_.id).result
  }

  def info(id: Long): Future[Seq[EntryFile]] = db.run {
    files.filter(_.id === id).result
  }

  def drop() = db.run {
    files.delete
  }
}
package dao

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait CommitEntryFileComponent extends CommitComponent with EntryFileComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import profile.api._

  class CommitEntryFileTable(tag: Tag) extends Table[CommitEntryFile](tag, "COMMITFILES") {
    def typeModification: Rep[Option[Int]] = column[Option[Int]]("typeModification")
    def copyPathId: Rep[Option[Long]] = column[Option[Long]]("copyPath_id")
    def copyRevisionId: Rep[Option[Long]] = column[Option[Long]]("copyRevision")
    def pathId: Rep[Long] = column[Long]("path_id")
    def revisionId: Rep[Long] = column[Long]("revision")
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * = (typeModification, copyPathId, copyRevisionId, pathId, revisionId, id) <> ((CommitEntryFile.apply _).tupled, CommitEntryFile.unapply)
    def revision = foreignKey("revision_fk", revisionId, TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag)))(_.id, onDelete = ForeignKeyAction.Cascade)
    def path = foreignKey("path_fk", pathId, TableQuery[EntryFilesTable]((tag: Tag) => new EntryFilesTable(tag)))(_.id, onDelete = ForeignKeyAction.Cascade)
  }
}

@Singleton
class CommitEntryFileDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends CommitEntryFileComponent
  with CommitComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val files = TableQuery[EntryFilesTable]((tag: Tag) => new EntryFilesTable(tag))
  val commitsFiles = TableQuery[CommitEntryFileTable]((tag: Tag) => new CommitEntryFileTable(tag))
  val commits = TableQuery[CommitTable]((tag: Tag) => new CommitTable(tag))

  def insert(es: Seq[(Seq[CommitEntryWriter], Long)]): Future[Seq[Int]] = db.run {
    def fileQuery(fileEntries: (Seq[CommitEntryWriter], Long)) = {
      val (entryFiles, revisionNumber) = fileEntries

      def updateInsert(c: CommitEntryWriter, entryId: Option[Long], commitId: Option[Long], copyFileId: Option[Long], pathId: Option[Long]) =
        (commitId, pathId) match {
          case (Some(commitRev),Some(file)) =>
            entryId match {
              case Some(id) => commitsFiles.insertOrUpdate(c.commit.copy(copyPath = copyFileId, pathId = file, revisionId = commitRev, id = id))
              case None => commitsFiles += c.commit.copy(copyPath = copyFileId, pathId = file, revisionId = commitRev)
            }
          case _ => DBIO.successful(0)
        }

      def insertFilePath(c: CommitEntryWriter, commitId: Option[Long]) =
        for {
          fileId <- files.filter(_.path === c.path).map(_.id).result.headOption
          copyFileId <- files.filter(_.path === c.pathCopy).map(_.id).result.headOption
          id <- commitsFiles.filter(_.revisionId === commitId).filter(_.pathId === fileId).map(_.id).result.headOption
          u <- updateInsert(c, id, commitId, copyFileId, fileId).asTry
        } yield u

      def insert(files: Seq[CommitEntryWriter]) =
        for {
          commitId <- commits.filter(_.revision === revisionNumber).map(_.id).result.headOption
          _ <- DBIO.sequence(files.map(insertFilePath(_, commitId))).asTry
        } yield files.size

      insert(entryFiles)
    }

    DBIO.sequence(es.map(fileQuery)).transactionally
  }

  def list(): Future[Seq[CommitEntryFile]] = db.run {
    commitsFiles.result
  }

  def info(id: Long): Future[Seq[CommitEntryFile]] = db.run {
    commitsFiles.filter(_.id === id).result
  }

  def drop() = db.run {
    commitsFiles.delete
  }
}
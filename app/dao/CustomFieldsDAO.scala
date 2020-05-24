package dao

import javax.inject.{Inject, Singleton}
import models.CustomFields
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait CustomFieldsComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import profile.api._

  class CustomFieldsTable(tag: Tag) extends Table[CustomFields](tag,"CUSTOM_FIELDS") {
    def field_value: Rep[Option[String]] = column[Option[String]]("field_value")
    def field: Rep[String] = column[String]("field")
    def taskId: Rep[Long] = column[Long]("task_id", O.Unique)
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * = (field_value, field, taskId, id) <> ((CustomFields.apply _).tupled, CustomFields.unapply)
  }
}

@Singleton
class CustomFieldsDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends CustomFieldsComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val customs = TableQuery[CustomFieldsTable]((tag: Tag) => new CustomFieldsTable(tag))

  def insert(ts: Seq[CustomFields]): Future[Seq[Int]] = db.run {
    def updateInsert(cf: CustomFields, customId: Option[Long]) =
      customId match {
        case Some(id) => customs.insertOrUpdate(cf.copy(id = id))
        case None => customs += cf
      }

    def customQuery(cf: CustomFields) = {
      for {
        customId <- customs.filter(_.taskId === cf.taskId).map(_.id).result.headOption
        u <- updateInsert(cf, customId)
      } yield u
    }
    DBIO.sequence(ts.map(customQuery)).transactionally
  }

  def list(): Future[Seq[CustomFields]] = db.run {
    customs.sortBy(_.id).result
  }

  def listField(field: String): Future[Seq[CustomFields]] = db.run {
    customs.filter(_.field === field).sortBy(_.id).result
  }

  def info(id: Long): Future[Seq[CustomFields]] = db.run {
    customs.filter(_.id === id).result
  }

  def drop() = db.run {
    customs.delete
  }
}
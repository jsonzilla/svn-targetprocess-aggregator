package controllers

import api.ApiController
import javax.inject._
import dao._
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.Langs
import play.api.mvc._

import scala.concurrent.ExecutionContext

class CustomFieldsController @Inject()
(override val dbc: DatabaseConfigProvider, dao: CustomFieldsDAO, l: Langs, mcc: MessagesControllerComponents)
(implicit ec: ExecutionContext)
  extends ApiController(dbc, l, mcc)  {

  def list(): Action[Unit] = ApiAction { implicit request =>
    maybeSeq(dao.list())
  }

  def listField(field: String): Action[Unit] = ApiAction { implicit request =>
    maybeSeq(dao.listField(field))
  }

  def info(id: Long): Action[Unit] = ApiAction { implicit request =>
    maybeSeq(dao.info(id))
  }
}
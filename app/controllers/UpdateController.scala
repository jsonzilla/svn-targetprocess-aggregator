package controllers

import api.ApiController
import javax.inject._
import dao._
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.Langs
import play.api.mvc._
import telemetrics.HandLogger

import scala.concurrent.ExecutionContext

class UpdateController @Inject() (override val dbc: DatabaseConfigProvider, dao: UpdateDAO, l: Langs, mcc: MessagesControllerComponents)(implicit executionContext: ExecutionContext)
  extends ApiController(dbc, l, mcc) {

  def updateAll(): Action[Unit] = ApiAction { implicit request =>
    dao.updateAll().flatMap{ _ =>
      HandLogger.debug("Update Ok")
      accepted()
    }
  }

  def update(): Action[Unit] = ApiAction { implicit request =>
    dao.update(None, None).flatMap { _ =>
      HandLogger.debug("Update Ok")
      accepted() }
  }

  def updateCustomFields(field: String): Action[Unit] = ApiAction { implicit request =>
    dao.updateCustomFields(field, None, None).flatMap { _ =>
      HandLogger.debug("Update Ok")
      accepted() }
  }
}
package controllers

import java.sql.Timestamp
import java.time.LocalTime

import api.ApiController
import javax.inject._
import dao.CommitDAO
import models.QueryLocalDate
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.Langs
import play.api.mvc._

import scala.concurrent.ExecutionContext

class CommitController @Inject()
(override val dbc: DatabaseConfigProvider, dao: CommitDAO, l: Langs, mcc: MessagesControllerComponents)
(implicit ec: ExecutionContext)
  extends ApiController(dbc, l, mcc)  {

  def list(): Action[Unit] = ApiAction { implicit request =>
    maybeSeq(dao.list())
  }

  def info(id: Long): Action[Unit] = ApiAction { implicit request =>
    maybeSeq(dao.info(id))
  }

  def infoRevision(revision: Long): Action[Unit] = ApiAction { implicit request =>
    maybeSeq(dao.infoRevision(revision))
  }

  def infoDate(from: QueryLocalDate, to: QueryLocalDate): Action[Unit] = ApiAction { implicit request =>
    val fromTime = Timestamp.valueOf(from.queryDate.atTime(LocalTime.MIDNIGHT))
    val toTime = Timestamp.valueOf(to.queryDate.atTime(LocalTime.MIDNIGHT))
    maybeSeq(dao.infoDate(fromTime, toTime))
  }
}
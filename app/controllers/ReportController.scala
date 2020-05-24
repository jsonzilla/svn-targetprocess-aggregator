/*
 * Copyright (c) 2019, Jeison Cardoso. All Rights Reserved
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE as published by
 * the Free Software Foundation; either version 3, or (at your option)
 * any later version.
 */

package controllers

import api.ApiController
import javax.inject._
import dao._
import models._
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.Langs
import play.api.mvc._
import reportio.{CvsIO, Writable}

import scala.concurrent.ExecutionContext

class ReportController @Inject()
(override val dbc: DatabaseConfigProvider, dao: ReportDAO, l: Langs, mcc: MessagesControllerComponents)
(implicit ec: ExecutionContext)
  extends ApiController(dbc, l, mcc)  {

  def getFilesBugs(): Action[Unit] = ApiAction { implicit request =>
    maybeSeq(dao.filesBugsCounter())
  }

  def getAuthorBugs(author: String): Action[Unit] = ApiAction { implicit request =>
    maybeSeq(dao.fileAuthorCommitsBugsCounter(author))
  }

  def listCommitCustomField(customField: String, from: QueryLocalDate, to: QueryLocalDate): Action[Unit] = ApiAction { implicit request =>
    maybeSeq(dao.countCommitByCustomField(customField, from.fromTime, to.toTime))
  }

  def dump(from: QueryLocalDate, to: QueryLocalDate): Action[Unit] = ApiAction { implicit request =>
    maybeSeq(dao.dump(from.toTime, to.toTime))
  }

  private def generateCsvFile(rows: Seq[Writable]) =
      Ok(CvsIO.write(rows)).withHeaders("Content-Type" -> "text/csv",
        "Content-Disposition" -> "attachment; filename=report.csv")

  def listCommitsCustomFieldCsv(fieldValue: String, from: QueryLocalDate, to: QueryLocalDate): Action[AnyContent] = Action.async {
    dao.countCommitByCustomField(fieldValue, from.fromTime, to.toTime)
      .map(_.map{r => DumpCounter(r._1, r._2)}.sorted.reverse)
      .map(generateCsvFile)
  }

  def dumpCsv(from: QueryLocalDate, to: QueryLocalDate): Action[AnyContent] = Action.async {
    dao.dump(from.fromTime, to.toTime)
      .map(_.map{r => DumpJoinDatabase(r._1, r._2, r._3, r._4, r._5, r._6, r._7, r._8, r._9, r._10, r._11, r._12)})
      .map(generateCsvFile)
  }
}

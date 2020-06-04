package controllers

import javax.inject._
import dao.BootDAO
import models.QueryMagic
import play.api.mvc._

class BootController @Inject()
(dao: BootDAO, cc: MessagesControllerComponents)
  extends MessagesAbstractController(cc) {

  def createTables(magic: QueryMagic): Action[AnyContent] = Action {
    dao.boot()
    Accepted("boot")
  }
}
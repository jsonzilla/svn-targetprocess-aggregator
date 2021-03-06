package controllers

import api.ApiError._
import api.JsonCombinator._
import models.User
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import akka.actor.ActorSystem
import api.ApiController
import dao.UserDAO

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

import play.api.i18n.Langs

class AuthController @Inject()
(override val dbc: DatabaseConfigProvider, l: Langs, mcc: MessagesControllerComponents, system: ActorSystem)
  extends ApiController(dbc, l, mcc) {

  val userDao = new UserDAO(dbc)

  implicit val loginInfoReads: Reads[(String, String)] =
    (__ \ "email").read[String](Reads.email) and
      (__ \ "password").read[String] tupled

  def signIn: Action[JsValue] = ApiActionWithBody { implicit request =>
    readFromRequest[(String, String)] {
      case (email, pwd) =>
        userDao.findByEmail(email).flatMap {
          case None => errorUserNotFound
          case Some(user) => user match {
            case user if (user.password != pwd) => errorUserNotFound
            case user if (! user.emailConfirmed) => errorUserEmailUnconfirmed
            case user if (! user.active) => errorUserInactive
            case _ => apiTokenDao.create (request.apiKeyOpt.get, user.id).flatMap {
              token =>
              ok (Json.obj (
              "token" -> token,
              "minutes" -> 10) )
            }
          }
        }
    }
  }

  def signOut: Action[Unit] = SecuredApiAction { implicit request =>
    apiTokenDao.delete(request.token).flatMap { _ =>
      noContent()
    }
  }

  implicit val signUpInfoReads: Reads[(String, String, User)] =
    (__ \ "email").read[String](Reads.email) and
      (__ \ "password").read[String](Reads.minLength[String](6)) and
      (__ \ "user").read[User] tupled

  def signUp: Action[JsValue] = ApiActionWithBody { implicit request =>
    readFromRequest[(String, String, User)] {
      case (email, password, user) =>
        userDao.findByEmail(email).flatMap {
          case Some(_ /*anotherUser*/ ) => errorCustom("api.error.signup.email.exists")
          case None => userDao.insert(email, password, user.name).flatMap {
            case Some(user_) =>

              // HIRO Send confirmation email.
              // You will have to catch the link and confirm the email and activate the user.
              // But meanwhile...
              system.scheduler.scheduleOnce(30 seconds) {
                userDao.confirmEmail(user_.id).onComplete(_ -> ())
              }

              ok(user_.name)
            case None => errorCustom("api.error.user.notfound")
          }
        }
    }
  }

}
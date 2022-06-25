package models

import play.api.libs.json.{ Json, OFormat }

final case class Scm(
  name: String,
  user: String,
  pass: String,
  usr: String,
  id: Long = 0L)

object Scm {
  implicit val scmFormat: OFormat[Scm] = Json.format[Scm]
}
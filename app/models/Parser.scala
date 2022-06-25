package models

import play.api.libs.json.{ Json, OFormat }

final case class Parser(
  name: String,
  pattern: String,
  split: String,
  separator: String,
  branch: String,
  id: Long = 0L)

object Parser {
  implicit val parserFormat: OFormat[Parser] = Json.format[Parser]
}
package models

import play.api.libs.json.{ Json, OFormat }

case class EntryFile(path: String, id: Long = 0L)

object EntryFile {
  implicit val entryFormat: OFormat[EntryFile] = Json.format[EntryFile]
}
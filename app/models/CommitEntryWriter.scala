package models

import play.api.libs.json.{ Json, OFormat }

case class CommitEntryWriter(commit: CommitEntryFile, path: String, pathCopy: String)

object CommitEntryWriter {
  implicit val commitEntryFormat: OFormat[CommitEntryWriter] = Json.format[CommitEntryWriter]
}
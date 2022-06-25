package models

import java.sql.Timestamp
import java.text.SimpleDateFormat

import play.api.libs.json._

/** A svn commit entity info.
 *
 *  @constructor create a new commit entity.
 *  @param message commit info message
 *  @param timestamp datetime of the commit in format yyyy-MM-dd'T'HH:mm:ss.SS'Z'
 *  @param revision svn revision number
 *  @param authorId author foreign id
 *  @param id table id
 */
case class  CommitEntry(
  message: Option[String],
  timestamp: Option[Timestamp],
  revision: Long,
  authorId: Long,
  id: Long = 0L)

object CommitEntry {
  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    def reads(json: JsValue): JsSuccess[Timestamp] = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }
    def writes(ts: Timestamp) = JsString(format.format(ts))
  }

  implicit val commitEntryFormat: OFormat[CommitEntry] = Json.format[CommitEntry]
}

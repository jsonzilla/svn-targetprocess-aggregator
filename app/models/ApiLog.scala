package models

import play.api.libs.json._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/** A API Log
 *
 *  @constructor create a log entity.
 *  @param date date and time of log creation
 *  @param ip ip address of the connection
 *  @param apiKey the api key
 *  @param token UUID 36 digits
 *  @param method method of http access
 *  @param uri uri path of required connection
 *  @param requestBody request of the connection
 *  @param responseStatus status of the response of the connection
 *  @param responseBody response of the connection
 *  @param id table id
 */
case class ApiLog(
  date: DateTime,
  ip: String,
  apiKey: Option[String],
  token: Option[String],
  method: String,
  uri: String,
  requestBody: Option[String],
  responseStatus: Int,
  responseBody: Option[String],
  id: Long = 0L) {

  def dateStr: String = ApiLog.dateTimeFormat.print(date)
}

object ApiLog {
  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("yyyy-MM-dd HH:mm:ss")
  implicit val dateTimeJsReader: Reads[DateTime] = JodaReads.jodaDateReads("yyyy-MM-dd HH:mm:ss")
  implicit val taskManagerFormat: OFormat[ApiLog] = Json.format[ApiLog]

  private val dateTimeFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:ss:mm")
}


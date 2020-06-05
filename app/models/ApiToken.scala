package models

import org.joda.time.DateTime
import play.api.libs.json._

/** A API Token
 *
 *  @constructor create a new api token entity.
 *  @param token UUID 36 digits
 *  @param apiKey the api key
 *  @param expirationTime expiration date and time
 *  @param userId foreign user id
 *  @param id table id
 */
case class ApiToken(
  token: String,
  apiKey: String,
  expirationTime: DateTime,
  userId: Long,
  id: Long = 0L) {

  def isExpired: Boolean = {
    expirationTime.isBeforeNow
  }
}

object ApiToken {
  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("yyyy-MM-dd HH:mm:ss")
  implicit val dateTimeJsReader: Reads[DateTime] = JodaReads.jodaDateReads("yyyy-MM-dd HH:mm:ss")
  implicit val taskManagerFormat: OFormat[ApiToken] = Json.format[ApiToken]
}

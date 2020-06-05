package models

import play.api.libs.json.{ Json, OFormat }

/** A API Key
 *
 *  @constructor create a api key entity.
 *  @param apiKey the api key
 *  @param name app name for the key reference
 *  @param active boolean flag for state of the key
 *  @param id table id
 */
case class ApiKey(
  apiKey: String,
  name: String,
  active: Boolean,
  id: Long = 0L)

object ApiKey {
  implicit val taskManagerFormat: OFormat[ApiKey] = Json.format[ApiKey]
}

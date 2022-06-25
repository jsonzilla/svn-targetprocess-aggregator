package models

import play.api.libs.json.{ Json, OFormat }

/** A author who make a commit.
 *
 *  @constructor create a new commit author with your name and id.
 *  @param author the commits author's name
 *  @param id for database
 */
case class Author(author: String, id: Long = 0L)

object Author {
  implicit val authorFormat: OFormat[Author] = Json.format[Author]
}

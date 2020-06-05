/*
 * Copyright (c) 2019, Jeison Cardoso. All Rights Reserved
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE as published by
 * the Free Software Foundation; either version 3, or (at your option)
 * any later version.
 */

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

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

/** A svn commit entity file info.
 *
 *  @constructor create a new commit entry file entity.
 *  @param typeModification type of modification like rename move add (65)
 *  @param copyPath path of the copy in move
 *  @param copyRevision svn revision number for orginal copy
 *  @param pathId file foreign id
 *  @param revisionId svn commit id
 *  @param id table id
 */
case class CommitEntryFile(
  typeModification: Option[Int],
  copyPath: Option[Long],
  copyRevision: Option[Long],
  pathId: Long,
  revisionId: Long,
  id: Long = 0L)

object CommitEntryFile {
  implicit val commitEntryFileFormat: OFormat[CommitEntryFile] = Json.format[CommitEntryFile]
}
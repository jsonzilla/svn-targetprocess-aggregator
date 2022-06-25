package models

import play.api.libs.json.{ Json, OFormat }

case class CommitTasks(taskId: Long, commitId: Long, id: Long = 0L)

object CommitTasks {
  implicit val commitTasksFormat: OFormat[CommitTasks] = Json.format[CommitTasks]
}

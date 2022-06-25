package models

import play.api.libs.json._

final case class Task(
  typeTask: Option[String],
  typeTaskId: Option[Long],
  userStory: Option[Long],
  timeSpend: Option[Double],
  parentId: Option[Long],
  taskId: Long,
  id: Long = 0L)

object Task {
  implicit val taskFormat: OFormat[Task] = Json.format[Task]
}

package tasks

trait TaskConnector {
  def assignable(id: Long): String
  def bugs(id: Long): String
  def customFields(id: Long): String
}

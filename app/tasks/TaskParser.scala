package tasks

trait TaskParser {
  def convert(s: Option[String]): Seq[Long]
}

case class TaskParserOctothorpe(patternParser: String, patternSplit: String, separator: String) extends TaskParser {
  private def parse(s: String): Seq[String] = {
    (patternParser.r findAllIn s).toSeq
  }

  private def split(s: String): Array[Long] = {
    s.split(patternSplit)
      .filter(!_.isEmpty)
      .map(_.toLong)
  }

  def convert(s: Option[String]): Seq[Long] = s match {
    case Some(v) => parse(v).flatMap(split)
    case None => Nil
  }
}

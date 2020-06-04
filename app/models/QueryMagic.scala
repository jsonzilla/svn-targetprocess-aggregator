package models

import play.api.mvc.PathBindable
import conf.RepoConf

case class QueryMagic(suffix: String)

object QueryMagic {
  private def allow(suffix: String) = {
    !suffix.trim.isEmpty && suffix.equals(RepoConf.magic)
  }

  implicit def pathBinder(implicit intBinder: PathBindable[String]): PathBindable[QueryMagic] = new PathBindable[QueryMagic] {
    override def bind(key: String, value: String): Either[String, QueryMagic] = {
      val data = intBinder.bind(key, value)
      data match {
        case Right(suffix) if allow(suffix) => Right(QueryMagic(suffix.trim))
        case _ => Left("Unable to find your magic")
      }
    }
    override def unbind(key: String, value: QueryMagic): String = {
      value.suffix
    }
  }
}
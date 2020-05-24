package conf

import com.typesafe.config.ConfigFactory
import play.api.Configuration

case class TaskConf(pattern: String, split: String, separator: String)

object RepoConf {
  private lazy val conf = new Configuration(ConfigFactory.load())

  def taskParser(): TaskConf = {
    TaskConf(conf.get[String]("task_model.patternParser"),
      conf.get[String]("task_model.patternSplit"),
      conf.get[String]("task_model.separator"))
  }

  def scm(): BasicAuthConf = {
    BasicAuthConf(conf.get[String]("repo.url"),
      conf.get[String]("repo.user"),
      conf.get[String]("repo.pass"))
  }

  def repos(): Seq[String] = {
    conf.getOptional[Seq[String]]("repos").getOrElse(Seq())
  }
}

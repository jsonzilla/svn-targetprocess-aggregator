package telemetrics

import com.typesafe.scalalogging.LazyLogging

object HandLogger extends LazyLogging {
  def debug(m: String): Unit = logger.debug(m)
  def info(m: String): Unit = logger.info(m)
  def error(m: String): Unit = logger.error(m)
  def warn(m: String): Unit = logger.warn(m)
}
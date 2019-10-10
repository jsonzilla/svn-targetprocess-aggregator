package reportio

import java.io.File

import com.github.tototoshi.csv.CSVWriter

object CvsIO extends ReportIO {
  def write(filename: String, lines: Seq[(String, Int)]): Unit = {
    lazy val f = new File(filename + ".csv")
    val writer = CSVWriter.open(f)
    lines.reverse.map(t => List(t._2, t._1)).foreach(writer.writeRow)
    writer.close()
  }
  def writeSLI(filename: String, lines: Seq[(String, Long, Int)]): Unit = {
    lazy val f = new File(filename + ".csv")
    val writer = CSVWriter.open(f)
    lines.reverse.map(t => List(t._2, t._3, t._1)).foreach(writer.writeRow)
    writer.close()
  }
}


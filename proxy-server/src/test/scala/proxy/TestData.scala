package proxy
import java.io.File

import org.apache.commons.io.FileUtils

trait TestData {
  implicit val tracker = "nnm"
  val htmlWithTorrents = FileUtils.readFileToString(new File("proxy-server/src/test/resources/results.html"), "utf-8")
  val htmlWithoutTorrents = FileUtils.readFileToString(new File("proxy-server/src/test/resources/noResults.html"), "utf-8")
}

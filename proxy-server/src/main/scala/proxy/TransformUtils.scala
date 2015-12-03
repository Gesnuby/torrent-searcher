package proxy
import akka.event.LoggingAdapter
import org.htmlcleaner.TagNode

import scala.util.{Failure, Success, Try}

trait TransformUtils {

  def log: LoggingAdapter

  /**
   * Find first element using xpath based selector
   */
  def findFirst[A](selector: String => String)(implicit node: TagNode, tracker: String): Option[A] = {
    find[A](selector)(node, tracker).headOption
  }

  /**
   * Find array of elements using xpath based selector
   */
  def find[A](selector: String => String)(implicit node: TagNode, tracker: String): List[A] = {
    Try(node.evaluateXPath(selector(tracker)).toList.map(_.asInstanceOf[A])) match {
      case Success(result) => result
      case Failure(error) =>
        log.warning(s"Usage of ${selector(tracker)} on $tracker} failed.\n $error")
        List[A]()
    }
  }

  /**
   * Get text from TagNode element
   */
  def getText[A <: TagNode](value: Option[A]): Option[String] = value.map(_.getText.toString)

}

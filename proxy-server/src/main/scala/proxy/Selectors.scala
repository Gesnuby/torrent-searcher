package proxy
import com.typesafe.config.Config
import model.Torrent
import net.ceedubs.ficus.Ficus._

trait Selectors {
  val config: Config

  def from(tracker: String)(selection: => Torrent) = selection

  val recordsSelector = (tracker: String) => config.as[String](s"$tracker.selectors.records")
  val forumSelector = (tracker: String) => config.as[String](s"$tracker.selectors.forum")
  val nameSelector = (tracker: String) => config.as[String](s"$tracker.selectors.name")
  val linkSelector = (tracker: String) => config.as[String](s"$tracker.selectors.link")
  val downloadLinkSelector = (tracker: String) => config.as[String](s"$tracker.selectors.downloadLink")
  val seedSelector = (tracker: String) => config.as[String](s"$tracker.selectors.seed")
  val leechSelector = (tracker: String) => config.as[String](s"$tracker.selectors.leech")
  val sizeSelector = (tracker: String) => config.as[String](s"$tracker.selectors.size")
  val dateSelector = (tracker: String) => config.as[String](s"$tracker.selectors.date")
}

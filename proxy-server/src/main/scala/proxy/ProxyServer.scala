package proxy

import java.net.URLEncoder

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import model.{SearchResult, Torrent}
import net.ceedubs.ficus.Ficus._
import org.htmlcleaner.{HtmlCleaner, TagNode}
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Formats extends DefaultJsonProtocol {
  implicit val torrentFormat = jsonFormat8(Torrent.apply)
  implicit val searchResultFormat = jsonFormat2(SearchResult.apply)
}

trait Service extends Formats with TransformUtils with Selectors {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  val cleaner = new HtmlCleaner()
  val route = {
    logRequest("proxy-server") {
      pathPrefix("api" / "search") {
        (get & path(Segment)) { query =>
          respondWithHeader(headers.`Access-Control-Allow-Origin`.*) {
            complete {
              search(URLEncoder.encode(query, "UTF-8")).map[ToResponseMarshallable] { result => result }
            }
          }
        }
      }
    }
  }

  val searchFlow = (tracker: String, url: String, encoding: String) => {
    Http().singleRequest(HttpRequest(method = HttpMethods.GET, uri = url)).flatMap { response =>
      transform(response.entity, encoding).run().map(getTorrents(_)(tracker))
    }
  }

  def search(query: String): Future[SearchResult] = {
    val result = searchTracker("nnm", query)
    for {
      r1 <- result
    } yield SearchResult(query, r1)
  }

  def searchTracker(tracker: String, query: String): Future[List[Torrent]] = {
    val url = config.as[String](s"$tracker.searchUrl") + query
    val encoding = config.as[String](s"$tracker.encoding")
    searchFlow(tracker, url, encoding)
  }

  def getTorrents(html: String)(implicit tracker: String): List[Torrent] = {
    val node: TagNode = cleaner.clean(html)
    val records = find[TagNode](recordsSelector)(node, tracker)
    records.map { implicit record =>
      from(tracker) {
        val forum = getText(findFirst[TagNode](forumSelector))
        val name = getText(findFirst[TagNode](nameSelector))
        val link = findFirst[String](linkSelector)
        val downloadLink = findFirst[String](downloadLinkSelector)
        val seed = getText(findFirst[TagNode](seedSelector))
        val leech = getText(findFirst[TagNode](leechSelector))
        val size = getText(findFirst[TagNode](sizeSelector))
        val date = getText(findFirst[TagNode](dateSelector))
        Torrent(forum, name, link, downloadLink, seed, leech, size, date)
      }
    }
  }

  def transform(entity: ResponseEntity, encoding: String): RunnableGraph[Future[String]] = {
    val transformer: Flow[ByteString, String, Unit] = Flow[ByteString].map(f => f.decodeString(encoding))
    val sink: Sink[String, Future[String]] = Sink.fold[String, String]("")(_ + _)
    val body: Source[ByteString, Any] = entity.dataBytes
    body.via(transformer).toMat(sink)(Keep.right)
  }

}

object ProxyServer extends App with Service with Configuration {
  implicit val system = ActorSystem("proxy-server")
  implicit val materializer = ActorMaterializer()
  val config = ConfigFactory.load()
  val log = system.log

  Http().bindAndHandle(route, host, port)
}
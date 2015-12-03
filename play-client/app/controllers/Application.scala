package controllers
import java.net.URLEncoder

import model.{SearchResult, Torrent}
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.ws._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

trait TorrentReads {
  implicit val torrentReads: Reads[Torrent] = (
      (JsPath \ "forum").readNullable[String] and
      (JsPath \ "name").readNullable[String] and
      (JsPath \ "link").readNullable[String] and
      (JsPath \ "downloadLink").readNullable[String] and
      (JsPath \ "seed").readNullable[String] and
      (JsPath \ "leech").readNullable[String] and
      (JsPath \ "size").readNullable[String] and
      (JsPath \ "date").readNullable[String]
    )(Torrent.apply _)

  implicit val searchResultReads: Reads[SearchResult] = (
      (JsPath \ "query").read[String] and
      (JsPath \ "torrents").read[Seq[Torrent]]
    )(SearchResult.apply _)
}

object Application extends Controller with TorrentReads {

  val searchForm = Form(single("name" -> text))

  def index = Action {
    Ok(views.html.list(List.empty))
  }

  def search = Action.async(parse.form(searchForm)) { implicit request =>
    val searchString = URLEncoder.encode(request.body, "UTF-8")
    val holder = WS.url("http://localhost:9091/api/search/" + searchString)
    val requestHolder = holder.withRequestTimeout(10000).get()
    val futureResult: Future[SearchResult] = requestHolder.map { response =>
      response.json.as[SearchResult]
    }
    futureResult.map(result => Ok(views.html.list(result.torrents)))
  }
}

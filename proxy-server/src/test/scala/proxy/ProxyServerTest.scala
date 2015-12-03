package proxy
import akka.event.LoggingAdapter
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.typesafe.config.ConfigFactory
import model.{SearchResult, Torrent}
import org.scalatest.{FunSpec, Matchers}
import SprayJsonSupport._

import scala.concurrent.duration._

class ProxyServerTest extends FunSpec with Matchers with ScalatestRouteTest with Service with TestData {

  def log: LoggingAdapter = system.log

  val config = ConfigFactory.load()

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(5.seconds)

  describe("Proxy server") {
    it("should respond with empty array of torrents if there are no search results") {
      Get("/api/search/asdfg") ~> route ~> check {
        contentType shouldBe `application/json`
        val response = responseAs[SearchResult]
        response.query shouldBe "asdfg"
        response.torrents shouldBe empty
      }
    }

    it("should respond with non-empty array of torrents if there are search results") {
      Get("/api/search/warhammer") ~> route ~> check {
        contentType shouldBe `application/json`
        val response = responseAs[SearchResult]
        response.query shouldBe "warhammer"
        response.torrents should have length 50
      }
    }
  }

  val torrentsList = getTorrents(htmlWithTorrents)
  val emptyTorrentsList = getTorrents(htmlWithoutTorrents)

  describe("getTorrents") {
    it("should return all torrents from tracker search results page") {
      torrentsList.length shouldBe 50
    }

    it("should find torrent name on page") {
      torrentsList.head shouldBe
        Torrent(
          Some("Проекты"), Some("Проекты - VideoHive - Grunge Film Style [AEP]"),
          Some("viewtopic.php?t=956514"), Some("download.php?id=809710"),
          Some("43"), Some("1"), Some("157766048 150 MB"), Some("1447514748 14-11-2015")
        )
    }

    it("should return empty list if there is no torrents on search results page") {
      emptyTorrentsList shouldBe empty
    }
  }
}

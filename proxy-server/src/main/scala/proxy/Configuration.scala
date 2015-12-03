package proxy

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

trait Configuration {
  val config: Config
  lazy val host: String = config.as[String]("http.host")
  lazy val port: Int = config.as[Int]("http.port")
}

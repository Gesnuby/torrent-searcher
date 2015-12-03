import play.sbt.Play.autoImport._
import sbt._

object Dependencies {

  val playVersion = "2.4.2"

  val akkaHttpVersion = "1.0"

  val akkaHttp = Seq("com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion)

  val akkaHttpSpray = Seq("com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaHttpVersion)

  val akkaHttpTestKit = Seq("com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaHttpVersion)

  val scalatest = Seq("org.scalatest" %% "scalatest" % "2.2.4")

  val htmlCleaner = Seq("net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.13")

  val ficus = Seq("net.ceedubs" %% "ficus" % "1.1.2")

  val bootstrap = Seq("org.webjars" % "bootstrap" % "3.3.5")

  val commonsIO = Seq("commons-io" % "commons-io" % "2.4")

  val playWs = Seq(ws)

  implicit class DependencySyntax(self: Project) {
    def libraryDependencies(dependencies: Seq[ModuleID]): Project =
      self.settings(Keys.libraryDependencies ++= dependencies)

    def testDependencies(dependencies: Seq[ModuleID]): Project =
      self.settings(Keys.libraryDependencies ++= dependencies.map(_.copy(configurations = Some("test"))))
  }

}
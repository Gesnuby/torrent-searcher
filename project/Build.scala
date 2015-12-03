import Dependencies._
import sbt.Keys._
import sbt._
import spray.revolver.RevolverPlugin.Revolver

object Build extends Build {
  val commonSettings = Seq(
    organization := "com.gesnuby",
    version := "0.1",
    scalaVersion := "2.11.7",
    scalaVersion in ThisBuild := "2.11.7",
    scalacOptions ++= Seq("-feature", "-deprecation"),
    resolvers ++= Seq("Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/")
  )

  lazy val commons = project("commons")

  lazy val proxyServer = project("proxy-server")
                         .settings(Revolver.settings: _*)
                         .dependsOn(commons)
                         .libraryDependencies(akkaHttp ++ akkaHttpSpray ++ htmlCleaner ++ commonsIO)
                         .testDependencies(akkaHttpTestKit ++ scalatest)

  lazy val playClient = project("play-client")
                        .dependsOn(commons)
                        .libraryDependencies(playWs ++ bootstrap)
                        .enablePlugins(play.sbt.PlayScala)

  def project(path: String): Project = Project(path, file(path))
                                       .settings(commonSettings: _*)
                                       .libraryDependencies(ficus)

}
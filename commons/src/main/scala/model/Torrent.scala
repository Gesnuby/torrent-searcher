package model

case class Torrent(forum: Option[String],
                   name: Option[String],
                   link: Option[String],
                   downloadLink: Option[String],
                   seed: Option[String],
                   leech: Option[String],
                   size: Option[String],
                   date: Option[String])

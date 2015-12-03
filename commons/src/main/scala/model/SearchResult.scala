package model

case class SearchResult(query: String, torrents: Seq[Torrent])

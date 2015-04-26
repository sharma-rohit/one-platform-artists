package domain

import play.api.libs.json.Json


case class SimilarArtist(
                          name: String,
                          mbid: String,
                          url: String
                          )

object SimilarArtist {
  implicit val artistFmt = Json.format[SimilarArtist]
}
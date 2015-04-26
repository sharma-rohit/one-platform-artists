package domain

import play.api.libs.json.Json

case class Artist(
                   name: String,
                   listeners: String,
                   mbid: String,
                   url: String,
                   streamable: String
                   )

object Artist {
  implicit val artistFmt = Json.format[Artist]
}
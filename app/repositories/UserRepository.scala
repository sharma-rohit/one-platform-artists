package repositories

import domain.{SimilarArtistResult, SimilarArtist, ArtistResult, Artist}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequestHolder, WS}
import play.api.Play.current
import play.mvc.Http
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class UserRepository {
  private val url = "http://ws.audioscrobbler.com/2.0/"
  private val key = play.Play.application().configuration().getString("api-key")

  def findArtist(name: String): Future[List[ArtistResult]] = {

    val requestHolder: WSRequestHolder = WS.url(url).withQueryString("method" -> "artist.search",
      "artist" -> name, "api_key" -> key, "format" -> "json")

    val artistResult = requestHolder.get().map {
      response =>
        response.status match {
          case Http.Status.OK =>
            val json: JsValue = Json.parse(response.body)


            Try {
              (json \ "results" \ "artistmatches" \ "artist").as[List[JsValue]].map {
                x =>
                  new ArtistResult(Json.fromJson[Artist](x).get, (x \\ "#text").map(_.as[String]))
              }
            }.getOrElse(Nil)

          case _ => Nil
        }
    }


    artistResult
  }

  def findSimilarArtist(similarName: String): Future[List[SimilarArtistResult]] = {

    val requestHolder: WSRequestHolder = WS.url(url).withQueryString("method" -> "artist.getsimilar",
      "artist" -> similarName, "api_key" -> key, "format" -> "json")

    val artistResult = requestHolder.get().map {
      response =>
        response.status match {
          case Http.Status.OK =>
            val json: JsValue = Json.parse(response.body)
            Try {
              (json \ "similarartists" \ "artist").as[List[JsValue]].map {
                x =>
                  new SimilarArtistResult(Json.fromJson[SimilarArtist](x).get, (x \ "match").as[String],
                    (x \\ "#text").map(_.as[String]))
              }
            }.getOrElse(Nil)

          case _ => Nil
        }
    }
    artistResult
  }

}

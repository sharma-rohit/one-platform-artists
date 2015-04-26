package services

import com.google.inject.Inject
import domain.{SimilarArtistResult, ArtistResult}
import repositories.UserRepository

import scala.concurrent.Future

class UserService @Inject()(userRepo: UserRepository) {

  def getArtist(artistName:String) :Future[List[ArtistResult]] = {
    userRepo.findArtist(artistName)
  }

  def getSimilarArtist(artistName:String) :Future[List[SimilarArtistResult]] = {
    userRepo.findSimilarArtist(artistName)
  }
}

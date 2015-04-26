package domain


case class SimilarArtistResult(
                                artistInfo: SimilarArtist,
                                mat: String,
                                imageUrl: Seq[String]
                                )

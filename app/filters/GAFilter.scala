package filters

import play.api.Logger
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object GAFilter extends Filter {

  def apply(nextFilter: (RequestHeader) => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val requestMaxProcessingTime = play.Play.application().configuration().getInt("requestMaxProcessingTime")
    val startTime = System.currentTimeMillis
    nextFilter(requestHeader).map {
      result =>
        val endTime = System.currentTimeMillis
        val requestTime = endTime - startTime
        Logger.info(s"${requestHeader.method} ${requestHeader.uri} " +
          s"took ${requestTime}ms and returned ${result.header.status}")

        if (requestTime > requestMaxProcessingTime)
          Logger.warn(s"${requestHeader.method} ${requestHeader.uri} " +
            s"took ${requestTime}ms and returned ${result.header.status}")
        result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}

object AccessControlFilter extends Filter {
  //todo: look to pick this value from config
  def apply(nextFilter: (RequestHeader) => Future[SimpleResult])
           (requestHeader: RequestHeader): Future[SimpleResult] = {
    nextFilter(requestHeader).map(_.withHeaders("Access-Control-Allow-Origin" -> "*"))
  }
}
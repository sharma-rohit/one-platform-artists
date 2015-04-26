import com.google.inject.Guice
import filters.{GAFilter, AccessControlFilter}
import play.api.GlobalSettings
import play.api.mvc.Results._
import play.api.mvc._
import util.InjectorModule

import scala.concurrent.Future


object Global extends WithFilters(GAFilter, AccessControlFilter) with GlobalSettings {

  private lazy val injector = {
    Guice.createInjector(new InjectorModule)
  }

  override def getControllerInstance[A](clazz: Class[A]) = {
    injector.getInstance(clazz)
  }

  override def onHandlerNotFound(r: RequestHeader) = {
    implicit val request = r
    Future.successful(NotFound("Page Not Found"))
  }

  override def onError(r: RequestHeader, throwable: Throwable) = {
    implicit val request = r
    Future.successful(InternalServerError("Internal Server Error"))
  }

  //
  override def onBadRequest(r: RequestHeader, error: String) = {
    implicit val request = r
    Future.successful(BadRequest(error))
  }
}

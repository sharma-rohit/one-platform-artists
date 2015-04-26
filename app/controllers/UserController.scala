package controllers

import domain.SignUp
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Cookie, Action}
import services.UserService
import com.google.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserController @Inject()(userService: UserService) extends BaseController {

  val artistName = Form(
    "artistName" -> nonEmptyText
  )
  val uAndP = Form(
    tuple(
      "userName" -> nonEmptyText,
      "password" -> nonEmptyText
    )
  )

  val uDetails = Form(
    mapping(
      "name" -> nonEmptyText,
      "emailId" -> nonEmptyText,
      "userName" -> nonEmptyText,
      "password" -> nonEmptyText
    )(SignUp.apply)(SignUp.unapply)
  )

  def index = Action {
    implicit request =>
      Ok(views.html.user.login(None))
  }

  def signIn = Action {
    implicit request =>
      try {
        val userAndPass = uAndP.bindFromRequest.get
        val canSignIn = CookieManager.checkSignInCookie(userAndPass)
        if (canSignIn._1) {
          Redirect("/artist").withCookies(Cookie("ili", true.toString, Some(100000000)))
        } else {
          if (canSignIn._2 == 1) {
            Ok(views.html.user.login(Some("Wrong username or password")))
          } else {
            Ok(views.html.user.login(Some("User does not exists")))
          }
        }
      } catch {
        case e: Exception => Ok(views.html.user.login(Some("Some details were wrong")))
      }
  }

  def signUp = Action {
    implicit request =>
      try {
        val userDetails = uDetails.bindFromRequest.get
        val cookieDetails = CookieManager.addSignUpCookie(userDetails)
        cookieDetails match {
          case Some(c) => Redirect("/artist").withCookies(c, Cookie("ili", true.toString, Some(100000000)))
          case _ => Ok(views.html.user.login(Some("User Already Exists")))
        }

      } catch {
        case e: Exception => Ok(views.html.user.login(Some("Some details were wrong")))
      }
  }

  def searchBar = Action {
    implicit request =>
      Ok(views.html.user.search())
  }

  def getArtists(n: Option[String]) = Action.async {
    implicit request =>


      def getName = {
        try {
          artistName.bindFromRequest.get
        } catch {
          case e: Exception => ""
        }
      }
      val name = n.isEmpty match {
        case true => getName
        case _ => n.getOrElse("")
      }

      CookieManager.isLoggedIn match {
        case true =>
          val artists = userService.getArtist(name)
          val similarArtists = userService.getSimilarArtist(name)
          for {
            a <- artists
            s <- similarArtists
          } yield {
            Ok(views.html.user.artist(a, s)).withCookies(Cookie("ili", true.toString, Some(10000000)),
              CookieManager.saveSearchHistory(name))
          }
        case _ => Future(Ok(views.html.user.login(Some("You are not logged in"))))
      }
  }


  def logOut = Action {
    implicit request =>
      Ok(views.html.user.login(None)).withCookies(Cookie("ili", false.toString, Some(100000000)))
  }

  def showSearchHistory = Action {
    implicit request =>
      val sHistory = CookieManager.getSearchHistory()
      Ok(views.html.user.searchHistory(sHistory))

  }

}

package controllers

import domain.SignUp
import play.api.libs.Crypto
import play.api.mvc.{AnyContent, Request, Cookie}
import util.JsonProvider

object CookieManager {

  def addSignUpCookie(signUp: SignUp)(implicit request: Request[AnyContent]): Option[Cookie] = {

    if (checkCookie(signUp.userName)) {
      None
    } else {
      Option(Cookie("s-".concat(signUp.userName), Crypto.encryptAES(JsonProvider.toJson(signUp)), Some(10000000)))
    }
  }

  def checkCookie(name: String)(implicit request: Request[AnyContent]) = {

    request.cookies.get("s-".concat(name)).nonEmpty
  }

  def checkSignInCookie(userAndPass: (String, String))(implicit request: Request[AnyContent]): (Boolean, Int) = {
    request.cookies.get("s-".concat(userAndPass._1)) match {
      case Some(c) => val userData = JsonProvider.fromJson[SignUp](Crypto.decryptAES(c.value))
        (userData.userName == userAndPass._1 &&
          userData.password == userAndPass._2, 1)
      case _ => (false, 2)
    }
  }

  def isLoggedIn()(implicit request: Request[AnyContent]): Boolean = {
    request.cookies.get("ili") match {
      case Some(c) => c.value.toBoolean
      case _ => false
    }
  }

  def saveSearchHistory(searchParam: String)(implicit request: Request[AnyContent]) = {
    request.cookies.get("shis") match {

      case Some(c) =>
        val value = c.value.split(",").contains(searchParam) match {
          case true => c.value
          case _ => c.value.concat("," + searchParam)
        }
        Cookie("shis", value, Some(100000000))

      case _ => Cookie("shis", searchParam, Some(100000000))
    }
  }

  def getSearchHistory()(implicit request: Request[AnyContent]): Seq[String] = {
    request.cookies.get("shis") match {

      case Some(c) => c.value.split(",").toSeq

      case _ => Nil
    }
  }
}

package services

import play.api.mvc.RequestHeader


/**
  * Created by Eric on 11/23/2016.
  */
object WebUtil {

  def refererOr(defaultUrl: String)(implicit request: RequestHeader): String = {
    request.headers.get("referer").getOrElse(defaultUrl)
  }

}

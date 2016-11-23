package controllers

import java.util.UUID
import java.util.Date
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n._
import models._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action {
    Ok(views.html.index(
      List(
        AccountListItem(UUID.randomUUID.toString, "Provision charge appartement", BigDecimal.valueOf(10)),
        AccountListItem(UUID.randomUUID.toString, "Epargne", BigDecimal.valueOf(-10))
      )
    ))
  }

  def show(id: String) = Action {
    Ok(views.html.show(
      AccountDetail(
        UUID.randomUUID.toString,
        "A description",
        BigDecimal.valueOf(10),
        List(
          AccountDetailTransaction(
            BigDecimal.valueOf(10),
            "A description",
            new Date
          )
        )
      )
    ))
  }

  def add = Action { implicit request =>
    val referer = request.headers.get("referer") match {
      case Some(r) => r
      case None => routes.HomeController.index().absoluteURL()
    }

    Ok(views.html.add(form, referer))
  }

  def create = Action { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.add(formWithErrors, formWithErrors.data("referer")))
      },
      formData => {
        Redirect(formData.referer)
      }
    )
  }

  def remove(id: String) = Action {
    Ok(s"remove account ${id}")
  }

  def delete(id: String) = Action {
    Ok(s"delete account ${id}")
  }

  def edit(id: String) = Action {
    Ok(s"edit account ${id}")
  }

  def update(id: String) = Action {
    Ok(s"update account ${id}")
  }

  private def form = Form(
    mapping(
      "description" -> nonEmptyText,
      "referer"     -> text
    )(AccountFormData.apply)(AccountFormData.unapply)
  )
}

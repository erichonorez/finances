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
import services.WebUtil._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AccountController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action { implicit request =>
    Ok(views.html.index(
      List(
        AccountListItem(UUID.randomUUID.toString, "Provision charge appartement", BigDecimal.valueOf(10)),
        AccountListItem(UUID.randomUUID.toString, "Epargne", BigDecimal.valueOf(-10))
      )
    ))
  }

  def show(id: String) = Action { implicit request =>
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
    Ok(views.html.add(form, referer))
  }

  def create = Action { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.add(formWithErrors, formWithErrors.data("referer")))
      },
      formData => {
        Redirect(formData.referer).flashing(
          "success" -> "Account successfully created"
        )
      }
    )
  }

  def remove(id: String) = Action { implicit request =>
    Ok(views.html.remove(UUID.randomUUID.toString, "A description", referer))
  }

  def delete(id: String) = Action { implicit request =>
    Redirect(request.body.asFormUrlEncoded.get.get("referer").head.head).flashing(
      "success" -> "Account successfully deleted"
    )
  }

  def edit(id: String) = Action { implicit request =>
    Ok(views.html.edit(id, form.fill(AccountFormData("a description", referer)), referer))
  }

  def update(id: String) = Action { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.edit(id, formWithErrors, formWithErrors.data("referer")))
      },
      formData => {
        Redirect(formData.referer).flashing(
          "success" -> "Account successfully edited"
        )
      }
    )
  }

  private def form = Form(
    mapping(
      "description" -> nonEmptyText,
      "referer"     -> text
    )(AccountFormData.apply)(AccountFormData.unapply)
  )

  private def referer(implicit request: RequestHeader):String = refererOr(routes.AccountController.index().absoluteURL())
}

package controllers

import java.util.UUID
import javax.inject._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n._
import models._
import org.svomz.apps.finances.core.domain.model.{Account, AccountRepository}
import services.WebUtil._
import org.svomz.apps.finances.core.domain.model.interpreter.AccountService._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AccountController @Inject()(val accountRepository: AccountRepository[String], val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action.async { implicit request =>
    val f: Future[Seq[(Account, BigDecimal)]] = for {
      accounts <- all(accountRepository)
      balances <- Future.traverse(accounts)(account => balance(account.no)(accountRepository))
    } yield accounts zip balances

    f map { xs =>
      Ok(views.html.account.index(
        xs map { x =>
          AccountListItem(x._1.no, x._1.name, x._2)
        } toList
      ))
    } recover { case _ => InternalServerError }
  }

  def show(id: String) = Action.async { implicit request =>
    fetch(id)(accountRepository) flatMap {
      case None => Future { NotFound }
      case Some(account) => {
        for {
          ts <- transactions(account.no)(accountRepository)
          b  <- balance(account.no)(accountRepository)
        } yield {
          Ok(views.html.account.show(
            AccountDetail(
              account.no,
              account.name,
              b,
              ts map {t => AccountDetailTransaction(t.value, t.descriptionOption getOrElse "", t.date)}
            )
          ))
        }
      }
    } recover {case _ => InternalServerError }
  }

  def add = Action { implicit request =>
    Ok(views.html.account.add(form, referer))
  }

  def create = Action { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.account.add(formWithErrors, formWithErrors.data("referer")))
      },
      formData => {

        Redirect(formData.referer).flashing(
          "success" -> "Account successfully created"
        )
      }
    )
  }

  def remove(id: String) = Action { implicit request =>
    Ok(views.html.account.remove(UUID.randomUUID.toString, "A description", referer))
  }

  def delete(id: String) = Action { implicit request =>
    Redirect(request.body.asFormUrlEncoded.get.get("referer").head.head).flashing(
      "success" -> "Account successfully deleted"
    )
  }

  def edit(id: String) = Action { implicit request =>
    Ok(views.html.account.edit(id, form.fill(AccountFormData("a description", referer)), referer))
  }

  def update(id: String) = Action { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.account.edit(id, formWithErrors, formWithErrors.data("referer")))
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

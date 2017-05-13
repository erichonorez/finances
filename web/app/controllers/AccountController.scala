package controllers

import java.time.{LocalDate, ZoneId}
import java.util.Date
import javax.inject._

import models._
import org.svomz.apps.finances.core.application.AccountNotFoundException
import org.svomz.apps.finances.core.application.interpreter.{AccountApi, ReportingApi, ThisMonth, TransactionApi}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._
import services.PlayApiEnv
import services.WebUtil._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.std.scalaFuture._

@Singleton
class AccountController @Inject()(val env: PlayApiEnv, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action.async { implicit request =>
    AccountApi.fetchAll.run(env) map { accounts =>
      Ok(views.html.account.index(accounts))
    }
  }

  def show(id: String) = Action.async { implicit request =>
    val sequence = for {
    account                         <- AccountApi.fetch(id)
      transactions                  <- TransactionApi.list(account.accountId)
      balanceByCategory             <- ReportingApi.balanceByCategory(account.accountId, ThisMonth)
    } yield (account, transactions, balanceByCategory)

    sequence.run(env) map { v => Ok(views.html.account.show(v._1, v._2, v._3)) } recover {
      case AccountNotFoundException(_) => NotFound
      case _ => InternalServerError
    }
  }

  def add = Action { implicit request =>
    Ok(views.html.account.add(form, referer))
  }

  def create = Action.async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => {
        Future { BadRequest(views.html.account.add(formWithErrors, formWithErrors.data("referer"))) }
      },
      formData => {
        AccountApi.open(formData.description).run(env) map { accountId =>
          Redirect(formData.referer).flashing(
            "success" -> "Account successfully created"
          )
        } recover { case _ => InternalServerError }
      }
    )
  }

  def remove(id: String) = Action.async { implicit request =>
    AccountApi.fetch(id) map { account => {
      Ok(views.html.account.remove(account.accountId, account.name, referer))
    } } run env recover {
      case AccountNotFoundException(_) => NotFound
      case _ => InternalServerError
    }
  }

  def delete(id: String) = Action.async { implicit request =>
    AccountApi.close(id) map { _ => Redirect(request.body.asFormUrlEncoded.get.get("referer").head.head).flashing(
      "success" -> "Account successfully deleted"
    ) } run env recover {
      case AccountNotFoundException(_) => NotFound
      case _ => InternalServerError
    }

  }

  def edit(id: String) = Action.async { implicit request =>
    AccountApi.fetch(id) map { account => {
      Ok(views.html.account.edit(account.accountId, form.fill(AccountFormData(account.name, referer)), referer))
    } } run env recover {
      case AccountNotFoundException(_) => NotFound
      case _ => InternalServerError
    }
  }

  def update(id: String) = Action.async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => {
        Future { BadRequest(views.html.account.edit(id, formWithErrors, formWithErrors.data("referer"))) }
      },
      formData => {
        AccountApi.rename(id, formData.description) map { _ => Redirect(formData.referer).flashing(
          "success" -> "Account successfully edited"
        ) } run env recover {
          case AccountNotFoundException(_) => NotFound
          case _ => InternalServerError
        }
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

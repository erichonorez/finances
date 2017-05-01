package controllers

import java.util.UUID
import javax.inject._

import models._
import org.svomz.apps.finances.core.application.{AccountNotFoundException, TransactionNotFoundException}
import org.svomz.apps.finances.core.application.interpreter.{AccountApi, TransactionApi}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._
import services.PlayApiEnv
import services.WebUtil.refererOr

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.std.scalaFuture._

@Singleton
class TransactionController @Inject()(val env: PlayApiEnv, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def add(id: String) = Action.async { implicit request =>
    AccountApi.fetch(id).run(env) map { account =>
      Ok(views.html.transaction.add(account.accountId, form, List(Credit, Debit).map(v => (TransactionType.map(v), TransactionType.map(v))), referer))
    } recover {
      case AccountNotFoundException(_) => NotFound
      case _ => InternalServerError
    }
  }

  def create(id: String) = Action.async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => {
        Future {
          BadRequest(
            views.html.transaction.add(
              id,
              formWithErrors,
              List(Credit, Debit).map(v => (TransactionType.map(v), TransactionType.map(v))),
              formWithErrors.data("referer")
            )
          )
        }
      },
      formData => {
        (formData.transactionType match {
          case Debit => AccountApi.debit(
            id,
            formData.date,
            formData.amount,
            Some(formData.description),
            formData.categoryOption
          )
          case Credit => AccountApi.credit(
            id,
            formData.date,
            formData.amount,
            Some(formData.description),
            formData.categoryOption
          )
        }).run(env) map { _ => Redirect(formData.referer).flashing(
          "success" -> "Transaction successfully created"
        ) }
      }
    )

  }

  def edit(accountId: String, transactionId: String) = Action.async { implicit request =>
    (for {
      account <- AccountApi.fetch(accountId)
      transaction <- TransactionApi.fetch(accountId, transactionId)
    } yield {
      val data = TransactionFormData(
        if (transaction.amount < 0) Debit else Credit,
        transaction.descriptionOption.getOrElse(""),
        transaction.amount.abs,
        transaction.date,
        transaction.categoryOption,
        referer
      )

      Ok(views.html.transaction.edit(account.accountId, transaction.transactionId, form.fill(data), List(Credit, Debit).map(v => (TransactionType.map(v), TransactionType.map(v))), referer))
    }).run(env) recover {
      case AccountNotFoundException(_) => NotFound
      case _ => InternalServerError
    }
  }

  def update(accountId: String, transactionId: String) = Action.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors => {
        Future { BadRequest(views.html.transaction.edit(accountId, transactionId, form, List(Credit, Debit).map(v => (TransactionType.map(v), TransactionType.map(v))), formWithErrors.data("referer"))) }
      },
      formData => {
        (for {
          _           <- AccountApi.cancelTransaction(accountId, transactionId)
          _           <- formData.transactionType match {
                            case Debit => AccountApi.debit(
                              accountId,
                              formData.date,
                              formData.amount,
                              Some(formData.description),
                              formData.categoryOption
                            )
                            case Credit => AccountApi.credit(
                              accountId,
                              formData.date,
                              formData.amount,
                              Some(formData.description),
                              formData.categoryOption
                            )
                          }
        } yield {
          Redirect(formData.referer).flashing(
            "success" -> "Transaction successfully edited"
          )
        }).run(env) recover {
          case AccountNotFoundException(_) => NotFound
          case TransactionNotFoundException(_) => NotFound
          case _ => InternalServerError
        }
      }
    )
  }

  def remove(accountId: String, transactionId: String) = Action.async { implicit request =>
    (for {
      account <- AccountApi.fetch(accountId)
      transaction <- TransactionApi.fetch(account.accountId, transactionId)
    } yield {
      Ok(views.html.transaction.remove(account.accountId, transaction, referer))
    }).run(env) recover {
      case AccountNotFoundException(_) => NotFound
      case TransactionNotFoundException(_) => NotFound
      case _ => InternalServerError
    }
  }

  def delete(accountId: String, transactionId: String) = Action.async { implicit request =>
    AccountApi.cancelTransaction(accountId, transactionId) map { _ => {
      Redirect(request.body.asFormUrlEncoded.get("referer").head).flashing(
        "success" -> "Transaction successfully deleted"
      )
    } } run(env) recover {
      case AccountNotFoundException(_) => NotFound
      case TransactionNotFoundException(_) => NotFound
      case _ => InternalServerError
    }
  }

  def addBulk(id: String) = Action { implicit request =>
    val referer = request.headers.get("referer").getOrElse(routes.AccountController.show(id).absoluteURL())

    Ok(views.html.transaction.addBulk(UUID.randomUUID.toString, formBulk, List(Credit, Debit).map(v => (TransactionType.map(v), TransactionType.map(v))),referer))
  }

  def createBulk(id: String) = Action { implicit request =>
    formBulk.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.transaction.addBulk(UUID.randomUUID.toString, formWithErrors, List(Credit, Debit).map(v => (TransactionType.map(v), TransactionType.map(v))), formWithErrors.data("referer"))),
      form => Redirect(form.referer)
    )
  }

  private def form = Form(
    mapping(
      "type"        -> nonEmptyText,
      "description" -> nonEmptyText,
      "amount"      -> bigDecimal,
      "date"        -> date,
      "category"    -> optional(text),
      "referer"     -> text
    )((transactionType, description, amount, date, categoryOptional, referer) => {
      TransactionFormData(
        TransactionType.unmap(transactionType).get,
        description,
        amount,
        date,
        categoryOptional,
        referer
      )
    })(v => Some((TransactionType.map(v.transactionType), v.description, v.amount, v.date, v.categoryOption, v.referer)))
  )

  private def formBulk = Form(
    mapping(
      "transactions" -> list[TransactionBulkFormDataItem](
        mapping(
          "type" -> nonEmptyText,
          "description" -> nonEmptyText,
          "amount" -> bigDecimal,
          "date" -> date
        )((transactionType, description, amount, date) => {
          TransactionBulkFormDataItem(
            TransactionType.unmap(transactionType).get,
            description,
            amount,
            date
          )
        })(v => Some((TransactionType.map(v.transactionType), v.description, v.amount, v.date)))),
      "referer" -> text
    )(TransactionBulkFormData.apply)(TransactionBulkFormData.unapply)
  )

  private def referer(implicit request: RequestHeader):String = refererOr(routes.AccountController.index().absoluteURL())


}

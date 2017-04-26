package controllers

import java.util.UUID
import javax.inject._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n._
import models._
import org.svomz.apps.finances.core.domain.model.AccountRepository
import org.svomz.apps.finances.core.domain.model.interpreter.AccountService._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TransactionController @Inject()(val accountRepository: AccountRepository[String], val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def add(id: String) = Action.async { implicit request =>
    fetch(id)(accountRepository) map {
      case None => NotFound
      case Some(account) => {
        val referer = request.headers.get("referer").getOrElse(routes.AccountController.show(id).absoluteURL())

        Ok(views.html.transaction.add(account.no, form, List(Credit(), Debit()).map(v => (TransactionType.map(v), TransactionType.map(v))), referer))
      }
    } recover { case _ => InternalServerError }
  }

  def create(id: String) = Action.async { implicit request =>
    fetch(id)(accountRepository) flatMap {
      case None => Future { NotFound }
      case Some(account) => {
        form.bindFromRequest.fold(
          formWithErrors => Future { BadRequest(views.html.transaction.add(UUID.randomUUID.toString, formWithErrors, List(Credit(), Debit()).map(v => (TransactionType.map(v), TransactionType.map(v))), formWithErrors.data("referer"))) },
          form => {
            (form.transactionType match {
              case Credit() => credit(account.no, form.date, form.amount, Some(form.description))(accountRepository)
              case Debit()  => debit(account.no, form.date, form.amount, Some(form.description))(accountRepository)
            }) map { _ => Redirect(form.referer)}
          }
        )
      }
    } recover { case _ => InternalServerError }
  }

  def addBulk(id: String) = Action { implicit request =>
    val referer = request.headers.get("referer").getOrElse(routes.AccountController.show(id).absoluteURL())

    Ok(views.html.transaction.addBulk(UUID.randomUUID.toString, formBulk, List(Credit(), Debit()).map(v => (TransactionType.map(v), TransactionType.map(v))),referer))
  }

  def createBulk(id: String) = Action { implicit request =>
    formBulk.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.transaction.addBulk(UUID.randomUUID.toString, formWithErrors, List(Credit(), Debit()).map(v => (TransactionType.map(v), TransactionType.map(v))), formWithErrors.data("referer"))),
      form => Redirect(form.referer)
    )
  }

  private def form = Form(
    mapping(
      "type"        -> nonEmptyText,
      "description" -> nonEmptyText,
      "amount"      -> bigDecimal,
      "date"        -> date,
      "referer"     -> text
    )((transactionType, description, amount, date, referer) => {
      TransactionFormData(
        TransactionType.unmap(transactionType).get,
        description,
        amount,
        date,
        referer
      )
    })(v => Some((TransactionType.map(v.transactionType), v.description, v.amount, v.date, v.referer)))
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

}

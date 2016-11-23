package controllers

import java.util.Date
import java.util.UUID

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n._
import models._

@Singleton
class TransactionController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def add(id: String) = Action { implicit request =>
    val referer = request.headers.get("referer").getOrElse(routes.HomeController.show(id).absoluteURL())

    Ok(views.html.transactions.add(UUID.randomUUID.toString, form, List(Credit(), Debit()).map(v => (TransactionType.map(v), TransactionType.map(v))), referer))
  }

  def create(id: String) = Action { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.transactions.add(UUID.randomUUID.toString, formWithErrors, List(Credit(), Debit()).map(v => (TransactionType.map(v), TransactionType.map(v))), formWithErrors.data("referer"))),
      form => Redirect(form.referer)
    )
  }

  def addBulk(id: String) = Action { implicit request =>
    val referer = request.headers.get("referer").getOrElse(routes.HomeController.show(id).absoluteURL())

    Ok(views.html.transactions.addBulk(UUID.randomUUID.toString, formBulk, List(Credit(), Debit()).map(v => (TransactionType.map(v), TransactionType.map(v))),referer))
  }

  def createBulk(id: String) = Action { implicit request =>
    formBulk.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.transactions.addBulk(UUID.randomUUID.toString, formWithErrors, List(Credit(), Debit()).map(v => (TransactionType.map(v), TransactionType.map(v))), formWithErrors.data("referer"))),
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

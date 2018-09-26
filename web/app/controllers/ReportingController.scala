package controllers

import javax.inject.Inject

import models.TimePeriodMapper
import org.svomz.apps.finances.core.application.AccountNotFoundException
import org.svomz.apps.finances.core.application.interpreter._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.PlayApiEnv

import scala.concurrent.ExecutionContext.Implicits.global

class ReportingController @Inject()(val env: PlayApiEnv, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def balancesByCategory(accountId: String, periodO: Option[String]) = Action.async { implicit request =>
    val period = periodO match {
      case None => ThisMonth
      case Some(s) => TimePeriodMapper.fromString(s)
    }

    ReportingApi.balanceByCategory(accountId, period) run env map { ts =>
      Ok(views.html.reporting.balancesByCategory(ts))
    } recover {
      case AccountNotFoundException(_) => NotFound
      case _ => InternalServerError
    }
  }

  def totalDebitsByCategory(accountId: String, periodO: Option[String]) = Action.async { implicit request =>
    val period = periodO match {
      case None => ThisMonth
      case Some(s) => TimePeriodMapper.fromString(s)
    }

    ReportingApi.totalExpensesByCategory(accountId, period) run env map { ts =>
      Ok(views.html.reporting.totalExpensesByCategory(accountId, ts, period))
    }
  }

  def totalDebits(accountId: String, periodO: Option[String]) = Action.async { implicit request =>
    val period = periodO match {
      case None => ThisMonth
      case Some(s) => TimePeriodMapper.fromString(s)
    }

    ReportingApi.totalDebit(accountId, period) run env map { amount =>
      Ok(views.html.reporting.totalDebits(accountId, amount, period))
    }
  }

}

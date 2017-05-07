package controllers

import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

import org.svomz.apps.finances.core.application.AccountNotFoundException
import org.svomz.apps.finances.core.application.interpreter.ReportingApi
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.PlayApiEnv

import scala.concurrent.ExecutionContext.Implicits.global

class ReportingController @Inject()(val env: PlayApiEnv, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def balancesByCategory(accountId: String, fromO: Option[String], toO: Option[String]) = Action.async { implicit request =>
    val from = fromO match {
      case None => None
      case Some(d) => Some(
        Date.from(
          LocalDateTime.parse(d, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            .toLocalDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
        )
      )
    }

    val to = toO match {
      case None => None
      case Some(d) => Some(
        Date.from(
          LocalDateTime.parse(d, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            .toLocalDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
        )
      )
    }

    ReportingApi.balanceByCategory(accountId, from, to) run env map { ts =>
      Ok(views.html.reporting.balancesByCategory(ts))
    } recover {
      case AccountNotFoundException(_) => NotFound
      case _ => InternalServerError
    }
  }

}

package org.svomz.apps.finances.core.application.interpreter

import java.time.{LocalDate, ZoneId}
import java.util.Date

import org.svomz.apps.finances.core.application.ReportingApi
import org.svomz.apps.finances.core.domain.model.TransactionService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.math.BigDecimal.RoundingMode
import scalaz.Kleisli

class ReportingApiInterpreter extends ReportingApi[String, String, BigDecimal, TimePeriod, ApiEnv] with Existing {

  override def balanceByCategory(accountId: String, timePeriod: TimePeriod): Query[List[(String, BigDecimal)]] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          env.transactionRepository.fetchAllCategories(account.id) flatMap { cs =>
            val from = Some(Date.from(timePeriod.from.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            val to = Some(Date.from(timePeriod.to.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            Future.traverse(cs)(c => env.transactionRepository.fetchAllWithCategory(account.id, c, from, to) map {
              ts => ts match {
                case List() => (c.name, BigDecimal(0))
                case _ => (c.name, TransactionService.balanceAfter(ts.head, ts))
              }
            })
          }
        })(env)
      }
    }
  }

  /**
    * Computes the total of expense for each categories over a period of time.
    *
    * @param accountId
    * @return
    */
  override def totalExpensesByCategory(accountId: String, timePeriod: TimePeriod): Query[List[(String, BigDecimal, Double)]] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          env.transactionRepository.fetchAllCategories(account.id) flatMap { cs =>
            val from = Some(Date.from(timePeriod.from.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            val to = Some(Date.from(timePeriod.to.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            Future.traverse(cs)(c => env.transactionRepository.fetchAllDebitWithCategory(account.id, c, from, to) map {
              ts => ts match {
                case List() => (c.name, BigDecimal(0))
                case _ => (c.name, TransactionService.balanceAfter(ts.head, ts).abs)
              }
            }) map { ts => // keep categories for which there is at least one transaction
              ts filter (t => t._2 != BigDecimal(0))
            }  map { ts => // enrich with the percentage of each total
              val total = ts.map(_._2).foldLeft(BigDecimal(0))(_ + _).abs
              ts.map(t => (t._1, t._2.abs, ((t._2.abs / total) * 100).setScale(2, RoundingMode.DOWN).toDouble))
            }
          }
        })(env)
      }
    }
  }

  /**
    * Computes the total of debit over a period of time.
    *
    * @param accountId
    * @param period
    * @return
    */
  override def totalDebit(accountId: String, period: TimePeriod): Query[BigDecimal] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          val from = Some(Date.from(period.from.atStartOfDay(ZoneId.systemDefault()).toInstant()))
          val to = Some(Date.from(period.to.atStartOfDay(ZoneId.systemDefault()).toInstant()))
          env.transactionRepository.fetchAllDebits(account.id, from, to) map { ts =>
            ts.foldLeft(BigDecimal(0))((v, t) => t.apply(v)).abs
          }
        })(env)
      }
    }
  }

  /**
    * Computes the total of debit over a period of time.
    *
    * @param accountId
    * @param period
    * @return
    */
  override def totalCredit(accountId: String, period: TimePeriod): Query[BigDecimal] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          val from = Some(Date.from(period.from.atStartOfDay(ZoneId.systemDefault()).toInstant()))
          val to = Some(Date.from(period.to.atStartOfDay(ZoneId.systemDefault()).toInstant()))
          env.transactionRepository.fetchAllCredits(account.id, from, to) map { ts =>
            ts.foldLeft(BigDecimal(0))((v, t) => t.apply(v)).abs
          }
        })(env)
      }
    }
  }
}

sealed trait TimePeriod {
  def from: LocalDate
  def to: LocalDate
}
object ThisMonth extends TimePeriod {
  override def from: LocalDate = LocalDate.now().withDayOfMonth(1).atStartOfDay().toLocalDate
  override def to: LocalDate = from.plusMonths(1)
}
object LastMonth extends TimePeriod {
  override def from: LocalDate = LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay().toLocalDate
  override def to: LocalDate = from.plusMonths(1)
}
object LastThreeMonths extends TimePeriod {
  override def from: LocalDate = LocalDate.now().minusMonths(3).withDayOfMonth(1).atStartOfDay().toLocalDate
  override def to: LocalDate = from.plusMonths(3)
}
object ThisYear extends TimePeriod {
  override def from: LocalDate =  LocalDate.now().withDayOfYear(1).atStartOfDay().toLocalDate
  override def to: LocalDate = from.plusYears(1)
}
object LastYear extends  TimePeriod {
  override def from: LocalDate = LocalDate.now().minusYears(1).withDayOfMonth(1).atStartOfDay().toLocalDate
  override def to: LocalDate = from.plusYears(1)
}
case class CustomTimePeriod(from: LocalDate, to: LocalDate) extends TimePeriod


object ReportingApi extends ReportingApiInterpreter

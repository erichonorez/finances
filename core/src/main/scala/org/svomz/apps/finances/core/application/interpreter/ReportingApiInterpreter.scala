package org.svomz.apps.finances.core.application.interpreter

import org.svomz.apps.finances.core.application.ReportingApi
import org.svomz.apps.finances.core.domain.model.TransactionService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Kleisli

class ReportingApiInterpreter extends ReportingApi[String, BigDecimal, ApiEnv] with Existing {

  override def balanceByCategoy(accountId: String): Query[List[(String, BigDecimal)]] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          env.transactionRepository.fetchAllCategories(account.id) flatMap { cs =>
            Future.traverse(cs)(c => env.transactionRepository.fetchAllWithCategory(account.id, c) map {
              ts => (c.name, TransactionService.balanceAfter(ts.head, ts))
            })
          }
        })(env)
      }
    }
  }
}

object ReportingApi extends ReportingApiInterpreter

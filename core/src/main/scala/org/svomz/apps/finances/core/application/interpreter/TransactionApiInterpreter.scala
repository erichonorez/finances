package org.svomz.apps.finances.core.application.interpreter

import org.svomz.apps.finances.core.application.{TransactionApi, TransactionListItemProjection, TransactionProjection}
import org.svomz.apps.finances.core.domain.model.TransactionService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Kleisli

class TransactionApiInterpreter extends TransactionApi[String, String, BigDecimal, String, ApiEnv] with Existing {
  /**
    * Lists all transaction of an account
    *
    * @param accountId
    * @return
    */
  override def list(accountId: String): Query[List[TransactionListItemProjection]] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          env.transactionRepository.fetchAll(account.id) map {
            ts => ts map {
              t => TransactionListItemProjection(t.id.value,
                t.date,
                t.value,
                TransactionService.balanceAfter(t, ts),
                t.descriptionOption,
                t.categoryOption match {
                  case None => None
                  case Some(c) => Some(c.name)
                })
            }
          }
        })(env)
      }
    }
  }

  /**
    *
    * @param accountId
    * @param transactionId
    * @return
    */
  override def fetch(accountId: String, transactionId: String): Query[TransactionProjection] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          withExistingTransaction(account.id.value, transactionId)(t => {
            Future {
              TransactionProjection(t.id.value,
                t.date,
                t.value,
                t.descriptionOption,
                t.categoryOption match {
                  case None => None
                  case Some(c) => Some(c.name)
                }
              )
            }
          })(env)
        })(env)
      }
    }
  }
}

object TransactionApi extends TransactionApiInterpreter
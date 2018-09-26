package org.svomz.apps.finances.core.application.interpreter

import org.svomz.apps.finances.core.application.{AccountNotFoundException, TransactionNotFoundException}
import org.svomz.apps.finances.core.domain.model.{Account, AccountId, Transaction, TransactionId}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Kleisli

trait Existing {

  protected def withExistingAccount[A](accountId: String)(f: Account => Future[A]): Kleisli[Future, ApiEnv, A] = Kleisli {
    env => {
      env.accountRepository.fetch(AccountId(accountId)) flatMap {
        case None => Future.failed(new AccountNotFoundException(accountId))
        case Some(account) => f(account)
      }
    }
  }

  protected def withExistingTransaction[A](accountId: String, transactionId: String)(f: Transaction => Future[A]): Kleisli[Future, ApiEnv, A] = Kleisli {
    env => {
      env.transactionRepository.fetch(AccountId(accountId), TransactionId(transactionId)) flatMap {
        case None => Future.failed(TransactionNotFoundException(transactionId))
        case Some(transaction) => f(transaction)
      }
    }
  }

}

package org.svomz.apps.finances.core.application.interpreter

import java.util.Date

import org.svomz.apps.finances.core.domain.model._
import org.svomz.apps.finances.core.application.{AccountApi, AccountProjection}

import scala.concurrent.Future
import scalaz.Kleisli.kleisli
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.Kleisli

class AccountApiInterpreter extends AccountApi[String, BigDecimal, String, String, ApiEnv] with Existing {

  override def open(name: String): Command[String] = {
    kleisli {
      env => {
        for {
          accountId <- env.accountRepository.nextIdentity
          account <- env.accountRepository.create(Account(accountId, name))
        } yield account.id.value
      }
    }
  }

  override def close(accountId: String): Command[String] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => env.accountRepository.delete(account) map { a => a.id.value })(env)
      }
    }
  }

  override def rename(accountId: String, name: String): Command[String] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          val toUpdate = Account(
            account.id,
            name
          )
          env.accountRepository.update(toUpdate) map { a => a.id.value }
        })(env)
      }
    }
  }

  override def credit(accountId: String,
                      date: Date,
                      amount: BigDecimal,
                      descriptionOption: Option[String],
                      categoryOption: Option[String]
                     ): Command[String] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          for {
            transactionId       <- env.transactionRepository.nextIdentity
            transaction         <- env.transactionRepository.create(Credit(
              account.id,
              transactionId,
              amount.abs,
              date,
              descriptionOption,
              categoryOption match {
                case None => None
                case Some(v) => Some(Category(v))
              }
            ))
          } yield transaction.id.value
        })(env)
      }
    }
  }

  override def debit(accountId: String,
                      date: Date,
                      amount: BigDecimal,
                      descriptionOption: Option[String],
                      categoryOption: Option[String]
                     ): Command[String] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          for {
            transactionId       <- env.transactionRepository.nextIdentity
            transaction         <- env.transactionRepository.create(Debit(
              account.id,
              transactionId,
              amount.abs,
              date,
              descriptionOption,
              categoryOption match {
                case None => None
                case Some(v) => Some(Category(v))
              }
            ))
          } yield transaction.id.value
        })(env)
      }
    }
  }

  override def cancelTransaction(accountId: String, transactionId: String): Command[String] = {
    Kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          withExistingTransaction(accountId, transactionId)(t => {
            env.transactionRepository.delete(t) map {t => t.id.value }
          })(env)
        })(env)
      }
    }
  }

  override def fetch(accountId: String): Query[AccountProjection] = {
    kleisli {
      env => {
        withExistingAccount(accountId)(account => {
          balance(account.id)(env) map { b =>
            AccountProjection(accountId, account.name, b)
          }
        })(env)
      }
    }
  }

  override def fetchAll: Query[List[AccountProjection]] = {
    Kleisli {
      env => {
        env.accountRepository.all flatMap { as => {
          Future.traverse(as)(a => balance(a.id)(env) map { b => AccountProjection(a.id.value, a.name, b) } )
        } }
      }
    }
  }

  private def balance(accountId: AccountId): Query[BigDecimal] = {
    Kleisli {
      env => {
        env.transactionRepository.fetchAll(accountId) map { ts => {
          if (ts.isEmpty) BigDecimal(0) else TransactionService.balanceAfter(ts.head, ts)
        } }
      }
    }
  }
}

object AccountApi extends AccountApiInterpreter

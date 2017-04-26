package org.svomz.apps.finances.core.domain.model.interpreter

import java.util.{Date, NoSuchElementException}

import org.svomz.apps.finances.core.domain.model._

import scala.concurrent.Future
import scalaz.{Failure, Kleisli, NonEmptyList, Reader, Success, Validation}
import scalaz.syntax.validation._
import scalaz.syntax.apply._
import scala.concurrent.ExecutionContext.Implicits.global

class AccountServiceInterpreter extends AccountService[Account, String, BigDecimal, Transaction, AccountRepository[String]] {

  override def open(no: String, name: String): AccountOp = {
    Kleisli {
      repository => {
        validate(
          Account(
            no,
            name,
            List()
          )
        ) match {
          case Failure(errors) => Future.failed(new ValidationException(errors))
          case Success(account) => repository.persist(
            account
          )
        }

      }
    }
  }

  override def credit(no: String, date: Date, amount: BigDecimal, descriptionOption: Option[String]): AccountOp = {
    Kleisli {
      repository => {
        fetch(no)(repository) flatMap { accountO => accountO match {
          case None => Future.failed(new NoSuchElementException)
          case Some(account) => repository.persist(
            account.copy(
              transactions = Credit(amount.abs, date, descriptionOption) :: account.transactions
            )
          )
        }}
      }
    }
  }

  override def debit(no: String, date: Date, amount: BigDecimal, descriptionOption: Option[String]): AccountOp = {
    Kleisli {
      repository => {
        fetch(no)(repository) flatMap { accountO => accountO match {
          case None => Future.failed(new NoSuchElementException)
          case Some(account) => repository.persist(
            account.copy(
              transactions = Debit(amount.abs, date, descriptionOption) :: account.transactions
            )
          )
        }}
      }
    }
  }

  override def balance(no: String): Kleisli[Future, AccountRepository[String], BigDecimal] = {
    Kleisli {
      repository => {
        transactions(no)(repository) map { transactions =>
          transactions.foldLeft(BigDecimal.valueOf(0))((left, transaction) => {
            transaction.apply(left)
          })
        }
      }
    }
  }


  override def transactions(no: String): Kleisli[Future, AccountRepository[String], List[Transaction]] = {
    Kleisli {
      repository => {
        repository.fetch(no) flatMap { accountO => accountO match {
          case Some(account) => Future {
            account.transactions
          }
          case None => Future.failed(new NoSuchElementException)
        }
        }
      }
    }
  }

  override def all: Kleisli[Future, AccountRepository[String], Seq[Account]] = {
    Kleisli {
      repository => {
        repository.all
      }
    }
  }

  override def fetch(id: String): Kleisli[Future, AccountRepository[String], Option[Account]] = {
    Kleisli {
      repository => {
        repository.fetch(id)
      }
    }
  }


  private def validate(account: Account): Validation[NonEmptyList[String], Account] = {
    def validateId(account: Account): Validation[NonEmptyList[String], Account] = {
      if (account.no.isEmpty) "Account no cannot be empty".failureNel[Account]
      else account.successNel
    }

    def validateName(account: Account): Validation[NonEmptyList[String], Account] = {
      if (account.name.isEmpty) "Account name cannot be empty".failureNel[Account]
      else account.success
    }

    (validateId(account) |@| validateName(account)) { (_, _) => account }
  }
}

object AccountService extends AccountServiceInterpreter
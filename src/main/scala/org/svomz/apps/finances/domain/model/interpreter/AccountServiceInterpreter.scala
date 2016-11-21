package org.svomz.apps.finances.domain.model.interpreter

import java.util.Date

import org.svomz.apps.finances.domain.model._

import scalaz.{-\/, Failure, NonEmptyList, Reader, Success, Validation, \/, \/-}
import scalaz.syntax.validation._
import scalaz.syntax.apply._

class AccountServiceInterpreter extends AccountService[Account, String, BigDecimal, Transaction, AccountRepository[String]] {
  override def open(no: String, name: String): AccountOp = {
    Reader(
      repository => {
        validate(
          Account(
            no,
            name,
            List()
          )
        ) match {
          case Failure(errors) => -\/(new ValidationException(errors))
          case Success(account) => \/-(
            repository.persist(
              account
            )
          )
        }

      }
    )
  }

  override def credit(no: String, date: Date, amount: BigDecimal, descriptionOption: Option[String]): AccountOp = {
    Reader(
      repository => {
        for {
          account <- existingAccount(no)(repository)
        } yield repository.persist(
          account.copy(
            transactions = Credit(amount.abs, date, descriptionOption) :: account.transactions
          )
        )
      }
    )
  }

  override def debit(no: String, date: Date, amount: BigDecimal, descriptionOption: Option[String]): AccountOp = {
    Reader(
      repository => {
        for {
          account <- existingAccount(no)(repository)
        } yield repository.persist(
          account.copy(
            transactions = Debit(amount.abs, date, descriptionOption) :: account.transactions
          )
        )
      }
    )
  }

  override def balance(no: String): Reader[AccountRepository[String], \/[Throwable, BigDecimal]] = {
    Reader(
      repository => {
        for {
          account <- existingAccount(no)(repository)
        } yield account.transactions.foldLeft(BigDecimal.valueOf(0))((left, transaction) => { transaction.apply(left) })
      }
    )
  }


  override def transactions(no: String): Reader[AccountRepository[String], \/[Throwable, List[Transaction]]] = {
    Reader(
      repository => {
        for (account <- existingAccount(no)(repository)) yield { account.transactions }
      }
    )
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

    (validateId(account) |@| validateName(account)){ (_, _) => account }
  }

  private def existingAccount(no: String)(repository: AccountRepository[String]): \/[Throwable, Account] = {
    repository.fetch(no) match {
      case None => -\/(new NoSuchElementException)
      case Some(account) => \/-(account)
    }
  }
}
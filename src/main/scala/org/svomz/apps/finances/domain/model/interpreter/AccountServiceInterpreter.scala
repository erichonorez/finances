package org.svomz.apps.finances.domain.model.interpreter

import java.util.Date

import org.svomz.apps.finances.domain.model._

import scalaz.{-\/, Reader, \/, \/-}

class AccountServiceInterpreter extends AccountService[Account, String, BigDecimal, Transaction, AccountRepository[String]] {
  override def open(no: String, name: String): AccountOp = {
    Reader(
      repository => {
        \/-(
          repository.persist(
            Account(
              no,
              name,
              List()
            )
          )
        )
      }
    )
  }

  override def credit(no: String, date: Date, amount: BigDecimal, descriptionOption: Option[String]): AccountOp = {
    Reader(
      repository => {
        for {
          account <- existingAccount(no)(repository)
          _       <- repository.persist(
            account.copy(
              transactions = Credit(amount, date, descriptionOption) :: account.transactions
            )
          )
        } yield account
      }
    )
  }

  override def debit(no: String, date: Date, amount: BigDecimal, descriptionOption: Option[String]): AccountOp = {
    Reader(
      repository => {
        for {
          account <- existingAccount(no)(repository)
          _       <- repository.persist(
            account.copy(
              transactions = Debit(amount, date, descriptionOption) :: account.transactions
            )
          )
        } yield account
      }
    )
  }

  override def balance(no: String): Reader[AccountRepository[String], \/[Throwable, BigDecimal]] = {
    Reader(
      repository => {
        for {
          account <- existingAccount(no)(repository)
        } yield account.transactions.foldLeft(BigDecimal.valueOf(0))((left, transaction) => left + transaction.amount)
      }
    )
  }


  override def transactions(no: String): Reader[AccountRepository[String], \/[Throwable, List[Transaction]]] = {
    Reader(
      repository => {
        for (account <- existingAccount(no)(repository)) yield account.transactions.sortBy(_.date).reverse
      }
    )
  }

  private def existingAccount(no: String)(repository: AccountRepository[String]): \/[Throwable, Account] = {
    repository.fetch(no) match {
      case None => -\/(new NoSuchElementException)
      case Some(account) => \/-(
        account
      )
    }
  }

}
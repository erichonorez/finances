package org.svomz.apps.finances.domain.model

import java.util.Date

import scalaz.{Reader, \/}

trait AccountService[Account, AccountNumber, Amount, Transaction, AccountRepository] {
  type AccountOp = Reader[AccountRepository, \/[Throwable, Account]]

  def open(no: AccountNumber, name: String): AccountOp
  def credit(no: AccountNumber, date: Date, amount: Amount, descriptionOption: Option[String]): AccountOp
  def debit(no: AccountNumber, date: Date, amount: Amount, descriptionOption: Option[String]): AccountOp
  def balance(no: AccountNumber): Reader[AccountRepository, \/[Throwable, Amount]]
  def transactions(no: AccountNumber): Reader[AccountRepository, \/[Throwable, List[Transaction]]]
}

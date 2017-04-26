package org.svomz.apps.finances.core.domain.model

import java.util.Date

import scala.concurrent.Future
import scalaz.{Kleisli, Reader, \/}

trait AccountService[Account, AccountNumber, Amount, Transaction, AccountRepository] {
  type AccountOp = Kleisli[Future, AccountRepository, Account]

  def open(no: AccountNumber, name: String): AccountOp
  def credit(no: AccountNumber, date: Date, amount: Amount, descriptionOption: Option[String]): AccountOp
  def debit(no: AccountNumber, date: Date, amount: Amount, descriptionOption: Option[String]): AccountOp

  def balance(no: AccountNumber): Kleisli[Future, AccountRepository, Amount]
  def transactions(no: AccountNumber): Kleisli[Future, AccountRepository, List[Transaction]]
  def all: Kleisli[Future, AccountRepository, Seq[Account]]
  def fetch(id: AccountNumber): Kleisli[Future, AccountRepository, Option[Account]]

}

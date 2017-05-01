package org.svomz.apps.finances.core.domain.model

import java.util.Date

case class TransactionId(value: String)

abstract sealed class Transaction(
  val accountId: AccountId,
  val id: TransactionId,
  val amount: BigDecimal,
  val date: Date,
  val descriptionOption: Option[String],
  val categoryOption: Option[Category]) {

  def apply(that: BigDecimal): BigDecimal = this match {
    case Credit(_, _, amount, _, _, _) => that + this.amount
    case Debit(_, _, amount, _, _, _) => that - this.amount
  }

  def value: BigDecimal = this match {
    case Credit(_, _, amount, _, _, _) => this.amount
    case Debit(_, _, amount, _, _, _) => this.amount * -1
  }

  def isCredit: Boolean = this match {
    case Credit(_, _, _, _, _, _) => true
    case Debit(_, _, _, _, _, _) => false
  }

  def isDebit: Boolean = !isCredit

}
case class Credit(override val accountId: AccountId,
                  override val id: TransactionId,
                  override val amount: BigDecimal,
                  override val date: Date,
                  override val descriptionOption: Option[String],
                  override val categoryOption: Option[Category])
                  extends Transaction(accountId, id, amount, date, descriptionOption, categoryOption)

case class Debit(override val accountId: AccountId,
                  override val id: TransactionId,
                  override val amount: BigDecimal,
                  override val date: Date,
                  override val descriptionOption: Option[String],
                  override val categoryOption: Option[Category])
                  extends Transaction(accountId, id, amount, date, descriptionOption, categoryOption)

package org.svomz.apps.finances.core.domain.model

import java.util.Date

abstract sealed class Transaction(val amount: BigDecimal, val date: Date, val descriptionOption: Option[String]) {
  def apply(that: BigDecimal): BigDecimal = this match {
    case Credit(amount, _, _) =>  that + this.amount
    case Debit(amount, _, _) => that - this.amount
  }

  def value: BigDecimal = this match {
    case Credit(amount, _, _) =>  this.amount
    case Debit(amount, _, _) => this.amount * -1
  }
}
case class Credit(override val amount: BigDecimal, override val date: Date, override val descriptionOption: Option[String]) extends Transaction(amount, date, descriptionOption)
case class Debit(override val amount: BigDecimal, override val date: Date, override val descriptionOption: Option[String]) extends Transaction(amount, date, descriptionOption)
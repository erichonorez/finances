package org.svomz.apps.finances.domain.model

import java.util.Date

abstract sealed class Transaction(val amount: BigDecimal, val date: Date, val descriptionOption: Option[String])
case class Credit(override val amount: BigDecimal, override val date: Date, override val descriptionOption: Option[String]) extends Transaction(amount, date, descriptionOption)
case class Debit(override val amount: BigDecimal, override val date: Date, override val descriptionOption: Option[String]) extends Transaction(amount, date, descriptionOption)
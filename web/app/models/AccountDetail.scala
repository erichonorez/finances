package models

import java.util.Date

case class AccountDetail(
  id: String,
  description: String,
  balance: BigDecimal,
  transactions: List[AccountDetailTransaction]
)

case class AccountDetailTransaction(
  amount: BigDecimal,
  description: String,
  date: Date
)
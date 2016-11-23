package models

import java.util.Date

case class TransactionBulkFormDataItem(
  transactionType: TransactionType,
  description: String,
  amount: BigDecimal,
  date: Date
)

case class TransactionBulkFormData(
  transactions: List[TransactionBulkFormDataItem],
  referer: String
)

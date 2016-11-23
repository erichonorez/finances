package models

import java.util.Date

case class TransactionFormData(
  transactionType: TransactionType,
  description: String,
  amount: BigDecimal,
  date: Date,
  referer: String
)

abstract sealed class TransactionType
case class Credit() extends TransactionType
case class Debit() extends TransactionType

object TransactionType {
  def map(v: TransactionType): String = v match {
    case Credit() => "credit"
    case Debit() => "debit"
  }

  def unmap(v: String): Option[TransactionType] = v match {
    case "credit" => Some(Credit())
    case "debit" => Some(Debit())
    case _ => None
  }
}
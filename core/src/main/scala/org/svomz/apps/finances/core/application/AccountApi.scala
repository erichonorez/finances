package org.svomz.apps.finances.core.application

import java.util.Date

trait AccountApi[AccountId, Amount, TransactionId, Category, Env] extends CommonApi[Env] {

  def open(name: String): Command[AccountId]

  def close(accountId: AccountId): Command[AccountId]

  def rename(accountId: AccountId, name: String): Command[AccountId]

  def credit(accountId: AccountId,
             date: Date,
             amount: Amount,
             descriptionOption: Option[String],
             categoryOption: Option[Category]
            ): Command[TransactionId]

  def debit(accountId: AccountId,
            date: Date,
            amount: Amount,
            descriptionOption: Option[String],
            categoryOption: Option[Category]
           ): Command[TransactionId]

  def cancelTransaction(accountId: AccountId, transactionId: TransactionId): Command[TransactionId]

  def fetch(accountId: AccountId): Query[AccountProjection]

  def fetchAll: Query[List[AccountProjection]]

}

case class AccountProjection(accountId: String, name: String, balance: BigDecimal)

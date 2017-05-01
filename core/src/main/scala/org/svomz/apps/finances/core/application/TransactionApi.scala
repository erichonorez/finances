package org.svomz.apps.finances.core.application

import java.util.Date


import scala.concurrent.Future
import scalaz.Kleisli

trait TransactionApi[AccountId, TransactionId, Amount, Category, Env] extends CommonApi[Env] {

  /**
    * Lists all transaction of an account
    *
    * @param accountId
    * @return
    */
  def list(accountId: AccountId): Query[List[TransactionListItemProjection]]

  /**
    *
    * @param accountId
    * @param transactionId
    * @return
    */
  def fetch(accountId: AccountId, transactionId: TransactionId): Query[TransactionProjection]

}

case class TransactionListItemProjection(transactionId: String,
                                         date: Date,
                                         amount: BigDecimal,
                                         balance: BigDecimal,
                                         descriptionOption: Option[String],
                                         categoryOption: Option[String])

case class TransactionProjection(transactionId: String,
                                 date: Date,
                                 amount: BigDecimal,
                                 descriptionOption: Option[String],
                                 categoryOption: Option[String])

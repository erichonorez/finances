package org.svomz.apps.finances.core.domain.model

import scala.concurrent.Future

trait TransactionRepository {

  def nextIdentity: Future[TransactionId]

  def create(expense: Transaction): Future[Transaction]

  def update(transaction: Transaction): Future[Transaction]

  def delete(transaction: Transaction): Future[Transaction]

  def fetch(accountId: AccountId, transactionId: TransactionId): Future[Option[Transaction]]

  def fetchAll(accountId: AccountId): Future[List[Transaction]]

  def fetchAllWithCategory(accountId: AccountId, category: Category): Future[List[Transaction]]

  def fetchAllCategories(accountId: AccountId):  Future[List[Category]]

}

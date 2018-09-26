package org.svomz.apps.finances.core.domain.model

import java.util.Date

import scala.concurrent.Future

trait TransactionRepository {

  def nextIdentity: Future[TransactionId]

  def create(expense: Transaction): Future[Transaction]

  def update(transaction: Transaction): Future[Transaction]

  def delete(transaction: Transaction): Future[Transaction]

  def fetch(accountId: AccountId, transactionId: TransactionId): Future[Option[Transaction]]

  def fetchAll(accountId: AccountId): Future[List[Transaction]]

  def fetchAllDebits(id: AccountId, from: Some[Date], to: Some[Date]): Future[List[Transaction]]

  def fetchAllCredits(id: AccountId, from: Some[Date], to: Some[Date]): Future[List[Transaction]]

  def fetchAllWithCategory(accountId: AccountId, category: Category, from: Option[Date], to: Option[Date]): Future[List[Transaction]]

  def fetchAllCategories(accountId: AccountId):  Future[List[Category]]

  def fetchAllDebitWithCategory(id: AccountId, c: Category, from: Option[Date], to: Option[Date]): Future[List[Transaction]]


}

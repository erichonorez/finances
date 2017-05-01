package org.svomz.apps.finances.core.domain.model

import scala.concurrent.Future

trait AccountRepository {

  def update(account: Account): Future[Account]

  def delete(account: Account): Future[Account]

  def create(account: Account): Future[Account]

  def fetch(no: AccountId): Future[Option[Account]]

  def all: Future[List[Account]]

  def nextIdentity: Future[AccountId]

}

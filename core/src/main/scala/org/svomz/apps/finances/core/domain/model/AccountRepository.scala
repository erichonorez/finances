package org.svomz.apps.finances.core.domain.model

import scala.concurrent.Future

trait AccountRepository[AccountNumber] {
  def persist(account: Account): Future[Account]
  def fetch(no: String): Future[Option[Account]]
  def all: Future[Seq[Account]]
  def nextAccountNumber: AccountNumber
}

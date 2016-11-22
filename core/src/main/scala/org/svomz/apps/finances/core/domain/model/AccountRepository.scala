package org.svomz.apps.finances.core.domain.model

trait AccountRepository[AccountNumber] {
  def persist(account: Account): Account
  def fetch(no: String): Option[Account]
  def nextAccountNumber: AccountNumber
}

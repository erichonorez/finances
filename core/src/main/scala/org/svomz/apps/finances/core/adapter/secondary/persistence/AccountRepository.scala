package org.svomz.apps.finances.core.adapter.secondary.persistence

import java.util.UUID

import org.svomz.apps.finances.core.domain.model.{Account, AccountRepository}

import scala.collection.mutable

class InMemoryAccountRepository extends AccountRepository[String] {
  val accounts: mutable.HashMap[String, Account] = mutable.HashMap.empty

  override def persist(account: Account): Account = {
    accounts.put(account.no, account)
    account
  }

  override def fetch(no: String): Option[Account] = accounts.get(no)
  override def nextAccountNumber: String = UUID.randomUUID().toString
}

package org.svomz.apps.finances.core.adapter.secondary.persistence

import java.util.UUID

import org.svomz.apps.finances.core.domain.model.{Account, AccountRepository}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class InMemoryAccountRepository extends AccountRepository[String] {
  val accounts: mutable.HashMap[String, Account] = mutable.HashMap.empty

  override def persist(account: Account): Future[Account] = {
    Future {
      accounts.put(account.no, account)
      account
    }
  }

  override def fetch(no: String): Future[Option[Account]] = Future { accounts.get(no) }
  override def nextAccountNumber: String = UUID.randomUUID().toString

  override def all: Future[Seq[Account]] = Future { accounts.values.toSeq }

}

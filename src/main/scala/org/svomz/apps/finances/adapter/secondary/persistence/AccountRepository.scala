package org.svomz.apps.finances.adapter.secondary.persistence

import java.util.UUID

import org.svomz.apps.finances.domain.model.{Account, AccountRepository}

import scala.collection.mutable.MutableList

object AccountRepository extends AccountRepository[String] {
  var accounts: MutableList[Account] = MutableList.empty

  override def persist(account: Account): Account = {
    accounts :+ account
    account
  }

  override def fetch(no: String): Option[Account] = accounts.filter(_.no.equals(no)).headOption
  override def nextAccountNumber: String = UUID.randomUUID().toString
}

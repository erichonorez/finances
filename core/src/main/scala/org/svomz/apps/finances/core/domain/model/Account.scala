package org.svomz.apps.finances.core.domain.model

case class Account(id: AccountId, name: String)
case class AccountId(value: String)

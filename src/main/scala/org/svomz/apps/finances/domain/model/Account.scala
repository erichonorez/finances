package org.svomz.apps.finances.domain.model

case class Account(no: String, name: String, transactions: List[Transaction])

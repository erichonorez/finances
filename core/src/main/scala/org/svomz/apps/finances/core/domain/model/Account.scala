package org.svomz.apps.finances.core.domain.model

case class Account(no: String, name: String, transactions: List[Transaction])

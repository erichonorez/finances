package org.svomz.apps.finances.core.application.interpreter

import org.svomz.apps.finances.core.domain.model.{AccountRepository, TransactionRepository}

trait ApiEnv {

  def accountRepository: AccountRepository
  def transactionRepository: TransactionRepository

}

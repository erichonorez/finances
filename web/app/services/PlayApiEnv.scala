package services

import com.google.inject.Inject
import org.svomz.apps.finances.core.domain.model.{AccountRepository, TransactionRepository}
import org.svomz.apps.finances.core.application.interpreter.ApiEnv

case class PlayApiEnv @Inject()(accountRepository: AccountRepository, transactionRepository: TransactionRepository) extends ApiEnv

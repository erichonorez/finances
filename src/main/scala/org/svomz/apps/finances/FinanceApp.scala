package org.svomz.apps.finances

import java.util.{Date, UUID}

import org.svomz.apps.finances.adapter.secondary.persistence.AccountRepository
import org.svomz.apps.finances.domain.model.{Account, AccountRepository}
import org.svomz.apps.finances.domain.model.interpreter.AccountServiceInterpreter

object FinanceApp extends AccountServiceInterpreter {

  val no: String = AccountRepository.nextAccountNumber

  private val op = for {
    a <- open(no, "test")
    _ <- credit(no, new Date, 10, None)
    _ <- debit(no, new Date, 10, None)
    b <- balance(no)
  } yield b

  op(AccountRepository)


  private val test = for {
    a <- open(no, "test")
    _ <- credit(no, new Date, 10, None)
    _ <- debit(no, new Date, 10, None)
    t <- transactions(no)
  } yield t

  test(AccountRepository)

}


package org.svomz.apps.finances.core.application

import scala.concurrent.Future
import scalaz.Kleisli

trait CommonApi[Env] {

  type Command[A] = Kleisli[Future, Env, A]
  type Query[A] = Command[A]

}

case class TransactionNotFoundException(transactionId: String) extends Exception
case class AccountNotFoundException(val accountId: String) extends Exception
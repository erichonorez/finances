package org.svomz.apps.finances.domain.model.interpreter

import java.util.Date

import org.scalacheck._
import Arbitrary._
import org.scalacheck.Prop.{forAll, BooleanOperators}
import org.svomz.apps.finances.adapter.secondary.persistence.InMemoryAccountRepository
import org.svomz.apps.finances.domain.model.Account

object AccountServiceInterpreterUnitTest extends Properties("AccountServiceInterpreter") {

  lazy val genBigDecimal: Gen[BigDecimal] = for {
    unscaledVal <- arbitrary[Int]
  } yield BigDecimal(unscaledVal, 2)

  val accountService = new AccountServiceInterpreter

  property("Debiting the credit shoulld lead to a balance equals to zero") = forAll(genBigDecimal) { amount: BigDecimal =>
    val repository: InMemoryAccountRepository = new InMemoryAccountRepository

    repository.persist(
      Account(
        "xxx",
        "An account",
        List()
      )
    )

    val balanceOp = for {
      _ <- accountService.credit("xxx", new Date, amount, None)
      _ <- accountService.debit("xxx", new Date, amount, None)
      b <- accountService.balance("xxx")
    } yield b

    balanceOp(repository).fold(_ => false, v => v.compare(BigDecimal.valueOf(0)) == 0)
  }

  property("Crediting an account should lead to an additional transaction") = forAll(genBigDecimal) { amount: BigDecimal =>
    val repository: InMemoryAccountRepository = new InMemoryAccountRepository

    repository.persist(
      Account(
        "xxx",
        "An account",
        List()
      )
    )

    val op = for {
      _ <- accountService.credit("xxx", new Date, amount, None)
      t <- accountService.transactions("xxx")
    } yield t

    op(repository).fold(_ => false, list => { list.size == 1 })
  }

  property("Debiting an account should lead to an additional transaction") = forAll(genBigDecimal) { amount: BigDecimal =>
    val repository: InMemoryAccountRepository = new InMemoryAccountRepository

    repository.persist(
      Account(
        "xxx",
        "An account",
        List()
      )
    )

    val op = for {
      _ <- accountService.debit("xxx", new Date, amount, None)
      t <- accountService.transactions("xxx")
    } yield t

    op(repository).fold(_ => false, _.size == 1)
  }

  property("An account number must be provided in order to open an account") = forAll { name: String =>
    accountService.open("", name)(new InMemoryAccountRepository).fold(_.isInstanceOf[ValidationException], _ => false)
  }

  property("An account name must be provided in order to open an account") = forAll { no: String =>
    accountService.open(no, "")(new InMemoryAccountRepository).fold(_.isInstanceOf[ValidationException], _ => false)
  }

  property("A newly opened account should have an empty list of transactions") = forAll { (no: String, name: String) =>
    (!no.isEmpty && !name.isEmpty) ==> {
      val op = for {
        _ <- accountService.open(no, name)
        t <- accountService.transactions(no)
      } yield t

      op(new InMemoryAccountRepository).fold(_ => false, _.size == 0)
    }

  }

}

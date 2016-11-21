package org.svomz.apps.finances.domain.model.command

import java.util.{Date, UUID}

import org.scalatest.FlatSpec
import org.svomz.apps.finances.domain.model.common.AccountId

class AccountUnitTest extends FlatSpec {
  import Account._

  implicit def nextIdentityProducer(): AccountId = AccountId(UUID.randomUUID().toString)

  "An account" should "produce an event when created" in {
    receive(
      CreateANewAccount(
        "an account name",
        1
      )
    )
  }

  it should "accept expenses" in {
    // Given an account
    val anAccount: Account = apply(
      receive(
        CreateANewAccount(
          "an existing account",
          1
        )
      )
    )

    receive(
      AddExpense(
        1,
        "a description of expense",
        new Date()
      ),
      anAccount
    )

    // TODO at least assert something here
  }

  it should "accept incomes" in {
    // Given an account
    val anAccount: Account = apply(
      receive(
        CreateANewAccount(
          "an existing account",
          0
        )
      )
    )

    receive(
      AddIncome(
        1,
        "a description of expense",
        new Date()
      ),
      anAccount
    )

    // TODO at least assert something here
  }

}

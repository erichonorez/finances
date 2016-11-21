package org.svomz.apps.finances.domain.model.query

import java.util.Date

import org.scalatest.FlatSpec
import org.svomz.apps.finances.domain.model.common._

import scala.util.Try

class AccountListViewUnitTest extends FlatSpec {
  import AccountListView._

  "The account list view" should "be created when the first account is created" in {
    val list = apply(
      AccountCreated(
          AccountId("anId"),
          new Date(),
          "an account name",
          0
        ),
      Nil
      )

    assert(!list.isEmpty)
  }

  it should "have a new account list item for the newly created account" in {
    val anAccountId: AccountId = AccountId("anId")
    val aCreationDate: Date = new Date()
    val anAccountName: String = "an account name"
    val anInitialBalance: BigDecimal = 0

    val list = apply(
      AccountCreated(
        anAccountId,
        aCreationDate,
        anAccountName,
        anInitialBalance
      ),
      Nil
    )

    assert(1 === list.length)

    val accountListItem: AccountListItemView = list(0)
    assert(anAccountId.value === accountListItem.accountId)
    assert(anAccountName === accountListItem.accountName)
    assert(anInitialBalance === accountListItem.currentBalance)
  }

  it should "append new account list item for each newly created account" in {
    val anotherId: String = "anotherId"

    val accountListView: List[AccountListItemView] = apply(
      AccountCreated(
        AccountId(anotherId),
        new Date(),
        "an account name",
        0
      ),
      apply(
        AccountCreated(
          AccountId("anId"),
          new Date(),
          "an account name",
          0
        ),
        Nil
      )
    )
    assert(accountListView.length === 2)
    assert(anotherId === accountListView(1).accountId)
  }

  it should "display the updated balance when the account is credited" in {

    // Given an exsisting account list item
    val anInitialAccountListView = apply(
      AccountCreated(
        AccountId("anId"),
        new Date(),
        "an account name",
        0
      ),
      Nil
    )

    val aCreditedAmount: BigDecimal = 10

    val updatedList: Try[List[AccountListItemView]] = apply(
      AccountCredited(
        AccountId("anId"),
        new Date(),
        new Date(),
        CreditedAmount(aCreditedAmount),
        "A description"
      ),
      anInitialAccountListView
    )

    assert(updatedList.isSuccess)

    val accountListItem: AccountListItemView = updatedList.get(0)
    assert(aCreditedAmount === accountListItem.currentBalance)
  }

  it should "display the updated balance when the account is debited" in {
    // Given an existing account list item
    val anInitialAccountListView = apply(
      AccountCreated(
        AccountId("anId"),
        new Date(),
        "an account name",
        0
      ),
      Nil
    )

    val aDebitedAmount: BigDecimal = 10
    val applyOperation: Try[List[AccountListItemView]] = apply(
      AccountDebited(
        AccountId("anId"),
        new Date(),
        new Date(),
        DebitedAmount(aDebitedAmount),
        "A description"
      ),
      anInitialAccountListView
    )

    assert(applyOperation.isSuccess)

    val accountListItem: AccountListItemView = applyOperation.get(0)
    assert(-aDebitedAmount === accountListItem.currentBalance)
  }

  it should "not update the account list item view not account with same identifier is present" in {
    // Given an existing account list item
    val anInitialAccountListView = apply(
      AccountCreated(
        AccountId("anId"),
        new Date(),
        "an account name",
        0
      ),
      Nil
    )

    // When an event is received for an account which is not present in the list
    val applyOperation: Try[List[AccountListItemView]] = apply(
      AccountDebited(
        AccountId("anotherId"),
        new Date(),
        new Date(),
        DebitedAmount(10),
        "A description"
      ),
      anInitialAccountListView
    )

    // Then it should be a failure
    assert(applyOperation.isFailure)

  }

  it should "not create " in {
    // Given an existing account list item
    val anInitialAccountListView = apply(
      AccountCreated(
        AccountId("anId"),
        new Date(),
        "an account name",
        0
      ),
      Nil
    )

    // When an event is received for an account which is not present in the list
    val applyOperationResult: Try[List[AccountListItemView]] = apply(
      AccountDebited(
        AccountId("anotherId"),
        new Date(),
        new Date(),
        DebitedAmount(10),
        "A description"
      ),
      anInitialAccountListView
    )

    // Then it should be a failure
    assert(applyOperationResult.isFailure)

  }

  it should "not be created on other events than AccountCreated " in {
    val applyOperationResult: Try[List[AccountListItemView]] = apply(
      AccountDebited(
        AccountId("anotherId"),
        new Date(),
        new Date(),
        DebitedAmount(10),
        "A description"
      ),
      Nil
    )
    assert(applyOperationResult.isFailure)
  }

}

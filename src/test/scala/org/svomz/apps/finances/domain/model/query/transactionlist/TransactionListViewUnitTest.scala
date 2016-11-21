package org.svomz.apps.finances.domain.model.query.transactionlist

import java.util.Date

import org.scalatest.FlatSpec
import org.svomz.apps.finances.domain.model.common._

class TransactionListViewUnitTest extends FlatSpec  {
  import org.svomz.apps.finances.domain.model.query.transactionlist.TransactionListView._

  "The transaction list view" should "be empty when a new account is created" in {
    val view: List[TransactionListItemView] = apply(
      AccountCreated(
        AccountId("anId"),
        new Date(),
        "an account name",
        0
      )
    )
    assert(Nil === view)
  }

  it should "append a new transaction list item when the account is credited" in {
    val aCreditedValue: BigDecimal = 10
    val aTransactionDescription: String = "A description"
    val aTransactionDate: Date = new Date()

    val view: List[TransactionListItemView] = apply(
      AccountCredited(
        AccountId("anId"),
        new Date(),
        aTransactionDate,
        CreditedAmount(aCreditedValue),
        aTransactionDescription
      ),
      Nil
    )

    assert(1 === view.length)
    assert(aCreditedValue === view(0).currentBalance)
    assert(aTransactionDescription === view(0).description)
    assert(aTransactionDate === view(0).date)
  }

  it should "append a new transaction list item when the account is debited" in {
    val aDebitedValue: BigDecimal = 10
    val aTransactionDescription: String = "A description"
    val aTransactionDate: Date = new Date()

    val view: List[TransactionListItemView] = apply(
      AccountDebited(
        AccountId("anId"),
        new Date(),
        aTransactionDate,
        DebitedAmount(aDebitedValue),
        aTransactionDescription
      ),
      Nil
    )

    assert(1 === view.length)
    assert(- aDebitedValue === view(0).currentBalance)
    assert(aTransactionDescription === view(0).description)
    assert(aTransactionDate === view(0).date)
  }

  it should "modify the balance of existing transaction list item view when a transaction is created and its date is older" in {
    // Given an transaction list with a transaction on today
    val aDebitedValue: BigDecimal = 10
    val aTransactionDescription: String = "A description"
    val aTransactionDate: Date = new Date

    val view: List[TransactionListItemView] = apply(
      AccountDebited(
        AccountId("anId"),
        new Date(),
        aTransactionDate,
        DebitedAmount(aDebitedValue),
        aTransactionDescription
      ),
      Nil
    )

    // When I add a Transaction on yesterday
    val updatedView: List[TransactionListItemView] = apply(
      AccountCredited(
        AccountId("anId"),
        new Date(),
        new Date(aTransactionDate.getTime() - (1000 * 60 * 60 * 24)),
        CreditedAmount(aDebitedValue),
        aTransactionDescription
      ),
      view
    )

    // Then the current balance for the transaction list item of today should be 0
    assert(0 === updatedView(0).currentBalance)
  }

  it should "sort transaction list item by date in a descending order" in {
    val lastTransactionDescription: String = "a second transaction"

    val view: List[TransactionListItemView] = apply(
      AccountCredited(
        AccountId("anId"),
        new Date(),
        new Date(),
        CreditedAmount(10),
        lastTransactionDescription
      ),
      apply(
        AccountCredited(
          AccountId("anId"),
          new Date(),
          new Date(new Date().getTime() - (1000 * 60 * 60 * 24)),
          CreditedAmount(10),
          "a first transaction"
        ),
        Nil
      )
    )

    assert(lastTransactionDescription === view.head.description)
  }

}

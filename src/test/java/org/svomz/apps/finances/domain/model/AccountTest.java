package org.svomz.apps.finances.domain.model;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.svomz.apps.finances.domain.model.Builders.an;

public class AccountTest {

  @Test
  public void itShouldHaveAInitialBalanceOfZero() {
    Account account = new Account("An account name");
    assertThat(account.getBalance())
      .isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  public void itShouldUpdateBalanceWhenExpenseIsAdded() {
    Account account = new Account("An account name");
    account.add(Builders.an(Expense.of(5)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 2))
      .withDescription("A description")));

    assertThat(account.getBalance())
      .isEqualByComparingTo(BigDecimal.valueOf(-5));
  }

  @Test
  public void itShouldUpdateBalanceWhenIncomeIsAdded() {
    Account account = new Account("An account name");
    account.add(an(Income.of(5)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 2))
      .withDescription("An income"))
    );

    assertThat(account.getBalance())
      .isEqualByComparingTo(BigDecimal.valueOf(5));
  }

  @Test
  public void itShouldReturnTheListOfTransactions() {
    Account account = new Account("An account name");

    Income firstIncome = an(Income.of(10)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 2))
      .withDescription("A description"));
    account.add(firstIncome);

    Income secondIncome = an(Income.of(5)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 2))
      .withDescription("A description"));
    account.add(secondIncome);

    Expense firstExpense = Builders.an(Expense.of(8)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 2))
      .withDescription("A description"));
    account.add(firstExpense);

    assertThat(account.getTransactions())
      .containsExactly(firstIncome, secondIncome, firstExpense);
  }

  @Test
  public void itShouldSortTheTransactionsByDate() {
    Account account = new Account("An account name");

    Income lastIncome = an(Income.of(5)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 2))
      .withDescription("A description"));
    account.add(lastIncome);

    Income secondIncome = an(Income.of(5)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 1))
      .withDescription("A description"));
    account.add(secondIncome);

    Income firstIncome = an(Income.of(5)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 0))
      .withDescription("A description"));
    account.add(firstIncome);

    assertThat(account.getTransactions())
      .isSortedAccordingTo((o1, o2) -> {
        return o1.getDate().isEqual(o2.getDate()) ? 0
          : o1.getDate().isBefore(o2.getDate()) ? -1 : 1;
      });
  }

  @Test
  public void itShouldNotBePossibleToModifyTheTransactionListDirectly() {
    Account account = new Account("An account name");

    Income lastIncome = an(Income.of(5)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 2))
      .withDescription("A description"));
    account.add(lastIncome);

    Income secondIncome = an(Income.of(5)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 1))
      .withDescription("A description"));
    account.add(secondIncome);

    Income firstIncome = an(Income.of(5)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 0))
      .withDescription("A description"));
    account.add(firstIncome);

    Income addedDirectly = an(Income.of(5)
      .occuredOn(LocalDateTime.of(2015, 10, 21, 10, 0))
      .withDescription("A description"));
    account.getTransactions().add(firstIncome);

    assertThat(account.getTransactions())
      .containsExactly(firstIncome, secondIncome, lastIncome)
      .doesNotContain(addedDirectly);
  }

}

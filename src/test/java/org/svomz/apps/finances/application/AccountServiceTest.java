package org.svomz.apps.finances.application;

import org.junit.Test;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.ports.adapters.persistence.InMemoryAccountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by eric on 28/02/16.
 */
public class AccountServiceTest {

  @Test
  public void itShouldRetrieveTheListAccounts() {
    //Given there are until now the following accounts:
    List<String> accounts = Arrays.asList(
      "account one",
      "account two",
      "account three"
    );

    AccountServiceImpl accountService = new AccountServiceImpl(new InMemoryAccountRepository());
    accounts.forEach(account -> {
      accountService.createAccount(account);
    });

    //Then
    assertThat(accountService.getAllAccounts())
      .extracting(account -> account.getDescription())
      .containsOnlyElementsOf(accounts);
  }

  @Test
  public void itShouldAddIncomeToAnAccount() throws AccountNotFoundException {
    AccountServiceImpl accountService = new AccountServiceImpl(new InMemoryAccountRepository());
    Account account = accountService.createAccount("An account");

    accountService.execute(
      new AddIncomeCommand(account.getAccountId().getId(), 5, LocalDateTime.of(2016, 01, 01, 0, 1), "Income 1"));

    assertThat(accountService.getAccount(account.getAccountId().getId()).getTransactions())
      .extracting(transaction -> transaction.getDescription())
      .containsOnly("Income 1");

    assertThat(account.getBalance())
      .isEqualByComparingTo(BigDecimal.valueOf(5));
  }

  @Test
  public void itShouldAddExpenseToAnAccount() throws AccountNotFoundException {
    AccountServiceImpl accountService = new AccountServiceImpl(new InMemoryAccountRepository());
    Account account = accountService.createAccount("An account");

    accountService.execute(
      new AddExpenseCommand(account.getAccountId().getId(), 5, LocalDateTime.of(2016, 01, 01, 0, 1), "Expense 1"));

    assertThat(accountService.getAccount(account.getAccountId().getId()).getTransactions())
      .extracting(transaction -> transaction.getDescription())
      .containsOnly("Expense 1");

    assertThat(account.getBalance())
      .isEqualByComparingTo(BigDecimal.valueOf(-5));
  }

  @Test(expected = AccountNotFoundException.class)
  public void itShouldThrowExceptionIfAccountNotFound() throws AccountNotFoundException {
    AccountServiceImpl accountService = new AccountServiceImpl(new InMemoryAccountRepository());
    accountService.getAccount(UUID.randomUUID().toString());
  }

  @Test
  public void itShouldRetrieveThePaginatedListOfTransactions() throws AccountNotFoundException {
    //Given an account with a list of three transactions
    AccountServiceImpl accountService = new AccountServiceImpl(new InMemoryAccountRepository());
    Account account = accountService.createAccount("An account");

    accountService.execute(
      new AddIncomeCommand(account.getAccountId().getId(), 5, LocalDateTime.of(2016, 01, 01, 0, 1), "Income 1"));

    accountService.execute(
      new AddIncomeCommand(account.getAccountId().getId(), 5, LocalDateTime.of(2016, 01, 01, 0, 2), "Income 2"));

    accountService.execute(
      new AddIncomeCommand(account.getAccountId().getId(), 5, LocalDateTime.of(2016, 01, 01, 0, 3), "Income 3"));


    assertThat(accountService.getTransactions(account.getAccountId().getId(), 0, 1))
      .extracting(transaction -> transaction.getDescription())
      .containsExactly("Income 1");

    assertThat(accountService.getTransactions(account.getAccountId().getId(), 1, 1))
      .extracting(transaction -> transaction.getDescription())
      .containsExactly("Income 2");

    assertThat(accountService.getTransactions(account.getAccountId().getId(), 2, 1))
      .extracting(transaction -> transaction.getDescription())
      .containsExactly("Income 3");
  }

}

package org.svomz.apps.finances.application;

import org.junit.Test;
import org.svomz.apps.finances.domain.model.*;
import org.svomz.apps.finances.ports.adapters.persistence.InMemoryAccountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
      accountService.execute(new CreateAccountCommand(account));
    });

    //Then
    assertThat(accountService.getAllAccounts())
      .extracting(account -> account.getDescription())
      .containsOnlyElementsOf(accounts);
  }

  @Test
  public void itShouldAddIncomeToAnAccount() throws AccountNotFoundException {
    AccountServiceImpl accountService = new AccountServiceImpl(new InMemoryAccountRepository());
    Account account = accountService.execute(new CreateAccountCommand("An account"));

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
    Account account = accountService.execute(new CreateAccountCommand("An account"));

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
  public void itShouldRetrieveTheListOfTransactionsSortedByDate() throws AccountNotFoundException {
    // Given an account with 5f24bd66-4cb5-4a9a-a73e-f26083677497
    // And the following transactions
    // Type       Amount        Date            Description
    // Expense    10            01-01-2016      EXP_01
    // Income     5             02-01-2016      INC_01
    // Income     8             03-01-2016      INC_02
    String anAccountId = "5f24bd66-4cb5-4a9a-a73e-f26083677497";
    Account anAccount = new Account(
        new AccountId(anAccountId),
        "A current account"
    );

    anAccount.add(
        Expense.of(10)
            .occuredOn(LocalDateTime.of(2016, 1, 1, 0, 0))
            .withDescription("EXP_01")
            .build()
    );

    anAccount.add(
        Income.of(5)
            .occuredOn(LocalDateTime.of(2016, 1, 2, 0, 0))
            .withDescription("INC_01")
            .build()
    );

    anAccount.add(
        Income.of(8)
            .occuredOn(LocalDateTime.of(2016, 1, 3, 0, 0))
            .withDescription("INC_02")
            .build()
    );

    AccountRepository accountRepository = mock(AccountRepository.class);

    when(accountRepository.find(anAccountId))
        .thenReturn(Optional.of(anAccount));

    // When the user retrieves the list of transactions
    AccountService accountService = new AccountServiceImpl(accountRepository);
    List<Transaction> transactions = accountService.getTransactions(anAccountId, 0, 3);

    // Then the transactions should be sorted on the date with the newer first
    assertThat(transactions)
      .isSortedAccordingTo((t1, t2) -> {
        return t1.getDate().compareTo(t2.getDate()) * -1;
      });
  }

  @Test
  public void itShouldRetrieveThePaginatedListOfTransactions() throws AccountNotFoundException {
    //Given an account with a list of three transactions
    // Given an account with 5f24bd66-4cb5-4a9a-a73e-f26083677497
    // And the following transactions
    // Type       Amount        Date            Description
    // Expense    10            01-01-2016      EXP_01
    // Income     5             02-01-2016      INC_01
    // Income     8             03-01-2016      INC_02
    String anAccountId = "5f24bd66-4cb5-4a9a-a73e-f26083677497";
    Account anAccount = new Account(
        new AccountId(anAccountId),
        "A current account"
    );

    anAccount.add(
        Expense.of(10)
            .occuredOn(LocalDateTime.of(2016, 1, 1, 0, 0))
            .withDescription("EXP_01")
            .build()
    );

    Income aFirstIncome = Income.of(5)
        .occuredOn(LocalDateTime.of(2016, 1, 2, 0, 0))
        .withDescription("INC_01")
        .build();

    anAccount.add(
        aFirstIncome
    );

    Income aSecondIncome = Income.of(8)
        .occuredOn(LocalDateTime.of(2016, 1, 3, 0, 0))
        .withDescription("INC_02")
        .build();

    anAccount.add(
        aSecondIncome
    );

    AccountRepository accountRepository = mock(AccountRepository.class);

    when(accountRepository.find(anAccountId))
        .thenReturn(Optional.of(anAccount));

    // When the user retrieves the paginated list of transactions with a size of two
    AccountService accountService = new AccountServiceImpl(accountRepository);
    List<Transaction> transactions = accountService.getTransactions(anAccountId, 0, 2);

    // Then the user should only receive the two incomes
    assertThat(transactions)
        .containsExactly(
            aSecondIncome,
            aFirstIncome
        );
  }

}

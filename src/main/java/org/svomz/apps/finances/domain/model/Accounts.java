package org.svomz.apps.finances.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * Created by eric on 15/04/17.
 */
public class Accounts {

  /**
   * Functional style
   * @param account
   * @param amount
   * @param occuredOn
   * @param description
   * @return
   */
  public static Function<TransactionRepository, Income> addIncome(Account account, BigDecimal amount, LocalDateTime occuredOn, String description) {
    return (TransactionRepository transactionRepository) -> {
      Income income = new Income(
        transactionRepository.nextIdentity(),
        account.getAccountId(),
        amount,
        occuredOn,
        description
      );

      transactionRepository.create(income);

      return income;
    };

  }

  public static Function<TransactionRepository, Expense> addExpense(Account account, BigDecimal amount, LocalDateTime occuredOn, String description) {
    return (TransactionRepository transactionRepository) -> {
      Expense expense = new Expense(
        transactionRepository.nextIdentity(),
        account.getAccountId(),
        amount,
        occuredOn,
        description
      );

      transactionRepository.create(expense);

      return expense;
    };
  }

  public static Function<TransactionRepository, BigDecimal> balance(Account account) {
    return (TransactionRepository transactionRepository) -> {
      return transactionRepository.findAllByAccountId(account.getAccountId())
        .stream()
        .map((t) -> { return t.value(); })
        .reduce((v1, v2) -> { return v1.add(v2); })
        .orElse(BigDecimal.ZERO);
    };
  }
}

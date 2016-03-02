package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * Created by eric on 28/02/16.
 */
public class Account {

  private final List<Transaction> transactions;
  private String description;
  private AccountId id;

  public Account(@Nullable final AccountId accountId, final String description) {
    this.id = accountId;
    this.description = Preconditions.checkNotNull(description);
    this.transactions = new ArrayList<Transaction>();
  }

  public Account(final String description) {
    this(null, description);
  }

  public AccountId getAccountId() {
    return this.id;
  }

  public String getDescription() {
    return this.description;
  }

  public BigDecimal getBalance() {
    BigDecimal balance = BigDecimal.ZERO;
    for (Transaction transaction : this.getTransactions()) {
      balance = transaction.apply(balance);
    }
    return balance;
  }

  public void add(final Expense expense) {
    Preconditions.checkNotNull(expense);

    this.transactions.add(expense);
  }

  public void add(final Income income) {
    Preconditions.checkNotNull(income);

    this.transactions.add(income);
  }

  public List<Transaction> getTransactions() {
    return this.transactions.stream().sorted((o1, o2) -> {
      return o1.getDate().isEqual(o2.getDate()) ? 0
          : o1.getDate().isBefore(o2.getDate()) ? -1 : 1;
    }).collect(Collectors.<Transaction>toList());
  }

  public void setDescription(final String description) {
    Preconditions.checkNotNull(description);
    this.description = description;
  }
}
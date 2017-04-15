package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by eric on 28/02/16.
 */
public abstract class Transaction {

  private final AccountId accountId;

  private final BigDecimal amount;

  private final String description;

  private final LocalDateTime date;

  Transaction(TransactionId transactionId, AccountId accountId, final BigDecimal amount, final LocalDateTime date,
    final String description) {
    Preconditions.checkNotNull(transactionId);
    Preconditions.checkNotNull(accountId);
    Preconditions.checkNotNull(amount);

    this.accountId = accountId;
    this.amount = amount.abs();
    this.date = date;
    this.description = description;
  }

  public abstract BigDecimal value();

  public LocalDateTime getDate() {
    return date;
  }

  public String getDescription() {
    return description;
  }

  public abstract BigDecimal apply(final BigDecimal balance);

  public AccountId getAccountId() {
    return accountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }
}

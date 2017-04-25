package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import com.mongodb.BulkWriteUpsert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eric on 28/02/16.
 */
public abstract class Transaction {

  private final AccountId accountId;
  private Set<Tag> tags;

  private BigDecimal amount;

  private String description;

  private LocalDateTime date;

  private final TransactionId transactionId;

  Transaction(TransactionId transactionId, AccountId accountId, final BigDecimal amount, final LocalDateTime date,
    final String description, final Set<Tag> tags) {
    Preconditions.checkNotNull(transactionId);
    Preconditions.checkNotNull(accountId);
    Preconditions.checkNotNull(amount);
    Preconditions.checkNotNull(tags);

    this.accountId = accountId;
    this.amount = amount.abs();
    this.date = date;
    this.description = description;
    this.transactionId = transactionId;
    this.tags = new HashSet<>(tags);
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

  public TransactionId getTransactionId() {
    return this.transactionId;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public abstract boolean isIncome();

  public abstract boolean isExpense();

  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = new HashSet<>(tags);
  }
}

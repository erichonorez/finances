package org.svomz.apps.finances.application.query.listtransactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by eric on 25/04/17.
 */
public class TransactionView {

  private final String accountId;

  private final List<String> tags;

  private final BigDecimal value;

  private final String description;

  private final LocalDateTime date;

  private final String transactionId;

  private final BigDecimal balance;

  public TransactionView(String accountId, List<String> tags, BigDecimal value, String description,
    LocalDateTime date, String transactionId, BigDecimal balance) {
    this.accountId = accountId;
    this.tags = tags;
    this.value = value;
    this.description = description;
    this.date = date;
    this.transactionId = transactionId;
    this.balance = balance;
  }

  public String getAccountId() {
    return accountId;
  }

  public List<String> getTags() {
    return tags;
  }

  public BigDecimal getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getTransactionId() {
    return transactionId;
  }

    public BigDecimal getBalance() {
        return balance;
    }
}

package org.svomz.apps.finances.application.query.accountsummary;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;

/**
 * Created by eric on 15/04/17.
 */
public class AccountSummaryView {

  private final String accountId;
  private final String description;
  private final BigDecimal balance;

  public AccountSummaryView(String accountId, String description, BigDecimal balance) {
    this.accountId = Preconditions.checkNotNull(accountId);
    this.description = Preconditions.checkNotNull(description);
    this.balance = Preconditions.checkNotNull(balance);
  }

  public String getAccountId() {
    return accountId;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getBalance() {
    return balance;
  }
}

package org.svomz.apps.finances.application;

import com.google.common.base.Preconditions;

import org.svomz.apps.finances.domain.model.AccountId;

import java.math.BigDecimal;

/**
 * Created by eric on 15/04/17.
 */
public class AccountSummary {

  private final String accountId;
  private final String description;
  private final BigDecimal balance;

  public AccountSummary(String accountId, String description, BigDecimal balance) {
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

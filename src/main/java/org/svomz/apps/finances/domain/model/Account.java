package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Account {

  private AccountId id;

  private String description;

  private List<Transaction> transactions;

  public Account(final AccountId accountId, final String description) {
    this.id = Preconditions.checkNotNull(accountId);
    this.description = Preconditions.checkNotNull(description);
    this.transactions = new ArrayList<>();
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

  public void setDescription(final String description) {
    Preconditions.checkNotNull(description);
    this.description = description;
  }

}

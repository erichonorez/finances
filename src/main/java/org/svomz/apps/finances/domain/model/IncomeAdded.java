package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.time.LocalDateTime;

/**
 * Created by eric on 15/03/16.
 */
public class IncomeAdded extends DomainEvent {

  private final AccountId accountId;
  private final String description;
  private final LocalDateTime date;

  public IncomeAdded(final AccountId accountId, final String description, final LocalDateTime date) {
    this.accountId = Preconditions.checkNotNull(accountId);
    this.description = Preconditions.checkNotNull(description);
    this.date = Preconditions.checkNotNull(date);
  }

  public AccountId getAccountId() {
    return accountId;
  }

  public String getDescription() {
    return description;
  }

  public LocalDateTime getDate() {
    return date;
  }
}

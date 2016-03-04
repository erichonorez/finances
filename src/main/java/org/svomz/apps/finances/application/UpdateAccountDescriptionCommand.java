package org.svomz.apps.finances.application;

import com.google.common.base.Preconditions;

/**
 * Created by eric on 04/03/16.
 */
public class UpdateAccountDescriptionCommand {

  private final String accountId;
  private final String description;

  public UpdateAccountDescriptionCommand(final String accountId, final String description) {
    this.accountId = Preconditions.checkNotNull(accountId);
    this.description = Preconditions.checkNotNull(description);
  }

  public String getDescription() {
    return this.description;
  }

  public String getAccountId() {
    return accountId;
  }
}

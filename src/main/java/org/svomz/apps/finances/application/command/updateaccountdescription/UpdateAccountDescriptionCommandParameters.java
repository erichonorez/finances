package org.svomz.apps.finances.application.command.updateaccountdescription;

import com.google.common.base.Preconditions;

/**
 * Created by eric on 04/03/16.
 */
public class UpdateAccountDescriptionCommandParameters {

  private final String accountId;
  private final String description;

  public UpdateAccountDescriptionCommandParameters(final String accountId, final String description) {
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

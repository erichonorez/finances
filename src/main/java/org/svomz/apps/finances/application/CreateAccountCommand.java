package org.svomz.apps.finances.application;

import com.google.common.base.Preconditions;

/**
 * Created by eric on 04/03/16.
 */
public class CreateAccountCommand {

  private final String accountDescription;

  public CreateAccountCommand(final String accountDescription) {
    this.accountDescription = Preconditions.checkNotNull(accountDescription);
  }

  public String getAccountDescription() {
    return this.accountDescription;
  }
}

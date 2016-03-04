package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

/**
 * Created by eric on 28/02/16.
 */
public class AccountId {

  private final String id;

  public AccountId(final String accountId) {
    this.id = Preconditions.checkNotNull(accountId);
  }

  public String getId() {
    return this.id;
  }
  
}

package org.svomz.apps.finances.domain;

/**
 * Created by eric on 28/02/16.
 */
public class AccountId {

  private final String id;

  public AccountId(final String accountId) {
    this.id = accountId;
  }

  public String getId() {
    return this.id;
  }
  
}

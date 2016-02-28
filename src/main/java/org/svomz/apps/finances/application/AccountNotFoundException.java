package org.svomz.apps.finances.application;

/**
 * Created by eric on 28/02/16.
 */
public class AccountNotFoundException extends Exception {

  private final String accountId;

  public AccountNotFoundException(String id) {
    this.accountId = id;
  }

}

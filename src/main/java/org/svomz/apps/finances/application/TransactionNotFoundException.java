package org.svomz.apps.finances.application;

/**
 * Created by eric on 22/04/17.
 */
public class TransactionNotFoundException extends Exception {

  private final String transactionId;

  public TransactionNotFoundException(String transactionId) {
    this.transactionId  = transactionId;
  }
}

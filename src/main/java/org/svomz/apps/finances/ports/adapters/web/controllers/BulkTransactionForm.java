package org.svomz.apps.finances.ports.adapters.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

public class BulkTransactionForm {

  @Valid
  private List<TransactionForm> transactions;

  public BulkTransactionForm() {
    this.transactions = new ArrayList<>();
  }

  public List<TransactionForm> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<TransactionForm> transactions) {
    this.transactions = transactions;
  }
}

package org.svomz.apps.finances.ports.adapters.web.controllers;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by eric on 28/02/16.
 */
public class TransactionForm {

  @NotNull
  @Size(min = 1, max = 255)
  private String description;

  @NotNull
  private Date date;

  @NotNull
  private TransactionType type;

  @NotNull
  private Double amount;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

}

package org.svomz.apps.finances.ports.adapters.web.controllers;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
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
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date date;

  @NotNull
  private TransactionType type;

  @NotNull
  @Digits(integer = 10, fraction = 2)
  @Min(0)
  private Double amount;

  private String tags;

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

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }
}

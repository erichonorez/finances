package org.svomz.apps.finances.application.command.addincome;

import com.google.common.base.Preconditions;

import java.time.LocalDateTime;

public class AddIncomeCommandParameters {

  private final String id;
  private final double value;
  private final LocalDateTime dateTime;
  private final String description;

  public AddIncomeCommandParameters(String id, double value, LocalDateTime dateTime, String description) {
    this.id = Preconditions.checkNotNull(id);
    this.value = value;
    this.dateTime = Preconditions.checkNotNull(dateTime);
    this.description = Preconditions.checkNotNull(description);
  }

  public String getAccountId() {
    return id;
  }

  public double getValue() {
    return value;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public String getDescription() {
    return description;
  }
}
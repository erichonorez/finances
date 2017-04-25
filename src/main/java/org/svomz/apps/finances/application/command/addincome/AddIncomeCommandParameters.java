package org.svomz.apps.finances.application.command.addincome;

import com.google.common.base.Preconditions;

import java.time.LocalDateTime;
import java.util.List;

public class AddIncomeCommandParameters {

  private final String id;
  private final double value;
  private final LocalDateTime dateTime;
  private final String description;
  private final List<String> tags;

  public AddIncomeCommandParameters(String id, double value, LocalDateTime dateTime, String description, List<String> tags) {
    this.id = Preconditions.checkNotNull(id);
    this.value = value;
    this.dateTime = Preconditions.checkNotNull(dateTime);
    this.description = Preconditions.checkNotNull(description);
    this.tags = Preconditions.checkNotNull(tags);
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

  public List<String> getTags() { return tags; }
}

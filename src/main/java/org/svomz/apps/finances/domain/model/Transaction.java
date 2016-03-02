package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by eric on 28/02/16.
 */
public abstract class Transaction {

  private final BigDecimal value;
  private final String description;
  private final LocalDateTime date;

  Transaction(final BigDecimal value, final LocalDateTime date, final String description) {
    Preconditions.checkNotNull(value);

    this.value = value;
    this.date = date;
    this.description = description;
  }

  public BigDecimal value() {
    return this.value;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getDescription() {
    return description;
  }

  public abstract BigDecimal apply(final BigDecimal balance);

  static abstract class TransactionBuilder<BUILDER, RETURN> {

    LocalDateTime date;
    String description;
    BigDecimal value;

    public BUILDER of(final BigDecimal value) {
      Preconditions.checkNotNull(value);
      this.value = value;
      return (BUILDER) this;
    }

    public BUILDER occuredOn(final LocalDateTime date) {
      Preconditions.checkNotNull(date);
      this.date = date;
      return (BUILDER) this;
    }

    public BUILDER withDescription(final String description) {
      Preconditions.checkNotNull(description);
      this.description = description;
      return (BUILDER) this;
    }

    public RETURN build() {
      Preconditions.checkState(this.value != null);
      Preconditions.checkState(this.date != null);
      Preconditions.checkState(this.description != null);

      return this.newTransaction();
    }

    protected abstract RETURN newTransaction();
  }
}

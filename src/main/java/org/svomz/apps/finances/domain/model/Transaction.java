package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;

/**
 * Created by eric on 28/02/16.
 */
@Entity
@Inheritance
@DiscriminatorColumn(name="transaction_type")
@Table(name = "transactions")
public abstract class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;

  @Column(name = "value")
  private BigDecimal value;

  @Column(name = "description")
  private String description;

  @Column(name = "occured_one")
  private LocalDateTime date;

  @Column(name="account_id")
  private String accountId;

  protected Transaction() {}

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

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

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

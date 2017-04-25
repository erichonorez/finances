package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by eric on 28/02/16.
 */
public final class Expense extends Transaction {

  public Expense(TransactionId transactionId, AccountId accountId, final BigDecimal value, final LocalDateTime date,
    final String description, final Set<Tag> tags) {
    super(transactionId, accountId, value, date, description, tags);
  }

  @Override
  public BigDecimal apply(final BigDecimal balance) {
    Preconditions.checkNotNull(balance);

    return balance.subtract(this.value());
  }

  @Override
  public boolean isIncome() {
    return false;
  }

  @Override
  public boolean isExpense() {
    return true;
  }

  public BigDecimal value() {
    return this.getAmount().negate();
  }

}

package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by eric on 28/02/16.
 */
public final class Expense extends Transaction {

  Expense(TransactionId transactionId, AccountId accountId, final BigDecimal value, final LocalDateTime date,
    final String description) {
    super(transactionId, accountId, value, date, description);
  }

  @Override
  public BigDecimal apply(final BigDecimal balance) {
    Preconditions.checkNotNull(balance);

    return balance.subtract(this.value());
  }

  public BigDecimal value() {
    return this.getAmount().negate();
  }

}

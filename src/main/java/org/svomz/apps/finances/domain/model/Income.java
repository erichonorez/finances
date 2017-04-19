package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by eric on 28/02/16.
 */
public final class Income extends Transaction {

  public Income(TransactionId transactionId, AccountId accountId, final BigDecimal value,
    final LocalDateTime date,
    final String description) {
    super(transactionId, accountId, value, date, description);
  }

  @Override
  public BigDecimal apply(BigDecimal balance) {
    Preconditions.checkNotNull(balance);

    return balance.add(this.value());
  }

  public BigDecimal value() {
    return this.getAmount();
  }

}

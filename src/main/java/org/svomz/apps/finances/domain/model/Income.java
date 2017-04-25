package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by eric on 28/02/16.
 */
public final class Income extends Transaction {

  public Income(TransactionId transactionId, AccountId accountId, final BigDecimal value,
    final LocalDateTime date, final String description, final Set<Tag> tags) {
    super(transactionId, accountId, value, date, description, tags);
  }

  @Override
  public BigDecimal apply(BigDecimal balance) {
    Preconditions.checkNotNull(balance);

    return balance.add(this.value());
  }

  @Override
  public boolean isIncome() {
    return true;
  }

  @Override
  public boolean isExpense() {
    return false;
  }

  public BigDecimal value() {
    return this.getAmount();
  }

}

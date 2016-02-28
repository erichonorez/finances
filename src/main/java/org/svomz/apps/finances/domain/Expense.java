package org.svomz.apps.finances.domain;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;

/**
 * Created by eric on 28/02/16.
 */
public final class Expense extends Transaction {

  private Expense(final TransactionBuilder<ExpenseBuilder, Expense> expenseBuilder) {
    super(expenseBuilder.value, expenseBuilder.date, expenseBuilder.description);
  }

  @Override
  public BigDecimal apply(final BigDecimal balance) {
    Preconditions.checkNotNull(balance);

    return balance.subtract(this.value());
  }

  public static ExpenseBuilder of(final double value) {
    return new ExpenseBuilder()
      .of(BigDecimal.valueOf(value));
  }

  public static class ExpenseBuilder extends TransactionBuilder<ExpenseBuilder, Expense> {

    @Override
    protected Expense newTransaction() {
      return new Expense(this);
    }
  }
}

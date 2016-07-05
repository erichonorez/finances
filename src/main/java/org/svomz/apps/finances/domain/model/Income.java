package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;

/**
 * Created by eric on 28/02/16.
 */
public final class Income extends Transaction {

  private Income(final TransactionBuilder<IncomeBuilder, Income> incomeBuilder) {
    super(incomeBuilder.value, incomeBuilder.date, incomeBuilder.description);
  }

  @Override
  public BigDecimal apply(BigDecimal balance) {
    Preconditions.checkNotNull(balance);

    return balance.add(this.value());
  }

  public static IncomeBuilder of(final double value) {
    return new IncomeBuilder()
      .of(BigDecimal.valueOf(value));
  }

  public static class IncomeBuilder extends TransactionBuilder<IncomeBuilder, Income> {

    @Override
    protected Income newTransaction() {
      return new Income(this);
    }
  }

}

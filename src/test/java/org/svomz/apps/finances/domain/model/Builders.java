package org.svomz.apps.finances.domain.model;

/**
 * Created by eric on 28/02/16.
 */
public class Builders {

  public static Expense an(final Expense.ExpenseBuilder builder) {
    return builder.build();
  }

  public static Income an(final Income.IncomeBuilder builder) {
    return builder.build();
  }
}

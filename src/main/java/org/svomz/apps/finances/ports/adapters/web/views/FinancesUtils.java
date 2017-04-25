package org.svomz.apps.finances.ports.adapters.web.views;

import com.google.common.base.Preconditions;

import org.svomz.apps.finances.domain.model.Expense;
import org.svomz.apps.finances.domain.model.Transaction;

import java.math.BigDecimal;

/**
 * Created by eric on 01/03/16.
 */
public class FinancesUtils {

  public String cssClassesOf(final BigDecimal amount) {
    Preconditions.checkNotNull(amount);

    if (BigDecimal.ZERO.compareTo(amount) <= 0) {
      return "positive-amount";
    }
    return "negative-amount";
  }

}

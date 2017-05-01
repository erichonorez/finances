package org.svomz.apps.finances.core.domain.model

object TransactionService {

  def balanceAfter(t: Transaction, ts: List[Transaction]): BigDecimal = {
    ts.dropWhile(el => !el.equals(t))
      .foldLeft(BigDecimal(0))((z, t) => t.apply(z))
  }

}

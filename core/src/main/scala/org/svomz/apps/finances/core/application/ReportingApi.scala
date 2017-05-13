package org.svomz.apps.finances.core.application

import java.util.Date

import org.svomz.apps.finances.core.domain.model.Category

trait ReportingApi[AccountId, Category, Amount, TimePeriod, Env] extends CommonApi[Env] {

  /**
    * Computes the balance of all transaction categories for an account.
    *
    * @param accountId
    * @return a list of balance for each category
    */
  def balanceByCategory(accountId: AccountId, period: TimePeriod): Query[List[(Category, Amount)]]

  /**
    * Computes the total of expense for each categories over a period of time.
    *
    * @param accountId
    * @return
    */
  def totalExpensesByCategory(accountId: AccountId, period: TimePeriod): Query[List[(Category, Amount, Double)]]

}

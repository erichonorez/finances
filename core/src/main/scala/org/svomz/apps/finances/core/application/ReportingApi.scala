package org.svomz.apps.finances.core.application

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

  /**
    * Computes the total of debit over a period of time.
    *
    * @param accountId
    * @param period
    * @return
    */
  def totalDebit(accountId: AccountId, period: TimePeriod): Query[Amount]

  /**
    * Computes the total of debit over a period of time.
    *
    * @param accountId
    * @param period
    * @return
    */
  def totalCredit(accountId: AccountId, period: TimePeriod): Query[Amount]

}

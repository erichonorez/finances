package org.svomz.apps.finances.core.application

trait ReportingApi[AccountId, Category, Amount, Env] extends CommonApi[Env] {

  /**
    * Computes the balance of all transaction categories for an account.
    *
    * @param accountId
    * @return a list of balance for each category
    */
  def balanceByCategoy(accountId: AccountId): Query[List[(Category, Amount)]]

}

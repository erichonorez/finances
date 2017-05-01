package org.svomz.apps.finances.core.application

trait ReportingApi[AccountId, Amount, Env] extends CommonApi[Env] {

  def balanceByCategoy(accountId: AccountId): Query[List[(AccountId, Amount)]]

}

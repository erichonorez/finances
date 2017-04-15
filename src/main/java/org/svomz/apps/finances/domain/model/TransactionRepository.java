package org.svomz.apps.finances.domain.model;

import org.svomz.apps.finances.domain.model.Expense;
import org.svomz.apps.finances.domain.model.Income;
import org.svomz.apps.finances.domain.model.Transaction;

import java.util.List;

/**
 * Created by eric on 15/04/17.
 */
public interface TransactionRepository {

  void create(Transaction expense);

  TransactionId nextIdentity();

  List<Transaction> findByAccountId(AccountId accountId, int page, int size);

  List<Transaction> findAllByAccountId(AccountId accountId);
}

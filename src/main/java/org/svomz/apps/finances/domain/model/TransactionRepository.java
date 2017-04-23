package org.svomz.apps.finances.domain.model;

import java.util.List;
import java.util.Optional;

/**
 * Created by eric on 15/04/17.
 */
public interface TransactionRepository {

  void create(Transaction expense);

  TransactionId nextIdentity();

  List<Transaction> findByAccountId(AccountId accountId, int page, int size);

  List<Transaction> findAllByAccountId(AccountId accountId);

  Optional<Transaction> findById(AccountId accountId, TransactionId transactionId);

  void update(Transaction transaction);

  void delete(Transaction transaction);
}

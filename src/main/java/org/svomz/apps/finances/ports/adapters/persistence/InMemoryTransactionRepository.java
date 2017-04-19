package org.svomz.apps.finances.ports.adapters.persistence;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.Transaction;
import org.svomz.apps.finances.domain.model.TransactionId;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryTransactionRepository implements TransactionRepository {

  private Map<AccountId, Set<Transaction>> transactionMap = new HashMap<>();

  @Override
  public void create(Transaction transaction) {
    AccountId accountId = transaction.getAccountId();
    if (!this.transactionMap.containsKey(accountId)) {
      this.transactionMap.put(accountId, new HashSet<>());
    }
    this.transactionMap.get(accountId).add(transaction);
  }

  @Override
  public TransactionId nextIdentity() {
    return new TransactionId(UUID.randomUUID().toString());
  }

  @Override
  public List<Transaction> findByAccountId(AccountId accountId, int page, int size) {
    if (!this.transactionMap.containsKey(accountId)) {
      return new ArrayList<>();
    }

    return this.transactionMap.get(accountId).stream()
      .sorted((t1, t2) -> t1.getDate().isBefore(t2.getDate()) ? 1 : -1 )
      .skip((page - 1) * size)
      .limit(size)
      .collect(Collectors.toList());
  }

  @Override
  public List<Transaction> findAllByAccountId(AccountId accountId) {
    if (!this.transactionMap.containsKey(accountId)) {
      return new ArrayList<>();
    }

    return this.transactionMap.get(accountId).stream().collect(Collectors.toList());
  }
}

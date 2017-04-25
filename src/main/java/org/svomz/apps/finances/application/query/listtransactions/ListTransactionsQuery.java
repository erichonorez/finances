package org.svomz.apps.finances.application.query.listtransactions;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountRepository;
import org.svomz.apps.finances.domain.model.Transaction;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Created by eric on 15/04/17.
 */
@Component
public class ListTransactionsQuery {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  @Inject
  public ListTransactionsQuery(AccountRepository accountRepository, TransactionRepository transactionRepository) {
    this.accountRepository = Preconditions.checkNotNull(accountRepository);
    this.transactionRepository = Preconditions.checkNotNull(transactionRepository);
  }

  public List<TransactionView> execute(String accountId, int page, int size) throws AccountNotFoundException {
    Preconditions.checkNotNull(accountId);

    Account account = this.accountRepository.find(accountId)
      .orElseThrow(() -> new AccountNotFoundException(accountId));

    return this.transactionRepository.findByAccountId(account.getAccountId(), page, size)
      .stream()
      .map(t -> {
        return new TransactionView(t.getAccountId().getId(),
          t.getTags().stream().map(v -> v.getName()).collect(Collectors.toList()),
          t.value(),
          t.getDescription(),
          t.getDate(),
          t.getTransactionId().getId());
      }).collect(Collectors.toList());
  }

}

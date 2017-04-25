package org.svomz.apps.finances.application.command;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.application.TransactionNotFoundException;
import org.svomz.apps.finances.application.query.listtransactions.TransactionView;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.AccountRepository;
import org.svomz.apps.finances.domain.model.Tag;
import org.svomz.apps.finances.domain.model.Transaction;
import org.svomz.apps.finances.domain.model.TransactionId;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Created by eric on 25/04/17.
 */
@Component
public class ListTransactionByTagQuery {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  @Inject
  public ListTransactionByTagQuery(final AccountRepository accountRepository, final
    TransactionRepository transactionRepository) {
    this.accountRepository = Preconditions.checkNotNull(accountRepository);
    this.transactionRepository = Preconditions.checkNotNull(transactionRepository);
  }

  public List<TransactionView> execute(String accountId, String tag) throws AccountNotFoundException {

    Account account =
      this.accountRepository.find(accountId)
        .orElseThrow(() -> new AccountNotFoundException(accountId));

    return this.transactionRepository.findAllByTag(account.getAccountId(), new Tag(tag))
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

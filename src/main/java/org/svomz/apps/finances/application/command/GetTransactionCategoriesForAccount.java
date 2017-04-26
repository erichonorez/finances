package org.svomz.apps.finances.application.command;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountRepository;
import org.svomz.apps.finances.domain.model.Accounts;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import javax.inject.Inject;

import javaslang.Tuple;
import javaslang.Tuple2;

/**
 * Created by eric on 25/04/17.
 */
@Component
public class GetTransactionCategoriesForAccount {

  private TransactionRepository transactionRepository;

  private AccountRepository accountRepository;

  @Inject
  public GetTransactionCategoriesForAccount(TransactionRepository transactionRepository,
    AccountRepository accountRepository) {
    this.transactionRepository = Preconditions.checkNotNull(transactionRepository);
    this.accountRepository = Preconditions.checkNotNull(accountRepository);
  }

  public java.util.List<Tuple2<String, BigDecimal>> execute(String accountId) throws AccountNotFoundException {

    Account account  = this.accountRepository.find(accountId)
      .orElseThrow(() -> new AccountNotFoundException(accountId));

    return this.transactionRepository.findAllTags(account.getAccountId())
      .stream()
      .map(t -> Tuple.of(t, this.transactionRepository.findAllByTag(account.getAccountId(), t)))
      .map(t -> Tuple.of(t._1.getName(), Accounts.balance(t._2)))
      .collect(Collectors.toList());

  }

}

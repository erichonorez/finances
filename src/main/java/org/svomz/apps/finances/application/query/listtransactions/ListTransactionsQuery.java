package org.svomz.apps.finances.application.query.listtransactions;

import com.google.common.base.Preconditions;

import com.google.common.collect.Lists;
import javaslang.Tuple;
import javaslang.Tuple2;
import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountRepository;
import org.svomz.apps.finances.domain.model.Transaction;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

    List<Transaction> transactions = this.transactionRepository.findByAccountId(account.getAccountId(), page, size);

    List<BigDecimal> balances = balance(transactions);

    List<Tuple2<Transaction, BigDecimal>> xs = new ArrayList<>();
    for(int i = 0; i < transactions.size(); i++){
      xs.add(Tuple.of(
        transactions.get(i),
        balances.get(i)
      ));
    }
    return xs
      .stream()
      .map(t -> {
        return new TransactionView(t._1.getAccountId().getId(),
          t._1.getTags().stream().map(v -> v.getName()).collect(Collectors.toList()),
          t._1.value(),
          t._1.getDescription(),
          t._1.getDate(),
          t._1.getTransactionId().getId(),
          t._2);
      }).collect(Collectors.toList());
  }

  public static List<BigDecimal> balance(List<Transaction> transactions) {
    return Lists.reverse(loop(transactions, new ArrayList<>()));
  }

  private static List<BigDecimal> loop(List<Transaction> transactions, List<BigDecimal> as) {
    if (transactions.isEmpty()) {
      return as;
    }

    BigDecimal lastValue = as.size() > 0 ? as.get(as.size() - 1) : BigDecimal.ZERO;
    as.add(lastValue.add(transactions.get(transactions.size() - 1).value()));

    return loop(
      transactions.subList(0, transactions.size() - 1),
      as
    );
  }

}

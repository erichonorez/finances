package org.svomz.apps.finances.application.query;

import com.google.common.base.Preconditions;

import javaslang.Tuple;
import javaslang.Tuple2;
import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.application.query.listtransactions.ListTransactionsQuery;
import org.svomz.apps.finances.application.query.listtransactions.TransactionView;
import org.svomz.apps.finances.domain.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
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


    List<Transaction> transactions = this.transactionRepository.findAllByTag(account.getAccountId(), new Tag(tag));
    List<BigDecimal> balances = ListTransactionsQuery.balance(transactions);

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

}

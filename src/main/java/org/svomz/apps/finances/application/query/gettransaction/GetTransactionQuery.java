package org.svomz.apps.finances.application.query.gettransaction;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.TransactionNotFoundException;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.Transaction;
import org.svomz.apps.finances.domain.model.TransactionId;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import javax.inject.Inject;

/**
 * Created by eric on 22/04/17.
 */
@Component
public class GetTransactionQuery {

  private TransactionRepository transactionRepository;

  @Inject
  public GetTransactionQuery(TransactionRepository transactionRepository) {
    this.transactionRepository = Preconditions.checkNotNull(transactionRepository);
  }

  public Transaction execute(String accountId, String transactionId) throws TransactionNotFoundException {
    Preconditions.checkNotNull(transactionId);

    return this.transactionRepository.findById(new AccountId(accountId), new TransactionId(transactionId))
      .orElseThrow(() -> new TransactionNotFoundException(transactionId));
  }

}

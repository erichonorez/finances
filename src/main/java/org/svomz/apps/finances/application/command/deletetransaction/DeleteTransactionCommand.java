package org.svomz.apps.finances.application.command.deletetransaction;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.TransactionNotFoundException;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.Transaction;
import org.svomz.apps.finances.domain.model.TransactionId;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.util.Optional;

import javax.inject.Inject;

/**
 * Created by eric on 22/04/17.
 */
@Component
public class DeleteTransactionCommand {

  private TransactionRepository transactionRepository;

  @Inject
  public DeleteTransactionCommand(TransactionRepository transactionRepository) {
    this.transactionRepository = Preconditions.checkNotNull(transactionRepository);
  }

  public void execute(String accountId, String transactionId) throws TransactionNotFoundException {
    Transaction transaction = this.transactionRepository.findById(
        new AccountId(accountId),new TransactionId(transactionId))
          .orElseThrow(() -> new TransactionNotFoundException(transactionId));

    this.transactionRepository.delete(transaction);
  }

}

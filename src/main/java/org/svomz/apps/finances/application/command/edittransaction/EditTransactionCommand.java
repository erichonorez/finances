package org.svomz.apps.finances.application.command.edittransaction;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.TransactionNotFoundException;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.Tag;
import org.svomz.apps.finances.domain.model.Transaction;
import org.svomz.apps.finances.domain.model.TransactionId;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Created by eric on 22/04/17.
 */
@Component
public class EditTransactionCommand {

  private final TransactionRepository transactionRepository;

  @Inject
  public EditTransactionCommand(TransactionRepository transactionRepository) {
    this.transactionRepository = Preconditions.checkNotNull(transactionRepository);
  }

  public void execute(String accountId, String transactionId, BigDecimal amount, LocalDateTime date, String description, List<String> tags)
    throws TransactionNotFoundException {

    Preconditions.checkNotNull(transactionId);
    Preconditions.checkNotNull(amount);
    Preconditions.checkNotNull(date);
    Preconditions.checkNotNull(description);

    Transaction transaction = this.transactionRepository.findById(new AccountId(accountId), new TransactionId(transactionId))
      .orElseThrow(() -> new TransactionNotFoundException(transactionId));

    transaction.setAmount(amount);
    transaction.setDate(date);
    transaction.setDescription(description);
    transaction.setTags(tags.stream().map(v -> new Tag(v)).collect(Collectors.toSet()));

    this.transactionRepository.update(transaction);
  }
}

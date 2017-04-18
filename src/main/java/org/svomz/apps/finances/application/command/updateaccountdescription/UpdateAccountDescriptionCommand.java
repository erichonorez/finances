package org.svomz.apps.finances.application.command.updateaccountdescription;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountRepository;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Created by eric on 15/04/17.
 */
@Component
public class UpdateAccountDescriptionCommand {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  @Inject
  public UpdateAccountDescriptionCommand(AccountRepository accountRepository, TransactionRepository transactionRepository) {
    this.accountRepository = Preconditions.checkNotNull(accountRepository);
    this.transactionRepository = Preconditions.checkNotNull(transactionRepository);
  }

  @Transactional
  public void execute(UpdateAccountDescriptionCommandParameters command) throws AccountNotFoundException {
    Preconditions.checkNotNull(command);

    String accountId = command.getAccountId();

    Account account = this.accountRepository.find(accountId)
      .orElseThrow(() -> new AccountNotFoundException(accountId));

    account.setDescription(command.getDescription());
  }

}

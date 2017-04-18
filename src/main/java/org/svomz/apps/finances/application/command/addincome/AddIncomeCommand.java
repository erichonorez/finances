package org.svomz.apps.finances.application.command.addincome;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountRepository;
import org.svomz.apps.finances.domain.model.Accounts;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Created by eric on 15/04/17.
 */
@Component
public class AddIncomeCommand {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  @Inject
  public AddIncomeCommand(AccountRepository accountRepository, TransactionRepository transactionRepository) {
    this.accountRepository = Preconditions.checkNotNull(accountRepository);
    this.transactionRepository = Preconditions.checkNotNull(transactionRepository);
  }

  @Transactional
  public void execute(AddIncomeCommandParameters command) throws AccountNotFoundException {
    Preconditions.checkNotNull(command);

    String accountId = command.getAccountId();

    Account account = this.accountRepository.find(accountId)
      .orElseThrow(() -> new AccountNotFoundException(accountId));

    Accounts.addIncome(
      account,
      BigDecimal.valueOf(command.getValue()),
      command.getDateTime(),
      command.getDescription()
    ).apply(this.transactionRepository);

  }
}

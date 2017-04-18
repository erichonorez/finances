package org.svomz.apps.finances.application.command.createaccount;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.AccountRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Created by eric on 15/04/17.
 */
@Component
public class CreateAccountCommand {

  private AccountRepository accountRepository;

  @Inject
  public CreateAccountCommand(final AccountRepository accountRepository) {
    this.accountRepository = Preconditions.checkNotNull(accountRepository);
  }

  @Transactional
  public String execute(String description) {
    Preconditions.checkNotNull(description);

    AccountId accountId = this.accountRepository.nextIdentity();
    Account account = new Account(accountId, description);
    this.accountRepository.create(account);

    return account.getAccountId().getId();
  }
}

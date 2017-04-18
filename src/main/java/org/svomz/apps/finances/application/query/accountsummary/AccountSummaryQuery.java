package org.svomz.apps.finances.application.query.accountsummary;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountRepository;
import org.svomz.apps.finances.domain.model.Accounts;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.math.BigDecimal;

import javax.inject.Inject;

/**
 * Created by eric on 15/04/17.
 */
@Component
public class AccountSummaryQuery {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  @Inject
  public AccountSummaryQuery(AccountRepository accountRepository, TransactionRepository transactionRepository) {
    this.accountRepository = Preconditions.checkNotNull(accountRepository);
    this.transactionRepository = Preconditions.checkNotNull(transactionRepository);
  }

  public AccountSummaryView execute(String accountId) throws AccountNotFoundException {
    Account account = this.accountRepository.find(accountId)
      .orElseThrow(() -> new AccountNotFoundException(accountId));

    BigDecimal balance = Accounts.balance(account)
      .apply(this.transactionRepository);

    return new AccountSummaryView(
      account.getAccountId().getId(),
      account.getDescription(),
      balance
    );
  }
}

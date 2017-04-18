package org.svomz.apps.finances.application.query.accountsoverview;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.query.accountsummary.AccountSummaryView;
import org.svomz.apps.finances.domain.model.AccountRepository;
import org.svomz.apps.finances.domain.model.Accounts;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Created by eric on 15/04/17.
 */
@Component
public class AccountsOverviewQuery {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  @Inject
  public AccountsOverviewQuery(AccountRepository accountRepository, TransactionRepository transactionRepository) {
    this.accountRepository = Preconditions.checkNotNull(accountRepository);
    this.transactionRepository = Preconditions.checkNotNull(transactionRepository);
  }

  public List<AccountSummaryView> execute() {
    return this.accountRepository.findAll().stream()
      .map((account) -> {
        BigDecimal balance = Accounts.balance(account).apply(transactionRepository);
        return new AccountSummaryView(
          account.getAccountId().getId(),
          account.getDescription(),
          balance);
      }).collect(Collectors.toList());
  }

}

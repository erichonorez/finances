package org.svomz.apps.finances.application;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.AccountRepository;
import org.svomz.apps.finances.domain.model.Accounts;
import org.svomz.apps.finances.domain.model.Expense;
import org.svomz.apps.finances.domain.model.Transaction;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Created by eric on 28/02/16.
 */
@Component
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;

  private final TransactionRepository transactionRepository;

  @Inject
  public AccountServiceImpl(final AccountRepository accountRepository, final TransactionRepository transactionRepository) {
    this.accountRepository = Preconditions.checkNotNull(accountRepository);
    this.transactionRepository = Preconditions.checkNotNull(transactionRepository);
  }

  @Override
  @Transactional
  public String execute(CreateAccountCommand command) {
    Preconditions.checkNotNull(command);

    AccountId accountId = this.accountRepository.nextIdentity();
    Account account = new Account(accountId, command.getAccountDescription());
    this.accountRepository.create(account);

    return account.getAccountId().getId();
  }

  @Override
  public Set<AccountSummary> getAllAccounts() {
    return this.accountRepository.findAll().stream()
      .map((account) -> {
        BigDecimal balance = Accounts.balance(account).apply(transactionRepository);
        return new AccountSummary(
          account.getAccountId().getId(),
          account.getDescription(),
          balance);
      }).collect(Collectors.toSet());

  }

  @Override
  @Transactional
  public void execute(final AddIncomeCommand command) throws AccountNotFoundException {
    Preconditions.checkNotNull(command);

    Account account = this.getAccount(command.getAccountId());

    Accounts.addIncome(
      account,
      BigDecimal.valueOf(command.getValue()),
      command.getDateTime(),
      command.getDescription()
    ).apply(this.transactionRepository);

  }

  @Override
  @Transactional
  public void execute(final AddExpenseCommand command) throws AccountNotFoundException {
    Preconditions.checkNotNull(command);

    Account account = this.getAccount(command.getAccountId());

    Expense expense = Accounts.addExpense(
      account,
      BigDecimal.valueOf(command.getValue()),
      command.getDateTime(),
      command.getDescription()
    ).apply(this.transactionRepository);

  }

  @Override
  public List<Transaction> getTransactions(String accountId, int page, int size)
    throws AccountNotFoundException {
    Preconditions.checkNotNull(accountId);

    Account account = this.getAccount(accountId);

    return this.transactionRepository.findByAccountId(account.getAccountId(), page, size);
  }

  @Override
  public Account getAccount(String id) throws AccountNotFoundException {
    Preconditions.checkNotNull(id);

    return this.accountRepository.find(id)
      .orElseThrow(() -> new AccountNotFoundException(id));
  }

  @Override
  public BigDecimal balance(String accountId) throws AccountNotFoundException {
    Preconditions.checkNotNull(accountId);

    Account account = this.getAccount(accountId);

    return Accounts.balance(account)
      .apply(this.transactionRepository);
  }

  @Override
  @Transactional
  public void execute(UpdateAccountDescriptionCommand command)
    throws AccountNotFoundException {
    Preconditions.checkNotNull(command);

    Account account = this.getAccount(command.getAccountId());
    account.setDescription(command.getDescription());
  }
}

package org.svomz.apps.finances.application;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.AccountRepository;
import org.svomz.apps.finances.domain.model.Expense;
import org.svomz.apps.finances.domain.model.Income;
import org.svomz.apps.finances.domain.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Created by eric on 28/02/16.
 */
@Component
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;

  @Inject
  public AccountServiceImpl(final AccountRepository accountRepository) {
    this.accountRepository = Preconditions.checkNotNull(accountRepository);
  }

  @Override
  @Transactional
  public Account execute(CreateAccountCommand command) {
    Preconditions.checkNotNull(command);

    AccountId accountId = this.accountRepository.nextIdentity();
    return this.accountRepository.create(new Account(accountId, command.getAccountDescription()));
  }

  @Override
  public Set<Account> getAllAccounts() {
    return this.accountRepository.findAll();
  }

  @Override
  @Transactional
  public void execute(final AddIncomeCommand command) throws AccountNotFoundException {
    Preconditions.checkNotNull(command);

    Account account = this.getAccount(command.getAccountId());

    account.add(Income.of(command.getValue())
      .occuredOn(command.getDateTime())
      .withDescription(command.getDescription())
      .build());
  }

  @Override
  @Transactional
  public void execute(final AddExpenseCommand command) throws AccountNotFoundException {
    Preconditions.checkNotNull(command);

    Account account = this.getAccount(command.getAccountId());

    account.add(Expense.of(command.getValue())
      .occuredOn(command.getDateTime())
      .withDescription(command.getDescription())
      .build());
  }

  @Override
  public List<Transaction> getTransactions(String accountId, int page, int size)
    throws AccountNotFoundException {
    Preconditions.checkNotNull(accountId);

    Account account = this.getAccount(accountId);

    List<Transaction> transactions = new ArrayList<>();

    int fromIndex = page * size;
    int toIndex = (page + 1) * size;
    for (int i = fromIndex; i < toIndex; i++) {
      transactions.add(account.getTransactions().get(i));
    }

    return transactions;
  }

  @Override
  public Account getAccount(String id) throws AccountNotFoundException {
    Preconditions.checkNotNull(id);

    return this.accountRepository.find(id)
      .orElseThrow(() -> new AccountNotFoundException(id));
  }

  @Override
  public Account execute(UpdateAccountDescriptionCommand command)
    throws AccountNotFoundException {
    Preconditions.checkNotNull(command);

    Account account = this.getAccount(command.getAccountId());
    account.setDescription(command.getDescription());
    return account;
  }
}

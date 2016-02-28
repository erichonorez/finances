package org.svomz.apps.finances.application;

import org.svomz.apps.finances.domain.Account;
import org.svomz.apps.finances.domain.Transaction;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

/**
 * Created by eric on 28/02/16.
 */
public interface AccountService {

  @Transactional
  Account createAccount(String description);

  Set<Account> getAllAccounts();

  @Transactional
  void execute(AddIncomeCommand command) throws AccountNotFoundException;

  @Transactional
  void execute(AddExpenseCommand command) throws AccountNotFoundException;

  List<Transaction> getTransactions(String accountId, int page, int size)
    throws AccountNotFoundException;

  Account getAccount(String id) throws AccountNotFoundException;

  Account updateAccount(String accountId, String description)
    throws AccountNotFoundException;
}

package org.svomz.apps.finances.application;

import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.Transaction;

import java.util.List;
import java.util.Set;

/**
 * Created by eric on 28/02/16.
 */
public interface AccountService {

  Set<Account> getAllAccounts();

  Account execute(CreateAccountCommand command);

  void execute(AddIncomeCommand command) throws AccountNotFoundException;

  void execute(AddExpenseCommand command) throws AccountNotFoundException;

  Account execute(UpdateAccountDescriptionCommand command)
    throws AccountNotFoundException;

  List<Transaction> getTransactions(String accountId, int page, int size)
    throws AccountNotFoundException;

  Account getAccount(String id) throws AccountNotFoundException;

}

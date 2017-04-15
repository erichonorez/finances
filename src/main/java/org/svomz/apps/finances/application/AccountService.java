package org.svomz.apps.finances.application;

import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by eric on 28/02/16.
 */
public interface AccountService {

  Set<AccountSummary> getAllAccounts();

  String execute(CreateAccountCommand command);

  void execute(AddIncomeCommand command) throws AccountNotFoundException;

  void execute(AddExpenseCommand command) throws AccountNotFoundException;

  void execute(UpdateAccountDescriptionCommand command)
    throws AccountNotFoundException;

  List<Transaction> getTransactions(String accountId, int page, int size)
    throws AccountNotFoundException;

  Account getAccount(String id) throws AccountNotFoundException;

  BigDecimal balance(String accountId) throws AccountNotFoundException;

}

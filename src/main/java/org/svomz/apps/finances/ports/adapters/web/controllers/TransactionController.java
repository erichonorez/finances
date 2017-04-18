package org.svomz.apps.finances.ports.adapters.web.controllers;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.application.command.addexpense.AddExpenseCommand;
import org.svomz.apps.finances.application.command.addincome.AddIncomeCommand;
import org.svomz.apps.finances.application.query.accountsummary.AccountSummaryView;
import org.svomz.apps.finances.application.command.addexpense.AddExpenseCommandParameters;
import org.svomz.apps.finances.application.command.addincome.AddIncomeCommandParameters;
import org.svomz.apps.finances.application.query.accountsummary.AccountSummaryQuery;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * Created by eric on 28/02/16.
 */
@Controller
@RequestMapping("/accounts/{accountId}/transactions")
public class TransactionController {

  private final AccountSummaryQuery accountSummaryQuery;
  private final AddExpenseCommand addExpenseCommand;
  private final AddIncomeCommand addIncomeCommand;

  @Inject
  public TransactionController(final AccountSummaryQuery accountSummaryQuery,
    final AddExpenseCommand addExpenseCommand,
    final AddIncomeCommand addIncomeCommand) {
    this.addExpenseCommand = Preconditions.checkNotNull(addExpenseCommand);
    this.addIncomeCommand = Preconditions.checkNotNull(addIncomeCommand);
    this.accountSummaryQuery = Preconditions.checkNotNull(accountSummaryQuery);
  }

  @RequestMapping(path = "/new", method = RequestMethod.GET)
  public String newTransaction(@RequestHeader(value = "referer", required = false) final String referer, @PathVariable final String accountId, final TransactionForm transactionForm, final Model model)
    throws AccountNotFoundException {
    AccountSummaryView account = this.accountSummaryQuery.execute(accountId);
    model.addAttribute("account", account);
    model.addAttribute("referer", referer);
    return "transaction/new";
  }

  @RequestMapping(path = "/create", method = RequestMethod.POST)
  public String createTransaction(@PathVariable final String accountId, @Valid TransactionForm transactionForm, BindingResult bindingResult, Model model)
    throws AccountNotFoundException {
    if (bindingResult.hasErrors()) {
      AccountSummaryView account = this.accountSummaryQuery.execute(accountId);
      model.addAttribute("account", account);
      return "transaction/new";
    }

    switch (transactionForm.getType()) {
      case EXPENSE:
        AddExpenseCommandParameters command = new AddExpenseCommandParameters(
          accountId,
          transactionForm.getAmount(),
          LocalDateTime.ofInstant(transactionForm.getDate().toInstant(), ZoneId.systemDefault()),
          transactionForm.getDescription()
        );
        this.addExpenseCommand.execute(command);
        break;
      case INCOME:
        AddIncomeCommandParameters incomeCommand = new AddIncomeCommandParameters(
          accountId,
          transactionForm.getAmount(),
          LocalDateTime.ofInstant(transactionForm.getDate().toInstant(), ZoneId.systemDefault()),
          transactionForm.getDescription()
        );
        this.addIncomeCommand.execute(incomeCommand);
        break;
      default:
        throw new UnsupportedOperationException();
    }
    return "redirect:/accounts/" + accountId;
  }

  @RequestMapping(path = "/bulk-new", method = RequestMethod.GET)
  public String bulkNew(@PathVariable final String accountId, @RequestParam(name="count", defaultValue = "1") int count, Model model)
    throws AccountNotFoundException {

    AccountSummaryView account = this.accountSummaryQuery.execute(accountId);
    model.addAttribute("account", account);
    BulkTransactionForm bulkTransactionForm = new BulkTransactionForm();

    IntStream.rangeClosed(1, count).forEach(i -> {
      bulkTransactionForm.getTransactions().add(new TransactionForm());
    });

    model.addAttribute("bulkTransactionForm", bulkTransactionForm);
    return "transaction/bulk-new";
  }

  @RequestMapping(path = "bulk-create", method = RequestMethod.POST)
  public String bulkCreate(@PathVariable final String accountId,
      @Valid BulkTransactionForm form, BindingResult bindingResult, Model model)
    throws AccountNotFoundException {

    if (bindingResult.hasErrors()) {
      AccountSummaryView account = this.accountSummaryQuery.execute(accountId);
      model.addAttribute("account", account);
      model.addAttribute("bulkTransactionForm", form);
      return "transaction/bulk-new";
    }

    return "redirect:/accounts/" + accountId;
  }

}

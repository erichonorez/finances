package org.svomz.apps.finances.ports.adapters.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.application.AccountService;
import org.svomz.apps.finances.application.AddExpenseCommand;
import org.svomz.apps.finances.application.AddIncomeCommand;
import org.svomz.apps.finances.domain.model.Account;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * Created by eric on 28/02/16.
 */
@Controller
@RequestMapping("/accounts/{accountId}/transactions")
public class TransactionController {

  private final AccountService accountService;

  @Inject
  public TransactionController(final AccountService accountService) {
    this.accountService = accountService;
  }

  @RequestMapping(path = "/new", method = RequestMethod.GET)
  public String newTransaction(@RequestHeader(value = "referer", required = false) final String referer, @PathVariable final String accountId, final TransactionForm transactionForm, final Model model)
    throws AccountNotFoundException {
    Account account = this.accountService.getAccount(accountId);
    model.addAttribute("account", account);
    model.addAttribute("referer", referer);
    return "transaction/new";
  }

  @RequestMapping(path = "/create", method = RequestMethod.POST)
  public String createTransaction(@PathVariable final String accountId, @Valid TransactionForm transactionForm, BindingResult bindingResult, Model model)

    throws AccountNotFoundException {
    if (bindingResult.hasErrors()) {
      Account account = this.accountService.getAccount(accountId);
      model.addAttribute("account", account);
      return "transaction/new";
    }

    switch (transactionForm.getType()) {
      case EXPENSE:
        AddExpenseCommand command = new AddExpenseCommand(
          accountId,
          transactionForm.getAmount(),
          LocalDateTime.ofInstant(transactionForm.getDate().toInstant(), ZoneId.systemDefault()),
          transactionForm.getDescription()
        );
        this.accountService.execute(command);
        break;
      case INCOME:
        AddIncomeCommand incomeCommand = new AddIncomeCommand(
          accountId,
          transactionForm.getAmount(),
          LocalDateTime.ofInstant(transactionForm.getDate().toInstant(), ZoneId.systemDefault()),
          transactionForm.getDescription()
        );
        this.accountService.execute(incomeCommand);
        break;
      default:
        throw new UnsupportedOperationException();
    }
    return "redirect:/accounts/" + accountId;
  }

}

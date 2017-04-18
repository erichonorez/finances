package org.svomz.apps.finances.ports.adapters.web.controllers;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.application.command.createaccount.CreateAccountCommand;
import org.svomz.apps.finances.application.command.updateaccountdescription.UpdateAccountDescriptionCommand;
import org.svomz.apps.finances.application.query.accountsummary.AccountSummaryView;
import org.svomz.apps.finances.application.command.updateaccountdescription.UpdateAccountDescriptionCommandParameters;

import org.svomz.apps.finances.application.query.accountsoverview.AccountsOverviewQuery;
import org.svomz.apps.finances.application.query.accountsummary.AccountSummaryQuery;
import org.svomz.apps.finances.application.query.listtransactions.ListTransactionsQuery;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * Created by eric on 28/02/16.
 */
@Controller
public class AccountController {

  private final AccountsOverviewQuery accountsOverviewQuery;
  private final AccountSummaryQuery accountSummaryQuery;
  private final ListTransactionsQuery listTransactionsQuery;
  private final CreateAccountCommand createAccountCommand;
  private final UpdateAccountDescriptionCommand updateAccountDescriptionCommand;

  @Inject
  public AccountController(
    final AccountsOverviewQuery accountsOverviewQuery,
    final AccountSummaryQuery accountSummaryQuery,
    final ListTransactionsQuery listTransactionsQuery,
    final CreateAccountCommand createAccountCommand,
    final UpdateAccountDescriptionCommand updateAccountDescriptionCommand) {
    this.accountsOverviewQuery = Preconditions.checkNotNull(accountsOverviewQuery);
    this.accountSummaryQuery = Preconditions.checkNotNull(accountSummaryQuery);
    this.listTransactionsQuery = Preconditions.checkNotNull(listTransactionsQuery);
    this.createAccountCommand = Preconditions.checkNotNull(createAccountCommand);
    this.updateAccountDescriptionCommand = Preconditions.checkNotNull(updateAccountDescriptionCommand);
  }

  @RequestMapping({
    "/",
    "/accounts"
  })
  public String index(Model model) {
    List<AccountSummaryView> accounts = this.accountsOverviewQuery.execute();
    model.addAttribute("accounts", accounts);
    return "account/index";
  }

  @RequestMapping(path = "/accounts/{accountId}", method = RequestMethod.GET)
  public String show(@PathVariable String accountId, Model model)
    throws AccountNotFoundException {
    model.addAttribute("account", this.accountSummaryQuery.execute(accountId));
    model.addAttribute("transactions", this.listTransactionsQuery.execute(accountId, 1, 10));
    return "account/show";
  }

  @RequestMapping(path = "/accounts/{accountId}/edit")
  public String edit(@RequestHeader(value = "referer", required = false) final String referer, @PathVariable String accountId, AccountForm accountForm, Model model)
    throws AccountNotFoundException {
    AccountSummaryView account = this.accountSummaryQuery.execute(accountId);
    accountForm.setDescription(account.getDescription());
    model.addAttribute("accountId", account.getAccountId());
    model.addAttribute("referer", referer);
    return "account/edit";
  }

  @RequestMapping("/accounts/new")
  public String newAccount(@RequestHeader(value = "referer", required = false) final String referer, AccountForm form, Model model) {
    model.addAttribute("referer", referer);
    return "account/new";
  }

  @RequestMapping(path = "/accounts", method = RequestMethod.POST)
  public String createAccount(@Valid AccountForm form, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "account/new";
    }

    String accountId = this.createAccountCommand.execute(form.getDescription());
    return "redirect:/accounts/" + accountId;
  }

  @RequestMapping(path = "/accounts/{accountId}/update", method = RequestMethod.POST)
  public String updateAccount(@PathVariable String accountId, @Valid AccountForm form, BindingResult bindingResult)

    throws AccountNotFoundException {
    if (bindingResult.hasErrors()) {
      return "account/edit";
    }

    this.updateAccountDescriptionCommand.execute(
      new UpdateAccountDescriptionCommandParameters(accountId, form.getDescription()));
    return "redirect:/accounts/" + accountId;
  }

}

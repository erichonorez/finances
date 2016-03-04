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
import org.svomz.apps.finances.application.CreateAccountCommand;
import org.svomz.apps.finances.application.UpdateAccountDescriptionCommand;
import org.svomz.apps.finances.domain.model.Account;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * Created by eric on 28/02/16.
 */
@Controller
public class AccountController {

  private final AccountService accountService;

  @Inject
  public AccountController(final AccountService accountService) {
    this.accountService = accountService;
  }

  @RequestMapping({
    "/",
    "/accounts"
  })
  public String index(Model model) {
    Set<Account> accounts = this.accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    return "account/index";
  }

  @RequestMapping(path = "/accounts/{accountId}", method = RequestMethod.GET)
  public String show(@PathVariable String accountId, Model model)
    throws AccountNotFoundException {
    model.addAttribute("account", this.accountService.getAccount(accountId));
    return "account/show";
  }

  @RequestMapping(path = "/accounts/{accountId}/edit")
  public String edit(@RequestHeader(value = "referer", required = false) final String referer, @PathVariable String accountId, AccountForm accountForm, Model model)
    throws AccountNotFoundException {
    Account account = this.accountService.getAccount(accountId);
    accountForm.setDescription(account.getDescription());
    model.addAttribute("accountId", account.getAccountId().getId());
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

    Account account = this.accountService.execute(
      new CreateAccountCommand(form.getDescription())
    );
    return "redirect:/accounts/" + account.getAccountId().getId();
  }

  @RequestMapping(path = "/accounts/{accountId}/update", method = RequestMethod.POST)
  public String updateAccount(@PathVariable String accountId, @Valid AccountForm form, BindingResult bindingResult)

    throws AccountNotFoundException {
    if (bindingResult.hasErrors()) {
      return "account/edit";
    }

    Account account = this.accountService.execute(
      new UpdateAccountDescriptionCommand(accountId, form.getDescription()));
    return "redirect:/accounts/" + account.getAccountId().getId();
  }

}

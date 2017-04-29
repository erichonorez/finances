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
import org.svomz.apps.finances.application.TransactionNotFoundException;
import org.svomz.apps.finances.application.command.addexpense.AddExpenseCommand;
import org.svomz.apps.finances.application.command.addincome.AddIncomeCommand;
import org.svomz.apps.finances.application.command.deletetransaction.DeleteTransactionCommand;
import org.svomz.apps.finances.application.command.edittransaction.EditTransactionCommand;
import org.svomz.apps.finances.application.query.accountsummary.AccountSummaryView;
import org.svomz.apps.finances.application.command.addexpense.AddExpenseCommandParameters;
import org.svomz.apps.finances.application.command.addincome.AddIncomeCommandParameters;
import org.svomz.apps.finances.application.query.accountsummary.AccountSummaryQuery;
import org.svomz.apps.finances.application.query.gettransaction.GetTransactionQuery;
import org.svomz.apps.finances.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * Created by eric on 28/02/16.
 */
@Controller
@RequestMapping("/accounts/{accountId}/transactions")
public class TransactionController {

  public static final String TAG_SEPARATOR_CHAR = " ";
  private final AccountSummaryQuery accountSummaryQuery;
  private final GetTransactionQuery getTransactionQuery;
  private final EditTransactionCommand editTransactionCommand;
  private final AddExpenseCommand addExpenseCommand;
  private final AddIncomeCommand addIncomeCommand;
  private final DeleteTransactionCommand deleteTransactionCommand;

  @Inject
  public TransactionController(final AccountSummaryQuery accountSummaryQuery,
    final AddExpenseCommand addExpenseCommand,
    final AddIncomeCommand addIncomeCommand,
    final GetTransactionQuery getTransactionQuery,
    final EditTransactionCommand editTransactionCommand,
    DeleteTransactionCommand deleteTransactionCommand) {
    this.addExpenseCommand = Preconditions.checkNotNull(addExpenseCommand);
    this.addIncomeCommand = Preconditions.checkNotNull(addIncomeCommand);
    this.accountSummaryQuery = Preconditions.checkNotNull(accountSummaryQuery);
    this.getTransactionQuery = Preconditions.checkNotNull(getTransactionQuery);
    this.editTransactionCommand = Preconditions.checkNotNull(editTransactionCommand);
    this.deleteTransactionCommand = Preconditions.checkNotNull(deleteTransactionCommand);
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

    List<String> tags = new ArrayList<>(Arrays.asList(transactionForm.getTags().split(TAG_SEPARATOR_CHAR)));
    tags.removeIf(s -> s.isEmpty());

    LocalDateTime dateTime = LocalDateTime.ofInstant(transactionForm.getDate().toInstant(), ZoneId.systemDefault());

    switch (transactionForm.getType()) {
      case EXPENSE:
        AddExpenseCommandParameters command = new AddExpenseCommandParameters(
          accountId,
          transactionForm.getAmount(),
          dateTime,
          transactionForm.getDescription(),
          tags
        );
        this.addExpenseCommand.execute(command);
        break;
      case INCOME:
        AddIncomeCommandParameters incomeCommand = new AddIncomeCommandParameters(
          accountId,
          transactionForm.getAmount(),
          dateTime,
          transactionForm.getDescription(),
          tags
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

  @RequestMapping(path = "/{transactionId}/edit", method = RequestMethod.GET)
  private String edit(@RequestHeader(value = "referer", required = false) final String referer, @PathVariable final String accountId, @PathVariable final String transactionId, Model model)
    throws TransactionNotFoundException {

    Transaction transaction = this.getTransactionQuery.execute(accountId, transactionId);

    TransactionForm form = new TransactionForm();
    form.setAmount(transaction.getAmount().doubleValue());
    form.setDate(Date.from(transaction.getDate().atZone(ZoneId.systemDefault()).toInstant()));
    form.setDescription(transaction.getDescription());
    form.setType(transaction.value().compareTo(BigDecimal.ZERO) < 0 ? TransactionType.EXPENSE : TransactionType.INCOME);
    form.setTags(String.join(TAG_SEPARATOR_CHAR, transaction.getTags().stream().map(t -> t.getName()).collect(
      Collectors.toList())));

    model.addAttribute("transactionForm", form)
     .addAttribute("referer", referer)
     .addAttribute("transactionId", transaction.getTransactionId().getId())
     .addAttribute("accountId", transaction.getAccountId().getId());

    return "transaction/edit";
  }

  @RequestMapping(path = "/{transactionId}/update", method = RequestMethod.POST)
  private String update(@PathVariable final String accountId, @PathVariable final String transactionId,
    @Valid TransactionForm form, BindingResult bindingResult, Model model)
    throws TransactionNotFoundException {

    if (bindingResult.hasErrors()) {
      model.addAttribute("transactionForm", form)
        .addAttribute("transactionId", transactionId)
        .addAttribute("accountId", accountId);

      return "transaction/edit";
    }

    List<String> tags = new ArrayList<>(Arrays.asList(form.getTags().split(TAG_SEPARATOR_CHAR)));
    tags.removeIf(s -> s.isEmpty());
    LocalDateTime date = LocalDateTime.ofInstant(form.getDate().toInstant(), ZoneId.systemDefault());

    this.editTransactionCommand.execute(
      accountId,
      transactionId,
      BigDecimal.valueOf(form.getAmount()),
      date,
      form.getDescription(),
      tags
    );

    return "redirect:/accounts/" + accountId;
  }

  @RequestMapping(path = "/{transactionId}/remove", method = RequestMethod.GET)
  public String remove(@RequestHeader(value = "referer", required = false) final String referer, @PathVariable final String accountId, @PathVariable final String transactionId, Model model) {
    model.addAttribute("accountId", accountId)
      .addAttribute("transactionId", transactionId)
      .addAttribute("referer", referer);

    return "transaction/remove";
  }

  @RequestMapping(path = "/{transactionId}/delete", method = RequestMethod.POST)
  public String delete(@PathVariable final String accountId, @PathVariable final String transactionId)
    throws TransactionNotFoundException {
    this.deleteTransactionCommand.execute(accountId, transactionId);

    return "redirect:/accounts/" + accountId;
  }
}

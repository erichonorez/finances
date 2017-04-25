package org.svomz.apps.finances.ports.adapters.web.controllers;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.application.command.ListTransactionByTagQuery;
import org.svomz.apps.finances.application.query.accountsummary.AccountSummaryQuery;
import org.svomz.apps.finances.application.query.listtransactions.TransactionView;

import java.util.List;

import javax.inject.Inject;
import javax.websocket.server.PathParam;

/**
 * Created by eric on 25/04/17.
 */
@Controller
@RequestMapping("/accounts/{accountId}/tags")
public class TagController {

  private ListTransactionByTagQuery listTransactionByTagQuery;
  private AccountSummaryQuery accountSummaryQuery;

  @Inject
  public TagController(ListTransactionByTagQuery listTransactionByTagQuery,
    AccountSummaryQuery accountSummaryQuery) {
    this.listTransactionByTagQuery = Preconditions.checkNotNull(listTransactionByTagQuery);
    this.accountSummaryQuery = Preconditions.checkNotNull(accountSummaryQuery);
  }

  @RequestMapping(path = "/{tag}", method = RequestMethod.GET)
  public String listTransactionByTag(@PathVariable("accountId") final String accountId, @PathVariable("tag")  final String tag, Model model) throws AccountNotFoundException {
    List<TransactionView> execute = this.listTransactionByTagQuery.execute(
      accountId,
      tag
    );

    model.addAttribute("account", this.accountSummaryQuery.execute(accountId));
    model.addAttribute("transactions", this.listTransactionByTagQuery.execute(
      accountId,
      tag
    ));

    return "account/show";

  }

}

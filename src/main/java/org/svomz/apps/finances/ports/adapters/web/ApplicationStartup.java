package org.svomz.apps.finances.ports.adapters.web;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.application.AccountService;
import org.svomz.apps.finances.application.AddExpenseCommand;
import org.svomz.apps.finances.application.CreateAccountCommand;
import org.svomz.apps.finances.domain.model.Account;

import java.time.LocalDateTime;

import javax.inject.Inject;

/**
 * Created by eric on 15/04/17.
 */
@Component
public class ApplicationStartup
  implements ApplicationListener<ApplicationReadyEvent> {

  @Inject
  private AccountService accountService;

  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {

    Account account = this.accountService
      .execute(new CreateAccountCommand(
        "First Account"
      ));

    try {
      this.accountService
        .execute(new AddExpenseCommand(
          account.getAccountId().getId(),
          10.02,
          LocalDateTime.now(),
          "First"
        ));
    } catch (AccountNotFoundException e) {
      e.printStackTrace();
    }
  }

}

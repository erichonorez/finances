package org.svomz.apps.finances.application.command;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;
import org.svomz.apps.finances.application.AccountNotFoundException;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.AccountRepository;

import javax.inject.Inject;

/**
 * Created by Eric on 4/29/2017.
 */
@Component
public class DeleteAccountCommand {

    private final AccountRepository accountRepository;

    @Inject
    public DeleteAccountCommand(AccountRepository accountRepository) {
        this.accountRepository = Preconditions.checkNotNull(accountRepository);
    }

    public void execute(String accountId) throws AccountNotFoundException {
        Preconditions.checkNotNull(accountId);

        Account account = this.accountRepository.find(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        this.accountRepository.delete(account);
    }

}

package org.svomz.apps.finances.ports.adapters.persistence;

import org.springframework.stereotype.Component;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.AccountRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by eric on 28/02/16.
 */
@Component
public class InMemoryAccountRepository implements AccountRepository {

  private Set<Account> accounts = new HashSet<>();

  @Override
  public Account create(Account account) {
    this.accounts.add(account);
    return account;
  }

  @Override
  public Set<Account> findAll() {
    return Collections.unmodifiableSet(this.accounts);
  }

  @Override
  public Optional<Account> find(String id) {
    return this.accounts.stream()
      .filter(account -> account.getAccountId().getId().equals(id))
      .findFirst();
  }

  @Override
  public AccountId nextIdentity() {
    return new AccountId(UUID.randomUUID().toString());
  }
}

package org.svomz.apps.finances.ports.adapters.persistence;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.AccountRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by eric on 28/02/16.
 */
public class InMemoryAccountRepository implements AccountRepository {

  private List<Account> accounts = new ArrayList<>();

  @Override
  public Account create(Account account) {
    this.accounts.add(account);
    return account;
  }

  @Override
  public void update(Account account) {
    this.accounts.remove(account);
    this.accounts.add(account);
  }

  @Override
  public void delete(Account account) {

  }

  @Override
  public List<Account> findAll() {
    return Collections.unmodifiableList(this.accounts);
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

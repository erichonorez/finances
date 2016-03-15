package org.svomz.apps.finances.ports.adapters.persistence;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Component;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.AccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;

/**
 * Created by eric on 04/03/16.
 */
@Component
public class JpaAccountRepository implements AccountRepository {

  @Autowired
  private BackendRepository backendRepository;

  @Override
  public Account create(Account account) {
    return this.backendRepository.save(account);
  }

  @Override
  public List<Account> findAll() {
    return Lists.newArrayList(this.backendRepository.findAll());
  }

  @Override
  public Optional<Account> find(String id) {
    return Optional.ofNullable(this.backendRepository.findOne(new AccountId(id)));
  }

  @Override
  public AccountId nextIdentity() {
    return new AccountId(UUID.randomUUID().toString());
  }

}

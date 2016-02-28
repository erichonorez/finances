package org.svomz.apps.finances.domain;

import java.util.Optional;
import java.util.Set;

/**
 * Created by eric on 28/02/16.
 */
public interface AccountRepository {

  Account create(Account account);

  Set<Account> findAll();

  Optional<Account> find(String id);

  AccountId nextIdentity();
}

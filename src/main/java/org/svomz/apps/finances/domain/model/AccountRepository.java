package org.svomz.apps.finances.domain.model;

import java.util.List;
import java.util.Optional;

/**
 * Created by eric on 28/02/16.
 */
public interface AccountRepository {

  Account create(Account account);

  List<Account> findAll();

  Optional<Account> find(String id);

  AccountId nextIdentity();
}

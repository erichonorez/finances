package org.svomz.apps.finances.ports.adapters.persistence;

import org.springframework.data.repository.CrudRepository;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountId;

/**
 * Created by eric on 04/03/16.
 */
public interface BackendRepository extends CrudRepository<Account, AccountId> {

}

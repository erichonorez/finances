package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.io.Serializable;

/**
 * Created by eric on 28/02/16.
 */
public class AccountId implements Serializable {

    private String id;

  public AccountId(final String accountId) {
      this.id = Preconditions.checkNotNull(accountId);
    }

  public String getId() {
    return this.id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AccountId accountId = (AccountId) o;

    return id.equals(accountId.id);

  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}

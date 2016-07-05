package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;

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
  
}

package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;

/**
 * Created by eric on 28/02/16.
 */
@Embeddable
public class AccountId implements Serializable {

  @Column(name = "id")
  private String id;

  private AccountId() {
  }

  public AccountId(final String accountId) {
    this.id = Preconditions.checkNotNull(accountId);
  }

  public String getId() {
    return this.id;
  }
  
}

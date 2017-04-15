package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.io.Serializable;

/**
 * Created by eric on 15/04/17.
 */
public class TransactionId implements Serializable {

  private String id;

  public TransactionId(final String transactionId) {
    this.id = Preconditions.checkNotNull(transactionId);
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

    TransactionId that = (TransactionId) o;

    return id.equals(that.id);

  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}

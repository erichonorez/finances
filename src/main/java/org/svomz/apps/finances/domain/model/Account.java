package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Created by eric on 28/02/16.
 */
@Entity
@Table(name = "accounts")
public class Account {

  @EmbeddedId
  private AccountId id;

  @Column(name = "description")
  private String description;

  @OneToMany
  @JoinColumn(name="account_id")
  @Cascade(CascadeType.ALL)
  private List<Transaction> transactions;

  private Account() {}

  public Account(@Nullable final AccountId accountId, final String description) {
    this.id = accountId;
    this.description = Preconditions.checkNotNull(description);
    this.transactions = new ArrayList<Transaction>();
  }

  public Account(final String description) {
    this(null, description);
  }

  public AccountId getAccountId() {
    return this.id;
  }

  public String getDescription() {
    return this.description;
  }

  public BigDecimal getBalance() {
    BigDecimal balance = BigDecimal.ZERO;
    for (Transaction transaction : this.getTransactions()) {
      balance = transaction.apply(balance);
    }
    return balance;
  }

  public void add(final Expense expense) {
    Preconditions.checkNotNull(expense);

    expense.setAccountId(this.getAccountId().getId());
    this.transactions.add(expense);
  }

  public void add(final Income income) {
    Preconditions.checkNotNull(income);

    income.setAccountId(this.getAccountId().getId());
    this.transactions.add(income);
  }

  public List<Transaction> getTransactions() {
    return this.transactions.stream().sorted((o1, o2) -> {
      return o1.getDate().isEqual(o2.getDate()) ? 0
          : o1.getDate().isBefore(o2.getDate()) ? -1 : 1;
    }).collect(Collectors.<Transaction>toList());
  }

  public void setDescription(final String description) {
    Preconditions.checkNotNull(description);
    this.description = description;
  }
}

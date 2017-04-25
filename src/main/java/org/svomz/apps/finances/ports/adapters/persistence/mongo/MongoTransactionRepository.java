package org.svomz.apps.finances.ports.adapters.persistence.mongo;

import static com.mongodb.client.model.Filters.*;

import com.google.common.base.Preconditions;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.springframework.stereotype.Component;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.Expense;
import org.svomz.apps.finances.domain.model.Income;
import org.svomz.apps.finances.domain.model.Tag;
import org.svomz.apps.finances.domain.model.Transaction;
import org.svomz.apps.finances.domain.model.TransactionId;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static com.mongodb.client.model.Sorts.descending;

/**
 * Created by eric on 18/04/17.
 */
@Component
public class MongoTransactionRepository implements TransactionRepository {

  private final MongoClient mongoClient;
  private final MongoDatabase mongoDatabase;

  @Inject
  public MongoTransactionRepository(MongoClient mongoClient) {
    this.mongoClient = Preconditions.checkNotNull(mongoClient);
    this.mongoDatabase = this.mongoClient.getDatabase("finances");
  }

  @Override
  public void create(Transaction expense) {
    this.mongoDatabase.getCollection("transactions").insertOne(this.map(expense));
  }

  @Override
  public TransactionId nextIdentity() {
    return new TransactionId(UUID.randomUUID().toString());
  }

  @Override
  public List<Transaction> findByAccountId(AccountId accountId, int page, int size) {
    MongoCursor<Document> cursor = this.mongoDatabase.getCollection("transactions")
      .find(eq("accountId", accountId.getId()))
      .sort(descending("date"))
      .limit(size)
      .skip((page - 1)* size)
      .iterator();

    List<Transaction> results = new ArrayList<>();
    while (cursor.hasNext()) {
      results.add(this.map(cursor.next()));
    }

    cursor.close();
    return results;
  }

  @Override
  public List<Transaction> findAllByAccountId(AccountId accountId) {
    MongoCursor<Document> cursor = this.mongoDatabase.getCollection("transactions")
      .find(eq("accountId", accountId.getId())).iterator();

    List<Transaction> results = new ArrayList<>();
    while (cursor.hasNext()) {
      results.add(this.map(cursor.next()));
    }

    cursor.close();
    return results;
  }

  @Override
  public Optional<Transaction> findById(AccountId accountId, TransactionId transactionId) {
    Document document = this.mongoDatabase.getCollection("transactions")
      .find(and(eq("accountId", accountId.getId()), eq("transactionId", transactionId.getId()))).first();

    if (document == null) {
      return Optional.empty();
    }

    return Optional.of(this.map(document));
  }

  @Override
  public void update(Transaction transaction) {
    this.mongoDatabase.getCollection("transactions").updateOne(
      eq("transactionId", transaction.getTransactionId().getId()), new Document("$set", this.map(transaction)));
  }

  @Override
  public void delete(Transaction transaction) {
    this.mongoDatabase.getCollection("transactions")
      .deleteOne(eq("transactionId", transaction.getTransactionId().getId()));
  }

  @Override
  public List<Transaction> findAllByTag(AccountId accountId, Tag tag) {
    MongoCursor<Document> cursor = this.mongoDatabase.getCollection("transactions")
      .find(
        and(
          eq("accountId", accountId.getId()),
          eq("tags", tag.getName())
        )
      ).iterator();

    List<Transaction> results = new ArrayList<>();
    while (cursor.hasNext()) {
      results.add(this.map(cursor.next()));
    }

    cursor.close();
    return results;
  }

  private Transaction map(Document next) {
    double amount = next.getDouble("amount");
    TransactionId transactionId = new TransactionId(next.getString("transactionId"));
    AccountId accountId = new AccountId(next.getString("accountId"));
    BigDecimal value = BigDecimal.valueOf(amount);
    LocalDateTime date =
      LocalDateTime.ofInstant(next.getDate("date").toInstant(), ZoneId.systemDefault());
    String description = next.getString("description");

    Set<Tag> tags = new HashSet<>();
    if (next.get("tags") != null) {
      tags = ((List<String>) next.get("tags")).stream().map(s -> new Tag(s)).collect(Collectors.toSet());
    }

    if (amount < 0) {
      return new Expense(
        transactionId,
        accountId,
        value,
        date,
        description,
        tags
      );
    }
    return new Income(
      transactionId,
      accountId,
      value,
      date,
      description,
      tags
    );
  }

  private Document map(Transaction expense) {
    return new Document("transactionId", expense.getTransactionId().getId())
      .append("accountId", expense.getAccountId().getId())
      .append("amount", expense.value().doubleValue())
      .append("date", Date.from(expense.getDate().atZone(ZoneId.systemDefault()).toInstant()))
      .append("description", expense.getDescription())
      .append("tags", expense.getTags().stream().map(t -> t.getName()).collect(Collectors.toList()));
  }
}

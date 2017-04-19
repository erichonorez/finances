package org.svomz.apps.finances.ports.adapters.persistence.mongo;

import com.google.common.base.Preconditions;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.springframework.stereotype.Component;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.Expense;
import org.svomz.apps.finances.domain.model.Income;
import org.svomz.apps.finances.domain.model.Transaction;
import org.svomz.apps.finances.domain.model.TransactionId;
import org.svomz.apps.finances.domain.model.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
      .find()
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
      .find().iterator();

    List<Transaction> results = new ArrayList<>();
    while (cursor.hasNext()) {
      results.add(this.map(cursor.next()));
    }

    cursor.close();
    return results;
  }

  private Transaction map(Document next) {
    double amount = next.getDouble("amount");
    if (amount < 0) {
      return new Expense(
        new TransactionId(next.getString("transactionId")),
        new AccountId(next.getString("accountId")),
        BigDecimal.valueOf(amount),
        LocalDateTime.ofInstant(next.getDate("date").toInstant(), ZoneId.systemDefault()),
        next.getString("description")
      );
    }
    return new Income(
      new TransactionId(next.getString("transactionId")),
      new AccountId(next.getString("accountId")),
      BigDecimal.valueOf(amount),
      LocalDateTime.ofInstant(next.getDate("date").toInstant(), ZoneId.systemDefault()),
      next.getString("description")
    );
  }

  private Document map(Transaction expense) {
    return new Document("transactionId", expense.getTransactionId().getId())
      .append("accountId", expense.getAccountId().getId())
      .append("amount", expense.value().doubleValue())
      .append("date", Date.from(expense.getDate().atZone(ZoneId.systemDefault()).toInstant()))
      .append("description", expense.getDescription());
  }
}

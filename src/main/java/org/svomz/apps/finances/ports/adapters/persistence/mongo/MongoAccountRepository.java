package org.svomz.apps.finances.ports.adapters.persistence.mongo;

import static com.mongodb.client.model.Filters.*;

import com.google.common.base.Preconditions;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.springframework.stereotype.Component;
import org.svomz.apps.finances.domain.model.Account;
import org.svomz.apps.finances.domain.model.AccountId;
import org.svomz.apps.finances.domain.model.AccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by eric on 18/04/17.
 */
@Component
public class MongoAccountRepository implements AccountRepository {

  private final MongoClient mongoClient;
  private final MongoDatabase mongoDatabase;

  @Inject
  public MongoAccountRepository(MongoClient mongoClient) {
    this.mongoClient = Preconditions.checkNotNull(mongoClient);
    this.mongoDatabase = this.mongoClient.getDatabase("finances");
  }

  @Override
  public List<Account> findAll() {
    MongoCursor<Document> cursor = this.mongoDatabase.getCollection("accounts")
      .find().iterator();

    List<Account> results = new ArrayList<>();
    while (cursor.hasNext()) {
      results.add(this.map(cursor.next()));
    }

    cursor.close();
    return results;
  }

  @Override
  public Optional<Account> find(String id) {
    Document first = this.mongoDatabase.getCollection("accounts")
      .find(eq("accountId", id)).first();

    if (null == first) {
      return Optional.empty();
    }

    return Optional.of(this.map(first));
  }

  @Override
  public AccountId nextIdentity() {
    return new AccountId(UUID.randomUUID().toString());
  }

  @Override
  public Account create(Account account) {
    this.mongoDatabase.getCollection("accounts")
      .insertOne(this.map(account));

    return account;
  }

  @Override
  public void update(Account account) {
    this.mongoDatabase.getCollection("accounts")
      .updateOne(eq("accountId", account.getAccountId().getId()), new Document("$set", this.map(account)));
  }

  private Account map(Document next) {
    return new Account(
      new AccountId(next.getString("accountId")),
      next.getString("description")
    );
  }

  private Document map(Account account) {
    return new Document("accountId", account.getAccountId().getId())
      .append("description", account.getDescription());
  }
}

package services.port.adapter.secondary.persistence

import java.util.UUID

import com.google.inject.Inject
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.svomz.apps.finances.core.domain.model.{Account, AccountId, AccountRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoAccountRepository @Inject() (db: MongoDatabase) extends AccountRepository {

  private val collection = db.getCollection("accounts")

  override def update(account: Account): Future[Account] = {
    collection.updateOne(equal("accountId", account.id.value), set("description", account.name)).toFuture() map { _ => account }
  }

  override def delete(account: Account): Future[Account] = {
    collection.deleteOne(equal("accountId", account.id.value)).toFuture() map { _ => account }
  }

  override def create(account: Account): Future[Account] = {
    collection.insertOne(toDocument(account)).toFuture() map { _ => account }
  }

  override def fetch(no: AccountId): Future[Option[Account]] = {
    collection.find(equal("accountId", no.value)).first().toFuture() map { s =>
      if (s.isEmpty) None else Some(fromDocument(s.head))
    }
  }

  override def all: Future[List[Account]] = {
    collection.find().toFuture() map { s =>
      s map { d => fromDocument(d)} toList
    }
  }

  override def nextIdentity: Future[AccountId] = Future { AccountId(UUID.randomUUID().toString) }

  private def toDocument(account: Account): Document = {
    Document("accountId" -> account.id.value, "description" -> account.name)
  }

  def fromDocument(doc: Document): Account = {
    Account(AccountId(doc("accountId").asString().getValue), doc("description").asString().getValue)
  }
}

package services.port.adapter.secondary.persistence.mongo

import java.util.{Date, UUID}

import com.google.inject.Inject
import org.joda.time.DateTime
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts.descending
import org.svomz.apps.finances.core.domain.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.api.Logger

class MongoTransactionRepository @Inject()(db: MongoDatabase) extends TransactionRepository {

  private val collection = db.getCollection("transactions")

  override def nextIdentity: Future[TransactionId] = Future { TransactionId(UUID.randomUUID.toString) }

  override def create(transaction: Transaction): Future[Transaction] = {
    collection.insertOne(toDocument(transaction)).toFuture() map { _ => transaction }
  }

  override def update(transaction: Transaction): Future[Transaction] = {
    collection.replaceOne(equal("transactionId", transaction.id.value), toDocument(transaction)).toFuture() map { _ => transaction }
  }

  override def delete(transaction: Transaction): Future[Transaction] = {
    collection.deleteOne(equal("transactionId", transaction.id.value)).toFuture() map { _ => transaction }
  }

  override def fetch(accountId: AccountId, transactionId: TransactionId): Future[Option[Transaction]] = {
    collection.find(equal("transactionId", transactionId.value)).first().toFuture() map { s => s.headOption match {
      case None => None
      case Some(d) => Some(fromDocument(d))
    } }
  }

  override def fetchAll(accountId: AccountId): Future[List[Transaction]] = {
    collection.find(equal("accountId", accountId.value))
      .sort(descending("date"))
      .toFuture() map { s =>
      s map { doc => fromDocument(doc) } toList
    }
  }

  override def fetchAllWithCategory(accountId: AccountId, category: Category, from: Option[Date], to: Option[Date]): Future[List[Transaction]] = {
    collection.find(and(equal("accountId", accountId.value), equal("tags", category.name)))
      .sort(descending("date"))
      .toFuture() map { s =>
      s map { doc => fromDocument(doc) } toList
    }
  }

  override def fetchAllCategories(accountId: AccountId): Future[List[Category]] = {
    collection.aggregate(
      Seq(
        `match`(equal("accountId", accountId.value)),
        unwind("$tags"),
        group("$tags")
      )
    ).toFuture() map { s => {
      s flatMap { i => i map { t => Category(t._2.asString().getValue)} } toList
    } }
  }

  private def toDocument(transaction: Transaction): Document = {
    val tags = transaction.categoryOption match {
      case None => List()
      case Some(c) => List(c.name)
    }

    Document(
      "accountId" -> transaction.accountId.value,
      "transactionId" -> transaction.id.value,
      "amount" -> transaction.amount.toDouble,
      "date" -> transaction.date,
      "description" -> transaction.descriptionOption.getOrElse(""),
      "tags" -> tags
    )
  }

  def fromDocument(doc: Document): Transaction = {
    val accountId: String = doc("accountId").asString().getValue
    val transactionId: String = doc("transactionId").asString().getValue
    val date: Date = new DateTime(doc("date").asDateTime().getValue).toDate
    val amount: BigDecimal = BigDecimal(doc("amount").asDouble().doubleValue())
    val description: String = doc("description").asString().getValue
    val tags = doc("tags").asArray()
    val category: String = if (tags.iterator().hasNext) tags.iterator().next().asString().getValue else ""
    val isDebit: Boolean = amount < 0

    if (isDebit) {
      Debit(
        AccountId(accountId),
        TransactionId(transactionId),
        amount.abs,
        date,
        if (description.isEmpty) None else Some(description),
        if (category.isEmpty) None else Some(Category(category))
      )
    } else {
      Credit(
        AccountId(transactionId),
        TransactionId(transactionId),
        amount.abs,
        date,
        if (description.isEmpty) None else Some(description),
        if (category.isEmpty) None else Some(Category(category))
      )
    }
  }

  override def fetchAllDebitWithCategory(id: AccountId, c: Category, from: Option[Date], to: Option[Date]): Future[List[Transaction]] = ???

  override def fetchAllDebits(id: AccountId, from: Some[Date], to: Some[Date]): Future[List[Transaction]] = ???

  override def fetchAllCredits(id: AccountId, from: Some[Date], to: Some[Date]): Future[List[Transaction]] = ???
}

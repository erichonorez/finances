package services.port.adapter.secondary.persistence.reactivemongo

import java.util.{Date, UUID}

import com.google.inject.Inject
import org.svomz.apps.finances.core.domain.model._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReactiveMongoTransactionRepository @Inject()(api: ReactiveMongoApi) extends TransactionRepository {

  private val transactions = api.database.map(_.collection[JSONCollection]("transactions"))

  override def nextIdentity: Future[TransactionId] = Future { TransactionId(UUID.randomUUID().toString) }

  override def create(transaction: Transaction): Future[Transaction] = transactions flatMap {
    _.insert[Transaction](transaction) map { _ => transaction }
  }

  override def update(transaction: Transaction): Future[Transaction] = transactions flatMap {
    _.update(transactionWithId(transaction.accountId, transaction.id), transaction) map { _ => transaction }
  }

  override def delete(transaction: Transaction): Future[Transaction] = transactions flatMap {
    _.remove(transactionWithId(transaction.accountId, transaction.id)) map { _ => transaction }
  }

  override def fetch(accountId: AccountId, transactionId: TransactionId): Future[Option[Transaction]] = transactions flatMap {
    _.find(transactionWithId(accountId, transactionId)).one[Transaction]
  }

  override def fetchAll(accountId: AccountId): Future[List[Transaction]] = transactions flatMap {
    _.find(transactionOfAccountWithId(accountId))
      .sort(Json.obj("date" -> -1))
      .cursor[Transaction].collect[List](1000)
  }

  override def fetchAllWithCategory(accountId: AccountId, category: Category): Future[List[Transaction]] = transactions flatMap {
    _.find(Json.obj("accountId" -> accountId.value, "tags" -> category.name))
      .sort(Json.obj("date" -> -1))
      .cursor[Transaction].collect[List](1000)
  }

  override def fetchAllCategories(accountId: AccountId): Future[List[Category]] = {
    val collection = api.db.collection[BSONCollection]("transactions")
    import collection.BatchCommands.AggregationFramework._

    collection.aggregate(
      Match(BSONDocument("accountId" -> accountId.value)),
      List(
        UnwindField("tags"),
        Group(BSONString("$tags"))("category" -> AddFieldToSet("tags"))
      )
    ).map(_.firstBatch.map { d => {
      d.getAs[String]("_id")
    } } withFilter(!_.isEmpty) map(catO => Category(catO.get)))
  }

  private def transactionWithId(accountId: AccountId, transactionId: TransactionId) = Json.obj(
    "accountId" -> accountId.value,
    "transactionId" -> transactionId.value
  )

  private def transactionOfAccountWithId(accountId: AccountId) = Json.obj("accountId" -> accountId.value)


  implicit val transactionWriter = new OWrites[Transaction] {
    override def writes(t: Transaction): JsObject = {
      val tags = t.categoryOption match {
        case None => List()
        case Some(c) => List(c.name)
      }

      Json.obj(
        "accountId" -> t.accountId.value,
        "transactionId" -> t.id.value,
        "amount" -> t.amount.toDouble,
        "date" -> t.date,
        "description" -> JsString(t.descriptionOption.getOrElse("")),
        "tags" -> tags
      )
    }
  }

  implicit val transactionReader: Reads[Transaction] = (
    (JsPath \ "accountId").read[String].map(AccountId(_)) and
    (JsPath \ "transactionId").read[String].map(TransactionId(_)) and
    (JsPath \ "amount").read[Double].map(BigDecimal(_)) and
    (JsPath \ "date").read[BSONDateTime].map(d => new Date(d.value)) and
    (JsPath \ "description").readNullable[String] and
    (JsPath \ "tags").read[List[String]].map(ts => ts map(Category(_)))
  )(transactionMaker(_, _, _, _, _, _))

  def transactionMaker(accountId: AccountId,
                               transactionId: TransactionId,
                               amount: BigDecimal,
                               date: Date,
                               description:Option[String],
                               tags: List[Category]) = {
    if (amount > 0) {
      Credit(
        accountId,
        transactionId,
        amount.abs,
        new Date(),
        description,
        tags.headOption
      )
    } else {
      Debit(
        accountId,
        transactionId,
        amount.abs,
        new Date(),
        description,
        tags.headOption
      )
    }
  }
}

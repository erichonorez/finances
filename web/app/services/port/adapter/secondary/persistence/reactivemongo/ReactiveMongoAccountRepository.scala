package services.port.adapter.secondary.persistence.reactivemongo

import java.util.UUID

import com.google.inject.Inject
import org.svomz.apps.finances.core.domain.model.{Account, AccountId, AccountRepository}
import play.api.libs.json._
import play.api.libs.functional.syntax._ // Combinator syntax
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson._
import reactivemongo.play.json.collection._
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReactiveMongoAccountRepository @Inject()(api: ReactiveMongoApi) extends AccountRepository {

  private val accounts = api.database.map(_.collection[JSONCollection]("accounts"))

  override def update(account: Account): Future[Account] = accounts.flatMap {
    _.update(accountWithId(account.id), account) map { _ => account}
  }

  override def delete(account: Account): Future[Account] = accounts.flatMap {
    _.remove(accountWithId(account.id)) map { _ => account}
  }

  override def create(account: Account): Future[Account] = accounts.flatMap {
    _.insert[Account](account) map { _ => account }
  }

  override def fetch(no: AccountId): Future[Option[Account]] = accounts.flatMap {
    _.find(accountWithId(no)).one[Account]
  }

  override def all: Future[List[Account]] = accounts.flatMap {
    _.find(Json.obj()).cursor[Account]().collect[List](1000)
  }

  override def nextIdentity: Future[AccountId] = Future { AccountId(UUID.randomUUID.toString) }

  private def accountWithId(id: AccountId) = BSONDocument("accountId" -> id.value)


  implicit val accountWriter = new OWrites[Account] {
    override def writes(a: Account): JsObject = Json.obj(
      "accountId"   -> a.id.value,
      "description" -> a.name
    )
  }

  implicit val accountReader: Reads[Account] = (
    ((JsPath \ "accountId").read[String].map(AccountId(_)) and (JsPath \ "description").read[String])(Account.apply(_, _))
  )


}

import com.google.inject.{AbstractModule, Provides, Singleton}
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.ScalaModule
import org.mongodb.scala.{MongoClient, MongoDatabase}
import org.svomz.apps.finances.core.domain.model.{AccountRepository, TransactionRepository}
import org.svomz.apps.finances.core.application.interpreter.ApiEnv
import services.PlayApiEnv
import services.port.adapter.secondary.persistence.mongo.{MongoAccountRepository, MongoTransactionRepository}
import services.port.adapter.secondary.persistence.reactivemongo.{ReactiveMongoAccountRepository, ReactiveMongoTransactionRepository}

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[AccountRepository].to[ReactiveMongoAccountRepository]
    bind[TransactionRepository].to[ReactiveMongoTransactionRepository]
    bind[ApiEnv].to[PlayApiEnv]
  }

}

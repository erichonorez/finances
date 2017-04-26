import com.google.inject.{AbstractModule, TypeLiteral}
import net.codingwell.scalaguice.ScalaModule
import org.svomz.apps.finances.core.adapter.secondary.persistence.InMemoryAccountRepository
import org.svomz.apps.finances.core.domain.model.AccountRepository

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
    bind[AccountRepository[String]].toInstance(new InMemoryAccountRepository)
    bind[Initializer].asEagerSingleton()
  }

}

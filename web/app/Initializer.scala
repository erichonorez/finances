import java.util.UUID

import com.google.inject.Inject
import org.svomz.apps.finances.core.domain.model.{Account, AccountRepository}

class Initializer @Inject()(val accountRepository: AccountRepository[String]) {

  for(i <- 1 to 10) yield accountRepository.persist(
    Account(
      UUID.randomUUID.toString,
      s"Account ${i}",
      List()
    )
  )

}

package org.svomz.apps.finances;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.svomz.apps.finances.ports.adapters.web.views.FinancesDialect;
import org.svomz.apps.finances.ports.adapters.web.views.FinancesUtils;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import nz.net.ultraq.thymeleaf.LayoutDialect;

/**
 * Created by eric on 28/02/16.
 */
@SpringBootApplication
@EnableTransactionManagement
public class WebApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebApplication.class, args);
  }

  @Bean
  public LayoutDialect templateDialect() {
    return new LayoutDialect();
  }

  @Bean
  public Java8TimeDialect java8TimeDialect() {
    return new Java8TimeDialect();
  }

  @Bean
  public FinancesDialect financesDialect() {
    return new FinancesDialect();
  }

}

package org.svomz.apps.finances;

import com.mongodb.MongoClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.svomz.apps.finances.ports.adapters.web.views.FinancesDialect;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@SpringBootApplication
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

  @Bean
  public MongoClient mongoClient() {
    return new MongoClient();
  }
}

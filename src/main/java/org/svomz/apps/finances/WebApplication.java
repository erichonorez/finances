package org.svomz.apps.finances;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.svomz.apps.finances.domain.model.AccountCreated;
import org.svomz.apps.finances.domain.model.DomainEvent;
import org.svomz.apps.finances.domain.model.DomainEventPublisher;
import org.svomz.apps.finances.domain.model.IncomeAdded;
import org.svomz.apps.finances.ports.adapters.web.views.FinancesDialect;
import org.svomz.apps.finances.ports.adapters.web.views.FinancesUtils;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

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

  @Bean
  public Filter eventSubscriberRegistration() {
    return new Filter() {
      @Override
      public void init(FilterConfig filterConfig) throws ServletException {

      }

      @Override
      public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        DomainEventPublisher.instance()
          .register(DomainEvent.class, event -> {

          });

        chain.doFilter(request, response);

        DomainEventPublisher.instance().tearDown();
      }

      @Override
      public void destroy() {

      }
    };
  }

}

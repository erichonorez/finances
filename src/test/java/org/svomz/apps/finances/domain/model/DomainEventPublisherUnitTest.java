package org.svomz.apps.finances.domain.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by eric on 15/03/16.
 */
public class DomainEventPublisherUnitTest {

  private boolean published;

  @Test
  public void itShouldPublishEventToAllSubscribers() {

    DomainEventPublisher publisher = new DomainEventPublisher();
    publisher.register(ADomainEvent.class, domainEvent -> {
      published = true;
    });

    publisher.publish(new ADomainEvent());

    assertThat(published).isTrue();
    this.published = false;
  }

  @Test
  public void itShouldOnlyPublishEventToSubscribers() {

    DomainEventPublisher publisher = new DomainEventPublisher();
    publisher.register(AnotherDomainEvent.class, aDomainEvent -> {
      this.published = true;
    });

    publisher.publish(new ADomainEvent());

    assertThat(published).isFalse();
  }

  @Test
  public void itShouldRegisterSubscriberPerThread() {

    Thread t1 = new Thread(() -> {
      DomainEventPublisher.instance()
        .register(ADomainEvent.class, aDomainEvent -> {

        });
    });

    Thread t2 = new Thread(() -> {
      DomainEventPublisher.instance()
        .register(AnotherDomainEvent.class, aDomainEvent -> {

        });
    });

  }

  @Test
  public void itShouldCallPublisherRegisteredForParentEvent() {
    DomainEventPublisher publisher = new DomainEventPublisher();
    publisher.register(AParentDomainEvent.class, domainEvent -> {
      published = true;
    });

    publisher.publish(new AChildDomainEvent());

    assertThat(published).isTrue();
    this.published = false;
  }

  private class ADomainEvent {

  }

  private class AnotherDomainEvent {

  }

  private class AParentDomainEvent {

  }

  private class AChildDomainEvent extends AParentDomainEvent {

  }
}

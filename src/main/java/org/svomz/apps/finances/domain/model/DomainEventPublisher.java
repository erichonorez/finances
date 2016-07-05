package org.svomz.apps.finances.domain.model;

import com.google.common.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by eric on 15/03/16.
 */
public final class DomainEventPublisher {

  private static final ThreadLocal<DomainEventPublisher> INSTANCE = new ThreadLocal<DomainEventPublisher>() {
    @Override
    protected DomainEventPublisher initialValue() {
      return new DomainEventPublisher();
    }
  };

  private Map<Class, List<Consumer>> subscribers;

  @VisibleForTesting
  DomainEventPublisher() {
    this.subscribers = new HashMap<>();
  }

  public <T> void register(Class<T> aDomainEventClass, Consumer<T> consumer) {

    if (!this.subscribers.containsKey(aDomainEventClass)) {
      this.subscribers.put(aDomainEventClass, new ArrayList<>());
    }

    this.subscribers.get(aDomainEventClass).add(consumer);
  }

  public <T> void publish(T aDomainEvent) {
    this.subscribers.keySet().stream()
      .filter(aClass -> {
        return aClass.isInstance(aDomainEvent);
      }).forEach(aClass1 -> {
      this.subscribers.get(aClass1)
        .stream()
        .forEach(consumer -> consumer.accept(aDomainEvent));
    });
  }

  public void tearDown() {
    this.subscribers = new HashMap<>();
  }

  public static DomainEventPublisher instance() {
    return INSTANCE.get();
  }

}

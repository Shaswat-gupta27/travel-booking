package buildingblocks.core.event;

import buildingblocks.core.model.AggregateRoot;
import buildingblocks.outboxprocessor.PersistMessageProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class EventDispatcherImplTests {
  public static class TestAggregate extends AggregateRoot<String> {
  }

  record TestEvent(String name) implements DomainEvent {
  }

  private EventDispatcherImpl dispatcher() {
    return new EventDispatcherImpl(mock(EventMapper.class), mock(PersistMessageProcessor.class), mock(ApplicationContext.class));
  }

  @Test
  void should_return_and_clear_the_domain_events_raised_by_aggregates() {
    EventDispatcherImpl dispatcher = dispatcher();
    dispatcher.clearDomainEvents();
    new TestAggregate().addDomainEvent(new TestEvent("first"));
    new TestAggregate().addDomainEvent(new TestEvent("second"));

    assertEquals(2, dispatcher.getDomainEvents().size());

    dispatcher.clearDomainEvents();

    assertTrue(dispatcher.getDomainEvents().isEmpty());
  }

  // Regression: finding the AggregateRoot instance scanned the whole classpath (~0.8 s) on every call, so a request with
  // N events cost N scans. Many calls must now be fast because the instance is found once and reused.
  @Test
  void should_not_rescan_the_classpath_on_every_call() {
    EventDispatcherImpl dispatcher = dispatcher();
    dispatcher.getDomainEvents(); // first call may scan

    long start = System.nanoTime();
    for (int i = 0; i < 200; i++) {
      dispatcher.getDomainEvents();
      dispatcher.clearDomainEvents();
    }
    long millis = (System.nanoTime() - start) / 1_000_000;

    assertTrue(millis < 2000, "400 calls took " + millis + " ms");
  }
}

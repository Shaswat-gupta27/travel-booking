package buildingblocks.core.event;

import buildingblocks.core.model.AggregateRoot;
import buildingblocks.outboxprocessor.PersistMessageProcessor;
import buildingblocks.outboxprocessor.PersistMessageProcessorConfiguration;
import buildingblocks.utils.reflection.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@Import(PersistMessageProcessorConfiguration.class)
public class EventDispatcherImpl implements EventDispatcher {
    private final EventMapper eventMapper;
    private volatile AggregateRoot<?> aggregateRoot;
    private final PersistMessageProcessor persistMessageProcessor;
  private final ApplicationContext applicationContext;

  public EventDispatcherImpl(EventMapper eventMapper, PersistMessageProcessor persistMessageProcessor, ApplicationContext applicationContext) {
        this.eventMapper = eventMapper;
        this.persistMessageProcessor = persistMessageProcessor;
    this.applicationContext = applicationContext;
  }

    @Override
    public <T extends DomainEvent> void send(List<T> domainEvents, Class<?> eventType) {
        List<IntegrationEvent> integrationEvents = domainEvents.stream().map(eventMapper::MapToIntegrationEvent).toList();

        integrationEvents.forEach(persistMessageProcessor::publishMessage);

        if (InternalCommand.class.isAssignableFrom(eventType)) {
            List<InternalCommand> internalCommands = domainEvents.stream().map(eventMapper::MapToInternalCommand).toList();
            internalCommands.forEach(persistMessageProcessor::addInternalMessage);
        }
    }

    @Override
    public List<DomainEvent> getDomainEvents() {
        return aggregateRoot().getDomainEvents();
    }

    @Override
    public void clearDomainEvents() {
        aggregateRoot().clearDomainEvents();
    }

    // Domain events live in a static list on AggregateRoot, so any instance will do. Finding one scans the whole
    // classpath (about 0.8 s), so it is done once, not on every request and every event.
    private AggregateRoot<?> aggregateRoot() {
        AggregateRoot<?> instance = aggregateRoot;
        if (instance == null) {
            instance = Objects.requireNonNull(ReflectionUtils.getInstanceOfSubclass(AggregateRoot.class, applicationContext));
            aggregateRoot = instance;
        }
        return instance;
    }
}

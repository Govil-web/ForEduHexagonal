package com.academia.domain.ports.out;
import com.academia.domain.model.events.DomainEvent;
import java.util.List;

public interface DomainEventPublisher {
    void publish(List<DomainEvent> events);
}
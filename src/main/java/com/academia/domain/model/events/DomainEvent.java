package com.academia.domain.model.events;
import java.time.Instant;

public interface DomainEvent {
    Long getEventId();
    Instant getOccurredOn();
}
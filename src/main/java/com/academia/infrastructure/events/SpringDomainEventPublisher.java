package com.academia.infrastructure.events;

import com.academia.domain.model.events.DomainEvent;
import com.academia.domain.ports.out.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(event -> {
            log.info("Publishing domain event: {}", event.getClass().getSimpleName());
            applicationEventPublisher.publishEvent(event);
        });
    }
}

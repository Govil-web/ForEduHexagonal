package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.OrganizationId;

import java.time.Instant;
import java.util.UUID;

/**
 * Eventos de dominio relacionados con el agregado Organization.
 * Siguen el patrón de inmutabilidad usando records de Java.
 */
public class OrganizationEvents {

    /**
     * Evento que se dispara cuando se crea una nueva organización.
     */
    public record OrganizationCreated(
            UUID eventId,
            Instant occurredOn,
            OrganizationId organizationId,
            String name,
            String subdomain
    ) implements DomainEvent {

        public OrganizationCreated(OrganizationId organizationId, String name, String subdomain) {
            this(UUID.randomUUID(), Instant.now(), organizationId, name, subdomain);
        }

        @Override
        public UUID getEventId() {
            return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }

    /**
     * Evento que se dispara cuando se actualiza una organización.
     */
    public record OrganizationUpdated(
            UUID eventId,
            Instant occurredOn,
            OrganizationId organizationId,
            String newName
    ) implements DomainEvent {

        public OrganizationUpdated(OrganizationId organizationId, String newName) {
            this(UUID.randomUUID(), Instant.now(), organizationId, newName);
        }

        @Override
        public UUID getEventId() {
            return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }

    /**
     * Evento que se dispara cuando se desactiva una organización.
     */
    public record OrganizationDeactivated(
            UUID eventId,
            Instant occurredOn,
            OrganizationId organizationId
    ) implements DomainEvent {

        public OrganizationDeactivated(OrganizationId organizationId) {
            this(UUID.randomUUID(), Instant.now(), organizationId);
        }

        @Override
        public UUID getEventId() {
            return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }
}

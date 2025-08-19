package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.OrganizationId;

import java.time.Instant;


/**
 * Eventos de dominio relacionados con el agregado Organization.
 * Siguen el patrón de inmutabilidad usando records de Java.
 */
public class OrganizationEvents {

    /**
     * Evento que se dispara cuando se crea una nueva organización.
     */
    public record OrganizationCreated(
            Long eventId,
            Instant occurredOn,
            OrganizationId organizationId,
            String name,
            String subdomain
    ) implements DomainEvent {

        public OrganizationCreated(OrganizationId organizationId, String name, String subdomain) {
            this(null, Instant.now(), organizationId, name, subdomain);
        }

        @Override
        public Long getEventId() {
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
            Long eventId,
            Instant occurredOn,
            OrganizationId organizationId,
            String newName
    ) implements DomainEvent {

        public OrganizationUpdated(OrganizationId organizationId, String newName) {
            this(null, Instant.now(), organizationId, newName);
        }

        @Override
        public Long getEventId() {
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
            Long eventId,
            Instant occurredOn,
            OrganizationId organizationId
    ) implements DomainEvent {

        public OrganizationDeactivated(OrganizationId organizationId) {
            this(null, Instant.now(), organizationId);
        }

        @Override
        public Long getEventId() {
            return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }
}

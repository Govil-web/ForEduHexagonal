package com.academia.domain.model.aggregates;

import com.academia.domain.model.events.DomainEvent;
import com.academia.domain.model.events.AcademicTermEvents;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.AcademicTermId;
import com.academia.domain.model.valueobjects.academic.TermDates;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AcademicTerm {
    private final AcademicTermId id;
    private final OrganizationId organizationId;
    private String name;
    private TermDates termDates;
    private boolean isActive;
    private boolean isCurrentTerm;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public AcademicTerm(AcademicTermId id, OrganizationId organizationId, String name, TermDates termDates) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Academic term name cannot be null or empty");
        }
        if (termDates == null) {
            throw new IllegalArgumentException("Term dates cannot be null");
        }
        
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.termDates = termDates;
        this.isActive = true;
        this.isCurrentTerm = false;
        
        domainEvents.add(new AcademicTermEvents.AcademicTermCreatedEvent(id, organizationId, name, termDates));
    }

    public void updateDates(TermDates newDates) {
        if (newDates == null) {
            throw new IllegalArgumentException("Term dates cannot be null");
        }
        if (hasStarted() && newDates.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot change start date of a term that has already started");
        }
        
        TermDates previousDates = this.termDates;
        this.termDates = newDates;
        
        domainEvents.add(new AcademicTermEvents.AcademicTermDatesUpdatedEvent(id, organizationId, previousDates, newDates));
    }

    public void startTerm() {
        if (hasStarted()) {
            throw new IllegalStateException("Academic term has already started");
        }
        if (!LocalDate.now().equals(termDates.getStartDate()) && LocalDate.now().isBefore(termDates.getStartDate())) {
            throw new IllegalStateException("Cannot start term before its scheduled start date");
        }
        
        domainEvents.add(new AcademicTermEvents.AcademicTermStartedEvent(id, organizationId));
    }

    public void endTerm() {
        if (hasEnded()) {
            throw new IllegalStateException("Academic term has already ended");
        }
        if (!hasStarted()) {
            throw new IllegalStateException("Cannot end a term that hasn't started");
        }
        
        domainEvents.add(new AcademicTermEvents.AcademicTermEndedEvent(id, organizationId));
    }

    public void activate() {
        if (this.isActive) {
            throw new IllegalStateException("Academic term is already active");
        }
        this.isActive = true;
        domainEvents.add(new AcademicTermEvents.AcademicTermActivatedEvent(id, organizationId));
    }

    public void deactivate() {
        if (!this.isActive) {
            throw new IllegalStateException("Academic term is already inactive");
        }
        if (isCurrentTerm) {
            throw new IllegalStateException("Cannot deactivate the current term");
        }
        this.isActive = false;
        domainEvents.add(new AcademicTermEvents.AcademicTermDeactivatedEvent(id, organizationId));
    }

    public void markAsCurrent() {
        this.isCurrentTerm = true;
    }

    public void unmarkAsCurrent() {
        this.isCurrentTerm = false;
    }

    public boolean hasStarted() {
        return LocalDate.now().isAfter(termDates.getStartDate()) || LocalDate.now().equals(termDates.getStartDate());
    }

    public boolean hasEnded() {
        return LocalDate.now().isAfter(termDates.getEndDate());
    }

    public boolean isCurrentlyActive() {
        return hasStarted() && !hasEnded() && isActive;
    }

    public boolean canEnroll() {
        return isCurrentlyActive() && !hasEnded();
    }

    public List<DomainEvent> getDomainEvents() { 
        return List.copyOf(domainEvents); 
    }
    
    public void clearDomainEvents() { 
        domainEvents.clear(); 
    }
}
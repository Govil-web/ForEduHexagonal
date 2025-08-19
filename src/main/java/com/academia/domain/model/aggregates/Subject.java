package com.academia.domain.model.aggregates;

import com.academia.domain.model.events.DomainEvent;
import com.academia.domain.model.events.SubjectEvents;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.SubjectId;
import com.academia.domain.model.valueobjects.academic.SubjectCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Subject {
    private final SubjectId id;
    private final OrganizationId organizationId;
    private String name;
    private SubjectCode subjectCode;
    private String description;
    private int credits;
    private boolean isActive;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public Subject(SubjectId id, OrganizationId organizationId, String name, SubjectCode subjectCode, String description, int credits) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name cannot be null or empty");
        }
        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be positive");
        }
        
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.subjectCode = subjectCode;
        this.description = description;
        this.credits = credits;
        this.isActive = true;
        
        domainEvents.add(new SubjectEvents.SubjectCreatedEvent(id, organizationId, subjectCode, name));
    }

    public void updateDetails(String name, String description, int credits) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name cannot be null or empty");
        }
        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be positive");
        }
        
        String previousName = this.name;
        int previousCredits = this.credits;
        
        this.name = name;
        this.description = description;
        this.credits = credits;
        
        domainEvents.add(new SubjectEvents.SubjectUpdatedEvent(id, previousName, name));
    }

    public void activate() {
        if (this.isActive) {
            throw new IllegalStateException("Subject is already active");
        }
        this.isActive = true;
        domainEvents.add(new SubjectEvents.SubjectActivatedEvent(id, organizationId));
    }

    public void deactivate() {
        if (!this.isActive) {
            throw new IllegalStateException("Subject is already inactive");
        }
        this.isActive = false;
        domainEvents.add(new SubjectEvents.SubjectDeactivatedEvent(id, organizationId));
    }

    public boolean canBeDeleted() {
        return !isActive;
    }

    public List<DomainEvent> getDomainEvents() { 
        return List.copyOf(domainEvents); 
    }
    
    public void clearDomainEvents() { 
        domainEvents.clear(); 
    }
}
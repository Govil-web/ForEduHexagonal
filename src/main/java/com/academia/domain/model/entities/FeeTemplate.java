package com.academia.domain.model.entities;

import com.academia.domain.model.valueobjects.ids.FeeTemplateId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.financial.Money;
import com.academia.domain.model.enums.FeeType;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Entity representing a template for generating fees within an organization.
 * Templates define standard fee structures that can be applied to students.
 */
@Getter
public class FeeTemplate {
    private final FeeTemplateId id;
    private final OrganizationId organizationId;
    private final FeeType feeType;
    private String name;
    private String description;
    private Money defaultAmount;
    private boolean isActive;
    private int dueDaysOffset; // Days from creation to due date
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public FeeTemplate(FeeTemplateId id, OrganizationId organizationId, FeeType feeType, 
                      String name, String description, Money defaultAmount, int dueDaysOffset) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Fee template name cannot be null or empty");
        }
        if (dueDaysOffset < 0) {
            throw new IllegalArgumentException("Due days offset cannot be negative");
        }
        
        this.id = id;
        this.organizationId = organizationId;
        this.feeType = feeType;
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.defaultAmount = defaultAmount;
        this.dueDaysOffset = dueDaysOffset;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Updates the template details.
     */
    public void updateDetails(String newName, String newDescription, Money newDefaultAmount) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Fee template name cannot be null or empty");
        }
        
        this.name = newName.trim();
        this.description = newDescription != null ? newDescription.trim() : "";
        this.defaultAmount = newDefaultAmount;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Updates the due days offset.
     */
    public void updateDueDaysOffset(int newDueDaysOffset) {
        if (newDueDaysOffset < 0) {
            throw new IllegalArgumentException("Due days offset cannot be negative");
        }
        
        this.dueDaysOffset = newDueDaysOffset;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Activates the fee template.
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Deactivates the fee template.
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if the template can be used to generate fees.
     */
    public boolean canGenerateFees() {
        return isActive;
    }
}
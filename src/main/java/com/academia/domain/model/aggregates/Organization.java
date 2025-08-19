package com.academia.domain.model.aggregates;

import com.academia.domain.model.valueobjects.ids.OrganizationId;
import lombok.Getter;

@Getter
public class Organization {
    private final OrganizationId id;
    private String name;
    private String subdomain;
    private int digitalConsentAge;
    private boolean isActive;

    public Organization(OrganizationId id, String name, int consentAge) {
        this.id = id;
        this.name = name;
        this.subdomain = generateDefaultSubdomain(name); // Generar subdomain por defecto
        this.digitalConsentAge = consentAge;
        this.isActive = true;
    }

    public Organization(OrganizationId id, String name, String subdomain, int consentAge) {
        this.id = id;
        this.name = name;
        this.subdomain = subdomain != null ? subdomain : generateDefaultSubdomain(name);
        this.digitalConsentAge = consentAge;
        this.isActive = true;
    }

    public void updateDetails(String newName, int newConsentAge) {
        this.name = newName;
        this.digitalConsentAge = newConsentAge;
    }

    private String generateDefaultSubdomain(String name) {
        if (name == null) return "org";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .substring(0, Math.min(name.length(), 20));
    }
}
package com.academia.domain.model.aggregates;

import com.academia.domain.model.entities.AcademicTerm;
import com.academia.domain.model.valueobjects.academic.TermDates;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Organization {
    private final OrganizationId id;
    private String name;
    private String subdomain; // AÑADIDO: para compatibilidad con la BD
    private int digitalConsentAge;
    private boolean isActive;
    private Set<AcademicTerm> academicTerms;

    // Constructor original (mantenido para compatibilidad)
    public Organization(OrganizationId id, String name, int consentAge) {
        this.id = id;
        this.name = name;
        this.subdomain = generateDefaultSubdomain(name); // Generar subdomain por defecto
        this.digitalConsentAge = consentAge;
        this.isActive = true;
        this.academicTerms = new HashSet<>();
    }

    // Constructor extendido con subdomain
    public Organization(OrganizationId id, String name, String subdomain, int consentAge) {
        this.id = id;
        this.name = name;
        this.subdomain = subdomain != null ? subdomain : generateDefaultSubdomain(name);
        this.digitalConsentAge = consentAge;
        this.isActive = true;
        this.academicTerms = new HashSet<>();
    }

    public void updateDetails(String newName, int newConsentAge) {
        this.name = newName;
        this.digitalConsentAge = newConsentAge;
    }

    public AcademicTerm createAcademicTerm(String name, TermDates dates) {
        // Lógica de negocio: evitar solapamiento de fechas
        boolean overlaps = academicTerms.stream().anyMatch(term ->
                dates.getStartDate().isBefore(term.getEndDate()) &&
                        term.getStartDate().isBefore(dates.getEndDate())
        );
        if (overlaps) {
            throw new IllegalStateException("Las fechas del nuevo período académico se solapan con uno existente.");
        }
        AcademicTerm newTerm = new AcademicTerm(null, this.id, name, dates.getStartDate(), dates.getEndDate());
        this.academicTerms.add(newTerm);
        return newTerm;
    }

    private String generateDefaultSubdomain(String name) {
        if (name == null) return "org";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .substring(0, Math.min(name.length(), 20));
    }

    public Set<AcademicTerm> getAcademicTerms() {
        return Collections.unmodifiableSet(academicTerms);
    }
}
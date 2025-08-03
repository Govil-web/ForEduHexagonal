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
    private int digitalConsentAge;
    private boolean isActive;
    private Set<AcademicTerm> academicTerms;

    public Organization(OrganizationId id, String name, int consentAge) {
        this.id = id;
        this.name = name;
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
        AcademicTerm newTerm = new AcademicTerm(null, this.id, name, dates.getStartDate(), dates.getEndDate()); // ID sería asignado por persistencia
        this.academicTerms.add(newTerm);
        return newTerm;
    }

    public Set<AcademicTerm> getAcademicTerms() {
        return Collections.unmodifiableSet(academicTerms);
    }
}

package com.academia.domain.model.entities;
import com.academia.domain.model.valueobjects.*;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import lombok.Getter;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Student {
    private final AccountId accountId;
    private final OrganizationId organizationId;
    private final String studentIdNumber;
    private final LocalDate enrollmentDate;
    private String currentGradeLevel;
    private Set<GuardianRelationship> guardians;

    public Student(AccountId accountId, OrganizationId organizationId, String studentIdNumber, LocalDate enrollmentDate) {
        this.accountId = accountId;
        this.organizationId = organizationId;
        this.studentIdNumber = studentIdNumber;
        this.enrollmentDate = enrollmentDate;
        this.guardians = new HashSet<>();
    }

    public void assignGuardian(Guardian guardian, String relationshipType, boolean isPrimary) {
        if (isPrimary) {
            guardians.forEach(rel -> rel.setPrimaryContact(false));
        }
        this.guardians.add(new GuardianRelationship(guardian.getAccountId(), relationshipType, isPrimary));
    }

    public Set<GuardianRelationship> getGuardians() {
        return Collections.unmodifiableSet(guardians);
    }
    public void changeGradeLevel(String newGrade) {
        if (newGrade == null || newGrade.isBlank()) {
            throw new IllegalArgumentException("El nivel de grado no puede estar vacío.");
        }
        this.currentGradeLevel = newGrade;
        // Opcional: Publicar evento de dominio
    }
}
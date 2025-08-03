package com.academia.domain.model.entities;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import lombok.Getter;
import java.time.LocalDate;
@Getter
public class AcademicTerm {
    private Long id;
    private OrganizationId organizationId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    public AcademicTerm(Long id, OrganizationId orgId, String name, LocalDate start, LocalDate end) { /* ... */ }
}

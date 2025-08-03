package com.academia.domain.model.entities;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import lombok.Getter;
@Getter
public class Subject {
    private Long id;
    private OrganizationId organizationId;
    private String name;
    private String subjectCode;
    public Subject(Long id, OrganizationId orgId, String name, String code) { /* ... */ }
}
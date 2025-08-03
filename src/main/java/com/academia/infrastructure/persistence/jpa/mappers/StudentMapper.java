package com.academia.infrastructure.persistence.jpa.mappers;

import com.academia.domain.model.entities.Student;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.infrastructure.persistence.jpa.entities.StudentProfileJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    // --- De Dominio a Entidad JPA ---
    @Mapping(source = "accountId.value", target = "userId")
    @Mapping(source = "organizationId.value", target = "organizationId")
    StudentProfileJpaEntity toJpa(Student student);

    // --- De Entidad JPA a Dominio ---
    @Mapping(source = "userId", target = "accountId", qualifiedByName = "longToAccountId")
    @Mapping(source = "organizationId", target = "organizationId", qualifiedByName = "longToOrganizationId")
    Student toDomain(StudentProfileJpaEntity jpaEntity);

    // --- Métodos Helper ---
    @Named("longToAccountId")
    default AccountId longToAccountId(Long id) {
        return id == null ? null : new AccountId(id);
    }

    @Named("longToOrganizationId")
    default OrganizationId longToOrganizationId(Long id) {
        return id == null ? null : new OrganizationId(id);
    }
}
package com.academia.infrastructure.persistence.adapters;

import com.academia.domain.model.aggregates.AcademicTerm;
import com.academia.domain.model.valueobjects.ids.AcademicTermId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.ports.out.AcademicTermRepository;
import com.academia.infrastructure.persistence.entities.AcademicTermEntity;
import com.academia.infrastructure.persistence.jpa.repositories.SpringAcademicTermRepository;
import com.academia.infrastructure.persistence.mappers.AcademicTermMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaAcademicTermRepositoryAdapter implements AcademicTermRepository {

    private final SpringAcademicTermRepository springAcademicTermRepository;
    private final AcademicTermMapper academicTermMapper;

    @Override
    public AcademicTerm save(AcademicTerm academicTerm) {
        AcademicTermEntity entity = academicTermMapper.toEntity(academicTerm);
        AcademicTermEntity savedEntity = springAcademicTermRepository.save(entity);
        return academicTermMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AcademicTerm> findById(AcademicTermId id) {
        return springAcademicTermRepository.findById(id.getValue())
                .map(academicTermMapper::toDomain);
    }

    @Override
    public List<AcademicTerm> findByOrganizationId(OrganizationId organizationId) {
        return springAcademicTermRepository.findByOrganizationId(organizationId.getValue())
                .stream()
                .map(academicTermMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AcademicTerm> findCurrentTermsByOrganization(OrganizationId organizationId) {
        return springAcademicTermRepository.findByOrganizationIdAndIsCurrentTermTrue(organizationId.getValue())
                .stream()
                .map(academicTermMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AcademicTerm> findCurrentTermByOrganization(OrganizationId organizationId) {
        return springAcademicTermRepository.findByOrganizationIdAndIsCurrentTermTrue(organizationId.getValue())
                .stream()
                .findFirst()
                .map(academicTermMapper::toDomain);
    }

    @Override
    public List<AcademicTerm> findActiveTermsByOrganization(OrganizationId organizationId) {
        return springAcademicTermRepository.findByOrganizationIdAndIsActiveTrue(organizationId.getValue())
                .stream()
                .map(academicTermMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(AcademicTermId id) {
        springAcademicTermRepository.deleteById(id.getValue());
    }

    @Override
    public boolean existsByOrganizationIdAndName(OrganizationId organizationId, String name) {
        return springAcademicTermRepository.existsByOrganizationIdAndName(organizationId.getValue(), name);
    }
}
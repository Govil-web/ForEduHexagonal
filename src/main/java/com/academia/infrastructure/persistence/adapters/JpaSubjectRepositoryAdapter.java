package com.academia.infrastructure.persistence.adapters;

import com.academia.domain.model.aggregates.Subject;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.SubjectId;
import com.academia.domain.ports.out.SubjectRepository;
import com.academia.infrastructure.persistence.entities.SubjectEntity;
import com.academia.infrastructure.persistence.jpa.repositories.SpringSubjectRepository;
import com.academia.infrastructure.persistence.mappers.SubjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaSubjectRepositoryAdapter implements SubjectRepository {

    private final SpringSubjectRepository springSubjectRepository;
    private final SubjectMapper subjectMapper;

    @Override
    public Subject save(Subject subject) {
        SubjectEntity entity = subjectMapper.toEntity(subject);
        SubjectEntity savedEntity = springSubjectRepository.save(entity);
        return subjectMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Subject> findById(SubjectId id) {
        return springSubjectRepository.findById(id.getValue())
                .map(subjectMapper::toDomain);
    }

    @Override
    public List<Subject> findByOrganizationId(OrganizationId organizationId) {
        return springSubjectRepository.findByOrganizationId(organizationId.getValue())
                .stream()
                .map(subjectMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subject> findActiveByOrganizationId(OrganizationId organizationId) {
        return springSubjectRepository.findByOrganizationIdAndIsActiveTrue(organizationId.getValue())
                .stream()
                .map(subjectMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(SubjectId id) {
        springSubjectRepository.deleteById(id.getValue());
    }

    @Override
    public boolean existsByOrganizationIdAndSubjectCode(OrganizationId organizationId, String subjectCode) {
        return springSubjectRepository.existsByOrganizationIdAndSubjectCode(organizationId.getValue(), subjectCode);
    }
}
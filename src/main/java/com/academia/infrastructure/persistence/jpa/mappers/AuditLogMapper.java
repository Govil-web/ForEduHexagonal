package com.academia.infrastructure.persistence.jpa.mappers;

import com.academia.domain.model.entities.AuditLog;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.infrastructure.persistence.jpa.entities.AuditLogJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogMapper {

    private final ObjectMapper objectMapper;

    public AuditLogJpaEntity toJpaEntity(AuditLog domain) {
        AuditLogJpaEntity entity = new AuditLogJpaEntity();
        
        entity.setEventType(domain.getEventType());
        entity.setUserId(domain.getUserId() != null ? domain.getUserId().getValue() : null);
        entity.setOrganizationId(domain.getOrganizationId() != null ? domain.getOrganizationId().getValue() : null);
        entity.setUserEmail(domain.getUserEmail());
        entity.setIpAddress(domain.getIpAddress());
        entity.setUserAgent(domain.getUserAgent());
        entity.setResource(domain.getResource());
        entity.setAction(domain.getAction());
        entity.setSuccess(domain.isSuccess());
        entity.setFailureReason(domain.getFailureReason());
        entity.setSessionId(domain.getSessionId());
        entity.setRequestId(domain.getRequestId());
        entity.setTimestamp(domain.getTimestamp());
        
        // Convertir metadata Map a JSON string
        if (domain.getMetadata() != null && !domain.getMetadata().isEmpty()) {
            try {
                entity.setMetadata(objectMapper.writeValueAsString(domain.getMetadata()));
            } catch (JsonProcessingException e) {
                log.warn("Error serializing metadata to JSON: {}", e.getMessage());
                entity.setMetadata("{}");
            }
        }
        
        return entity;
    }

    public AuditLog toDomain(AuditLogJpaEntity entity) {
        // Convertir JSON string a Map
        Map<String, Object> metadata = null;
        if (entity.getMetadata() != null && !entity.getMetadata().trim().isEmpty()) {
            try {
                metadata = objectMapper.readValue(entity.getMetadata(), Map.class);
            } catch (JsonProcessingException e) {
                log.warn("Error deserializing metadata from JSON: {}", e.getMessage());
                metadata = Map.of();
            }
        }

        return new AuditLog(
                entity.getId(),
                entity.getEventType(),
                entity.getUserId() != null ? new AccountId(entity.getUserId()) : null,
                entity.getOrganizationId() != null ? new OrganizationId(entity.getOrganizationId()) : null,
                entity.getUserEmail(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getResource(),
                entity.getAction(),
                entity.isSuccess(),
                entity.getFailureReason(),
                metadata,
                entity.getSessionId(),
                entity.getRequestId(),
                entity.getTimestamp()
        );
    }
}
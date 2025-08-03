package com.academia.domain.model.valueobjects;

import com.academia.domain.model.valueobjects.ids.AccountId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Value Object que representa la relación entre un estudiante y un tutor.
 * Es parte del Agregado de Estudiante.
 */
@Getter
@AllArgsConstructor
public class GuardianRelationship {
    private final AccountId guardianId;
    private final String relationshipType; // "Padre", "Madre", "Tutor Legal"
    @Setter private boolean isPrimaryContact;
}
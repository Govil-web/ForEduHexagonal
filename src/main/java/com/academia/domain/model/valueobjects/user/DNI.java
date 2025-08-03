package com.academia.domain.model.valueobjects.user;

import lombok.Value;

@Value
public class DNI {
    String value;
    public DNI(String value) {
        // Aquí iría una validación más robusta según el país
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío.");
        }
        this.value = value;
    }
}
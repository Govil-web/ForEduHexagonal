package com.academia.domain.model.valueobjects.user;

import lombok.Getter;
import lombok.Value;

/**
 * Value Object que representa el nombre completo de una persona.
 * Es inmutable y encapsula la lógica de formato del nombre.
 */
@Value // Asegura inmutabilidad, getters, equals, hashCode y toString.
public class Name {
    String firstName;
    String lastName;

    public Name(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío.");
        }
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    /**
     * Devuelve el nombre completo formateado.
     * @return El nombre y apellido concatenados.
     */
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
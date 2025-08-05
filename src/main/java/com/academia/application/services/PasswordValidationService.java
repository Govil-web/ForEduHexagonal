// PasswordValidationService.java
package com.academia.application.services;

import com.academia.domain.model.aggregates.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Servicio para validar políticas de contraseñas según las reglas de la organización.
 */
@Service
@Slf4j
public class PasswordValidationService {

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

    /**
     * Valida si una contraseña cumple con las políticas de la organización.
     *
     * @param password Contraseña a validar
     * @param organization Organización que define las políticas
     * @return Lista de errores de validación (vacía si es válida)
     */
    public List<String> validatePassword(String password, Organization organization) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("La contraseña no puede estar vacía");
            return errors;
        }

        // Validaciones básicas
        if (password.length() < getMinimumLength(organization)) {
            errors.add("La contraseña debe tener al menos " + getMinimumLength(organization) + " caracteres");
        }

        if (password.length() > 100) {
            errors.add("La contraseña no puede tener más de 100 caracteres");
        }

        // Validaciones de complejidad
        if (requiresUppercase(organization) && !UPPERCASE_PATTERN.matcher(password).matches()) {
            errors.add("La contraseña debe contener al menos una letra mayúscula");
        }

        if (requiresLowercase(organization) && !LOWERCASE_PATTERN.matcher(password).matches()) {
            errors.add("La contraseña debe contener al menos una letra minúscula");
        }

        if (requiresDigit(organization) && !DIGIT_PATTERN.matcher(password).matches()) {
            errors.add("La contraseña debe contener al menos un número");
        }

        if (requiresSpecialChar(organization) && !SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            errors.add("La contraseña debe contener al menos un carácter especial");
        }

        // Validaciones adicionales
        if (containsSequentialChars(password)) {
            errors.add("La contraseña no puede contener secuencias consecutivas como '123' o 'abc'");
        }

        if (containsCommonWords(password)) {
            errors.add("La contraseña no puede contener palabras comunes como 'password' o 'admin'");
        }

        log.debug("Validación de contraseña completada con {} errores", errors.size());
        return errors;
    }

    private int getMinimumLength(Organization organization) {
        // Por ahora usamos una longitud fija, pero podría ser configurable por organización
        return 8;
    }

    private boolean requiresUppercase(Organization organization) {
        return true; // Configurable por organización en el futuro
    }

    private boolean requiresLowercase(Organization organization) {
        return true;
    }

    private boolean requiresDigit(Organization organization) {
        return true;
    }

    private boolean requiresSpecialChar(Organization organization) {
        return false; // Menos estricto por defecto
    }

    private boolean containsSequentialChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char current = password.charAt(i);
            char next1 = password.charAt(i + 1);
            char next2 = password.charAt(i + 2);

            // Verificar secuencias numéricas o alfabéticas
            if ((current + 1 == next1 && next1 + 1 == next2) ||
                    (current - 1 == next1 && next1 - 1 == next2)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsCommonWords(String password) {
        String lowerPassword = password.toLowerCase();
        String[] commonWords = {
                "password", "admin", "user", "login", "guest", "test",
                "123456", "qwerty", "abc123", "password123"
        };

        for (String word : commonWords) {
            if (lowerPassword.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
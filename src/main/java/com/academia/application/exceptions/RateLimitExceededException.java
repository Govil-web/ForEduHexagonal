package com.academia.application.exceptions;

import lombok.Getter;

/**
 * Excepción lanzada cuando se excede el límite de intentos de autenticación.
 */
@Getter
public class RateLimitExceededException extends RuntimeException {

    private final long waitTimeMinutes;

    public RateLimitExceededException(String message, long waitTimeMinutes) {
        super(message);
        this.waitTimeMinutes = waitTimeMinutes;
    }

}

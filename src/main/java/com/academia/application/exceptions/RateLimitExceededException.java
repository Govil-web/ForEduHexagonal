package com.academia.application.exceptions;

/**
 * Excepción lanzada cuando se excede el límite de intentos de autenticación.
 */
public class RateLimitExceededException extends RuntimeException {

    private final long waitTimeMinutes;

    public RateLimitExceededException(String message, long waitTimeMinutes) {
        super(message);
        this.waitTimeMinutes = waitTimeMinutes;
    }

    public long getWaitTimeMinutes() {
        return waitTimeMinutes;
    }
}

package com.academia.application.exceptions;

/**
 * Excepción específica para errores de autenticación.
 */
public class AuthenticationException extends RuntimeException {

    private final String errorCode;
    private final int remainingAttempts;

    public AuthenticationException(String message) {
        super(message);
        this.errorCode = "AUTH_FAILED";
        this.remainingAttempts = -1;
    }

    public AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.remainingAttempts = -1;
    }

    public AuthenticationException(String message, String errorCode, int remainingAttempts) {
        super(message);
        this.errorCode = errorCode;
        this.remainingAttempts = remainingAttempts;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }
}
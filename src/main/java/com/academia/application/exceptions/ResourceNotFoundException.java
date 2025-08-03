package com.academia.application.exceptions;

/**
 * Excepción que se lanza cuando no se encuentra un recurso solicitado,
 * como una Organización, Usuario o Curso.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

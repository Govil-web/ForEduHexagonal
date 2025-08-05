package com.academia.infrastructure.web.exceptions;

import com.academia.application.exceptions.AuthenticationException;
import com.academia.application.exceptions.RateLimitExceededException;
import com.academia.application.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la aplicación.
 * Centraliza el manejo de errores y proporciona respuestas consistentes.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        log.warn("Error de autenticación: {} - {}", ex.getErrorCode(), ex.getMessage());

        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", ex.getErrorCode());

        if (ex.getRemainingAttempts() >= 0) {
            details.put("remainingAttempts", ex.getRemainingAttempts());
        }

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Authentication Failed")
                .message(ex.getMessage())
                .path(extractPath(request))
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitException(
            RateLimitExceededException ex, WebRequest request) {

        log.warn("Rate limit excedido: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();
        details.put("waitTimeMinutes", ex.getWaitTimeMinutes());
        details.put("errorCode", "RATE_LIMIT_EXCEEDED");

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error("Too Many Requests")
                .message(ex.getMessage())
                .path(extractPath(request))
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        log.warn("Recurso no encontrado: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Resource Not Found")
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warn("Error de validación: {}", ex.getMessage());

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing // Mantener el primer error por campo
                ));

        Map<String, Object> details = new HashMap<>();
        details.put("fieldErrors", fieldErrors);
        details.put("errorCode", "VALIDATION_FAILED");

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Los datos proporcionados no son válidos")
                .path(extractPath(request))
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }



    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        log.warn("Acceso denegado: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "ACCESS_DENIED");

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Access Denied")
                .message("No tiene permisos para acceder a este recurso")
                .path(extractPath(request))
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("Argumento inválido: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Argument")
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {

        log.warn("Estado inválido: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Invalid State")
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Error interno del servidor", ex);

        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "INTERNAL_SERVER_ERROR");

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocurrió un error interno en el servidor")
                .path(extractPath(request))
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    /**
     * DTO para respuestas de error estandarizadas.
     */
    @lombok.Builder
    @lombok.Data
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, Object> details;
    }
}
package com.hometail.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application that centralizes exception handling
 * across all {@code @RequestMapping} methods through {@code @ExceptionHandler} methods.
 * <p>
 * This class provides consistent error responses for various types of exceptions
 * that might be thrown during request processing.
 *
 * @see org.springframework.web.bind.annotation.RestControllerAdvice
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Common method to create a standardized error response body.
     *
     * @param status  the HTTP status code
     * @param error   the error reason phrase
     * @param message the detailed error message
     * @return a map containing the error details
     */
    private Map<String, Object> createErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return body;
    }

    /**
     * Handles {@link ResourceNotFoundException} by returning a 404 Not Found response.
     * This exception is thrown when a requested resource cannot be found.
     *
     * @param ex the caught exception
     * @return a {@link ResponseEntity} containing error details and HTTP status 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = createErrorResponse(
            HttpStatus.NOT_FOUND,
            "Not Found",
            ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link AnimalAlreadyAdoptedException} by returning a 400 Bad Request response.
     * This exception is thrown when attempting to process an adoption request for an already adopted animal.
     *
     * @param ex the caught exception
     * @return a {@link ResponseEntity} containing error details and HTTP status 400
     */
    @ExceptionHandler(AnimalAlreadyAdoptedException.class)
    public ResponseEntity<Map<String, Object>> handleAnimalAlreadyAdopted(AnimalAlreadyAdoptedException ex) {
        Map<String, Object> body = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link IllegalArgumentException} by returning a 400 Bad Request response.
     * This exception is thrown when a method receives an illegal or inappropriate argument.
     *
     * @param ex the caught exception
     * @return a {@link ResponseEntity} containing error details and HTTP status 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler that catches all unhandled exceptions.
     * Returns a 500 Internal Server Error response with a generic error message.
     *
     * @param ex the caught exception
     * @return a {@link ResponseEntity} containing error details and HTTP status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        Map<String, Object> body = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred: " + ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

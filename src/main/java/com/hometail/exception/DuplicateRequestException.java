package com.hometail.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user attempts to create a duplicate adoption request
 * for an animal they've already requested.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateRequestException extends RuntimeException {

    /**
     * Constructs a new DuplicateRequestException with the specified detail message.
     *
     * @param message the detail message
     */
    public DuplicateRequestException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateRequestException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DuplicateRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

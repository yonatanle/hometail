package com.hometail.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an attempt is made to process an adoption request for an animal that has already been adopted.
 * This is a runtime exception that results in an HTTP 400 Bad Request response when thrown from a controller method.
 *
 * @see org.springframework.web.bind.annotation.ResponseStatus
 * @since 1.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AnimalAlreadyAdoptedException extends RuntimeException {
    
    /**
     * Constructs a new AnimalAlreadyAdoptedException with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to initCause.
     *
     * @param message the detail message which provides information about the exception
     */
    public AnimalAlreadyAdoptedException(String message) { 
        super(message); 
    }

    /**
     * Constructs a new AnimalAlreadyAdoptedException with the specified detail message and cause.
     *
     * @param message the detail message which provides information about the exception
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public AnimalAlreadyAdoptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
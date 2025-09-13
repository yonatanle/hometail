package com.hometail.exception;

/**
 * Exception thrown when a requested resource cannot be found in the system.
 * This is a runtime exception that should be thrown when a lookup for a specific
 * resource (such as a database entity) fails because the resource does not exist.
 *
 * <p>Example usage:
 * <pre>{@code
 * public User findUserById(Long id) {
 *     return userRepository.findById(id)
 *             .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
 * }
 * </pre>
 *
 * @see RuntimeException
 * @since 1.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to initCause.
     *
     * @param message the detail message which provides information about the exception
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified resource type and identifier.
     * The message is formatted as: "{resourceType} not found with {identifierName}: {identifierValue}"
     *
     * @param resourceType    the type of the resource that was not found (e.g., "User", "Animal")
     * @param identifierName  the name of the identifier used for lookup (e.g., "id", "email")
     * @param identifierValue the value of the identifier that was used
     */
    public ResourceNotFoundException(String resourceType, String identifierName, Object identifierValue) {
        super(String.format("%s not found with %s: %s", resourceType, identifierName, identifierValue));
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message which provides information about the exception
     * @param cause   the cause (which is saved for later retrieval by the getCause() method)
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

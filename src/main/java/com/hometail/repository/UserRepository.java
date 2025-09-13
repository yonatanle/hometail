package com.hometail.repository;

import com.hometail.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for {@link User} entity providing data access and query methods.
 * Extends {@link JpaRepository} for basic CRUD operations and custom query methods.
 *
 * <p>This repository includes methods for user authentication and profile management,
 * with built-in validation for email and username fields.</p>
 *
 * @see com.hometail.model.User
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @since 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Checks if a user with the given email already exists.
     *
     * @param email the email to check (must be a valid, non-blank email address)
     * @return true if a user with the given email exists, false otherwise
     * @throws jakarta.validation.ConstraintViolationException if the email is blank or invalid
     */
    boolean existsByEmail(
            @Email(message = "Email should be valid")
            @NotBlank(message = "Email is required")
            String email
    );

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for (must be a valid, non-blank email address)
     * @return an Optional containing the user if found, or empty if no user exists with the given email
     * @throws jakarta.validation.ConstraintViolationException if the email is blank or invalid
     */
    Optional<User> findByEmail(
            @NotBlank(message = "Email cannot be empty")
            @Email(message = "Email should be valid")
            String email
    );
}

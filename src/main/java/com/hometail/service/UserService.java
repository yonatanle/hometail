package com.hometail.service;

import com.hometail.dto.AuthResponse;
import com.hometail.dto.LoginDTO;
import com.hometail.dto.UserDTO;
import com.hometail.dto.PasswordChangeDTO;
import com.hometail.dto.UserRegistrationDTO;
import com.hometail.model.User;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Service interface for managing users in the application.
 * Provides methods for user registration, authentication, and CRUD operations.
 *
 * @since 1.0
 */
public interface UserService {
    /**
     * Creates a new user with administrator privileges.
     * Intended for administrative use only.
     *
     * @param userDTO the user data transfer object containing user details
     * @return the created user as a DTO
     */
    UserDTO createUser(UserDTO userDTO);
    
    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the user's unique identifier
     * @return the user DTO if found, or null if not found
     */
    UserDTO getUserById(Long id);
    
    /**
     * Retrieves a list of all users in the system.
     *
     * @return a list of user DTOs
     */
    List<UserDTO> getAllUsers();
    
    /**
     * Updates an existing user's information.
     *
     * @param id the ID of the user to update
     * @param userDTO the updated user data
     * @return the updated user DTO, or null if the user was not found
     */
    UserDTO updateUser(Long id, UserDTO userDTO);
    
    /**
     * Deletes a user from the system.
     *
     * @param id the ID of the user to delete
     */
    void deleteUser(Long id);
    
    /**
     * Registers a new user in the system.
     * Validates the registration data and ensures the email is not already in use.
     *
     * @param userDTO the registration data transfer object
     * @return the newly registered user entity
     * @throws IllegalArgumentException if the email is already in use
     */
    User registerUser(@Valid UserRegistrationDTO userDTO);
    
    /**
     * Authenticates a user and generates an authentication token.
     *
     * @param loginDTO the login credentials (email and password)
     * @return an authentication response containing the JWT token
     * @throws IllegalArgumentException if the credentials are invalid
     */
    AuthResponse authenticateUser(LoginDTO loginDTO);

    /**
     * Changes the password for the current user.
     *
     * @param email the email of the user changing their password
     * @param passwordChangeDTO the DTO containing current and new password
     * @throws IllegalArgumentException if the current password is incorrect or the user is not found
     */
    void changePassword(String email, @Valid PasswordChangeDTO passwordChangeDTO);
}

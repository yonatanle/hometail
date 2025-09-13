package com.hometail.service.impl;

import com.hometail.dto.*;
import com.hometail.mapper.UserMapper;
import com.hometail.model.User;
import com.hometail.repository.UserRepository;
import com.hometail.security.JwtUtil;
import com.hometail.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link UserService} interface.
 * Provides business logic for user management including registration, authentication, and CRUD operations.
 *
 * <p>This service handles user-related operations, password encoding, and JWT token generation
 * for authenticated users.</p>
 *
 * @see UserService
 * @see UserRepository
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    /** Repository for user data access */
    private final UserRepository userRepository;
    
    /** Service for password encoding and verification */
    private final PasswordEncoder passwordEncoder;
    
    /** Utility for JWT token generation and validation */
    private final JwtUtil jwtUtil;

    /**
     * Constructs a new UserServiceImpl with required dependencies.
     *
     * @param passwordEncoder the password encoder for hashing passwords
     * @param userRepository the user repository for data access
     * @param jwtUtil the JWT utility for token operations
     */
    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, 
                          UserRepository userRepository,
                          JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Creates a new user with administrator privileges. This method is intended for
     * administrative use only and does not perform password hashing or validation.</p>
     *
     * @param userDTO the user data transfer object containing user details
     * @return the created user as a DTO
     */
    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        return UserMapper.toDTO(userRepository.save(user));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Retrieves a user by their unique identifier. Returns null if no user is found.</p>
     */
    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDTO)
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Retrieves a list of all users in the system. The returned list is read-only
     * and contains DTO representations of the users.</p>
     */
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Updates an existing user's information. Only updates non-null fields from the provided DTO.
     * Does not update the password - use a dedicated password update method for that purpose.</p>
     *
     * @return the updated user DTO, or null if the user was not found
     */
    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id).map(existing -> {
            // Only allow updating these fields for non-admin users
            existing.setFullName(userDTO.getFullName());
            existing.setEmail(userDTO.getEmail());
            existing.setPhoneNumber(userDTO.getPhoneNumber());
            
            // Check if role is being changed
            if (userDTO.getRole() != null && !userDTO.getRole().equals(existing.getRole())) {
                // Get current user's email from security context
                String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
                User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new SecurityException("Current user not found"));
                
                // Only allow role change if current user is admin
                if (currentUser.getRole() == null || !currentUser.getRole().equals("ROLE_ADMIN")) {
                    throw new SecurityException("Only administrators can change user roles");
                }
                
                // If we get here, current user is admin and can change the role
                existing.setRole(userDTO.getRole());
            }
            
            return UserMapper.toDTO(userRepository.save(existing));
        }).orElse(null);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Deletes a user from the system. This operation is permanent and cannot be undone.
     * The method does not check for existing references to the user in other parts of the system.</p>
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Registers a new user in the system. Validates the registration data,
     * checks for existing email addresses, and hashes the password before saving.</p>
     *
     * @throws IllegalArgumentException if the email is already in use
     */
    @Override
    @Transactional
    public User registerUser(@Valid UserRegistrationDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already in use.");
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setFullName(userDTO.getFullName());
        // Password encoding
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Authenticates a user by validating their credentials and generates a JWT token
     * upon successful authentication.</p>
     *
     * @throws IllegalArgumentException if the email is not found or the password is incorrect
     */
    @Override
    @Transactional
    public void changePassword(String email, PasswordChangeDTO passwordChangeDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update to new password
        user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse authenticateUser(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Verify the password
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate JWT token for the authenticated user
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, UserMapper.toDTO(user));
    }

}

package com.hometail.controller;

import com.hometail.dto.PasswordChangeDTO;
import com.hometail.dto.UserDTO;
import com.hometail.dto.UserRegistrationDTO;
import com.hometail.mapper.UserMapper;
import com.hometail.model.User;
import com.hometail.repository.UserRepository;
import com.hometail.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing user accounts and profiles.
 * <p>
 * Provides REST endpoints for user registration, profile management, and administrative operations.
 * Some endpoints are secured and require authentication.
 *
 * @version 1.0
 * @see UserService
 * @see UserDTO
 */
@Tag(name = "Users", description = "Endpoints for user management and profiles")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Creates a new user (Admin only).
     *
     * @param dto The user data transfer object containing user information
     * @return The created {@link UserDTO}
     */
    @Operation(
            summary = "Create a new user (Admin only)",
            description = "Creates a new user account. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Admin role required"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User with the same email already exists"
                    )
            }
    )
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    /**
     * Retrieves a user by ID (Admin only).
     *
     * @param id The ID of the user to retrieve
     * @return The requested {@link UserDTO}
     */
    @Operation(
            summary = "Get user by ID (Admin only)",
            description = "Retrieves detailed information about a specific user. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Admin role required"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> get(
            @Parameter(description = "ID of the user to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Retrieves all users (Admin only).
     *
     * @return List of all {@link UserDTO} objects
     */
    @Operation(
            summary = "Get all users (Admin only)",
            description = "Retrieves a list of all users. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of users",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Admin role required"
                    )
            }
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Updates a user by ID (Admin only).
     *
     * @param id The ID of the user to update
     * @param dto The updated user data
     * @return The updated {@link UserDTO}
     */
    @Operation(
            summary = "Update user (Admin only)",
            description = "Updates an existing user's information. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Admin role required"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> update(
            @Parameter(description = "ID of the user to update", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    /**
     * Deletes a user by ID (Admin only).
     *
     * @param id The ID of the user to delete
     * @return ResponseEntity with no content
     */
    @Operation(
            summary = "Delete user (Admin only)",
            description = "Deletes a user by ID. Requires ADMIN role. This operation cannot be undone.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Admin role required"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the user to delete", required = true, example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Registers a new user account.
     *
     * @param registrationDTO The registration data transfer object
     * @return The created {@link UserDTO}
     */
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided registration details.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully registered",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User with the same email already exists"
                    )
            }
    )
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDTO> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegistrationDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody UserRegistrationDTO registrationDTO) {
        User registered = userService.registerUser(registrationDTO);
        return ResponseEntity.ok(UserMapper.toDTO(registered));
    }

    /**
     * Retrieves the currently authenticated user's profile.
     *
     * @param userDetails The authenticated user's details
     * @return The current user's {@link UserDTO}
     */
    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile of the currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user profile",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required"
                    )
            }
    )
    @GetMapping(
            value = "/me",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDTO> getCurrentUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    /**
     * Changes the current user's password.
     *
     * @param passwordChangeDTO The DTO containing current and new password
     * @param userDetails The authenticated user's details
     * @return ResponseEntity with no content
     */
    @Operation(
        summary = "Change password",
        description = "Changes the password for the currently authenticated user.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Password successfully changed"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input or current password is incorrect"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthenticated"
            )
        }
    )
    @PutMapping(
        value = "/change-password",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> changePassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Password change details",
                required = true,
                content = @Content(schema = @Schema(implementation = PasswordChangeDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody PasswordChangeDTO passwordChangeDTO,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        
        userService.changePassword(userDetails.getUsername(), passwordChangeDTO);
        return ResponseEntity.noContent().build();
    }
}

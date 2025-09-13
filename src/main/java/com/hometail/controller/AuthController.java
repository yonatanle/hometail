package com.hometail.controller;

import com.hometail.dto.AuthResponse;
import com.hometail.dto.LoginDTO;
import com.hometail.dto.UserDTO;
import com.hometail.dto.UserRegistrationDTO;
import com.hometail.mapper.UserMapper;
import com.hometail.model.User;
import com.hometail.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling authentication-related operations.
 * <p>
 * This controller provides endpoints for user authentication and token generation.
 * All endpoints are prefixed with "/api/auth".
 */
@Tag(name = "Authentication", description = "Authentication API for user login")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Service responsible for user-related operations including authentication.
     */
    private final UserService userService;

    /**
     * Constructs a new AuthController with the specified UserService.
     *
     * @param userService the user service to be used for authentication
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Authenticates a user and returns an authentication token.
     * <p>
     * This endpoint validates the provided credentials and, if valid, returns a JWT token
     * that can be used for subsequent authenticated requests.
     *
     * @param loginDTO the login credentials (username/email and password)
     * @return ResponseEntity containing the authentication token and user details
     * @throws org.springframework.security.authentication.BadCredentialsException if authentication fails
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if user is not found
     * @throws org.springframework.security.authentication.LockedException if account is locked
     * @throws org.springframework.security.authentication.DisabledException if account is disabled
     *
     * @apiNote The returned JWT token should be included in the Authorization header
     *          of subsequent requests as: "Bearer {token}"
     */
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates a user and returns a JWT token for authorization",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Authentication successful",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Authentication failed"
            )
        }
    )
    @PostMapping(
        value = "/login",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
        AuthResponse response = userService.authenticateUser(loginDTO);
        return ResponseEntity.ok(response);
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
                description = "Email already in use"
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
        User registeredUser = userService.registerUser(registrationDTO);
        return ResponseEntity.ok(UserMapper.toDTO(registeredUser));
    }
}

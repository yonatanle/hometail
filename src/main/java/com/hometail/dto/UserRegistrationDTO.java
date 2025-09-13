package com.hometail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for user registration requests.
 * Contains all necessary information to create a new user account.
 *
 * @version 1.0
 */
@Data
@Schema(description = "Data Transfer Object for user registration requests")
public class UserRegistrationDTO {

    @Schema(
            description = "User's full name",
            example = "John Doe",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @Schema(
            description = "User's email address",
            example = "user@example.com",
            requiredMode = RequiredMode.REQUIRED,
            format = "email"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Schema(
            description = "Account password",
            example = "securePassword123!",
            requiredMode = RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.WRITE_ONLY,
            minLength = 6
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Schema(
            description = "User's phone number",
            example = "+1234567890",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\s\\(\\)\\-]+$", 
             message = "Please provide a valid phone number")
    private String phoneNumber;
}

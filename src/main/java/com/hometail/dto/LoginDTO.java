package com.hometail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for user authentication requests.
 * <p>
 * Contains the credentials required for user authentication.
 *
 * @version 1.0
 */
@Schema(description = "Data Transfer Object for user authentication requests")
@Data
public class LoginDTO {
    @Schema(
            description = "User's email address used for authentication",
            example = "user@example.com",
            requiredMode = RequiredMode.REQUIRED,
            format = "email"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Schema(
            description = "User's password for authentication",
            example = "yourSecurePassword123!",
            requiredMode = RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.WRITE_ONLY, // Prevents password from being returned in responses
            minLength = 6
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "\"Password must be at least 6 characters long\"")
    private String password;
}

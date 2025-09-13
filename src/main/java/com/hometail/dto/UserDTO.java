package com.hometail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object representing a user in the system.
 * <p>
 * Contains user profile information including contact details and system role.
 *
 * @version 1.0
 * @see com.hometail.model.User
 */
@Schema(description = "Data Transfer Object representing a user in the system")
@Data
public class UserDTO {
    @Schema(
            description = "Unique identifier of the user",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;
    @Schema(
            description = "User's full name",
            example = "John Doe",
            requiredMode = RequiredMode.REQUIRED,
            maxLength = 100
    )
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;
    @Schema(
            description = "User's email address",
            example = "john.doe@example.com",
            requiredMode = RequiredMode.REQUIRED,
            format = "email"
    )
    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is required")
    private String email;
    @Schema(
            description = "User's phone number in format 05xxxxxxxx",
            example = "0512345678",
            pattern = "^05\\d{8}$",
            maxLength = 10
    )
    @Pattern(regexp = "^05\\d{8}$", message = "Phone number must be in format 05xxxxxxxx")
    private String phoneNumber;
    @Schema(
            description = "User's role in the system (e.g., USER, ADMIN)",
            example = "USER",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String role;
}

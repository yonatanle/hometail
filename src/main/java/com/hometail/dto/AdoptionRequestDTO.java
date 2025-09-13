package com.hometail.dto;

import com.hometail.model.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for adoption requests.
 * <p>
 * Represents a request from a user to adopt an animal, including all relevant details
 * about the request, the requester, and the animal being requested for adoption.
 *
 * @version 1.0
 * @see com.hometail.model.AdoptionRequest
 */
@Schema(description = "Data Transfer Object for adoption requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionRequestDTO {

    @Schema(
            description = "Unique identifier of the adoption request",
            example = "123",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "ID of the animal being requested for adoption",
            example = "456",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull(message = "Animal ID must not be null")
    private Long animalId;

    @Schema(
            description = "ID of the user making the adoption request",
            example = "789",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long requesterId;

    @Schema(
            description = "Additional notes or message from the requester",
            example = "I have a large backyard and previous experience with this breed.",
            maxLength = 500,
            requiredMode = RequiredMode.REQUIRED
    )
    @NotBlank(message = "Note must not be blank")
    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;

    @Schema(
            description = "Current status of the adoption request",
            implementation = RequestStatus.class,
            example = "PENDING"
    )
    private RequestStatus status;

    @Schema(
            description = "Timestamp when the request was created",
            example = "2023-08-28T10:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Timestamp when a decision was made on the request",
            example = "2023-08-29T15:45:00"
    )
    private LocalDateTime decisionAt;

    // Requester fields
    @Schema(
            description = "Full name of the requester",
            example = "John Doe",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String requesterName;

    @Schema(
            description = "Email address of the requester",
            example = "john.doe@example.com",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String requesterEmail;

    @Schema(
            description = "Contact phone number of the requester",
            example = "+1234567890",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String requesterPhone;

    // Animal fields
    @Schema(
            description = "Name of the animal being requested for adoption",
            example = "Max",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String animalName;

    @Schema(
            description = "Category of the animal (e.g., Dog, Cat)",
            example = "Dog",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String animalCategory;

    @Schema(
            description = "ID of the current owner of the animal",
            example = "101",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long animalOwnerId;

    @Schema(
            description = "Name of the current owner of the animal",
            example = "Jane Smith",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String ownerName;

    @Schema(
            description = "Detailed information about the animal",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private AnimalDTO animal;

}


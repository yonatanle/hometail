package com.hometail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Data Transfer Object representing an animal breed in the system.
 * <p>
 * Contains information about a specific animal breed, including its associated category
 * and display properties.
 *
 * @version 1.0
 * @see com.hometail.model.Breed
 */
@Schema(description = "Data Transfer Object representing an animal breed")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreedDTO {
    @Schema(
            description = "Unique identifier of the breed",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;
    @Schema(
            description = "Name of the breed",
            example = "Golden Retriever",
            requiredMode = RequiredMode.REQUIRED,
            maxLength = 100
    )
    @NotBlank(message = "Breed name must not be blank")
    @Size(max = 100, message = "Breed name must not exceed 100 characters")
    private String name;
    @Schema(
            description = "ID of the category this breed belongs to",
            example = "2",
            requiredMode = RequiredMode.REQUIRED
    )
    private Long categoryId;
    @Schema(
            description = "Name of the category this breed belongs to (read-only)",
            example = "Dogs",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String categoryName;
    @Schema(
            description = "Whether the breed is visible in public listings",
            example = "true",
            defaultValue = "true"
    )
    private boolean active = true;

    @Schema(
            description = "Order in which the breed should appear in UI listings",
            example = "1"
    )
    private Integer sortOrder;
}

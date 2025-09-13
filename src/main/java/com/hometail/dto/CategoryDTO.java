package com.hometail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing an animal category in the system.
 * <p>
 * Contains information about a specific animal category, including its display properties
 * and visibility status.
 *
 * @version 1.0
 * @see com.hometail.model.Category
 */
@Schema(description = "Data Transfer Object representing an animal category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    @Schema(
            description = "Unique identifier of the category",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Name of the category",
            example = "Dogs",
            requiredMode = RequiredMode.REQUIRED,
            maxLength = 100
    )
    @NotBlank(message = "Category name must not be blank")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @Schema(
            description = "Whether the category is visible in public listings",
            example = "true",
            defaultValue = "true"
    )
    private boolean active = true;

    @Schema(
            description = "Order in which the category should appear in UI listings",
            example = "1"
    )
    private Integer sortOrder;
}

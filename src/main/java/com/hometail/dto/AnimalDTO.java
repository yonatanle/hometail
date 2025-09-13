package com.hometail.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hometail.model.AgeGroup;
import com.hometail.model.Gender;
import com.hometail.model.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 * Data Transfer Object representing an animal in the system.
 * <p>
 * Contains detailed information about an animal available for adoption,
 * including its characteristics, owner information, and adoption status.
 *
 * @version 1.0
 * @see com.hometail.model.Animal
 */
@Schema(description = "Data Transfer Object representing an animal in the system")
@Data
public class AnimalDTO {
    @Schema(
            description = "Unique identifier of the animal",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    // Strings -> @NotBlank
    @Schema(
            description = "Name of the animal",
            example = "Max",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotBlank(message = "Animal name must not be blank")
    private String name;

    // IDs / Enums -> @NotNull (and optionally @Positive for IDs)
    @Schema(
            description = "ID of the animal's category",
            example = "2",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull(message = "Category is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;

    @Schema(
            description = "Name of the animal's category (read-only)",
            example = "Dog",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String categoryName;

    @Schema(
            description = "ID of the animal's breed",
            example = "5",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull(message = "Breed is required")
    @Positive(message = "Breed ID must be positive")
    private Long breedId;

    @Schema(
            description = "Name of the animal's breed (read-only)",
            example = "Golden Retriever",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String breedName;

    @Schema(
            description = "Gender of the animal",
            implementation = Gender.class,
            requiredMode = RequiredMode.REQUIRED,
            example = "MALE"
    )
    @NotNull(message = "Gender is required")
    private Gender gender;

    @Schema(
            description = "Size category of the animal",
            implementation = Size.class,
            requiredMode = RequiredMode.REQUIRED,
            example = "MEDIUM"
    )
    @NotNull(message = "Size is required")
    private Size size;

    // Descriptions
    @Schema(
            description = "Brief description of the animal (max 255 characters)",
            example = "Friendly and playful golden retriever",
            requiredMode = RequiredMode.REQUIRED,
            maxLength = 255
    )
    @NotBlank(message = "Short description must not be blank")
    @jakarta.validation.constraints.Size(max = 255, message = "Short description must not exceed 255 characters")
    private String shortDescription;

    @Schema(
            description = "Detailed description of the animal (max 1000 characters)",
            example = "Max is a 3-year-old golden retriever who loves playing fetch and going for long walks. He's great with children and other dogs.",
            maxLength = 1000
    )
    @jakarta.validation.constraints.Size(max = 1000, message = "Long description must not exceed 1000 characters")
    private String longDescription;

    @Schema(
            description = "URL or path to the animal's profile image",
            example = "/uploads/animals/max.jpg",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String image;

    @Schema(
            description = "ID of the animal's current owner",
            example = "10",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull(message = "Owner ID must not be null")
    @Positive(message = "Owner ID must be positive")
    private Long ownerId;

    // Birthday drives derived age fields
    @Schema(
            description = "Date of birth of the animal (YYYY-MM-DD)",
            example = "2020-05-15",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull(message = "Birthday is required")
    @PastOrPresent(message = "Birthday cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate birthday;

    @Schema(
            description = "Name of the animal's owner (read-only)",
            example = "John Smith",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String ownerName;

    @Schema(
            description = "Email of the animal's owner (read-only)",
            example = "john.smith@example.com",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String ownerEmail;

    @Schema(
            description = "Phone number of the animal's owner (read-only)",
            example = "+1234567890",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String ownerPhone;

    @Schema(
            description = "Indicates if the animal has been adopted",
            example = "false"
    )
    private boolean adopted;

    // --- Derived / read-only properties ---

    @Schema(
            description = "Age of the animal in years (derived from birthday, read-only)",
            example = "3",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Integer getAge() {
        if (birthday == null) return null;
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    @Schema(
            description = "Human-readable description of the animal's age (read-only)",
            example = "3 years",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty("ageDescription")
    public String getAgeDescription() {
        if (birthday == null) return "Unknown";
        LocalDate today = LocalDate.now();
        Period period = Period.between(birthday, today);

        if (period.getYears() > 0) {
            return period.getYears() + (period.getYears() == 1 ? " year" : " years");
        } else if (period.getMonths() > 0) {
            return period.getMonths() + (period.getMonths() == 1 ? " month" : " months");
        } else {
            long days = ChronoUnit.DAYS.between(birthday, today);
            if (days == 0) return "Less than a day old";
            if (days == 1) return "1 day";
            if (days < 7) return days + " days";
            if (days < 30) return (days / 7) + " weeks";
            return days + " days";
        }
    }

    @Schema(
            description = "Age group category of the animal (read-only)",
            implementation = AgeGroup.class,
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "ADULT"
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public AgeGroup getAgeGroup() {
        if (birthday == null) return null;
        long months = ChronoUnit.MONTHS.between(birthday, LocalDate.now());
        if (months < 6) return AgeGroup.BABY;
        else if (months < 24) return AgeGroup.YOUNG;
        else if (months < 84) return AgeGroup.ADULT;
        else return AgeGroup.SENIOR;
    }
}

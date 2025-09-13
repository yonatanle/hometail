package com.hometail.model;

/**
 * Represents the biological gender of an animal in the HomeTail platform.
 * 
 * <p>This enum is used to categorize animals based on their biological sex.
 * It's used for various purposes including medical records, breeding information,
 * and filtering in the application.</p>
 *
 * <p>The gender categories are defined as follows:
 * <ul>
 *   <li>MALE: Represents male animals</li>
 *   <li>FEMALE: Represents female animals</li>
 *   <li>UNKNOWN: Used when the gender is not known or not specified</li>
 * </ul>
 * </p>
 *
 * @see com.hometail.model.Animal
 * @since 1.0
 */
public enum Gender {
    /**
     * Represents a male animal.
     * This is used for animals that have been identified as biologically male.
     */
    MALE,

    /**
     * Represents a female animal.
     * This is used for animals that have been identified as biologically female.
     */
    FEMALE,

    /**
     * Represents an animal of unknown gender.
     * This is used when the gender hasn't been determined yet or cannot be determined.
     * It's also appropriate for animals that are intersex or when gender information
     * is not relevant or available.
     */
    UNKNOWN
}

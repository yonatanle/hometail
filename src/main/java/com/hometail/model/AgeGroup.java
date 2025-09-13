package com.hometail.model;

/**
 * Represents the age categories for animals in the HomeTail platform.
 * 
 * <p>This enum is used to categorize animals based on their age ranges.
 * The age groups help in providing appropriate care recommendations,
 * filtering search results, and understanding the animal's life stage.</p>
 *
 * <p>The age groups are defined with the following ranges:
 * <ul>
 *   <li>BABY: Young animals under 6 months old</li>
 *   <li>YOUNG: Animals between 6 months and 2 years old</li>
 *   <li>ADULT: Mature animals between 2 and 7 years old</li>
 *   <li>SENIOR: Older animals over 7 years old</li>
 * </ul>
 * These ranges are general guidelines and may vary by species and breed.
 * </p>
 *
 * @see com.hometail.model.Animal
 * @since 1.0
 */
public enum AgeGroup {
    /**
     * Represents baby animals, typically under 6 months old.
     * These are very young animals that may require special care, feeding,
     * and more frequent veterinary check-ups.
     */
    BABY,

    /**
     * Represents young animals, typically between 6 months and 2 years old.
     * These animals are past infancy but not yet fully mature.
     * They are usually very active and may need training and socialization.
     */
    YOUNG,

    /**
     * Represents adult animals, typically between 2 and 7 years old.
     * These are fully grown animals in their prime years.
     * They typically require regular exercise and routine veterinary care.
     */
    ADULT,

    /**
     * Represents senior animals, typically over 7 years old.
     * These are older animals that may require special attention to diet,
     * exercise, and more frequent health check-ups.
     * The exact age when an animal becomes a senior varies by species and breed.
     */
    SENIOR
}
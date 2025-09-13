package com.hometail.model;

/**
 * Represents the size categories for animals in the HomeTail platform.
 * 
 * <p>This enum defines the standard size categories used to classify animals
 * based on their physical dimensions. The size can be used for filtering,
 * searching, and providing appropriate care recommendations.</p>
 * 
 * <p>The size categories are defined as follows:
 * <ul>
 *   <li>SMALL: Small-sized animals (e.g., small dog breeds, cats, rabbits)</li>
 *   <li>MEDIUM: Medium-sized animals (e.g., beagles, cocker spaniels)</li>
 *   <li>LARGE: Large-sized animals (e.g., labradors, golden retrievers)</li>
 *   <li>EXTRA_LARGE: Extra large-sized animals (e.g., great danes, mastiffs)</li>
 * </ul>
 * </p>
 *
 * @see com.hometail.model.Animal
 * @since 1.0
 */
public enum Size {
    /**
     * Small-sized animals, typically weighing under 10 kg (22 lbs).
     * Examples include small dog breeds, cats, and rabbits.
     */
    SMALL,

    /**
     * Medium-sized animals, typically weighing between 10-25 kg (22-55 lbs).
     * Examples include beagles, cocker spaniels, and some larger cat breeds.
     */
    MEDIUM,

    /**
     * Large-sized animals, typically weighing between 25-45 kg (55-100 lbs).
     * Examples include labradors, golden retrievers, and german shepherds.
     */
    LARGE,

    /**
     * Extra large-sized animals, typically weighing over 45 kg (100 lbs).
     * Examples include great danes, mastiffs, and st. bernards.
     */
    EXTRA_LARGE
}

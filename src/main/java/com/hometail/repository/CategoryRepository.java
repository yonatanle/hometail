package com.hometail.repository;

import com.hometail.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for {@link Category} entity providing data access and query methods.
 * Extends {@link JpaRepository} for basic CRUD operations and custom query methods.
 *
 * <p>This repository includes methods for checking category existence by name
 * and retrieving categories with various filtering and sorting options.</p>
 *
 * @see com.hometail.model.Category
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @since 1.0
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Checks if a category with the given name (case-insensitive) exists, excluding a specific category ID.
     * Useful for update operations to prevent duplicate category names.
     *
     * @param name the category name to check (case-insensitive)
     * @param excludeId the ID of the category to exclude from the check
     * @return true if another category with the given name exists, false otherwise
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long excludeId);
    /**
     * Finds all active categories, ordered by sort order and name in ascending order.
     *
     * @return a list of active categories, ordered by sort order and name
     */
    List<Category> findAllByActiveTrueOrderBySortOrderAscNameAsc();
    /**
     * Finds categories by their exact name (case-sensitive).
     *
     * @param name the exact name to search for
     * @return a list of categories with the exact name match
     */
    List<Category> findByName(String name);

    /**
     * Checks if a category with the given name (case-insensitive) exists.
     *
     * @param name the category name to check (case-insensitive)
     * @return true if a category with the given name exists, false otherwise
     * @throws jakarta.validation.ConstraintViolationException if name is blank or exceeds 100 characters
     */
    boolean existsByNameIgnoreCase(
            @NotBlank(message = "Category name must not be blank")
            @Size(max = 100, message = "Category name must not exceed 100 characters")
            String name
    );
}

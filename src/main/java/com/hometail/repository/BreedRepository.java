package com.hometail.repository;

import com.hometail.model.Breed;
import com.hometail.model.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for {@link Breed} entity providing data access and query methods.
 * Extends {@link JpaRepository} for basic CRUD operations and custom query methods.
 *
 * @see com.hometail.model.Breed
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @since 1.0
 */
public interface BreedRepository extends JpaRepository<Breed, Long> {
    /**
     * Checks if a breed with the given name (case-insensitive) exists in the specified category.
     *
     * @param categoryId the ID of the category to check
     * @param name the breed name to check (case-insensitive)
     * @return true if a breed with the given name exists in the category, false otherwise
     */
    boolean existsByCategoryIdAndNameIgnoreCase(Long categoryId, String name);
    /**
     * Checks if another breed with the given name (case-insensitive) exists in the same category,
     * excluding a specific breed ID (useful for updates).
     *
     * @param categoryId the ID of the category to check
     * @param name the breed name to check (case-insensitive)
     * @param excludeId the ID of the breed to exclude from the check
     * @return true if another breed with the given name exists in the category, false otherwise
     */
    boolean existsByCategoryIdAndNameIgnoreCaseAndIdNot(Long categoryId, String name, Long excludeId);
    /**
     * Finds all active breeds for a specific category, ordered by sort order and name.
     *
     * @param categoryId the ID of the category
     * @return a list of active breeds in the specified category, ordered by sort order and name
     */
    List<Breed> findAllByCategoryIdAndActiveTrueOrderBySortOrderAscNameAsc(Long categoryId);
    /**
     * Finds all breeds belonging to a specific category.
     *
     * @param category the category to search breeds for
     * @return a list of breeds in the specified category
     */
    List<Breed> findByCategory(Category category);

    /**
     * Finds all breeds for a specific category, ordered by sort order and name.
     * Includes both active and inactive breeds.
     *
     * @param categoryId the ID of the category
     * @return a list of breeds in the specified category, ordered by sort order and name
     */
    List<Breed> findAllByCategoryIdOrderBySortOrderAscNameAsc(Long categoryId);

    /**
     * Finds all breeds for a specific category with custom sorting.
     *
     * @param categoryId the ID of the category
     * @param sort the sorting criteria to apply
     * @return a list of breeds in the specified category, sorted according to the provided criteria
     */
    List<Breed> findAllByCategoryId(Long categoryId, Sort sort);

    /**
     * Finds all breeds with optional category filtering, specifically designed for admin interfaces.
     * Includes breeds with null sort orders (placed after those with sort orders).
     *
     * @param categoryId optional category ID to filter by (returns all breeds if null)
     * @return a list of breeds, optionally filtered by category, with proper sorting
     */
    @Query("""
    select b
    from Breed b
    left join fetch b.category c
    where (:categoryId is null or c.id = :categoryId)
    order by case when b.sortOrder is null then 1 else 0 end,
             b.sortOrder asc,
             upper(b.name) asc
""")
    List<Breed> findAllForAdmin(@Param("categoryId") Long categoryId);
}

package com.hometail.repository;

import com.hometail.model.Animal;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link Animal} entity providing data access and query methods.
 * Extends {@link JpaRepository} for basic CRUD operations and
 * {@link JpaSpecificationExecutor} for dynamic query generation.
 *
 * @see com.hometail.model.Animal
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor
 * @since 1.0
 */
@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long>,
        JpaSpecificationExecutor<Animal> {

    /**
     * Finds all animals owned by a specific user.
     *
     * @param ownerId the ID of the owner
     * @return a list of animals belonging to the specified owner, or an empty list if none found
     */
    List<Animal> findByOwnerId(Long ownerId);

    /**
     * Checks if any animal exists with the given category ID.
     *
     * @param categoryId the category ID to check
     * @return true if at least one animal exists with the given category ID, false otherwise
     */
    boolean existsByCategoryId(Long categoryId);
    /**
     * Checks if any animal exists with the given breed ID.
     *
     * @param breedId the breed ID to check
     * @return true if at least one animal exists with the given breed ID, false otherwise
     */
    boolean existsByBreedId(Long breedId);
    /**
     * Checks if an animal with the given ID exists and is marked as adopted.
     *
     * @param animalId the ID of the animal to check (must not be null)
     * @return true if an adopted animal with the given ID exists, false otherwise
     * @throws jakarta.validation.ConstraintViolationException if animalId is null
     */
    boolean existsByIdAndIsAdoptedTrue(@NotNull(message = "Animal ID must not be null") Long animalId);
}

package com.hometail.service;

import com.hometail.dto.BreedDTO;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

/**
 * Service interface for managing animal breeds.
 * Provides methods for CRUD operations and listing breeds with different visibility levels.
 *
 * @since 1.0
 */
public interface BreedService {

    /**
     * Retrieves a list of active breeds for a specific category, sorted by sort order and name.
     * Intended for public use, only returns active breeds.
     *
     * @param categoryId the ID of the category to filter by (required)
     * @return a list of active breed DTOs, or empty list if categoryId is null
     */
    List<BreedDTO> listPublic(Long categoryId);

    /**
     * Retrieves all breeds for a specific category, including inactive ones.
     * Intended for administrative use.
     *
     * @param categoryId the ID of the category to filter by
     * @return a list of all breed DTOs for the category
     */
    List<BreedDTO> listAdmin(Long categoryId);

    /**
     * Retrieves a breed by its ID.
     *
     * @param id the ID of the breed to retrieve
     * @return the breed DTO
     * @throws EntityNotFoundException if no breed is found with the given ID
     */
    BreedDTO getById(Long id);

    /**
     * Creates a new breed with the provided details.
     * Validates that the breed name is unique within the category.
     *
     * @param dto the DTO containing breed details
     * @return the created breed DTO
     * @throws EntityNotFoundException if the category is not found
     * @throws IllegalStateException if a breed with the same name already exists in the category
     */
    BreedDTO create(BreedDTO dto);

    /**
     * Updates an existing breed with the provided details.
     * Validates that the updated breed name remains unique within its category.
     *
     * @param id the ID of the breed to update
     * @param dto the DTO containing updated breed details
     * @return the updated breed DTO
     * @throws EntityNotFoundException if the breed or category is not found
     * @throws IllegalStateException if the update would result in a duplicate breed name in the category
     * @throws IllegalStateException if trying to deactivate a breed that is still referenced by animals
     */
    BreedDTO update(Long id, BreedDTO dto);

    /**
     * Deletes a breed by its ID.
     * Validates that the breed is not referenced by any animals.
     *
     * @param id the ID of the breed to delete
     * @throws EntityNotFoundException if no breed is found with the given ID
     * @throws IllegalStateException if the breed is referenced by animals
     */
    void delete(Long id);
}

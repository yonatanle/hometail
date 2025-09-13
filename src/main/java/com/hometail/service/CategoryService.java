package com.hometail.service;

import com.hometail.dto.CategoryDTO;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

/**
 * Service interface for managing animal categories.
 * Provides methods for CRUD operations and listing categories with different visibility levels.
 *
 * @since 1.0
 */
public interface CategoryService {

    /**
     * Retrieves a list of active categories, sorted by sort order and name.
     * Intended for public use, only returns active categories.
     *
     * @return a list of active category DTOs
     */
    List<CategoryDTO> listPublic();
    
    /**
     * Retrieves all categories including inactive ones, sorted by sort order and name.
     * Intended for administrative use.
     *
     * @return a list of all category DTOs
     */
    List<CategoryDTO> listAdmin();
    
    /**
     * Retrieves a category by its ID for public use.
     *
     * @param id the ID of the category to retrieve
     * @return the category DTO
     * @throws EntityNotFoundException if no category is found with the given ID
     */
    CategoryDTO getCategoryById(Long id);
    
    /**
     * Creates a new category with the provided details.
     * Validates that the category name is unique.
     *
     * @param dto the DTO containing category details
     * @return the created category DTO
     * @throws IllegalStateException if a category with the same name already exists
     */
    CategoryDTO create(CategoryDTO dto);
    
    /**
     * Updates an existing category with the provided details.
     * Validates that the updated category name remains unique.
     *
     * @param id the ID of the category to update
     * @param dto the DTO containing updated category details
     * @return the updated category DTO
     * @throws EntityNotFoundException if no category is found with the given ID
     * @throws IllegalStateException if the update would create a duplicate category name
     * @throws IllegalStateException if trying to deactivate a category that has active breeds or animals
     */
    CategoryDTO update(Long id, CategoryDTO dto);
    
    /**
     * Deletes a category by its ID.
     * Validates that the category is not referenced by any breeds or animals.
     *
     * @param id the ID of the category to delete
     * @throws EntityNotFoundException if no category is found with the given ID
     * @throws IllegalStateException if the category is referenced by breeds or animals
     */
    void delete(Long id);

    /**
     * Retrieves a category by its ID for administrative use.
     * May include additional details not available in the public endpoint.
     *
     * @param id the ID of the category to retrieve
     * @return the category DTO with administrative details
     * @throws EntityNotFoundException if no category is found with the given ID
     */
    CategoryDTO getAdmin(Long id);

}

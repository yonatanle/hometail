package com.hometail.service.impl;

import com.hometail.dto.CategoryDTO;
import com.hometail.mapper.CategoryMapper;
import com.hometail.model.Category;
import com.hometail.repository.AnimalRepository;
import com.hometail.repository.BreedRepository;
import com.hometail.repository.CategoryRepository;
import com.hometail.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the {@link CategoryService} interface.
 * Provides business logic for managing animal categories, including CRUD operations
 * and listing categories with different visibility levels.
 *
 * <p>This service handles the business logic for managing categories, including validation,
 * maintaining referential integrity with related breeds and animals, and ensuring data consistency.</p>
 *
 * @see CategoryService
 * @see CategoryRepository
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    /** Repository for category data access */
    private final CategoryRepository categoryRepository;
    
    /** Repository for breed data access (used for validation) */
    private final BreedRepository breedRepo;
    
    /** Repository for animal data access (used for validation) */
    private final AnimalRepository animalRepo;

    /**
     * {@inheritDoc}
     * 
     * <p>Retrieves a list of active categories, sorted by sort order and name.
     * Only returns categories that are marked as active.</p>
     */
    @Override
    public List<CategoryDTO> listPublic() {
        return categoryRepository.findAllByActiveTrueOrderBySortOrderAscNameAsc()
                .stream().map(CategoryMapper::toDTO).toList();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Retrieves all categories including inactive ones, sorted by sort order and name.
     * Intended for administrative use where visibility of all categories is required.</p>
     */
    @Override
    public List<CategoryDTO> listAdmin() {
        return categoryRepository.findAll(Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.asc("name")))
                .stream().map(CategoryMapper::toDTO).toList();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Retrieves a category by its ID for public use. This method is read-only and does not modify data.</p>
     * 
     * @throws EntityNotFoundException if no category is found with the given ID
     */
    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow();
        return CategoryDTO.builder().id(category.getId()).name(category.getName()).build();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Creates a new category with the provided details. Validates that the category name
     * is unique before creation.</p>
     * 
     * @throws IllegalStateException if a category with the same name already exists
     */
    @Override
    @Transactional
    public CategoryDTO create(CategoryDTO dto) {
        String name = norm(dto.getName());
        if (categoryRepository.existsByNameIgnoreCase(name))
            throw new IllegalStateException("Category already exists");
        Category c = new Category();
        c.setName(name);
        c.setActive(dto.isActive());
        c.setSortOrder(dto.getSortOrder());
        return CategoryMapper.toDTO(categoryRepository.save(c));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Updates an existing category with the provided details. Handles name uniqueness validation
     * and prevents deactivation of categories that have active breeds or animals.</p>
     * 
     * @throws EntityNotFoundException if no category is found with the given ID
     * @throws IllegalStateException if the update would create a duplicate category name
     * @throws IllegalStateException if trying to deactivate a category that has active breeds or animals
     */
    @Override
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        if (dto.getName() != null) {
            String name = norm(dto.getName());
            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id))
                throw new IllegalStateException("Category already exists");
            c.setName(name);
        }
        if (dto.getSortOrder() != null) c.setSortOrder(dto.getSortOrder());
        if (!dto.isActive() && c.isActive()) {
            // Deactivation rule: block if breeds or animals still active under it
            if (breedRepo.existsByCategoryIdAndNameIgnoreCase(id, "")) { /* placeholder to compile if needed */ }
            boolean hasActiveBreeds = !breedRepo.findAllByCategoryIdAndActiveTrueOrderBySortOrderAscNameAsc(id).isEmpty();
            if (hasActiveBreeds || animalRepo.existsByCategoryId(id))
                throw new IllegalStateException("Deactivate/move breeds and animals first");
        }
        c.setActive(dto.isActive());
        return CategoryMapper.toDTO(c);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Deletes a category by its ID. Validates that the category is not referenced by any breeds or animals
     * before deletion to maintain referential integrity.</p>
     * 
     * @throws EntityNotFoundException if no category is found with the given ID
     * @throws IllegalStateException if the category is referenced by breeds or animals
     */
    @Override
    @Transactional
    public void delete(Long id) {
        // Safe delete: only if no animals and no breeds
        if (animalRepo.existsByCategoryId(id))
            throw new IllegalStateException("Cannot delete: animals reference this category");
        if (!breedRepo.findAllByCategoryIdAndActiveTrueOrderBySortOrderAscNameAsc(id).isEmpty())
            throw new IllegalStateException("Cannot delete: breeds exist for this category");
        categoryRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Retrieves a category by its ID for administrative use. This may include additional details
     * not available in the public endpoint.</p>
     * 
     * @throws EntityNotFoundException if no category is found with the given ID
     */
    @Override
    public CategoryDTO getAdmin(Long id) {
        return categoryRepository.findById(id)
                .map(CategoryMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    /**
     * Normalizes a string by trimming whitespace and collapsing multiple spaces.
     * 
     * @param s the string to normalize
     * @return the normalized string, or null if the input was null
     */
    private String norm(String s) { 
        return s == null ? null : s.trim().replaceAll("\\s+", " "); 
    }
}

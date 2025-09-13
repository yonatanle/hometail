package com.hometail.service.impl;

import com.hometail.dto.BreedDTO;
import com.hometail.mapper.BreedMapper;
import com.hometail.model.Breed;
import com.hometail.model.Category;
import com.hometail.repository.AnimalRepository;
import com.hometail.repository.BreedRepository;
import com.hometail.repository.CategoryRepository;
import com.hometail.service.BreedService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of the {@link BreedService} interface.
 * Provides business logic for managing animal breeds, including CRUD operations
 * and listing breeds with different visibility levels.
 *
 * <p>This service handles the business logic for managing breeds, including validation,
 * category association, and ensuring data integrity with related entities.</p>
 *
 * @see BreedService
 * @see BreedRepository
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BreedServiceImpl implements BreedService {
    /** Repository for breed data access */
    private final BreedRepository breedRepo;
    
    /** Repository for category data access */
    private final CategoryRepository categoryRepo;
    
    /** Repository for animal data access (used for validation) */
    private final AnimalRepository animalRepo;

    /**
     * {@inheritDoc}
     * 
     * <p>Retrieves a list of active breeds for a specific category, sorted by sort order and name.
     * Only returns breeds that are marked as active.</p>
     */
    @Override
    public List<BreedDTO> listPublic(Long categoryId) {
        if (categoryId == null) return List.of(); // only load by category for UI
        return breedRepo.findAllByCategoryIdAndActiveTrueOrderBySortOrderAscNameAsc(categoryId)
                .stream().map(BreedMapper::toDto).toList();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Retrieves all breeds for a specific category, including inactive ones.
     * Intended for administrative use where visibility of all breeds is required.</p>
     */
    @Override
    public List<BreedDTO> listAdmin(Long categoryId) {
        return breedRepo.findAllForAdmin(categoryId)
                .stream()
                .map(BreedMapper::toDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Retrieves a breed by its ID. This method is read-only and does not modify data.</p>
     * 
     * @throws EntityNotFoundException if no breed is found with the given ID
     */
    @Override
    public BreedDTO getById(Long id) {
        Breed breed = breedRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Breed not found"));
        return BreedMapper.toDto(breed);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Creates a new breed with the provided details. Validates that the breed name
     * is unique within the specified category before creation.</p>
     * 
     * @throws EntityNotFoundException if the specified category does not exist
     * @throws IllegalStateException if a breed with the same name already exists in the category
     */
    @Override
    @Transactional
    public BreedDTO create(BreedDTO dto) {
        Category cat = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        String name = norm(dto.getName());
        if (breedRepo.existsByCategoryIdAndNameIgnoreCase(cat.getId(), name))
            throw new IllegalStateException("Breed exists in this category");

        Breed b = new Breed();
        b.setCategory(cat);
        b.setName(name);
        b.setActive(dto.isActive());
        b.setSortOrder(dto.getSortOrder());
        return BreedMapper.toDto(breedRepo.save(b));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Updates an existing breed with the provided details. Handles category changes,
     * name uniqueness validation, and prevents deactivation of breeds that are in use.</p>
     * 
     * @throws EntityNotFoundException if the breed or new category is not found
     * @throws IllegalStateException if the update would create a duplicate breed name
     * @throws IllegalStateException if trying to deactivate a breed that is in use by animals
     */
    @Override
    @Transactional
    public BreedDTO update(Long id, BreedDTO dto) {
        Breed b = breedRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Breed not found"));

        if (dto.getCategoryId() != null && !Objects.equals(dto.getCategoryId(), b.getCategory().getId())) {
            Category newCat = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            // uniqueness check in new category
            String nameToCheck = norm(dto.getName() != null ? dto.getName() : b.getName());
            if (breedRepo.existsByCategoryIdAndNameIgnoreCase(newCat.getId(), nameToCheck)
                    && !nameToCheck.equalsIgnoreCase(b.getName())) {
                throw new IllegalStateException("Breed exists in target category");
            }
            b.setCategory(newCat);
        }

        if (dto.getName() != null) {
            String name = norm(dto.getName());
            if (breedRepo.existsByCategoryIdAndNameIgnoreCaseAndIdNot(b.getCategory().getId(), name, id))
                throw new IllegalStateException("Breed exists in this category");
            b.setName(name);
        }

        if (dto.getSortOrder() != null) b.setSortOrder(dto.getSortOrder());

        if (!dto.isActive() && b.isActive()) {
            if (animalRepo.existsByBreedId(b.getId()))
                throw new IllegalStateException("Cannot deactivate: animals reference this breed");
        }
        b.setActive(dto.isActive());

        b = breedRepo.saveAndFlush(b);
        return BreedMapper.toDto(b);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Deletes a breed by its ID. Validates that the breed is not referenced by any animals
     * before deletion to maintain referential integrity.</p>
     * 
     * @throws EntityNotFoundException if no breed is found with the given ID
     * @throws IllegalStateException if the breed is referenced by animals
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (animalRepo.existsByBreedId(id))
            throw new IllegalStateException("Cannot delete: animals reference this breed");
        breedRepo.deleteById(id);
    }

    /**
     * Saves a breed to the database.
     *
     * @param breed the breed to save
     * @return the saved breed
     */
    public Breed save(Breed breed) {
        return breedRepo.save(breed);
    }

    /**
     * Normalizes a breed name by trimming whitespace.
     *
     * @param name the name to normalize
     * @return the normalized name, or null if the input was null
     */
    private String norm(String name) {
        return name == null ? null : name.trim().replaceAll("\\s+"," ");
    }
}

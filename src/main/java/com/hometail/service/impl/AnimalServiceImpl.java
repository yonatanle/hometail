package com.hometail.service.impl;

import com.hometail.dto.AnimalDTO;
import com.hometail.exception.ResourceNotFoundException;
import com.hometail.mapper.AnimalMapper;
import com.hometail.model.*;
import com.hometail.repository.AnimalRepository;
import com.hometail.repository.BreedRepository;
import com.hometail.repository.CategoryRepository;
import com.hometail.repository.UserRepository;
import com.hometail.service.AnimalService;
import com.hometail.spec.AnimalSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link AnimalService} interface.
 * Provides business logic for animal-related operations including CRUD and search functionality.
 *
 * <p>This service handles the business logic for managing animals, including creation, retrieval,
 * updating, and deletion of animal records, as well as searching and filtering capabilities.</p>
 *
 * @see AnimalService
 * @see AnimalRepository
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AnimalServiceImpl implements AnimalService {

    /** Repository for animal data access */
    private final AnimalRepository animalRepository;
    
    /** Repository for user data access */
    private final UserRepository userRepository;
    
    /** Repository for category data access */
    private final CategoryRepository categoryRepository;
    
    /** Repository for breed data access */
    private final BreedRepository breedRepository;

    @Override
    @Transactional
    public AnimalDTO createAnimal(AnimalDTO dto) {
        // Load category (required)
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found: " + dto.getCategoryId()
                ));

        // Load breed (optional)
        Breed breed = null;
        if (dto.getBreedId() != null) {
            breed = breedRepository.findById(dto.getBreedId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Breed not found: " + dto.getBreedId()
                    ));
        }

        // Map DTO -> Entity
        Animal entity = AnimalMapper.toEntity(dto, category, breed);

        // Save
        Animal saved = animalRepository.save(entity);

        // Map back to DTO
        return AnimalMapper.toDTO(saved);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Retrieves an animal by its ID. If the user is not authenticated, some sensitive
     * information may be omitted from the response.</p>
     *
     * @param id the ID of the animal to retrieve
     * @return the animal DTO with appropriate visibility based on authentication status
     * @throws ResourceNotFoundException if no animal is found with the given ID
     */
    @Override
    public AnimalDTO getAnimalById(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean userLoggedIn = (auth != null) &&
                auth.isAuthenticated();

        System.out.println("userLoggedIn: " + userLoggedIn);

        return AnimalMapper.toDTO(animal, userLoggedIn);
    }


    /**
     * Builds a JPA Specification for querying animals based on various criteria.
     * This method combines multiple specifications into a single one using AND conditions.
     *
     * @param q the search query string (searches in name and description)
     * @param categoryId filter by category ID
     * @param breedId filter by breed ID
     * @param gender filter by gender
     * @param size filter by size
     * @param ageGroup filter by age group
     * @param isAdopted filter by adoption status
     * @return a combined Specification object for the query, or null if no criteria were provided
     */
    private Specification<Animal> buildSpec(String q,
                                          Long categoryId,
                                          Long breedId,
                                          Gender gender,
                                          Size size,
                                          AgeGroup ageGroup,
                                          Boolean isAdopted) {

        List<Specification<Animal>> specs = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            specs.add(AnimalSpecs.textSearch(q));
        }

        if (categoryId != null) specs.add(AnimalSpecs.hasCategoryId(categoryId));
        if (breedId    != null) specs.add(AnimalSpecs.hasBreedId(breedId));
        if (gender     != null) specs.add(AnimalSpecs.hasGender(gender));
        if (size       != null) specs.add(AnimalSpecs.hasSize(size));
        if (ageGroup   != null) specs.add(AnimalSpecs.inAgeGroup(ageGroup));
        if (isAdopted  != null) specs.add(AnimalSpecs.isAdopted(isAdopted));

        return specs.stream().reduce(Specification::and).orElse(null);
    }

    @Override
    public Page<Animal> search(String q,
                               Long categoryId,
                               Long breedId,
                               Gender gender,
                               Size size,
                               AgeGroup ageGroup,
                               Boolean isAdopted,
                               Pageable pageable) {

        Specification<Animal> spec = buildSpec(q, categoryId, breedId, gender, size, ageGroup, isAdopted);
        return animalRepository.findAll(spec, pageable);
    }

    @Override
    public List<AnimalDTO> getAllAnimals() {
        return animalRepository.findAll()
                .stream()
                .map(AnimalMapper::toDTO)
                .collect(Collectors.toList());
    }


    /**
     * {@inheritDoc}
     * 
     * <p>Updates an existing animal with the provided details. Validates that the category exists
     * and optionally validates the breed if provided. The breed must belong to the specified category.</p>
     *
     * @param dto the DTO containing animal details to update
     * @return the updated animal as a DTO
     * @throws IllegalArgumentException if the category is not found or the breed doesn't belong to the category
     * @throws UsernameNotFoundException if the current user cannot be found
     * @throws AccessDeniedException if the current user is not authorized to update the animal
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AnimalDTO updateAnimal(AnimalDTO dto) {
        // Current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Load existing animal
        Animal existing = animalRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with ID: " + dto.getId()));

        // Ownership check
        if (!existing.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to update this animal.");
        }

        // Update scalar fields (only if provided)
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getImage() != null) existing.setImage(dto.getImage());

        // Short/long description fields:
        if (dto.getShortDescription() != null) existing.setShortDescription(dto.getShortDescription());
        if (dto.getLongDescription()  != null) existing.setLongDescription(dto.getLongDescription());

        if (dto.getBirthday() != null) existing.setBirthday(dto.getBirthday()); // assuming LocalDate in DTO

        // Update enums (DTO already uses enums — no toUpperCase!)
        if (dto.getGender() != null) existing.setGender(dto.getGender());
        if (dto.getSize()   != null) existing.setSize(dto.getSize());

        // Update relations by ID (load entities)
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.getCategoryId()));
            existing.setCategory(category);
        }

        if (dto.getBreedId() != null) {
            Breed breed = breedRepository.findById(dto.getBreedId())
                    .orElseThrow(() -> new ResourceNotFoundException("Breed not found: " + dto.getBreedId()));
            existing.setBreed(breed);
        }

        // Save & map
        Animal saved = animalRepository.save(existing);
        return AnimalMapper.toDTO(saved); // or animalMapper::toDto if you use a bean
    }


    @Override
    @Transactional
    public void deleteAnimal(Long animalId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // From JWT
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        if (!animal.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this animal.");
        }
        animalRepository.delete(animal); // Cascade delete will handle related requests
    }


    @Override
    public List<AnimalDTO> getAnimalsByOwnerId(Long ownerId) {
        List<Animal> animals = animalRepository.findByOwnerId(ownerId);
        return animals.stream()
                .map(animal -> AnimalMapper.toDTO(animal, true))
                .collect(Collectors.toList());
    }

}

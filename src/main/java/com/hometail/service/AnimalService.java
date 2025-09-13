package com.hometail.service;

import com.hometail.dto.AnimalDTO;
import com.hometail.model.AgeGroup;
import com.hometail.exception.ResourceNotFoundException;
import com.hometail.model.Animal;
import com.hometail.model.Gender;
import com.hometail.model.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * Service interface for managing animal-related operations.
 * Provides methods for CRUD operations and searching of animals.
 *
 * @since 1.0
 */
public interface AnimalService {
    /**
     * Creates a new animal with the provided details.
     *
     * @param dto the DTO containing animal details
     * @return the created animal as a DTO
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    AnimalDTO createAnimal(AnimalDTO dto);
    /**
     * Retrieves an animal by its ID.
     *
     * @param id the ID of the animal to retrieve
     * @return the animal DTO
     * @throws ResourceNotFoundException if no animal is found with the given ID
     */
    AnimalDTO getAnimalById(Long id);
    /**
     * Retrieves all animals in the system.
     *
     * @return a list of all animal DTOs
     */
    List<AnimalDTO> getAllAnimals();
    /**
     * Updates an existing animal with new details.
     *
     * @param dto the DTO containing updated animal details
     * @return the updated animal as a DTO
     * @throws ResourceNotFoundException if no animal is found with the given ID
     * @throws AccessDeniedException if the user is not authorized to update the animal
     */
    AnimalDTO updateAnimal(AnimalDTO dto);
    /**
     * Deletes an animal by its ID.
     *
     * @param id the ID of the animal to delete
     * @throws ResourceNotFoundException if no animal is found with the given ID
     * @throws AccessDeniedException if the user is not authorized to delete the animal
     */
    void deleteAnimal(Long id);

    /**
     * Retrieves all animals owned by a specific user.
     *
     * @param ownerId the ID of the owner
     * @return a list of animal DTOs owned by the specified user
     */
    List<AnimalDTO> getAnimalsByOwnerId(Long ownerId);
    /**
     * Searches for animals based on various criteria with pagination support.
     *
     * @param q the search query string (searches in name and description)
     * @param categoryId filter by category ID
     * @param breedId filter by breed ID
     * @param gender filter by gender
     * @param size filter by size
     * @param ageGroup filter by age group
     * @param isAdopted filter by adoption status
     * @param pageable pagination information
     * @return a page of animals matching the criteria
     */
    Page<Animal> search(String q, Long categoryId,
                       Long breedId,
                       Gender gender,
                       Size size,
                       AgeGroup ageGroup,
                       Boolean isAdopted,
                       Pageable pageable);
}

package com.hometail.service;

import com.hometail.dto.AdoptionRequestDTO;
import com.hometail.exception.AnimalAlreadyAdoptedException;
import com.hometail.exception.DuplicateRequestException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Service interface for managing adoption requests in the system.
 * Provides methods for creating, retrieving, updating, and deleting adoption requests,
 * as well as querying requests based on various criteria.
 */
public interface AdoptionRequestService {
    /**
     * Creates a new adoption request.
     *
     * @param dto the adoption request data transfer object containing request details
     * @return the created adoption request DTO
     * @throws AnimalAlreadyAdoptedException if the animal is already adopted
     * @throws DuplicateRequestException if a duplicate request exists for the same animal
     * @throws IllegalArgumentException if the requester is the owner of the animal
     * @throws jakarta.persistence.EntityNotFoundException if the animal or requester is not found
     */
    AdoptionRequestDTO createAdoptionRequest(AdoptionRequestDTO dto);
    /**
     * Retrieves all adoption requests in the system.
     * 
     * @return a list of all adoption request DTOs
     */
    List<AdoptionRequestDTO> getAllRequests();
    /**
     * Retrieves all adoption requests for a specific animal.
     * 
     * @param animalId the ID of the animal
     * @return a list of adoption request DTOs for the specified animal
     */
    List<AdoptionRequestDTO> getRequestsByAnimalId(Long animalId);
    /**
     * Retrieves all adoption requests made by a specific user.
     * 
     * @param requesterId the ID of the user who made the requests
     * @return a list of adoption request DTOs made by the specified user
     */
    List<AdoptionRequestDTO> getRequestsByRequesterId(Long requesterId);
    /**
     * Retrieves all adoption requests for animals owned by a specific user.
     * 
     * @param ownerId the ID of the animal owner
     * @return a list of adoption request DTOs for the owner's animals
     */
    List<AdoptionRequestDTO> getRequestsForOwnerAnimals(Long ownerId);
    /**
     * Retrieves all adoption requests for a specific animal owned by a specific user.
     * 
     * @param ownerId the ID of the animal owner
     * @param animalId the ID of the animal
     * @return a list of adoption request DTOs for the specified animal
     * @throws org.springframework.security.access.AccessDeniedException if the user doesn't own the animal
     */
    List<AdoptionRequestDTO> getRequestsForSpecificAnimalByOwner(Long ownerId, Long animalId);
    /**
     * Updates the status of an adoption request.
     * 
     * @param id the ID of the adoption request to update
     * @param status the new status (must be 'APPROVED' or 'REJECTED')
     * @return the updated adoption request DTO
     * @throws jakarta.persistence.EntityNotFoundException if the request is not found
     * @throws IllegalArgumentException if the status is invalid
     * @throws IllegalStateException if the request is not in PENDING state
     */
    AdoptionRequestDTO updateStatus(Long id, String status);
    /**
     * Deletes an adoption request by ID.
     * 
     * @param id the ID of the adoption request to delete
     * @throws jakarta.persistence.EntityNotFoundException if the request is not found
     */
    void deleteAdoptionRequest(Long id);
    /**
     * Updates the note of an adoption request.
     * 
     * @param id the ID of the adoption request to update
     * @param note the new note text (must not be blank and not exceed 500 characters)
     * @return the updated adoption request DTO
     * @throws jakarta.persistence.EntityNotFoundException if the request is not found
     * @throws IllegalStateException if the request is not in PENDING state
     * @throws AnimalAlreadyAdoptedException if the animal is already adopted
     */
    AdoptionRequestDTO updateNote(
            Long id, 
            @NotBlank(message = "Note must not be blank") 
            @Size(max = 500, message = "Note must not exceed 500 characters") 
            String note
    );
    /**
     * Retrieves an adoption request by ID.
     * 
     * @param id the ID of the adoption request to retrieve
     * @return the adoption request DTO
     * @throws jakarta.persistence.EntityNotFoundException if the request is not found
     */
    AdoptionRequestDTO getById(Long id);
    /**
     * Counts the number of adoption requests for a specific animal with a given status.
     * 
     * @param animalId the ID of the animal
     * @param status the status to filter by (case-insensitive)
     * @return the count of matching adoption requests
     */
    long countByAnimalAndStatus(Long animalId, String status);
}
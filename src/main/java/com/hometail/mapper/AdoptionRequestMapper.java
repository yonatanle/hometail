package com.hometail.mapper;

import com.hometail.dto.AdoptionRequestDTO;
import com.hometail.model.AdoptionRequest;
import com.hometail.model.Animal;
import com.hometail.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between {@link AdoptionRequest} entities and {@link AdoptionRequestDTO} objects.
 * Handles the transformation of data between the persistence layer and the API layer.
 *
 * @see AdoptionRequest
 * @see AdoptionRequestDTO
 * @since 1.0
 */
@Component
public class AdoptionRequestMapper {

    /**
     * Converts an {@link AdoptionRequest} entity to an {@link AdoptionRequestDTO}.
     * This method includes detailed information about the animal, owner, and requester
     * to provide a complete view of the adoption request.
     *
     * @param request the adoption request entity to convert, can be null
     * @return the corresponding DTO, or null if the input is null
     * @throws NullPointerException if the request's animal, owner, or category is null
     */
    public static AdoptionRequestDTO toDTO(AdoptionRequest request) {
        if (request == null) {
            return null;
        }

        Animal animal = request.getAnimal();
        User owner = animal.getOwner();
        User requester = request.getRequester();

        return AdoptionRequestDTO.builder()
                .id(request.getId())
                // Animal details
                .animalId(animal.getId())
                .animalName(animal.getName())
                .animalCategory(animal.getCategory().getName())
                .animal(AnimalMapper.toDTO(animal, true))
                
                // Owner details
                .ownerName(owner.getFullName())
                .animalOwnerId(owner.getId())
                
                // Requester details
                .requesterId(requester.getId())
                .requesterName(requester.getFullName())
                .requesterEmail(requester.getEmail())
                .requesterPhone(requester.getPhoneNumber())
                
                // Request details
                .note(request.getNote())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .decisionAt(request.getDecisionAt())
                .build();
    }

    /**
     * Converts an {@link AdoptionRequestDTO} to an {@link AdoptionRequest} entity.
     * This method creates a new entity with the provided DTO data and associated entities.
     *
     * @param dto the DTO to convert, must not be null
     * @param animal the animal associated with the request, must not be null
     * @param requester the user making the request, must not be null
     * @return a new AdoptionRequest entity populated with the DTO data
     * @throws NullPointerException if dto, animal, or requester is null
     */
    public static AdoptionRequest toEntity(AdoptionRequestDTO dto, Animal animal, User requester) {
        if (dto == null || animal == null || requester == null) {
            throw new NullPointerException("DTO, animal, and requester must not be null");
        }

        return AdoptionRequest.builder()
                .id(dto.getId())
                .animal(animal)
                .requester(requester)
                .note(dto.getNote())
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .decisionAt(dto.getDecisionAt())
                .build();
    }
}

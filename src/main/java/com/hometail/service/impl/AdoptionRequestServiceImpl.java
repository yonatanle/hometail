package com.hometail.service.impl;

import com.hometail.dto.AdoptionRequestDTO;
import com.hometail.exception.AnimalAlreadyAdoptedException;
import com.hometail.mapper.AdoptionRequestMapper;
import com.hometail.model.AdoptionRequest;
import com.hometail.model.Animal;
import com.hometail.model.RequestStatus;
import com.hometail.model.User;
import com.hometail.repository.AdoptionRequestRepository;
import com.hometail.repository.AnimalRepository;
import com.hometail.repository.UserRepository;
import com.hometail.service.AdoptionRequestService;
import com.hometail.exception.DuplicateRequestException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link AdoptionRequestService} interface.
 * Provides business logic for managing adoption requests including creation,
 * retrieval, updating, and deletion of requests with proper validation and
 * transaction management.
 */
@Transactional
@Service
public class AdoptionRequestServiceImpl implements AdoptionRequestService {

    /** Repository for adoption request data access */
    private final AdoptionRequestRepository repository;
    
    /** Repository for animal data access */
    private final AnimalRepository animalRepository;
    
    /** Repository for user data access */
    private final UserRepository userRepository;


    /**
     * Constructs a new AdoptionRequestServiceImpl with the required repositories.
     *
     * @param adoptionRequestRepository the adoption request repository
     * @param animalRepository the animal repository
     * @param userRepository the user repository
     */
    @Autowired
    public AdoptionRequestServiceImpl(AdoptionRequestRepository adoptionRequestRepository,
                                     AnimalRepository animalRepository,
                                     UserRepository userRepository) {
        this.repository = adoptionRequestRepository;
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Constructs a new AdoptionRequestServiceImpl with only the required repository.
     * Primarily used for testing purposes.
     *
     * @param repository the adoption request repository
     */
    public AdoptionRequestServiceImpl(AdoptionRequestRepository repository) {
        this.repository = repository;
        this.animalRepository = null;
        this.userRepository = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @return a list of all adoption request DTOs
     */
    @Override
    public List<AdoptionRequestDTO> getAllRequests() {
        return repository.findAll()
                .stream()
                .map(AdoptionRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * 
     * @param requesterId the ID of the requester
     * @return a list of adoption request DTOs made by the specified user
     */
    @Override
    public List<AdoptionRequestDTO> getRequestsByRequesterId(Long requesterId) {
        List<AdoptionRequest> requests = repository.findByRequesterId(requesterId);
        return requests.stream()
                .map(AdoptionRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * 
     * @param dto the adoption request data transfer object
     * @return the created adoption request DTO
     * @throws EntityNotFoundException if the animal or requester is not found
     * @throws AnimalAlreadyAdoptedException if the animal is already adopted
     * @throws AccessDeniedException if the requester is the owner of the animal
     * @throws DuplicateRequestException if a duplicate open request exists
     */
    @Override
    @Transactional
    public AdoptionRequestDTO createAdoptionRequest(AdoptionRequestDTO dto) {
        // Validate note is not blank or empty
        if (dto.getNote() == null || dto.getNote().trim().isEmpty()) {
            throw new IllegalArgumentException("Note cannot be blank or empty");
        }
        
        // Validate and load required entities
        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Animal not found with id: " + dto.getAnimalId()));

        User requester = userRepository.findById(dto.getRequesterId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + dto.getRequesterId()));

        // Business rule: Cannot request an already adopted animal
        if (animal.isAdopted()) {
            throw new AnimalAlreadyAdoptedException("This animal is already adopted.");
        }

        // Business rule: Cannot request your own animal
        if (animal.getOwner() != null && Objects.equals(animal.getOwner().getId(), requester.getId())) {
            throw new AccessDeniedException("You cannot create a request for your own animal.");
        }

        // Business rule: Prevent duplicate open requests
        boolean duplicateOpen = repository.existsByAnimalIdAndRequesterIdAndStatusIn(
                dto.getAnimalId(),
                dto.getRequesterId(),
                List.of(RequestStatus.PENDING, RequestStatus.APPROVED));
        if (duplicateOpen) {
            throw new DuplicateRequestException("You already have an active request for this animal.");
        }

        // Create and persist the new adoption request
        AdoptionRequest request = AdoptionRequestMapper.toEntity(dto, animal, requester);
        request.setId(null);                        // Ensure it's a new entity
        request.setStatus(RequestStatus.PENDING);   // New requests are always PENDING
        request.setCreatedAt(LocalDateTime.now());
        request.setDecisionAt(null);

        AdoptionRequest saved = repository.saveAndFlush(request);
        return AdoptionRequestMapper.toDTO(saved);
    }

    /**
     * {@inheritDoc}
     * 
     * @param animalId the ID of the animal
     * @return a list of adoption request DTOs for the specified animal
     */
    @Override
    public List<AdoptionRequestDTO> getRequestsByAnimalId(Long animalId) {
        return repository.findByAnimalId(animalId)
                .stream()
                .map(AdoptionRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * 
     * @param ownerId the ID of the animal owner
     * @return a list of adoption request DTOs for the owner's animals
     */
    @Override
    public List<AdoptionRequestDTO> getRequestsForOwnerAnimals(Long ownerId) {
        List<AdoptionRequest> requests = repository.findByAnimalOwnerId(ownerId);
        return requests.stream()
                .map(AdoptionRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * 
     * @param ownerId the ID of the animal owner
     * @param animalId the ID of the animal
     * @return a list of adoption request DTOs for the specified animal
     * @throws IllegalArgumentException if the animal is not found
     * @throws AccessDeniedException if the user doesn't own the animal
     */
    @Override
    public List<AdoptionRequestDTO> getRequestsForSpecificAnimalByOwner(Long ownerId, Long animalId) {
        // Verify the animal exists and belongs to the specified owner
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new IllegalArgumentException("Animal not found"));

        if (animal.getOwner() == null || !animal.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You do not own this animal");
        }

        // Return all requests for this animal
        return repository.findByAnimalId(animalId)
                .stream()
                .map(AdoptionRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * 
     * @param id the ID of the adoption request to update
     * @param status the new status (must be 'APPROVED' or 'REJECTED')
     * @return the updated adoption request DTO
     * @throws EntityNotFoundException if the request is not found
     * @throws IllegalArgumentException if the status is invalid
     * @throws IllegalStateException if the request is not in PENDING state
     */
    @Override
    @Transactional
    public AdoptionRequestDTO updateStatus(Long id, String status) {
        // Load and validate the request
        AdoptionRequest request = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        // Validate the new status
        RequestStatus newStatus = RequestStatus.valueOf(status.toUpperCase());
        if (newStatus != RequestStatus.APPROVED && newStatus != RequestStatus.REJECTED) {
            throw new IllegalArgumentException("Status must be either APPROVED or REJECTED");
        }
        
        // Ensure the request is in a valid state for this operation
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved or rejected.");
        }

        // Update the request status and decision timestamp
        request.setStatus(newStatus);
        request.setDecisionAt(LocalDateTime.now());
        repository.save(request);

        // If approved, handle additional business logic
        if (newStatus == RequestStatus.APPROVED) {
            // Reject all other pending requests for this animal
            repository.rejectOtherRequests(request.getAnimal().getId(), request.getId());
            
            // Mark the animal as adopted
            Animal animal = request.getAnimal();
            animal.setAdopted(true);
            animalRepository.save(animal);
        }
        
        return AdoptionRequestMapper.toDTO(request);
    }

    /**
     * {@inheritDoc}
     * 
     * @param requestId the ID of the adoption request to update
     * @param newNote the new note text
     * @return the updated adoption request DTO
     * @throws IllegalArgumentException if the request is not found
     * @throws IllegalStateException if the request is not in PENDING state
     * @throws AnimalAlreadyAdoptedException if the animal is already adopted
     */
    @Transactional
    @Override
    public AdoptionRequestDTO updateNote(Long requestId, String newNote) {
        // Validate note is not blank or empty
        if (newNote == null || newNote.trim().isEmpty()) {
            throw new IllegalArgumentException("Note cannot be blank or empty");
        }
        
        // Load and validate the request
        AdoptionRequest request = repository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
     
        // Business rule: Only allow updates to pending requests
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException(
                "Cannot update note. Only pending requests can be modified.");
        }

        // Business rule: Cannot update notes for adopted animals
        if (request.getAnimal().isAdopted()) {
            throw new AnimalAlreadyAdoptedException("This animal is already adopted.");
        }
        
        // Update the note and save
        request.setNote(newNote);
        AdoptionRequest saved = repository.save(request);
        return AdoptionRequestMapper.toDTO(saved);
    }

    /**
     * {@inheritDoc}
     * 
     * @param id the ID of the adoption request to retrieve
     * @return the adoption request DTO
     * @throws EntityNotFoundException if the request is not found
     */
    @Override
    public AdoptionRequestDTO getById(Long id) {
        AdoptionRequest request = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Adoption request not found with id: " + id));

        return AdoptionRequestMapper.toDTO(request);
    }

    /**
     * {@inheritDoc}
     * 
     * @param id the ID of the adoption request to delete
     * @throws IllegalArgumentException if the ID is null
     */
    @Override
    public void deleteAdoptionRequest(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Adoption request ID must not be null");
        }
        repository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     * 
     * @param animalId the ID of the animal
     * @param status the status to filter by (case-insensitive)
     * @return the count of matching adoption requests
     * @throws IllegalArgumentException if the status is invalid
     */
    public long countByAnimalAndStatus(Long animalId, String status) {
        try {
            return repository.countByAnimalIdAndStatus(animalId, 
                RequestStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status, e);
        }
    }
}
package com.hometail.controller;

import com.hometail.dto.AdoptionRequestDTO;
import com.hometail.model.User;
import com.hometail.repository.AdoptionRequestRepository;
import com.hometail.repository.AnimalRepository;
import com.hometail.repository.UserRepository;
import com.hometail.service.AdoptionRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for handling adoption request related operations.
 * All endpoints require authentication.
 * Base URL: /api/adoption-requests
 */
@PreAuthorize("isAuthenticated()")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/adoption-requests")
public class AdoptionRequestController {
    // Service layer for adoption request business logic
    private final AdoptionRequestService service;
    
    // Repository for user data access
    private final UserRepository userRepository;
    
    // Repository for animal data access
    private final AnimalRepository animalRepository;
    
    // Repository for adoption request data access
    private final AdoptionRequestRepository adoptionRequestRepository;


    /**
     * Creates a new adoption request.
     *
     * @param dto The adoption request data transfer object containing request details
     * @param userDetails The authenticated user details
     * @return The created adoption request DTO
     * @throws UsernameNotFoundException if the authenticated user is not found
     */
    @PostMapping
    public AdoptionRequestDTO create(
            @RequestBody @Valid AdoptionRequestDTO dto, 
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get the authenticated user and set them as the requester
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        dto.setRequesterId(user.getId());
        
        // Delegate to service layer to handle business logic
        return service.createAdoptionRequest(dto);
    }

    /**
     * Retrieves all adoption requests (Admin only).
     * 
     * @return List of all adoption request DTOs
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AdoptionRequestDTO>> getAll() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    /**
     * Retrieves all adoption requests for a specific animal.
     * 
     * @param animalId The ID of the animal
     * @return List of adoption request DTOs for the specified animal
     */
    @GetMapping("/animal/{animalId}")
    public List<AdoptionRequestDTO> getByAnimal(@PathVariable Long animalId) {
        return service.getRequestsByAnimalId(animalId);
    }


    /**
     * Updates the status of an adoption request (APPROVED/REJECTED).
     * Only the animal owner can update the status.
     * 
     * @param id The ID of the adoption request to update
     * @param status The new status (must be 'APPROVED' or 'REJECTED')
     * @param userDetails The authenticated user details
     * @return The updated adoption request DTO, or error response
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Normalize and validate status
        String normalizedStatus = status.trim().toUpperCase();
        if (!List.of("APPROVED", "REJECTED").contains(normalizedStatus)) {
            return ResponseEntity.badRequest()
                    .body("Invalid status. Must be APPROVED or REJECTED.");
        }

        // Get the authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the request exists
        AdoptionRequestDTO existing = service.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // Verify the user is the owner of the animal
        if (!existing.getAnimalOwnerId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to update this request.");
        }

        // Update the status through the service layer
        AdoptionRequestDTO updated = service.updateStatus(id, normalizedStatus);
        return ResponseEntity.ok(updated);
    }


    /**
     * Deletes an adoption request.
     * Only the requester or an admin can delete the request.
     * 
     * @param id The ID of the adoption request to delete
     */
    @PreAuthorize("@adoptionRequestSecurity.isRequester(#id, authentication.name)")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteAdoptionRequest(id);
    }

    /**
     * Retrieves all adoption requests made by the authenticated user.
     * 
     * @param userDetails The authenticated user details
     * @return List of adoption request DTOs made by the user
     */
    @GetMapping("/my-requests")
    public ResponseEntity<List<AdoptionRequestDTO>> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
            
        // Get the authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Get requests made by this user
        List<AdoptionRequestDTO> requests = service.getRequestsByRequesterId(user.getId());
        return ResponseEntity.ok(requests);
    }

    /**
     * Retrieves all adoption requests for animals owned by the authenticated user.
     * 
     * @param userDetails The authenticated user details
     * @return List of adoption request DTOs for the user's animals
     */
    @GetMapping("/requests-for-my-animals")
    public ResponseEntity<List<AdoptionRequestDTO>> getRequestsForMyAnimals(
            @AuthenticationPrincipal UserDetails userDetails) {
                
        // Get the authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Get requests for animals owned by this user
        List<AdoptionRequestDTO> requests = service.getRequestsForOwnerAnimals(user.getId());
        return ResponseEntity.ok(requests);
    }

    /**
     * Retrieves all adoption requests for a specific animal owned by the authenticated user.
     * 
     * @param animalId The ID of the animal
     * @param userDetails The authenticated user details
     * @return List of adoption request DTOs for the specified animal
     */
    @GetMapping("/requests-for-my-animal/{animalId}")
    public ResponseEntity<List<AdoptionRequestDTO>> getRequestsForMyAnimal(
            @PathVariable Long animalId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Get the authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                
        // Get requests for the specific animal that this user owns
        List<AdoptionRequestDTO> requests = service.getRequestsForSpecificAnimalByOwner(
            user.getId(), 
            animalId
        );
        return ResponseEntity.ok(requests);
    }


    /**
     * Cancels an adoption request.
     * Only the original requester can cancel their own request.
     * 
     * @param id The ID of the adoption request to cancel
     * @return No content response on success
     */
    @PreAuthorize("@adoptionRequestSecurity.isRequester(#id, authentication.name)")
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelRequest(@PathVariable Long id) {
        // Delete the request (could be implemented as soft delete)
        service.deleteAdoptionRequest(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the note of an adoption request.
     * Only the original requester can update the note.
     * 
     * @param id The ID of the adoption request to update
     * @param body Request body containing the new note
     * @return The updated adoption request DTO
     */
    @PreAuthorize("@adoptionRequestSecurity.isRequester(#id, authentication.name)")
    @PutMapping("/{id}/note")
    public ResponseEntity<AdoptionRequestDTO> updateRequest(
            @PathVariable Long id,
            @RequestBody Map<String,String> body) {
                
        // Extract and trim the note from the request body
        String newNote = body.getOrDefault("note", "").trim();
        
        // Update the note through the service layer
        AdoptionRequestDTO updated = service.updateNote(id, newNote);
        return ResponseEntity.ok(updated);
    }

    /**
     * Gets the count of pending adoption requests for a specific animal.
     * 
     * @param animalId The ID of the animal
     * @param userDetails The authenticated user details
     * @return The count of pending requests for the specified animal
     */
    @GetMapping("/animal/{animalId}/pending/count")
    public ResponseEntity<Long> getPendingCount(
            @PathVariable Long animalId,
            @AuthenticationPrincipal UserDetails userDetails) {
                
        // Note: Could add verification that the user owns the animal
        long count = service.countByAnimalAndStatus(animalId, "PENDING");
        return ResponseEntity.ok(count);
    }

}

package com.hometail.repository;

import com.hometail.model.AdoptionRequest;
import com.hometail.model.RequestStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * Repository interface for managing {@link AdoptionRequest} entities.
 * Provides CRUD operations and custom queries for adoption requests.
 */
@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {
    /**
     * Finds all adoption requests made by a specific user.
     *
     * @param userId the ID of the user who made the requests
     * @return a list of adoption requests made by the specified user
     */
    List<AdoptionRequest> findByRequesterId(Long userId);
    /**
     * Finds all adoption requests for a specific animal.
     *
     * @param animalId the ID of the animal
     * @return a list of adoption requests for the specified animal
     */
    List<AdoptionRequest> findByAnimalId(Long animalId);
    /**
     * Finds all adoption requests for animals owned by a specific user.
     *
     * @param ownerId the ID of the animal owner
     * @return a list of adoption requests for the owner's animals
     */
    List<AdoptionRequest> findByAnimalOwnerId(Long ownerId);
    /**
     * Rejects all pending adoption requests for an animal except the approved one.
     * This is typically called when an adoption request is approved to automatically
     * reject other pending requests for the same animal.
     *
     * @param animalId the ID of the animal
     * @param approvedRequestId the ID of the approved request (this one won't be rejected)
     */
    @Modifying
    @Query("UPDATE AdoptionRequest r SET r.status = 'REJECTED', r.decisionAt = CURRENT_TIMESTAMP " +
            "WHERE r.animal.id = :animalId AND r.id <> :approvedRequestId AND r.status = 'PENDING'")
    void rejectOtherRequests(@Param("animalId") Long animalId, @Param("approvedRequestId") Long approvedRequestId);
    /**
     * Checks if an adoption request exists for the specified animal and requester.
     *
     * @param animalId the ID of the animal
     * @param requesterId the ID of the requester
     * @return true if a request exists, false otherwise
     */
    boolean existsByAnimalIdAndRequesterId(Long animalId, Long requesterId);
    /**
     * Counts the number of adoption requests for a specific animal with the given status.
     *
     * @param animalId the ID of the animal
     * @param status the status to filter by
     * @return the count of matching adoption requests
     */
    long countByAnimalIdAndStatus(@Param("animalId") Long animalId, RequestStatus status);

    /**
     * Checks if an adoption request exists for the specified animal and requester with any of the given statuses.
     *
     * @param animalId the ID of the animal (must not be null)
     * @param requesterId the ID of the requester
     * @param statuses the list of statuses to check for
     * @return true if a matching request exists, false otherwise
     * @throws jakarta.validation.ConstraintViolationException if animalId is null
     */
    boolean existsByAnimalIdAndRequesterIdAndStatusIn(
            @NotNull(message = "Animal ID must not be null") Long animalId, 
            Long requesterId, 
            List<RequestStatus> statuses
    );
    
    /**
     * Retrieves an adoption request by its ID with the requester and animal owner eagerly loaded.
     *
     * @param id the ID of the adoption request
     * @return an Optional containing the adoption request if found, empty otherwise
     */
    @Query("SELECT r FROM AdoptionRequest r " +
           "LEFT JOIN FETCH r.requester " +
           "LEFT JOIN FETCH r.animal a " +
           "LEFT JOIN FETCH a.owner " +
           "WHERE r.id = :id")
    Optional<AdoptionRequest> findByIdWithRequesterAndAnimalOwner(@Param("id") Long id);
}

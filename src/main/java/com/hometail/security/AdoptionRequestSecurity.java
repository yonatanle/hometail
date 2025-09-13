package com.hometail.security;

import com.hometail.model.User;
import com.hometail.repository.AdoptionRequestRepository;
import com.hometail.repository.AnimalRepository;
import com.hometail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Security component for handling authorization checks related to adoption requests.
 * Provides methods to verify user permissions for adoption request operations.
 *
 * <p>This component ensures that users can only access and modify adoption requests
 * that they are authorized to, either as the requester or as the animal owner.</p>
 *
 * @see com.hometail.repository.AdoptionRequestRepository
 * @see com.hometail.repository.AnimalRepository
 * @see com.hometail.repository.UserRepository
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class AdoptionRequestSecurity {

    /** Repository for accessing adoption request data */
    private final AdoptionRequestRepository adoptionRequestRepository;
    
    /** Repository for accessing animal data */
    private final AnimalRepository animalRepository;
    
    /** Repository for accessing user data */
    private final UserRepository userRepository;

    /**
     * Checks if the user with the given email is the requester of the specified adoption request.
     *
     * @param requestId the ID of the adoption request to check
     * @param email the email of the user to verify
     * @return true if the user is the requester of the adoption request, false otherwise
     *         or if the request or user is not found
     */
    public boolean isRequester(Long requestId, String email) {
        return adoptionRequestRepository.findById(requestId)
                .map(request -> {
                    Long requesterId = request.getRequester().getId();
                    return userRepository.findByEmail(email)
                            .map(user -> user.getId().equals(requesterId))
                            .orElse(false);
                }).orElse(false);
    }

    /**
     * Checks if the user with the given email is the owner of the specified animal.
     *
     * @param animalId the ID of the animal to check ownership for
     * @param email the email of the user to verify
     * @return true if the user is the owner of the animal, false otherwise
     *         or if the animal has no owner or the user is not found
     */
    public boolean isOwnerOfAnimal(Long animalId, String email) {
        return animalRepository.findById(animalId)
                .map(animal -> {
                    User owner = animal.getOwner();
                    if (owner == null) {
                        return false;
                    }
                    return userRepository.findByEmail(email)
                            .map(user -> user.getId().equals(owner.getId()))
                            .orElse(false);
                }).orElse(false);
    }

}

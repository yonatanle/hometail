package com.hometail.controller;

import com.hometail.dto.AnimalDTO;
import com.hometail.mapper.AnimalMapper;
import com.hometail.model.AgeGroup;
import com.hometail.model.Gender;
import com.hometail.model.Size;
import com.hometail.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing animal-related operations.
 * <p>
 * This controller handles CRUD operations for animals, including creating, retrieving,
 * updating, and deleting animal records. It also supports searching with various
 * filters and pagination.
 *
 * All endpoints are prefixed with "/api/animals".
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/animals")
public class AnimalController {

    /**
     * Service responsible for handling animal-related business logic.
     */
    private final AnimalService animalService;
    
    /**
     * Directory where animal images are stored.
     */
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * Creates a new animal record with an optional image.
     * <p>
     * This endpoint accepts multipart form data containing the animal details as JSON
     * and an optional image file. If an image is provided, it will be saved to the
     * server and the path will be stored with the animal record.
     *
     * @param dto The animal data transfer object containing animal details
     * @param imageFile Optional image file to be associated with the animal
     * @return ResponseEntity containing the created AnimalDTO with HTTP 200 status on success,
     *         or HTTP 500 status if there was an error processing the image
     * @throws IOException if there is an error saving the uploaded file
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnimalDTO> create(
            @RequestPart("animal") @Valid AnimalDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadDir = System.getProperty("user.dir") + "/uploads/";
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                dto.setImage("/uploads/" + fileName);
            }
            AnimalDTO result = animalService.createAnimal(dto);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Searches for animals based on various filter criteria with pagination support.
     * <p>
     * Example usage:
     * {@code GET /api/animals?categoryId=1&breedId=42&gender=FEMALE&size=SMALL&ageGroup=YOUNG&page=0&size=20&sort=createdAt,desc}
     *
     * @param q Optional search query string for full-text search
     * @param categoryId Optional category ID to filter by
     * @param breedId Optional breed ID to filter by
     * @param gender Optional gender to filter by
     * @param size Optional size to filter by
     * @param ageGroup Optional age group to filter by
     * @param adopted Optional boolean to filter by adoption status
     * @param pageable Pagination and sorting configuration (default: 10 items per page, sorted by id descending)
     * @return Page of AnimalDTOs matching the criteria
     */
    @GetMapping
    public ResponseEntity<Page<AnimalDTO>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long breedId,
            @RequestParam(required = false) Gender gender,
            @RequestParam(name = "animalSize", required = false) Size size,
            @RequestParam(required = false) AgeGroup ageGroup,
            @RequestParam(required = false) Boolean adopted,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<AnimalDTO> page = animalService
                .search(q, categoryId, breedId, gender, size, ageGroup, adopted, pageable)
                .map(AnimalMapper::toDTO);
        return ResponseEntity.ok(page);
    }

    /**
     * Retrieves all animals owned by a specific user.
     * <p>
     * This endpoint is typically used for "My Animals" functionality where a user
     * wants to see all animals they have listed.
     *
     * @param ownerId The ID of the owner whose animals to retrieve
     * @return List of AnimalDTOs belonging to the specified owner
     */
    @GetMapping("/by-owner/{ownerId}")
    public ResponseEntity<List<AnimalDTO>> getAnimalsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(animalService.getAnimalsByOwnerId(ownerId));
    }

    /**
     * Retrieves a single animal by its ID.
     *
     * @param id The ID of the animal to retrieve
     * @return AnimalDTO with the specified ID
     * @throws jakarta.persistence.EntityNotFoundException if no animal exists with the given ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnimalDTO> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(animalService.getAnimalById(id));
    }

    /**
     * Updates an existing animal record.
     * <p>
     * This endpoint allows updating animal details and optionally replacing the animal's image.
     * The ID in the path takes precedence over any ID in the DTO.
     *
     * @param id The ID of the animal to update (from path variable)
     * @param dto The updated animal data
     * @param imageFile Optional new image file for the animal
     * @return ResponseEntity containing the updated AnimalDTO
     * @throws IOException if there is an error processing the image file
     * @throws jakarta.persistence.EntityNotFoundException if no animal exists with the given ID
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnimalDTO> update(
            @PathVariable Long id,
            @RequestPart("animal") @Valid AnimalDTO dto,
            @RequestPart(name = "image", required = false) MultipartFile imageFile) throws IOException {
        try {
            // Ensure the ID from the path takes precedence
        dto.setId(id);
        
        // Process new image if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            // Generate unique filename to prevent collisions
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR, fileName);
            
            // Ensure upload directory exists
            Files.createDirectories(filePath.getParent());
            
            // Save the new image
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Update DTO with the relative path to the new image
            dto.setImage("/" + UPLOAD_DIR + fileName);
            
            // Note: Old image file is not automatically deleted to prevent issues with concurrent requests
            // Consider implementing a cleanup mechanism if storage space is a concern
        }
            AnimalDTO updated = animalService.updateAnimal(dto);
            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Deletes an animal record by its ID.
     * <p>
     * This is a hard delete operation. The animal record will be permanently removed
     * from the database. Associated image files are not automatically deleted.
     *
     * @param id The ID of the animal to delete
     * @return ResponseEntity with no content (HTTP 204) on success
     * @throws jakarta.persistence.EntityNotFoundException if no animal exists with the given ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return ResponseEntity.noContent().build();
    }
}
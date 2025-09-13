package com.hometail.controller;

import com.hometail.dto.BreedDTO;
import com.hometail.service.BreedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin controller for managing animal breeds.
 * <p>
 * Provides REST endpoints for CRUD operations on animal breeds, accessible only by administrators.
 * This controller handles operations such as listing, retrieving, creating, updating, and deleting breeds.
 *
 * @version 1.0
 * @see BreedService
 * @see BreedDTO
 */
@Tag(name = "Breed Administration", description = "Endpoints for managing animal breeds (Admin only)")
@RestController
@RequestMapping("/api/admin/breeds")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class BreedAdminController {

    private final BreedService breedService;

    /**
     * Admin list: all breeds.
     * Optional filter by categoryId to narrow the list in admin UI.
     */
    /**
     * Retrieves a list of all animal breeds, optionally filtered by category.
     *
     * @param categoryId Optional category ID to filter breeds by category
     * @return List of {@link BreedDTO} objects containing breed information
     */
    @Operation(
            summary = "List all breeds",
            description = "Retrieves a list of all animal breeds, optionally filtered by category ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of breeds",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = BreedDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Admin role required"
                    )
            }
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BreedDTO> list(
            @Parameter(description = "Filter breeds by category ID (optional)", example = "1")
            @RequestParam(required = false) Long categoryId) {
//        if (categoryId == null) {
//            return breedService.listAdmin();
//        }
        return breedService.listAdmin(categoryId);
    }

    /**
     * Creates a new animal breed with the provided details.
     *
     * @param breedDTO The breed data transfer object containing breed information
     * @return The created {@link BreedDTO} with generated ID
     * @throws org.springframework.web.bind.MethodArgumentNotValidException if validation fails
     */
    @Operation(
            summary = "Create a new breed",
            description = "Creates a new animal breed with the provided details.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Breed successfully created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BreedDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Admin role required"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Breed with the same name already exists"
                    )
            }
    )
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public BreedDTO create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Breed details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BreedDTO.class))
            )
            @org.springframework.web.bind.annotation.RequestBody
            @Valid BreedDTO breedDTO) {
        BreedDTO created = breedService.create(breedDTO);
        return created;
    }

    /**
     * Updates an existing breed with the provided details.
     *
     * @param id The ID of the breed to update
     * @param breedDTO The updated breed data
     * @return The updated {@link BreedDTO}
     * @throws com.hometail.exception.ResourceNotFoundException if breed with given ID is not found
     * @throws org.springframework.web.bind.MethodArgumentNotValidException if validation fails
     */
    @Operation(
            summary = "Update a breed",
            description = "Updates an existing breed with the provided details.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Breed successfully updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BreedDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Admin role required"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Breed not found"
                    )
            }
    )
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public BreedDTO update(
            @Parameter(description = "ID of the breed to update", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated breed details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BreedDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody BreedDTO breedDTO) {
        return breedService.update(id, breedDTO);
    }

    /**
     * Deletes a breed by its ID.
     *
     * @param id The ID of the breed to delete
     * @return ResponseEntity with no content
     * @throws com.hometail.exception.ResourceNotFoundException if breed with given ID is not found
     */
    @Operation(
            summary = "Delete a breed",
            description = "Deletes a breed by its ID. This operation cannot be undone.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Breed successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Admin role required"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Breed not found"
                    )
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the breed to delete", required = true, example = "1")
            @PathVariable Long id) {
        breedService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
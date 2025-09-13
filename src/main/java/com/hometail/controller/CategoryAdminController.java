package com.hometail.controller;

import com.hometail.dto.CategoryDTO;
import com.hometail.service.CategoryService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Admin controller for managing animal categories.
 * <p>
 * Provides REST endpoints for CRUD operations on animal categories, accessible only by administrators.
 * This controller handles operations such as listing, retrieving, creating, updating, and deleting categories.
 *
 * @version 1.0
 * @see CategoryService
 * @see CategoryDTO
 */
@Tag(name = "Category Administration", description = "Endpoints for managing animal categories (Admin only)")
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class CategoryAdminController {

    private final CategoryService categoryService;

    /**
     * Retrieves a list of all animal categories.
     *
     * @return List of {@link CategoryDTO} objects containing category information
     */
    @Operation(
            summary = "List all categories",
            description = "Retrieves a paginated list of all animal categories.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of categories",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = CategoryDTO.class))
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
    public List<CategoryDTO> list() {
        return categoryService.listAdmin();
    }

    /**
     * Retrieves a specific category by its ID.
     *
     * @param id The ID of the category to retrieve
     * @return The requested {@link CategoryDTO}
     */
    @Operation(
            summary = "Get category by ID",
            description = "Retrieves detailed information about a specific category.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved category",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryDTO.class)
                            )
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
                            description = "Category not found"
                    )
            }
    )
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CategoryDTO get(
            @Parameter(description = "ID of the category to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        return categoryService.getAdmin(id);
    }

    /**
     * Creates a new animal category.
     *
     * @param dto The category data transfer object containing category information
     * @return ResponseEntity containing the created {@link CategoryDTO} and location header
     */
    @Operation(
            summary = "Create a new category",
            description = "Creates a new animal category with the provided details.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Category successfully created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryDTO.class)
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
                            description = "Category with the same name already exists"
                    )
            }
    )
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoryDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody CategoryDTO dto) {
        CategoryDTO created = categoryService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Updates an existing category.
     *
     * @param id The ID of the category to update
     * @param dto The updated category data
     * @return The updated {@link CategoryDTO}
     */
    @Operation(
            summary = "Update a category",
            description = "Updates an existing category with the provided details.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Category successfully updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryDTO.class)
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
                            description = "Category not found"
                    )
            }
    )
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public CategoryDTO update(
            @Parameter(description = "ID of the category to update", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated category details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody CategoryDTO dto) {
        return categoryService.update(id, dto);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id The ID of the category to delete
     * @return ResponseEntity with no content
     */
    @Operation(
            summary = "Delete a category",
            description = "Deletes a category by its ID. This operation cannot be undone.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Category successfully deleted"
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
                            description = "Category not found"
                    )
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the category to delete", required = true, example = "1")
            @PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

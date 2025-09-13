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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing public category-related operations.
 * <p>
 * Provides REST endpoints for retrieving category information.
 * This controller is accessible to all users and provides read-only access to category data.
 *
 * @version 1.0
 * @see CategoryService
 * @see CategoryDTO
 */
@Tag(name = "Categories", description = "Endpoints for retrieving category information")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Creates a new category (Admin only).
     *
     * @param dto The category data transfer object containing category information
     * @return ResponseEntity containing the created {@link CategoryDTO} and location header
     * @deprecated This endpoint should be moved to CategoryAdminController
     */
    @Deprecated(forRemoval = true, since = "1.0")
    @Operation(
            summary = "Create a new category (Deprecated)",
            description = "Creates a new category. This endpoint is deprecated and will be removed in a future version. Use /api/admin/categories instead.",
            deprecated = true,
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
                    )
            }
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoryDTO> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody CategoryDTO dto) {
        CategoryDTO created = categoryService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves a minimal list of all categories containing only ID and name.
     * <p>
     * This endpoint is optimized for UI dropdowns and other scenarios where only basic
     * category information is needed, reducing payload size.
     *
     * @return List of {@link CategoryDTO} objects containing only ID and name
     */
    @Operation(
            summary = "Get all categories (minimal)",
            description = "Retrieves a list of all categories containing only ID and name fields. " +
                    "Optimized for dropdown menus and other UI components that require minimal category data.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of categories",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = CategoryDTO.class))
                            )
                    )
            }
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.listPublic()
                .stream()
                .map(c -> {
                    CategoryDTO minimal = new CategoryDTO();
                    minimal.setId(c.getId());
                    minimal.setName(c.getName());
                    return minimal;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    /**
     * Retrieves a list of all category names.
     *
     * @return List of category names as strings
     */
    @Operation(
            summary = "Get all category names",
            description = "Retrieves a list of all category names.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of category names",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = String.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/names", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getCategoryNames() {
        return ResponseEntity.ok(
                categoryService.listPublic().stream()
                        .map(CategoryDTO::getName)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Retrieves a category by its ID.
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
                            responseCode = "404",
                            description = "Category not found"
                    )
            }
    )
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> getCategory(
            @Parameter(description = "ID of the category to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

}

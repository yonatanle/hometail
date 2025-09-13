package com.hometail.mapper;

import com.hometail.dto.CategoryDTO;
import com.hometail.model.Category;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between {@link Category} entities and {@link CategoryDTO} objects.
 * Handles the transformation of category data between the persistence and presentation layers.
 *
 * <p>This mapper provides methods to convert between Category entity and CategoryDTO,
 * including handling of basic category properties like name, active status, and sort order.</p>
 *
 * @since 1.0
 */
@Component
public class CategoryMapper {

    /**
     * Converts a Category entity to a CategoryDTO.
     *
     * @param category The Category entity to convert, can be null
     * @return The corresponding CategoryDTO, or null if the input is null
     */
    public static CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .active(category.isActive())
                .sortOrder(category.getSortOrder())
                .build();
    }

    /**
     * Converts a CategoryDTO to a Category entity.
     * Can be used for both creating new categories and updating existing ones.
     *
     * @param dto The DTO containing category data, can be null
     * @return A new Category entity, or null if the DTO is null
     */
    public static Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        return Category.builder()
                .id(dto.getId()) // optional, only if updating
                .name(dto.getName())
                .active(dto.isActive())
                .sortOrder(dto.getSortOrder())
                .build();
    }
}

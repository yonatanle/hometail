package com.hometail.mapper;

import com.hometail.dto.BreedDTO;
import com.hometail.model.Breed;
import com.hometail.model.Category;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between {@link Breed} entities and {@link BreedDTO} objects.
 * Handles the transformation of breed data between the persistence and presentation layers.
 *
 * <p>This mapper provides methods to convert between Breed entity and BreedDTO,
 * including handling of the related Category association.</p>
 *
 * @since 1.0
 */
@Component
public class BreedMapper {

    /**
     * Converts a Breed entity to a BreedDTO.
     * Includes basic breed information and related category details.
     *
     * @param entity The Breed entity to convert, can be null
     * @return The corresponding BreedDTO, or null if the input is null
     */
    public static BreedDTO toDto(Breed entity) {
        if (entity == null) return null;
        return BreedDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .active(entity.isActive())
                .sortOrder(entity.getSortOrder())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .build();
    }

    /**
     * Creates a new Breed entity from a DTO and associated Category.
     *
     * @param dto The DTO containing breed data, can be null
     * @param category The Category to associate with the breed, can be null
     * @return A new Breed entity, or null if the DTO is null
     */
    public static Breed toEntity(BreedDTO dto, Category category) {
        if (dto == null) return null;
        return Breed.builder()
                .id(dto.getId()) // usually null for create
                .name(dto.getName() != null ? dto.getName().trim() : null)
                .category(category)
                .active(dto.isActive())
                .sortOrder(dto.getSortOrder())
                .build();
    }

    /**
     * Updates an existing Breed entity with values from a DTO and associated Category.
     * Only non-null fields in the DTO will be used to update the target entity.
     *
     * @param dto The DTO containing updated breed data, must not be null
     * @param target The target Breed entity to update, must not be null
     * @param category The Category to associate with the breed, can be null
     * @throws NullPointerException if dto or target is null
     */
    public void updateEntity(BreedDTO dto, Breed target, Category category) {
        if (dto.getName() != null) {
            target.setName(dto.getName().trim());
        }
        if (category != null) {
            target.setCategory(category);
        }
    }
}

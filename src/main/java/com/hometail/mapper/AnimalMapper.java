package com.hometail.mapper;

import com.hometail.dto.AnimalDTO;
import com.hometail.model.*;

import java.time.LocalDate;
import java.time.Period;

/**
 * Mapper class for converting between Animal entities and AnimalDTO objects.
 * Handles the transformation of animal data between the persistence and presentation layers.
 * 
 * <p>This mapper provides methods to convert between Animal entity and AnimalDTO,
 * including handling of related entities like Category, Breed, and Owner.</p>
 * 
 * @since 1.0
 */
public class AnimalMapper {

    /**
     * Calculates the age of an animal based on its birth date.
     * 
     * @param birthday The birth date of the animal
     * @return The calculated age in years, or 0 if birthday is null
     */
    private static int calculateAge(LocalDate birthday) {
        return (birthday != null) ? Period.between(birthday, LocalDate.now()).getYears() : 0;
    }

    /**
     * Converts an Animal entity to an AnimalDTO with basic information.
     * Includes minimal owner information (only owner ID).
     * 
     * @param animal The Animal entity to convert
     * @return The corresponding AnimalDTO, or null if the input is null
     */
    public static AnimalDTO toDTO(Animal animal) {
        if (animal == null) return null;
        AnimalDTO dto = new AnimalDTO();
        dto.setId(animal.getId());
        dto.setName(animal.getName());
        dto.setCategoryId(animal.getCategory() != null ? animal.getCategory().getId() : null);
        dto.setCategoryName(animal.getCategory() != null ? animal.getCategory().getName() : null);
        dto.setGender(animal.getGender());
        dto.setBreedId(animal.getBreed() != null ? animal.getBreed().getId() : null);
        dto.setBreedName(animal.getBreed() != null ? animal.getBreed().getName() : null);
        dto.setBirthday(animal.getBirthday());
//        dto.setAge(calculateAge(animal.getBirthday())); // dynamically calculate
        dto.setSize(animal.getSize());
        dto.setShortDescription(animal.getShortDescription());
        dto.setLongDescription(animal.getLongDescription());
        dto.setAdopted(animal.isAdopted());
        dto.setImage(animal.getImage());
        if (animal.getOwner() != null) {
            dto.setOwnerId(animal.getOwner().getId());
        }
        return dto;
    }

    /**
     * Converts an Animal entity to an AnimalDTO with optional owner information.
     * 
     * @param animal The Animal entity to convert
     * @param includeOwnerInfo If true, includes detailed owner information
     * @return The corresponding AnimalDTO, or null if the input is null
     */
    public static AnimalDTO toDTO(Animal animal, boolean includeOwnerInfo) {
        if (animal == null) return null;
        AnimalDTO dto = new AnimalDTO();
        dto.setId(animal.getId());
        dto.setName(animal.getName());
        dto.setCategoryId(animal.getCategory() != null ? animal.getCategory().getId() : null);
        dto.setCategoryName(animal.getCategory() != null ? animal.getCategory().getName() : null);
        dto.setBreedId(animal.getBreed() != null ? animal.getBreed().getId() : null);
        dto.setBreedName(animal.getBreed() != null ? animal.getBreed().getName() : null);
        dto.setGender(animal.getGender());
        dto.setBirthday(animal.getBirthday());
//        dto.setAge(calculateAge(animal.getBirthday())); // dynamically calculate
        dto.setSize(animal.getSize());
        dto.setShortDescription(animal.getShortDescription());
        dto.setLongDescription(animal.getLongDescription());
        dto.setAdopted(animal.isAdopted());
        dto.setImage(animal.getImage());
        if (includeOwnerInfo && animal.getOwner() != null) {
            dto.setOwnerId(animal.getOwner().getId());
            dto.setOwnerName(animal.getOwner().getFullName());
            dto.setOwnerEmail(animal.getOwner().getEmail());
            dto.setOwnerPhone(animal.getOwner().getPhoneNumber());
        }
        return dto;
    }

    /**
     * Converts an AnimalDTO to an Animal entity.
     * Requires associated Category and Breed entities to be provided.
     * 
     * @param dto The DTO to convert
     * @param category The Category entity to associate with the animal
     * @param breed The Breed entity to associate with the animal
     * @return The corresponding Animal entity, or null if the DTO is null
     */
    public static Animal toEntity(AnimalDTO dto, Category category, Breed breed) {
        if (dto == null) return null;
        Animal animal = new Animal();
        animal.setId(dto.getId());
        animal.setName(dto.getName());
        animal.setCategory(category);
        animal.setBreed(breed);
        animal.setBirthday(dto.getBirthday());
        animal.setSize(dto.getSize());
        animal.setGender(dto.getGender());
        animal.setShortDescription(dto.getShortDescription());
        animal.setLongDescription(dto.getLongDescription());
        animal.setAdopted(dto.isAdopted());
        animal.setImage(dto.getImage());

        if (dto.getOwnerId() != null) {
            User owner = new User();
            owner.setId(dto.getOwnerId());
            animal.setOwner(owner);
        }
        return animal;
    }
}

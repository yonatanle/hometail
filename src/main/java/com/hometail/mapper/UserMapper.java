package com.hometail.mapper;

import com.hometail.dto.UserDTO;
import com.hometail.model.User;

/**
 * Mapper class for converting between {@link User} entities and {@link UserDTO} objects.
 * Handles the transformation of user data between the persistence and presentation layers.
 *
 * <p>This mapper provides methods to convert between User entity and UserDTO,
 * including handling of user details such as username, email, and role information.</p>
 *
 * @since 1.0
 */
public class UserMapper {

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user The User entity to convert, can be null
     * @return The corresponding UserDTO containing user details, or null if the input is null
     */
    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        return dto;
    }

    /**
     * Converts a UserDTO to a User entity.
     * Can be used for both creating new users and updating existing ones.
     *
     * @param dto The DTO containing user data, can be null
     * @return A new User entity with the provided data, or null if the DTO is null
     */
    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(dto.getRole());
        return user;
    }
}

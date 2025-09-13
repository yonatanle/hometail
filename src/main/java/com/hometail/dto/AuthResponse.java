package com.hometail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an authentication response.
 * Contains the JWT token and user information returned upon successful authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    /**
     * JSON Web Token (JWT) used for authenticated requests.
     * This token should be included in the Authorization header of subsequent requests.
     */
    private String token;
    
    /**
     * User information associated with the authenticated session.
     * Contains user details such as ID, username, email, and roles.
     */
    private UserDTO user;
}

package com.hometail.service.impl;

import com.hometail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring Security's {@link UserDetailsService} interface.
 * This service is responsible for loading user details during the authentication process.
 *
 * <p>This implementation retrieves user information from the database and constructs
 * a Spring Security {@link UserDetails} object that includes the user's credentials
 * and authorities (roles).</p>
 *
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see org.springframework.security.core.userdetails.User
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    /** Repository for user data access */
    private final UserRepository userRepository;

    /**
     * Loads the user details by username (email in this implementation).
     * This method is called by Spring Security during the authentication process.
     *
     * @param email the email address identifying the user whose data is required
     * @return a fully populated UserDetails object (never null)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *         GrantedAuthority (roles)
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find user by email in the database
        com.hometail.model.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Default to "USER" role if no role is specified
        String role = user.getRole() == null ? "USER" : user.getRole();

        // Build and return Spring Security User object with user details and authorities
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(role)
                .build();
    }
}

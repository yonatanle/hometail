package com.hometail.security;

import com.hometail.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter that processes incoming requests and validates JWT tokens.
 * This filter is responsible for authenticating users based on the JWT token
 * present in the Authorization header of HTTP requests.
 *
 * <p>It extends {@link OncePerRequestFilter} to ensure a single execution per request.</p>
 *
 * @see org.springframework.web.filter.OncePerRequestFilter
 * @see com.hometail.security.JwtUtil
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Utility class for JWT token operations */
    private final JwtUtil jwtUtil;
    
    /** Service for loading user details */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Processes each HTTP request to extract and validate JWT token.
     * If a valid token is found, it sets the authentication in the security context.
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                   @NotNull HttpServletResponse response,
                                   @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        // Extract Authorization header
        String authHeader = request.getHeader("Authorization");

        // Check if the Authorization header contains a Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract JWT token from the Authorization header (remove "Bearer " prefix)
            String jwt = authHeader.substring(7);
            
            // Extract email from the JWT token
            String email = jwtUtil.extractEmail(jwt);

            // If we have a valid email and no existing authentication in the security context
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from the database using email
                var userDetails = userDetailsService.loadUserByUsername(email);
                
                // Validate the token against the user details
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Create authentication token and set it in the security context
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                    );
                    // Add request details to the authentication token
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}

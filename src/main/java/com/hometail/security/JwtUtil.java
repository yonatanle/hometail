package com.hometail.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for JWT (JSON Web Token) operations including token generation,
 * validation, and extraction of claims.
 *
 * <p>This component is responsible for all JWT-related operations in the application,
 * including generating tokens, validating tokens, and extracting information from tokens.</p>
 *
 * @see org.springframework.security.core.userdetails.UserDetails
 * @see io.jsonwebtoken.Jwts
 * @since 1.0
 */
@Component
public class JwtUtil {

    /**
     * Secret key used for signing and verifying JWT tokens.
     * Injected from application-secret.properties.
     * Must be at least 32 characters long for HS256 algorithm.
     */
    @Value("${jwt.secret}")
    private String secretKey;
    
    /**
     * Token expiration time in milliseconds.
     * Injected from application-secret.properties.
     */
    @Value("${jwt.expiration}")
    private long expirationTime;

    /**
     * Generates a signing key from the secret key string.
     *
     * @return a Key object suitable for signing JWT tokens
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Generates a JWT token for the specified email.
     *
     * @param email the email to include in the token
     * @return a signed JWT token as a String
     * @throws IllegalArgumentException if email is null or empty
     */
    public String generateToken(String email) {
        Date now = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the email (subject) from the JWT token.
     *
     * @param token the JWT token to extract from
     * @return the email (subject) from the token
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    /**
     * @deprecated Use extractEmail instead
     */
    @Deprecated
    public String extractUsername(String token) {
        return extractEmail(token);
    }

    /**
     * Validates a JWT token against the provided UserDetails.
     *
     * @param token the JWT token to validate
     * @param userDetails the user details to validate against
     * @return true if the token is valid for the given user and not expired, false otherwise
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if a JWT token is expired.
     *
     * @param token the JWT token to check
     * @return true if the token is expired, false otherwise
     * @throws io.jsonwebtoken.JwtException if the token is invalid
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token the JWT token to extract claims from
     * @return a Claims object containing all claims from the token
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

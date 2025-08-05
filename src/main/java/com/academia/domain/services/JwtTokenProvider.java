package com.academia.domain.services;

import java.util.Date;
import java.util.Map;

/**
 * Interface for JWT token generation and validation.
 */
public interface JwtTokenProvider {
    
    /**
     * Generates a JWT token for the specified username and roles.
     * 
     * @param username The username
     * @param roles The user's roles
     * @param organizationId The organization ID
     * @return The generated JWT token
     */
    String generateToken(String username, String[] roles, String organizationId);
    
    /**
     * Generates a JWT token with custom claims.
     * 
     * @param claims The custom claims to include in the token
     * @param expiration The token expiration time
     * @return The generated JWT token
     */
    String generateToken(Map<String, Object> claims, Date expiration);
    
    /**
     * Validates a JWT token.
     * 
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);
    
    /**
     * Extracts the username from a JWT token.
     * 
     * @param token The JWT token
     * @return The username extracted from the token
     */
    String getUsernameFromToken(String token);
    
    /**
     * Extracts the organization ID from a JWT token.
     * 
     * @param token The JWT token
     * @return The organization ID extracted from the token
     */
    String getOrganizationIdFromToken(String token);
    
    /**
     * Extracts the expiration date from a JWT token.
     * 
     * @param token The JWT token
     * @return The expiration date of the token
     */
    Date getExpirationDateFromToken(String token);
    
    /**
     * Extracts all claims from a JWT token.
     * 
     * @param token The JWT token
     * @return A map of all claims in the token
     */
    Map<String, Object> getAllClaimsFromToken(String token);
}

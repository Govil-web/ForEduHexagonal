package com.academia.domain.services;

/**
 * Service for password validation and strength checking.
 * Follows domain-driven design principles by keeping password validation logic in the domain layer.
 */
public interface PasswordValidationService {
    
    /**
     * Validates if a password meets the security requirements.
     * 
     * @param password The password to validate
     * @return true if the password is valid, false otherwise
     * @throws IllegalArgumentException if password is null or empty
     */
    boolean isValid(String password);
    
    /**
     * Checks the strength of a password.
     * 
     * @param password The password to check
     * @return A score indicating password strength (0-4, where 4 is strongest)
     * @throws IllegalArgumentException if password is null
     */
    int checkStrength(String password);
    
    /**
     * Validates if the password meets the organization's password policy.
     * 
     * @param password The password to validate
     * @return true if the password meets the policy, false otherwise
     */
    default boolean meetsPolicy(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Default policy: at least 8 characters, 1 uppercase, 1 lowercase, 1 number, 1 special char
        return password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*\\d.*") &&
               password.matches(".*[^A-Za-z0-9].*");
    }
}

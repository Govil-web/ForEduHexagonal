package com.academia.domain.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Tracks authentication attempts to prevent brute force attacks.
 * This service is part of the domain layer to maintain security concerns within the domain.
 */
public class AuthenticationAttemptService {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final long ATTEMPT_WINDOW_MINUTES = 15;
    
    private final Map<String, UserAttempts> attemptsCache;
    
    public AuthenticationAttemptService() {
        this.attemptsCache = new ConcurrentHashMap<>();
    }
    
    /**
     * Records a failed login attempt for the given username.
     * 
     * @param username The username that failed to authenticate
     */
    public void loginFailed(String username) {
        UserAttempts attempts = attemptsCache.computeIfAbsent(
            username.toLowerCase(), 
            k -> new UserAttempts()
        );
        
        attempts.incrementAttempts();
        attempts.setLastAttemptTime(System.currentTimeMillis());
        
        // Clean up old entries to prevent memory leaks
        cleanUpOldEntries();
    }
    
    /**
     * Records a successful login and clears the attempt counter.
     * 
     * @param username The username that successfully authenticated
     */
    public void loginSucceeded(String username) {
        attemptsCache.remove(username.toLowerCase());
    }
    
    /**
     * Checks if the user is blocked due to too many failed attempts.
     * 
     * @param username The username to check
     * @return true if the user is blocked, false otherwise
     */
    public boolean isBlocked(String username) {
        UserAttempts attempts = attemptsCache.get(username.toLowerCase());
        if (attempts == null) {
            return false;
        }
        
        // Reset attempts if the window has passed
        if (System.currentTimeMillis() - attempts.getLastAttemptTime() > 
            TimeUnit.MINUTES.toMillis(ATTEMPT_WINDOW_MINUTES)) {
            attemptsCache.remove(username.toLowerCase());
            return false;
        }
        
        return attempts.getAttempts() >= MAX_ATTEMPTS;
    }
    
    /**
     * Gets the number of remaining attempts before the account is blocked.
     * 
     * @param username The username to check
     * @return Number of remaining attempts
     */
    public int getRemainingAttempts(String username) {
        UserAttempts attempts = attemptsCache.get(username.toLowerCase());
        if (attempts == null) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - attempts.getAttempts());
    }
    
    /**
     * Cleans up entries older than the attempt window.
     */
    private void cleanUpOldEntries() {
        long currentTime = System.currentTimeMillis();
        attemptsCache.entrySet().removeIf(entry -> 
            currentTime - entry.getValue().getLastAttemptTime() > 
            TimeUnit.MINUTES.toMillis(ATTEMPT_WINDOW_MINUTES * 2)
        );
    }
    
    /**
     * Inner class to track user login attempts.
     */
    private static class UserAttempts {
        private int attempts;
        private long lastAttemptTime;
        
        public UserAttempts() {
            this.attempts = 0;
            this.lastAttemptTime = System.currentTimeMillis();
        }
        
        public void incrementAttempts() {
            this.attempts++;
        }
        
        public int getAttempts() {
            return attempts;
        }
        
        public long getLastAttemptTime() {
            return lastAttemptTime;
        }
        
        public void setLastAttemptTime(long lastAttemptTime) {
            this.lastAttemptTime = lastAttemptTime;
        }
    }
}

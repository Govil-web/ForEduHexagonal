package com.academia.domain.services;

import java.util.Optional;

/**
 * Manages organization-specific context and settings for the current request.
 * This service provides access to organization-specific configurations and settings
 * that might affect authentication and authorization flows.
 */
public interface OrganizationContextService {
    
    /**
     * Gets the current organization ID from the security context.
     * 
     * @return An Optional containing the organization ID if available, empty otherwise
     */
    Optional<String> getCurrentOrganizationId();
    
    /**
     * Gets the organization's authentication settings.
     * 
     * @param organizationId The ID of the organization
     * @return The organization's authentication settings
     */
    OrganizationAuthSettings getOrganizationAuthSettings(String organizationId);
    
    /**
     * Checks if the organization has a specific feature enabled.
     * 
     * @param organizationId The ID of the organization
     * @param feature The feature to check
     * @return true if the feature is enabled, false otherwise
     */
    boolean isFeatureEnabled(String organizationId, String feature);
    
    /**
     * Represents an organization's authentication settings.
     */
    class OrganizationAuthSettings {
        private final boolean mfaRequired;
        private final int passwordMinLength;
        private final boolean passwordRequiresSpecialChar;
        private final boolean passwordRequiresNumber;
        private final boolean passwordRequiresUppercase;
        private final int maxLoginAttempts;
        private final long accountLockoutDurationMinutes;
        
        public OrganizationAuthSettings(boolean mfaRequired, int passwordMinLength, 
                                      boolean passwordRequiresSpecialChar, 
                                      boolean passwordRequiresNumber,
                                      boolean passwordRequiresUppercase,
                                      int maxLoginAttempts,
                                      long accountLockoutDurationMinutes) {
            this.mfaRequired = mfaRequired;
            this.passwordMinLength = passwordMinLength;
            this.passwordRequiresSpecialChar = passwordRequiresSpecialChar;
            this.passwordRequiresNumber = passwordRequiresNumber;
            this.passwordRequiresUppercase = passwordRequiresUppercase;
            this.maxLoginAttempts = maxLoginAttempts;
            this.accountLockoutDurationMinutes = accountLockoutDurationMinutes;
        }
        
        public boolean isMfaRequired() {
            return mfaRequired;
        }
        
        public int getPasswordMinLength() {
            return passwordMinLength;
        }
        
        public boolean isPasswordRequiresSpecialChar() {
            return passwordRequiresSpecialChar;
        }
        
        public boolean isPasswordRequiresNumber() {
            return passwordRequiresNumber;
        }
        
        public boolean isPasswordRequiresUppercase() {
            return passwordRequiresUppercase;
        }
        
        public int getMaxLoginAttempts() {
            return maxLoginAttempts;
        }
        
        public long getAccountLockoutDurationMinutes() {
            return accountLockoutDurationMinutes;
        }
        
        /**
         * Creates a builder for OrganizationAuthSettings.
         * 
         * @return A new builder instance
         */
        public static Builder builder() {
            return new Builder();
        }
        
        /**
         * Builder for OrganizationAuthSettings.
         */
        public static class Builder {
            private boolean mfaRequired;
            private int passwordMinLength = 8;
            private boolean passwordRequiresSpecialChar = true;
            private boolean passwordRequiresNumber = true;
            private boolean passwordRequiresUppercase = true;
            private int maxLoginAttempts = 5;
            private long accountLockoutDurationMinutes = 15;
            
            public Builder mfaRequired(boolean mfaRequired) {
                this.mfaRequired = mfaRequired;
                return this;
            }
            
            public Builder passwordMinLength(int passwordMinLength) {
                this.passwordMinLength = passwordMinLength;
                return this;
            }
            
            public Builder passwordRequiresSpecialChar(boolean passwordRequiresSpecialChar) {
                this.passwordRequiresSpecialChar = passwordRequiresSpecialChar;
                return this;
            }
            
            public Builder passwordRequiresNumber(boolean passwordRequiresNumber) {
                this.passwordRequiresNumber = passwordRequiresNumber;
                return this;
            }
            
            public Builder passwordRequiresUppercase(boolean passwordRequiresUppercase) {
                this.passwordRequiresUppercase = passwordRequiresUppercase;
                return this;
            }
            
            public Builder maxLoginAttempts(int maxLoginAttempts) {
                this.maxLoginAttempts = maxLoginAttempts;
                return this;
            }
            
            public Builder accountLockoutDurationMinutes(long accountLockoutDurationMinutes) {
                this.accountLockoutDurationMinutes = accountLockoutDurationMinutes;
                return this;
            }
            
            public OrganizationAuthSettings build() {
                return new OrganizationAuthSettings(
                    mfaRequired,
                    passwordMinLength,
                    passwordRequiresSpecialChar,
                    passwordRequiresNumber,
                    passwordRequiresUppercase,
                    maxLoginAttempts,
                    accountLockoutDurationMinutes
                );
            }
        }
    }
}

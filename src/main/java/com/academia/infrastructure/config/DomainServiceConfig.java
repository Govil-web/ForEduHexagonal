package com.academia.infrastructure.config;

import com.academia.domain.model.services.EnrollmentEligibilityChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public EnrollmentEligibilityChecker enrollmentEligibilityChecker() {
        return new EnrollmentEligibilityChecker();
    }
}

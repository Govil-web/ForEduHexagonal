package com.academia.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración para el encoder de contraseñas.
 * Proporciona un bean de PasswordEncoder usando BCrypt para toda la aplicación.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Bean para encodear contraseñas usando BCrypt.
     * BCrypt es considerado seguro y resistente a ataques de rainbow table.
     *
     * @return PasswordEncoder configurado con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

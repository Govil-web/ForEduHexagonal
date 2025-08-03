package com.academia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Clase principal de Spring Boot para el Sistema Académico Enterprise
 * Configuración completa con arquitectura hexagonal y DDD
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@EnableCaching
@EnableAsync
public class SistemaAcademicoApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SistemaAcademicoApplication.class, args);
    }
} 
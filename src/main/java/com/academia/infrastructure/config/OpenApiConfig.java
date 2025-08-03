package com.academia.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI para la documentación de la API
 * Define la información general, contactos y servidores disponibles
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema Académico Enterprise API")
                        .description("API REST para la gestión integral de un sistema académico empresarial. " +
                                "Implementa arquitectura hexagonal y Domain-Driven Design (DDD) para " +
                                "proporcionar una solución robusta y escalable para la gestión de estudiantes, " +
                                "materias, asistencias y evaluaciones.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("desarrollo@academia.edu")
                                .url("https://www.academia.edu"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api/v1")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("https://api.academia.edu/v1")
                                .description("Servidor de producción")
                ));
    }
} 
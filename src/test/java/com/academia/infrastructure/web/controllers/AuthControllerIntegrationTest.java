// AuthControllerIntegrationTest.java
package com.academia.infrastructure.web.controllers;

import com.academia.config.TestSecurityConfig;
import com.academia.infrastructure.persistence.jpa.repositories.SpringRefreshTokenRepository;
import com.academia.infrastructure.persistence.jpa.repositories.SpringUserRepository;
import com.academia.infrastructure.web.requests.LoginRequest;
import com.academia.infrastructure.web.requests.RefreshTokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestSecurityConfig.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringUserRepository userRepository;

    @Autowired
    private SpringRefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        // Limpiar datos de pruebas anteriores
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Debe devolver 400 Bad Request cuando faltan credenciales en login")
    void login_shouldReturn400_whenCredentialsAreMissing() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest(
                "", // Email vacío
                "password123"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe devolver 400 Bad Request cuando el email es inválido")
    void login_shouldReturn400_whenEmailIsInvalid() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest(
                "email-invalido", // Email sin formato válido
                "password123"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe devolver 400 Bad Request cuando la contraseña es muy corta")
    void login_shouldReturn400_whenPasswordTooShort() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest(
                "user@test.com",
                "123" // Contraseña muy corta
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe devolver 400 Bad Request cuando el refresh token está vacío")
    void refreshToken_shouldReturn400_whenTokenIsEmpty() throws Exception {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe permitir acceso al endpoint de logout sin autenticación")
    void logout_shouldBeAccessible_withoutAuthentication() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout exitoso"));
    }

    @Test
    @DisplayName("Debe devolver 401 Unauthorized para /me sin autenticación")
    void getCurrentUser_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Los endpoints de auth deben ser públicos")
    void authEndpoints_shouldBePublic() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest(
                "test@example.com",
                "password123"
        );

        // Act & Assert - Los endpoints no deberían devolver 401 Unauthorized
        // sino errores de validación o lógica de negocio
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(not(401))); // No debe ser Unauthorized

        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("fake.token");
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().is(not(401))); // No debe ser Unauthorized
    }
}
package com.academia.infrastructure.web.controllers;

import com.academia.config.TestSecurityConfig;
import com.academia.infrastructure.persistence.jpa.repositories.SpringOrganizationRepository;
import com.academia.infrastructure.persistence.jpa.repositories.SpringUserRepository;
import com.academia.infrastructure.web.requests.CreateOrganizationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestSecurityConfig.class)
class OrganizationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringUserRepository userRepository;

    @Autowired
    private SpringOrganizationRepository organizationRepository;

    @BeforeEach
    void setUp() {
        // Limpiar datos de pruebas anteriores
        userRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    @DisplayName("Debe crear una organización exitosamente y devolver 201 Created")
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void createOrganization_shouldReturn201_whenRequestIsValid() throws Exception {
        // Arrange
        CreateOrganizationRequest request = new CreateOrganizationRequest(
                "Universidad Tecnológica del Futuro",
                "unitecfuturo",
                16,
                "Ana",
                "García",
                "admin@unitecfuturo.edu",
                "AdminPass123"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.organizationId", notNullValue()))
                .andExpect(jsonPath("$.name", is("Universidad Tecnológica del Futuro")))
                .andExpect(jsonPath("$.subdomain", is("unitecfuturo")))
                .andExpect(jsonPath("$.digitalConsentAge", is(16)))
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.initialAdmin", notNullValue()))
                .andExpect(jsonPath("$.initialAdmin.fullName", is("Ana García")))
                .andExpect(jsonPath("$.initialAdmin.email", is("admin@unitecfuturo.edu")))
                .andExpect(jsonPath("$.initialAdmin.accountStatus", is("ACTIVE")));

        // Verificar que se crearon los registros en la base de datos
        assertEquals(1, organizationRepository.count());
        assertEquals(1, userRepository.count());
    }

    @Test
    @DisplayName("Debe fallar con 400 Bad Request cuando el email es inválido")
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void createOrganization_shouldReturn400_whenEmailIsInvalid() throws Exception {
        // Arrange
        CreateOrganizationRequest request = new CreateOrganizationRequest(
                "Universidad Test",
                "unitest",
                16,
                "Test",
                "Admin",
                "email-invalido", // Email inválido
                "AdminPass123"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verificar que no se creó nada en la base de datos
        assertEquals(0, organizationRepository.count());
        assertEquals(0, userRepository.count());
    }

    @Test
    @DisplayName("Debe fallar con 400 Bad Request cuando el subdominio es inválido")
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void createOrganization_shouldReturn400_whenSubdomainIsInvalid() throws Exception {
        // Arrange
        CreateOrganizationRequest request = new CreateOrganizationRequest(
                "Universidad Test",
                "SUBDOMINIO_INVALIDO", // Subdominio con mayúsculas y guión bajo
                16,
                "Test",
                "Admin",
                "admin@test.edu",
                "AdminPass123"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe fallar con 403 Forbidden cuando el usuario no tiene permisos")
    @WithMockUser(roles = "ORGANIZATION_ADMIN") // Rol insuficiente
    void createOrganization_shouldReturn403_whenUserLacksPermissions() throws Exception {
        // Arrange
        CreateOrganizationRequest request = new CreateOrganizationRequest(
                "Universidad Test",
                "unitest",
                16,
                "Test",
                "Admin",
                "admin@test.edu",
                "AdminPass123"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
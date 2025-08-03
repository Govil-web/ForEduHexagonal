package com.academia.infrastructure.web.controllers;

import com.academia.config.TestSecurityConfig; // <-- IMPORTAR LA CONFIG DE SEGURIDAD
import com.academia.infrastructure.persistence.jpa.entities.OrganizationJpaEntity;
import com.academia.infrastructure.persistence.jpa.repositories.SpringOrganizationRepository;
import com.academia.infrastructure.persistence.jpa.repositories.SpringStudentProfileRepository;
import com.academia.infrastructure.persistence.jpa.repositories.SpringUserRepository;
import com.academia.infrastructure.web.requests.RegisterStudentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import; // <-- IMPORTAR
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestSecurityConfig.class) // <-- AÑADIR ESTA LÍNEA
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringUserRepository userRepository;

    @Autowired
    private SpringStudentProfileRepository studentProfileRepository;

    @Autowired
    private SpringOrganizationRepository organizationRepository;

    private OrganizationJpaEntity testOrganization;

    @BeforeEach
    void setUp() {
        studentProfileRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();

        OrganizationJpaEntity org = new OrganizationJpaEntity();
        org.setName("Universidad de Prueba");
        org.setSubdomain("test-uni");
        org.setDigitalConsentAge(18);
        org.setActive(true);
        org.setUuid("test-uuid-123");
        testOrganization = organizationRepository.save(org);
    }

    @Test
    @DisplayName("Debe registrar un nuevo estudiante exitosamente y devolver 201 Created")
    void registerStudent_shouldReturn201_whenRequestIsValid() throws Exception {
        RegisterStudentRequest request = new RegisterStudentRequest(
                testOrganization.getId(),
                "Sofia",
                "Castillo",
                "sofia.test@unifuturo.edu",
                LocalDate.of(2006, 2, 15),
                "UF-TEST-001",
                LocalDate.now(),
                "Ingeniería de Software - Semestre 1"
        );

        mockMvc.perform(post("/api/v1/students/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName", is("Sofia Castillo")))
                .andExpect(jsonPath("$.studentIdNumber", is("UF-TEST-001")))
                .andExpect(jsonPath("$.accountStatus", is("PENDING_VERIFICATION")));

        assertEquals(1, userRepository.count());
        assertEquals(1, studentProfileRepository.count());
    }

    @Test
    @DisplayName("Debe fallar el registro y devolver 400 Bad Request si el email es inválido")
    void registerStudent_shouldReturn400_whenEmailIsInvalid() throws Exception {
        RegisterStudentRequest request = new RegisterStudentRequest(
                testOrganization.getId(),
                "Invalid", "User", "invalid-email",
                LocalDate.of(2000, 1, 1),
                "INVALID-001", LocalDate.now(), "Some Grade"
        );

        mockMvc.perform(post("/api/v1/students/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
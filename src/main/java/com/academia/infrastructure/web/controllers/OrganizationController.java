package com.academia.infrastructure.web.controllers;

import com.academia.domain.ports.in.commands.CreateOrganizationCommand;
import com.academia.domain.ports.in.dtos.OrganizationDetailsDTO;
import com.academia.domain.ports.in.organization.CreateOrganizationUseCase;
import com.academia.infrastructure.web.mappers.OrganizationRequestMapper;
import com.academia.infrastructure.web.requests.CreateOrganizationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Organizations", description = "API para gestión de organizaciones")
public class OrganizationController {

    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final OrganizationRequestMapper requestMapper;

    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')") // Solo administradores del sistema pueden crear organizaciones
    @Operation(
            summary = "Crear nueva organización",
            description = "Crea una nueva organización en el sistema junto con su administrador inicial. " +
                    "Solo accesible para administradores del sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organización creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para realizar esta operación"),
            @ApiResponse(responseCode = "409", description = "El subdominio ya está en uso")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrganizationDetailsDTO> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request) {

        log.info("Solicitud para crear organización: {} con subdominio: {}",
                request.organizationName(), request.subdomain());

        try {
            // 1. Mapear el Request HTTP a un Comando del Dominio
            CreateOrganizationCommand command = requestMapper.toCommand(request);

            // 2. Invocar el Caso de Uso
            OrganizationDetailsDTO organizationDetails = createOrganizationUseCase.createOrganization(command);

            // 3. Retornar la respuesta con código 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(organizationDetails);

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al crear organización: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error inesperado al crear organización", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{organizationId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or (hasRole('ORGANIZATION_ADMIN') and @organizationSecurityService.belongsToOrganization(authentication.name, #organizationId))")
    @Operation(
            summary = "Obtener detalles de organización",
            description = "Obtiene los detalles de una organización específica"
    )
    public ResponseEntity<OrganizationDetailsDTO> getOrganization(@PathVariable Long organizationId) {
        // Placeholder para futura implementación
        log.info("Solicitud para obtener organización con ID: {}", organizationId);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}

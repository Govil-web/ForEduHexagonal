package com.academia.infrastructure.web.mappers;

import com.academia.domain.ports.in.commands.CreateOrganizationCommand;
import com.academia.infrastructure.web.requests.CreateOrganizationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * Mapper responsable de convertir DTOs de la capa web a comandos de dominio.
 * Utiliza MapStruct para generar automáticamente las implementaciones.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrganizationRequestMapper {

    OrganizationRequestMapper INSTANCE = Mappers.getMapper(OrganizationRequestMapper.class);

    /**
     * Convierte el DTO de la solicitud HTTP en un Comando de dominio.
     * MapStruct mapea automáticamente los campos con el mismo nombre.
     *
     * @param request El DTO de la solicitud entrante
     * @return El Comando listo para ser procesado por el caso de uso
     */
    CreateOrganizationCommand toCommand(CreateOrganizationRequest request);
}

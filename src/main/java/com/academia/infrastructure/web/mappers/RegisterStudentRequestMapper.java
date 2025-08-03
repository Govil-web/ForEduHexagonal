package com.academia.infrastructure.web.mappers;

import com.academia.domain.ports.in.commands.RegisterNewStudentCommand;
import com.academia.infrastructure.web.requests.RegisterStudentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegisterStudentRequestMapper {

    RegisterStudentRequestMapper INSTANCE = Mappers.getMapper(RegisterStudentRequestMapper.class);

    /**
     * Convierte el DTO de la solicitud web en un Comando de dominio.
     * MapStruct se encarga de la correspondencia de campos con el mismo nombre.
     * @param request El DTO de la solicitud entrante.
     * @return El Comando listo para ser procesado por el caso de uso.
     */
    RegisterNewStudentCommand toCommand(RegisterStudentRequest request);
}

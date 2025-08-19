package com.academia.application.handlers.commands;

import com.academia.application.services.CreateAcademicTermService;
import com.academia.domain.model.aggregates.AcademicTerm;
import com.academia.domain.ports.in.commands.CreateAcademicTermCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAcademicTermCommandHandler {
    
    private final CreateAcademicTermService createAcademicTermService;
    
    public AcademicTerm handle(CreateAcademicTermCommand command) {
        return createAcademicTermService.createAcademicTerm(
            command.organizationId(),
            command.name(),
            command.startDate(),
            command.endDate(),
            command.isCurrentTerm()
        );
    }
}
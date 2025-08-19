package com.academia.application.handlers.commands;

import com.academia.application.services.CreateSubjectService;
import com.academia.domain.model.aggregates.Subject;
import com.academia.domain.ports.in.commands.CreateSubjectCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSubjectCommandHandler {
    
    private final CreateSubjectService createSubjectService;
    
    public Subject handle(CreateSubjectCommand command) {
        return createSubjectService.createSubject(
            command.organizationId(),
            command.name(),
            command.subjectCode(),
            command.description(),
            command.credits()
        );
    }
}
package com.academia.application.handlers.commands;

import com.academia.application.services.UpdateSubjectService;
import com.academia.domain.model.aggregates.Subject;
import com.academia.domain.ports.in.commands.UpdateSubjectCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateSubjectCommandHandler {
    
    private final UpdateSubjectService updateSubjectService;
    
    public Subject handle(UpdateSubjectCommand command) {
        return updateSubjectService.updateSubject(
            command.subjectId(),
            command.name(),
            command.description(),
            command.credits()
        );
    }
}
package com.academia.infrastructure.web.controllers;

import com.academia.application.handlers.commands.CreateSubjectCommandHandler;
import com.academia.application.handlers.commands.UpdateSubjectCommandHandler;
import com.academia.application.handlers.queries.GetSubjectDetailsQueryHandler;
import com.academia.application.handlers.queries.GetSubjectsByOrganizationQueryHandler;
import com.academia.application.services.ActivateSubjectService;
import com.academia.application.services.DeactivateSubjectService;
import com.academia.domain.model.aggregates.Subject;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.SubjectId;
import com.academia.domain.ports.in.commands.CreateSubjectCommand;
import com.academia.domain.ports.in.commands.UpdateSubjectCommand;
import com.academia.infrastructure.web.dto.requests.CreateSubjectRequest;
import com.academia.infrastructure.web.dto.requests.UpdateSubjectRequest;
import com.academia.infrastructure.web.dto.responses.SubjectResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {
    
    private final CreateSubjectCommandHandler createSubjectCommandHandler;
    private final UpdateSubjectCommandHandler updateSubjectCommandHandler;
    private final GetSubjectDetailsQueryHandler getSubjectDetailsQueryHandler;
    private final GetSubjectsByOrganizationQueryHandler getSubjectsByOrganizationQueryHandler;
    private final ActivateSubjectService activateSubjectService;
    private final DeactivateSubjectService deactivateSubjectService;
    
    @PostMapping
    @PreAuthorize("hasPermission('ACADEMIC_SUBJECT_CREATE')")
    public ResponseEntity<SubjectResponse> createSubject(@Valid @RequestBody CreateSubjectRequest request) {
        CreateSubjectCommand command = new CreateSubjectCommand(
            new OrganizationId(request.organizationId()),
            request.name(),
            request.subjectCode(),
            request.description(),
            request.credits()
        );
        
        Subject subject = createSubjectCommandHandler.handle(command);
        SubjectResponse response = SubjectResponse.fromDomain(subject);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{subjectId}")
    @PreAuthorize("hasPermission('ACADEMIC_SUBJECT_VIEW')")
    public ResponseEntity<SubjectResponse> getSubject(@PathVariable Long subjectId) {
        Subject subject = getSubjectDetailsQueryHandler.handle(new SubjectId(subjectId));
        SubjectResponse response = SubjectResponse.fromDomain(subject);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasPermission('ACADEMIC_SUBJECT_VIEW')")
    public ResponseEntity<List<SubjectResponse>> getSubjectsByOrganization(
            @RequestParam Long organizationId,
            @RequestParam(defaultValue = "false") boolean onlyActive) {
        
        List<Subject> subjects = getSubjectsByOrganizationQueryHandler.handle(
            new OrganizationId(organizationId), onlyActive);
        
        List<SubjectResponse> responses = subjects.stream()
            .map(SubjectResponse::fromDomain)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{subjectId}")
    @PreAuthorize("hasPermission('ACADEMIC_SUBJECT_UPDATE')")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable Long subjectId,
            @Valid @RequestBody UpdateSubjectRequest request) {
        
        UpdateSubjectCommand command = new UpdateSubjectCommand(
            new SubjectId(subjectId),
            request.name(),
            request.description(),
            request.credits()
        );
        
        Subject subject = updateSubjectCommandHandler.handle(command);
        SubjectResponse response = SubjectResponse.fromDomain(subject);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{subjectId}/activate")
    @PreAuthorize("hasPermission('ACADEMIC_SUBJECT_UPDATE')")
    public ResponseEntity<SubjectResponse> activateSubject(@PathVariable Long subjectId) {
        Subject subject = activateSubjectService.activateSubject(new SubjectId(subjectId));
        SubjectResponse response = SubjectResponse.fromDomain(subject);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{subjectId}/deactivate")
    @PreAuthorize("hasPermission('ACADEMIC_SUBJECT_UPDATE')")
    public ResponseEntity<SubjectResponse> deactivateSubject(@PathVariable Long subjectId) {
        Subject subject = deactivateSubjectService.deactivateSubject(new SubjectId(subjectId));
        SubjectResponse response = SubjectResponse.fromDomain(subject);
        
        return ResponseEntity.ok(response);
    }
}
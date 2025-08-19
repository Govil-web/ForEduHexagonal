package com.academia.infrastructure.web.controllers;

import com.academia.application.handlers.commands.CreateAcademicTermCommandHandler;
import com.academia.application.handlers.queries.GetAcademicTermDetailsQueryHandler;
import com.academia.application.services.UpdateAcademicTermDatesService;
import com.academia.application.services.StartAcademicTermService;
import com.academia.application.services.EndAcademicTermService;
import com.academia.application.services.SetCurrentTermService;
import com.academia.domain.model.aggregates.AcademicTerm;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.AcademicTermId;
import com.academia.domain.ports.in.commands.CreateAcademicTermCommand;
import com.academia.domain.ports.out.AcademicTermRepository;
import com.academia.infrastructure.web.dto.requests.CreateAcademicTermRequest;
import com.academia.infrastructure.web.dto.requests.UpdateTermDatesRequest;
import com.academia.infrastructure.web.dto.responses.AcademicTermResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/academic-terms")
@RequiredArgsConstructor
public class AcademicTermController {
    
    private final CreateAcademicTermCommandHandler createAcademicTermCommandHandler;
    private final GetAcademicTermDetailsQueryHandler getAcademicTermDetailsQueryHandler;
    private final UpdateAcademicTermDatesService updateAcademicTermDatesService;
    private final StartAcademicTermService startAcademicTermService;
    private final EndAcademicTermService endAcademicTermService;
    private final SetCurrentTermService setCurrentTermService;
    private final AcademicTermRepository academicTermRepository;
    
    @PostMapping
    @PreAuthorize("hasPermission('ACADEMIC_TERM_CREATE')")
    public ResponseEntity<AcademicTermResponse> createAcademicTerm(@Valid @RequestBody CreateAcademicTermRequest request) {
        CreateAcademicTermCommand command = new CreateAcademicTermCommand(
            new OrganizationId(request.organizationId()),
            request.name(),
            request.startDate(),
            request.endDate(),
            request.isCurrentTerm()
        );
        
        AcademicTerm academicTerm = createAcademicTermCommandHandler.handle(command);
        AcademicTermResponse response = AcademicTermResponse.fromDomain(academicTerm);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{termId}")
    @PreAuthorize("hasPermission('ACADEMIC_TERM_VIEW')")
    public ResponseEntity<AcademicTermResponse> getAcademicTerm(@PathVariable Long termId) {
        AcademicTerm academicTerm = getAcademicTermDetailsQueryHandler.handle(new AcademicTermId(termId));
        AcademicTermResponse response = AcademicTermResponse.fromDomain(academicTerm);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasPermission('ACADEMIC_TERM_VIEW')")
    public ResponseEntity<List<AcademicTermResponse>> getAcademicTermsByOrganization(
            @RequestParam Long organizationId,
            @RequestParam(defaultValue = "false") boolean onlyActive) {
        
        List<AcademicTerm> terms;
        if (onlyActive) {
            terms = academicTermRepository.findActiveTermsByOrganization(new OrganizationId(organizationId));
        } else {
            terms = academicTermRepository.findByOrganizationId(new OrganizationId(organizationId));
        }
        
        List<AcademicTermResponse> responses = terms.stream()
            .map(AcademicTermResponse::fromDomain)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/current")
    @PreAuthorize("hasPermission('ACADEMIC_TERM_VIEW')")
    public ResponseEntity<AcademicTermResponse> getCurrentTerm(@RequestParam Long organizationId) {
        return academicTermRepository.findCurrentTermByOrganization(new OrganizationId(organizationId))
            .map(term -> ResponseEntity.ok(AcademicTermResponse.fromDomain(term)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{termId}/dates")
    @PreAuthorize("hasPermission('ACADEMIC_TERM_UPDATE')")
    public ResponseEntity<AcademicTermResponse> updateTermDates(
            @PathVariable Long termId,
            @Valid @RequestBody UpdateTermDatesRequest request) {
        
        AcademicTerm academicTerm = updateAcademicTermDatesService.updateAcademicTermDates(
            new AcademicTermId(termId),
            request.startDate(),
            request.endDate()
        );
        
        AcademicTermResponse response = AcademicTermResponse.fromDomain(academicTerm);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{termId}/start")
    @PreAuthorize("hasPermission('ACADEMIC_TERM_MANAGE')")
    public ResponseEntity<AcademicTermResponse> startTerm(@PathVariable Long termId) {
        AcademicTerm academicTerm = startAcademicTermService.startAcademicTerm(new AcademicTermId(termId));
        AcademicTermResponse response = AcademicTermResponse.fromDomain(academicTerm);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{termId}/end")
    @PreAuthorize("hasPermission('ACADEMIC_TERM_MANAGE')")
    public ResponseEntity<AcademicTermResponse> endTerm(@PathVariable Long termId) {
        AcademicTerm academicTerm = endAcademicTermService.endAcademicTerm(new AcademicTermId(termId));
        AcademicTermResponse response = AcademicTermResponse.fromDomain(academicTerm);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{termId}/set-current")
    @PreAuthorize("hasPermission('ACADEMIC_TERM_MANAGE')")
    public ResponseEntity<AcademicTermResponse> setCurrentTerm(
            @PathVariable Long termId,
            @RequestParam Long organizationId) {
        
        AcademicTerm academicTerm = setCurrentTermService.setCurrentTerm(
            new AcademicTermId(termId),
            new OrganizationId(organizationId)
        );
        
        AcademicTermResponse response = AcademicTermResponse.fromDomain(academicTerm);
        return ResponseEntity.ok(response);
    }
}
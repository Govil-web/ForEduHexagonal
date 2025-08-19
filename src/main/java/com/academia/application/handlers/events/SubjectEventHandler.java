package com.academia.application.handlers.events;

import com.academia.domain.model.events.SubjectEvents;
import com.academia.application.services.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubjectEventHandler {
    
    private final SecurityAuditService securityAuditService;
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleSubjectCreated(SubjectEvents.SubjectCreatedEvent event) {
        log.info("Subject created: {} for organization: {}", 
            event.subjectId().getValue(), event.organizationId().getValue());
        
        // Auditar la creación de la materia
        securityAuditService.logEvent(
            "SUBJECT_CREATED",
            "Subject created with ID: " + event.subjectId().getValue(),
            event.organizationId().getValue().toString()
        );
        
        // Aquí podrías agregar lógica adicional como:
        // - Notificaciones a administradores
        // - Sincronización con sistemas externos
        // - Actualización de estadísticas
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleSubjectUpdated(SubjectEvents.SubjectUpdatedEvent event) {
        log.info("Subject updated: {} - Name changed from '{}' to '{}'", 
            event.subjectId().getValue(), event.previousName(), event.newName());
        
        // Auditar la actualización
        securityAuditService.logEvent(
            "SUBJECT_UPDATED",
            String.format("Subject %s updated. Name: %s -> %s", 
                event.subjectId().getValue(), event.previousName(), event.newName()),
            null
        );
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleSubjectActivated(SubjectEvents.SubjectActivatedEvent event) {
        log.info("Subject activated: {} for organization: {}", 
            event.subjectId().getValue(), event.organizationId().getValue());
        
        securityAuditService.logEvent(
            "SUBJECT_ACTIVATED",
            "Subject activated with ID: " + event.subjectId().getValue(),
            event.organizationId().getValue().toString()
        );
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleSubjectDeactivated(SubjectEvents.SubjectDeactivatedEvent event) {
        log.info("Subject deactivated: {} for organization: {}", 
            event.subjectId().getValue(), event.organizationId().getValue());
        
        securityAuditService.logEvent(
            "SUBJECT_DEACTIVATED",
            "Subject deactivated with ID: " + event.subjectId().getValue(),
            event.organizationId().getValue().toString()
        );
        
        // Lógica adicional para desactivación:
        // - Notificar a profesores asignados
        // - Revisar cursos activos que usan esta materia
        // - Actualizar reportes académicos
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async  
    public void handlePrerequisitesAdded(SubjectEvents.PrerequisitesAddedEvent event) {
        log.info("Prerequisites added to subject: {} -> prerequisite: {}", 
            event.subjectId().getValue(), event.prerequisiteSubjectId().getValue());
        
        securityAuditService.logEvent(
            "SUBJECT_PREREQUISITE_ADDED",
            String.format("Prerequisite %s added to subject %s", 
                event.prerequisiteSubjectId().getValue(), event.subjectId().getValue()),
            null
        );
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handlePrerequisitesRemoved(SubjectEvents.PrerequisitesRemovedEvent event) {
        log.info("Prerequisites removed from subject: {} -> prerequisite: {}", 
            event.subjectId().getValue(), event.prerequisiteSubjectId().getValue());
        
        securityAuditService.logEvent(
            "SUBJECT_PREREQUISITE_REMOVED",
            String.format("Prerequisite %s removed from subject %s", 
                event.prerequisiteSubjectId().getValue(), event.subjectId().getValue()),
            null
        );
    }
}
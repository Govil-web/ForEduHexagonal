package com.academia.application.handlers.events;

import com.academia.domain.model.events.AcademicTermEvents;
import com.academia.application.services.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AcademicTermEventHandler {
    
    private final SecurityAuditService securityAuditService;
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAcademicTermCreated(AcademicTermEvents.AcademicTermCreatedEvent event) {
        log.info("Academic term created: {} for organization: {}", 
            event.termName(), event.organizationId().getValue());
        
        securityAuditService.logBusinessEvent(
            "CREATE",
            "ACADEMIC_TERM",
            String.format("Academic term '%s' created for period %s to %s", 
                event.termName(), 
                event.termDates().getStartDate(), 
                event.termDates().getEndDate()),
            event.organizationId(),
            Map.of("termName", event.termName())
        );
        
        // Lógica adicional:
        // - Notificar a administradores académicos
        // - Preparar plantillas de cursos para el nuevo término
        // - Activar procesos de inscripción si corresponde
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAcademicTermStarted(AcademicTermEvents.AcademicTermStartedEvent event) {
        log.info("Academic term started: {} for organization: {}", 
            event.termId().getValue(), event.organizationId().getValue());
        
        securityAuditService.logBusinessEvent(
            "START",
            "ACADEMIC_TERM",
            "Academic term started with ID: " + event.termId().getValue(),
            event.organizationId(),
            Map.of("termId", event.termId().getValue())
        );
        
        // Lógica de inicio de término:
        // - Activar cursos del término
        // - Enviar notificaciones a estudiantes y profesores
        // - Inicializar sistemas de calificaciones
        // - Activar calendario académico
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAcademicTermEnded(AcademicTermEvents.AcademicTermEndedEvent event) {
        log.info("Academic term ended: {} for organization: {}", 
            event.termId().getValue(), event.organizationId().getValue());
        
        securityAuditService.logBusinessEvent(
            "END",
            "ACADEMIC_TERM",
            "Academic term ended with ID: " + event.termId().getValue(),
            event.organizationId(),
            Map.of("termId", event.termId().getValue())
        );
        
        // Lógica de fin de término:
        // - Cerrar procesos de calificación
        // - Generar reportes finales
        // - Archivar cursos del término
        // - Procesar promociones de estudiantes
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAcademicTermDatesUpdated(AcademicTermEvents.AcademicTermDatesUpdatedEvent event) {
        log.info("Academic term dates updated: {} - Dates changed from {}-{} to {}-{}", 
            event.termId().getValue(),
            event.previousDates().getStartDate(), event.previousDates().getEndDate(),
            event.newDates().getStartDate(), event.newDates().getEndDate());
        
        securityAuditService.logBusinessEvent(
            "UPDATE_DATES",
            "ACADEMIC_TERM",
            String.format("Academic term %s dates updated from %s-%s to %s-%s", 
                event.termId().getValue(),
                event.previousDates().getStartDate(), event.previousDates().getEndDate(),
                event.newDates().getStartDate(), event.newDates().getEndDate()),
            event.organizationId(),
            Map.of("termId", event.termId().getValue())
        );
        
        // Lógica de actualización de fechas:
        // - Notificar cambios a estudiantes y profesores
        // - Actualizar calendarios académicos
        // - Revisar programación de cursos
        // - Ajustar fechas de evaluaciones
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAcademicTermActivated(AcademicTermEvents.AcademicTermActivatedEvent event) {
        log.info("Academic term activated: {} for organization: {}", 
            event.termId().getValue(), event.organizationId().getValue());
        
        securityAuditService.logBusinessEvent(
            "ACTIVATE",
            "ACADEMIC_TERM",
            "Academic term activated with ID: " + event.termId().getValue(),
            event.organizationId(),
            Map.of("termId", event.termId().getValue())
        );
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAcademicTermDeactivated(AcademicTermEvents.AcademicTermDeactivatedEvent event) {
        log.info("Academic term deactivated: {} for organization: {}", 
            event.termId().getValue(), event.organizationId().getValue());
        
        securityAuditService.logBusinessEvent(
            "DEACTIVATE",
            "ACADEMIC_TERM",
            "Academic term deactivated with ID: " + event.termId().getValue(),
            event.organizationId(),
            Map.of("termId", event.termId().getValue())
        );
        
        // Lógica de desactivación:
        // - Archivar cursos asociados
        // - Preservar datos históricos
        // - Notificar cambio de estado
    }
}
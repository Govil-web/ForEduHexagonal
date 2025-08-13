package com.academia.application.services;

import com.academia.application.exceptions.ResourceNotFoundException;
import com.academia.application.mappers.StudentDTOMapper;
import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.Student;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.model.valueobjects.user.Name;
import com.academia.domain.ports.in.commands.RegisterNewStudentCommand;
import com.academia.domain.ports.in.dtos.StudentDetailsDTO;
import com.academia.domain.ports.in.student.RegisterNewStudentUseCase;
import com.academia.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterNewStudentServiceImpl implements RegisterNewStudentUseCase {

    private final OrganizationRepository organizationRepository;
    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final EmailOrganizationIndexRepository emailIndexRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final StudentDTOMapper studentDTOMapper;

    @Override
    @Transactional
    public StudentDetailsDTO registerStudent(RegisterNewStudentCommand command) {
        OrganizationId orgId = new OrganizationId(command.organizationId());
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada con ID: " + orgId.getValue()));

        Email email = new Email(command.email());
        
        // Validar email globalmente
        validateEmailAvailability(email);
        
        // Validar legajo en la organización
        if (studentRepository.existsByStudentIdNumber(orgId, command.studentIdNumber())) {
            throw new IllegalArgumentException("El legajo '" + command.studentIdNumber() + "' ya está en uso en esta organización.");
        }

        Name name = new Name(command.firstName(), command.lastName());

        int age = Period.between(command.birthDate(), LocalDate.now()).getYears();
        AccountStatus initialStatus = (age < organization.getDigitalConsentAge())
                ? AccountStatus.TUTOR_MANAGED
                : AccountStatus.PENDING_VERIFICATION;

        // ID es null inicialmente, será asignado por la BD
        User user = new User(null, orgId, name, email, command.birthDate(), null, initialStatus);
        UserAccount userAccount = UserAccount.register(user, organization);

        UserAccount savedUserAccount = userAccountRepository.save(userAccount);
        AccountId newAccountId = savedUserAccount.getUser().getId();
        
        // Registrar email en el índice global
        emailIndexRepository.registerEmail(
                email, 
                orgId, 
                savedUserAccount.getUser().getId().getValue()
        );

        Student student = new Student(newAccountId, orgId, command.studentIdNumber(), command.enrollmentDate());
        student.changeGradeLevel(command.initialGradeLevel());
        Student savedStudent = studentRepository.save(student);

        domainEventPublisher.publish(savedUserAccount.getDomainEvents());

        log.info("Estudiante registrado exitosamente: {} con ID: {} en organización: {}", 
                email.value(), newAccountId.getValue(), orgId.getValue());

        return studentDTOMapper.toDTO(savedUserAccount, savedStudent);
    }
    
    /**
     * Valida que el email esté disponible globalmente.
     * 
     * @param email El email a validar
     * @throws IllegalArgumentException si el email ya está registrado en cualquier organización
     */
    private void validateEmailAvailability(Email email) {
        log.debug("Validando disponibilidad global del email: {}", email.value());
        
        if (emailIndexRepository.emailExistsGlobally(email)) {
            log.warn("El email {} ya está registrado en el sistema", email.value());
            throw new IllegalArgumentException("El email '" + email.value() + "' ya está registrado. Por favor, utilice otro email.");
        }
        
        log.debug("Email {} disponible para registro", email.value());
    }
}
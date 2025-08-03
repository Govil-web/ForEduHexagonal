package com.academia.infrastructure.persistence.jpa.entities;

import com.academia.domain.model.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long organizationId;

    // Nombres de campo explícitos que coinciden con el Value Object del Dominio
    private String firstName;
    private String lastName;

    private String email;
    private String passwordHash;
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    // ... otros campos y timestamps
}
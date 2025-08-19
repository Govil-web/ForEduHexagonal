package com.academia.infrastructure.persistence.jpa.repositories;

import com.academia.infrastructure.persistence.jpa.entities.EmailOrganizationIndexJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad EmailOrganizationIndexJpaEntity.
 * Proporciona métodos para buscar organizaciones por email de forma global.
 */
public interface SpringEmailOrganizationIndexRepository extends JpaRepository<EmailOrganizationIndexJpaEntity, Long> {

    /**
     * Busca el ID de la organización asociada a un email.
     * 
     * @param email El email a buscar
     * @return Optional con el ID de la organización si existe, o empty si no existe
     */
    @Query("SELECT e.organizationId FROM EmailOrganizationIndexJpaEntity e WHERE e.email = :email")
    Optional<Long> findOrganizationIdByEmail(@Param("email") String email);
    
    /**
     * Verifica si un email existe en el índice global.
     * 
     * @param email El email a verificar
     * @return true si el email existe, false en caso contrario
     */
    boolean existsByEmail(String email);
    
    /**
     * Elimina una entrada del índice por ID de usuario.
     * 
     * @param userId El ID del usuario
     */
    void deleteByUserId(Long userId);
}
package com.academia.domain.model.enums;

/**
 * Representa el ciclo de vida y el estado de la cuenta de un usuario.
 * Es crucial para la lógica de negocio como el inicio de sesión y la gestión de menores.
 */
public enum AccountStatus {
    /**
     * La cuenta ha sido creada pero el usuario aún no ha verificado su email.
     * No puede iniciar sesión.
     */
    PENDING_VERIFICATION,

    /**
     * La cuenta está activa y el usuario puede iniciar sesión.
     */
    ACTIVE,

    /**
     * La cuenta pertenece a un menor de edad (según la política del tenant).
     * No puede iniciar sesión. Es gestionada por uno o más tutores.
     */
    TUTOR_MANAGED,

    /**
     * La cuenta ha sido suspendida por un administrador.
     * No puede iniciar sesión temporalmente.
     */
    SUSPENDED,

    /**
     * La cuenta ha sido desactivada por el usuario o por un administrador.
     * Es una baja lógica y permanente. No se puede reactivar.
     */
    DEACTIVATED
}
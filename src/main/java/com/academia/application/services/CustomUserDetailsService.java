package com.academia.application.services;

import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.Role;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.ports.out.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Cargando detalles del usuario para email: {}", email);

        // Nota: Este método será llamado durante la validación JWT
        // El email debe incluir información de la organización o usar otro mecanismo
        // Por simplicidad, buscamos por email globalmente por ahora

        // En una implementación completa, necesitaríamos el contexto de la organización
        // desde el token JWT o desde el request

        throw new UnsupportedOperationException(
                "loadUserByUsername requiere contexto de organización. " +
                        "Usar loadUserByEmailAndOrganization en su lugar."
        );
    }

    /**
     * Carga los detalles del usuario por email y organización.
     * Método personalizado que respeta el contexto multi-tenant.
     */
    public UserDetails loadUserByEmailAndOrganization(String email, Long organizationId)
            throws UsernameNotFoundException {

        log.debug("Cargando detalles del usuario para email: {} en organización: {}", email, organizationId);

        // Buscar usuario por email en la organización específica
        UserAccount userAccount = userAccountRepository.findByEmail(
                new com.academia.domain.model.valueobjects.ids.OrganizationId(organizationId),
                new com.academia.domain.model.valueobjects.user.Email(email)
        ).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        User user = userAccount.getUser();

        // Convertir a UserDetails de Spring Security
        return createUserDetails(user);
    }

    /**
     * Carga los detalles del usuario por ID de cuenta.
     * Útil para validación de tokens JWT.
     */
    public UserDetails loadUserByAccountId(Long accountId) throws UsernameNotFoundException {
        log.debug("Cargando detalles del usuario por ID: {}", accountId);

        UserAccount userAccount = userAccountRepository.findById(new AccountId(accountId))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ID: " + accountId));

        User user = userAccount.getUser();
        return createUserDetails(user);
    }

    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail().value())
                .password(user.getPasswordHash() != null ? user.getPasswordHash() : "")
                .authorities(mapRolesToAuthorities(user.getRoles()))
                .accountExpired(false)
                .accountLocked(user.getAccountStatus() == AccountStatus.SUSPENDED)
                .credentialsExpired(false)
                .disabled(user.getAccountStatus() != AccountStatus.ACTIVE)
                .build();
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }
}

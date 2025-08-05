package com.academia.infrastructure.security.jwt;

import com.academia.application.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * Filtro de autenticación JWT que se ejecuta en cada petición HTTP.
 * Se encarga de extraer, validar y establecer la autenticación basada en tokens JWT.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProviderImpl jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                authenticateUser(jwt, request);
            }
        } catch (Exception e) {
            log.error("Error al procesar autenticación JWT: {}", e.getMessage());
            // No lanzamos excepción para permitir que endpoints públicos funcionen
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization de la petición HTTP.
     *
     * @param request Petición HTTP
     * @return Token JWT sin el prefijo "Bearer " o null si no existe
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * Autentica al usuario usando el token JWT y establece el contexto de seguridad.
     *
     * @param jwt Token JWT válido
     * @param request Petición HTTP para agregar atributos de contexto
     */
    private void authenticateUser(String jwt, HttpServletRequest request) {
        try {
            // Extraer información del token
            Long userId = jwtTokenProvider.extractUserId(jwt);
            String email = jwtTokenProvider.extractEmail(jwt);
            Long organizationId = jwtTokenProvider.extractOrganizationId(jwt);

            // Cargar detalles del usuario usando nuestro UserDetailsService personalizado
            UserDetails userDetails = userDetailsService.loadUserByAccountId(userId);

            // Crear token de autenticación de Spring Security
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // No necesitamos credenciales ya que el JWT es válido
                            userDetails.getAuthorities()
                    );

            // Agregar información adicional a los detalles de la autenticación
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Guardar información del contexto organizacional en la request
            // Esto será útil para validaciones de multi-tenancy
            Map<String, Object> claims = jwtTokenProvider.extractClaims(jwt);
            request.setAttribute("organizationId", organizationId);
            request.setAttribute("organizationSubdomain", claims.get("organizationSubdomain"));
            request.setAttribute("userClaims", claims);
            request.setAttribute("userId", userId);

            // Establecer la autenticación en el contexto de seguridad de Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Usuario autenticado: {} para organización: {}", email, organizationId);

        } catch (UsernameNotFoundException e) {
            log.warn("Usuario no encontrado durante autenticación JWT: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error al autenticar usuario con JWT: {}", e.getMessage());
        }
    }

    /**
     * Determina si este filtro debe ser omitido para ciertas rutas.
     *
     * @param request Petición HTTP
     * @return true si el filtro debe ser omitido, false en caso contrario
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // No filtrar endpoints públicos que no requieren autenticación
        return path.startsWith("/api/v1/auth/") ||
                path.startsWith("/api/v1/swagger-ui") ||
                path.startsWith("/api/v1/v3/api-docs") ||
                path.startsWith("/actuator/health") ||
                path.equals("/") ||
                path.equals("/favicon.ico");
    }
}
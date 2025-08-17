package com.academia.infrastructure.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Filtro que agrega headers de seguridad adicionales a todas las respuestas.
 * Implementa defensas contra ataques comunes y mejora la postura de seguridad.
 */
@Component
@Slf4j
public class SecurityHeadersFilter extends OncePerRequestFilter {

    private final Environment environment;
    private final boolean isProduction;

    public SecurityHeadersFilter(Environment environment) {
        this.environment = environment;
        this.isProduction = Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Headers de seguridad básicos
        addBasicSecurityHeaders(response);

        // Headers específicos por entorno
        if (isProduction) {
            addProductionSecurityHeaders(response);
        } else {
            addDevelopmentHeaders(response);
        }

        // Content Security Policy
        addContentSecurityPolicy(response);

        // Headers de información del servidor (removidos por seguridad)
        removeServerInformationHeaders(response);

        filterChain.doFilter(request, response);
    }

    private void addBasicSecurityHeaders(HttpServletResponse response) {
        // Prevenir ataques de MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");

        // Prevenir clickjacking
        response.setHeader("X-Frame-Options", "DENY");

        // Habilitar protección XSS del navegador
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // Controlar referrer information
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Cache control para contenido sensible
        if (isSecureEndpoint(response)) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }

        // Permissions Policy (Feature Policy)
        response.setHeader("Permissions-Policy", 
            "camera=(), microphone=(), geolocation=(), payment=(), usb=(), " +
            "accelerometer=(), gyroscope=(), magnetometer=(), fullscreen=()");
    }

    private void addProductionSecurityHeaders(HttpServletResponse response) {
        // HSTS más estricto en producción
        response.setHeader("Strict-Transport-Security", 
            "max-age=31536000; includeSubDomains; preload");

        // Cross-Origin policies más estrictas
        response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
        response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
        response.setHeader("Cross-Origin-Resource-Policy", "same-origin");

        // Expect-CT para Certificate Transparency
        response.setHeader("Expect-CT", 
            "max-age=86400, enforce, report-uri=\"https://your-domain.com/ct-report\"");
    }

    private void addDevelopmentHeaders(HttpServletResponse response) {
        // Headers más permisivos para desarrollo
        response.setHeader("X-Development-Mode", "true");
        
        // HSTS menos estricto en desarrollo
        response.setHeader("Strict-Transport-Security", "max-age=300");
    }

    private void addContentSecurityPolicy(HttpServletResponse response) {
        StringBuilder csp = new StringBuilder();
        
        if (isProduction) {
            // CSP estricto para producción
            csp.append("default-src 'self'; ")
               .append("script-src 'self'; ")
               .append("style-src 'self' 'unsafe-inline'; ")
               .append("img-src 'self' data: https:; ")
               .append("font-src 'self'; ")
               .append("connect-src 'self'; ")
               .append("frame-ancestors 'none'; ")
               .append("form-action 'self'; ")
               .append("base-uri 'self'; ")
               .append("object-src 'none'; ")
               .append("upgrade-insecure-requests; ");
        } else {
            // CSP más permisivo para desarrollo
            csp.append("default-src 'self' 'unsafe-inline' 'unsafe-eval'; ")
               .append("script-src 'self' 'unsafe-inline' 'unsafe-eval'; ")
               .append("style-src 'self' 'unsafe-inline'; ")
               .append("img-src 'self' data: blob: https:; ")
               .append("connect-src 'self' ws: wss:; ")
               .append("frame-ancestors 'none'; ");
        }

        response.setHeader("Content-Security-Policy", csp.toString());

        // CSP en modo report-only para monitoreo
        if (isProduction) {
            response.setHeader("Content-Security-Policy-Report-Only", 
                csp.toString() + "report-uri /csp-violation-report-endpoint");
        }
    }

    private void removeServerInformationHeaders(HttpServletResponse response) {
        // Remover headers que revelan información del servidor
        response.setHeader("Server", "");
        response.setHeader("X-Powered-By", "");
    }

    private boolean isSecureEndpoint(HttpServletResponse response) {
        // Determinar si el endpoint contiene información sensible
        String contentType = response.getContentType();
        return contentType != null && (
            contentType.contains("application/json") ||
            contentType.contains("application/xml")
        );
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // No aplicar a recursos estáticos innecesarios
        return path.startsWith("/actuator/health") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/static/");
    }
}
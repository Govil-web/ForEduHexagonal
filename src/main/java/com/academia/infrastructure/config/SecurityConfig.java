package com.academia.infrastructure.config;

import com.academia.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.academia.infrastructure.security.jwt.JwtAuthenticationEntryPoint;
import com.academia.infrastructure.security.filters.SecurityHeadersFilter;
import com.academia.infrastructure.security.filters.RateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;
    private final SecurityHeadersFilter securityHeadersFilter;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF ya que usamos JWT
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar headers de seguridad (Spring Security 6.1+ sintaxis)
                .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions.deny()) // Prevenir clickjacking
                    .contentTypeOptions(Customizer.withDefaults()) // Prevenir MIME type sniffing
                    .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                        .maxAgeInSeconds(31536000) // 1 año
                        .includeSubDomains(true)
                    )
                )

                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configuración de sesiones - sin estado (stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Punto de entrada para errores de autenticación
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // Configuración de autorización
                .authorizeHttpRequests(auth -> auth
                                // Endpoints públicos
                                .requestMatchers("/auth/**").permitAll()
// Swagger UI endpoints
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/swagger-ui.html").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/swagger-resources/**").permitAll()
                                .requestMatchers("/webjars/**").permitAll()
// Otros endpoints públicos
                                .requestMatchers("/actuator/health").permitAll()
                                .requestMatchers("/").permitAll()

                        // Endpoints de sistema (solo SYSTEM_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/organizations").hasRole("SYSTEM_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/organizations/*/admin/**").hasRole("SYSTEM_ADMIN")

                        // Endpoints de organización (ORGANIZATION_ADMIN + contexto)
                        .requestMatchers(HttpMethod.POST, "/students/**").hasAnyRole("ORGANIZATION_ADMIN", "ACADEMIC_DIRECTOR")
                        .requestMatchers(HttpMethod.GET, "/students/**").hasAnyRole("ORGANIZATION_ADMIN", "ACADEMIC_DIRECTOR", "TEACHER")

                        // Endpoints de cursos (TEACHER + contexto)
                        .requestMatchers(HttpMethod.POST, "/courses/*/enroll").hasAnyRole("ORGANIZATION_ADMIN", "ACADEMIC_DIRECTOR")
                        .requestMatchers(HttpMethod.GET, "/courses/**").hasAnyRole("ORGANIZATION_ADMIN", "ACADEMIC_DIRECTOR", "TEACHER", "STUDENT")

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )

                // Agregar filtros de seguridad en orden de prioridad
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Configuración CORS restrictiva basada en entorno
        configuration.setAllowedOriginPatterns(getAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Headers específicos permitidos (más restrictivo que "*")
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type", 
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-Organization-Subdomain" // Header personalizado para multi-tenancy
        ));
        
        // Headers que se exponen al cliente
        configuration.setExposedHeaders(Arrays.asList(
            "X-Total-Count",
            "X-Page-Count", 
            "Authorization"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight por 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

    /**
     * Obtiene los orígenes permitidos basado en el entorno activo.
     */
    private List<String> getAllowedOrigins() {
        // Obtener perfiles activos
        String[] activeProfiles = environment.getActiveProfiles();
        
        if (Arrays.asList(activeProfiles).contains("prod")) {
            // PRODUCCIÓN: Solo dominios específicos
            return Arrays.asList(
                "https://academy.yourdomain.com",
                "https://admin.yourdomain.com",
                "https://api.yourdomain.com"
            );
        } else if (Arrays.asList(activeProfiles).contains("test")) {
            // TESTING: Dominios de test
            return Arrays.asList(
                "http://localhost:3000",
                "http://localhost:4200",
                "http://test.academy.local"
            );
        } else {
            // DESARROLLO: Más permisivo pero aún restringido
            return Arrays.asList(
                "http://localhost:3000",
                "http://localhost:4200", 
                "http://localhost:8080",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:4200"
            );
        }
    }
}

package com.academia.infrastructure.config;

import com.academia.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.academia.infrastructure.security.jwt.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF ya que usamos JWT
                .csrf(AbstractHttpConfigurer::disable)

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

                // Agregar filtro JWT antes del filtro de autenticación estándar
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

        // Permitir orígenes específicos en producción
        configuration.setAllowedOriginPatterns(List.of("*")); // En prod: dominios específicos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}

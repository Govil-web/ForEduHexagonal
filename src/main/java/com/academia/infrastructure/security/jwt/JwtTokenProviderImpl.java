package com.academia.infrastructure.security.jwt;

import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.Role;
import com.academia.domain.model.entities.User;
import com.academia.domain.ports.out.JwtTokenProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenProviderImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration:900000}") long accessTokenExpirationMs, // 15 min
            @Value("${jwt.refresh-token-expiration:604800000}") long refreshTokenExpirationMs // 7 días
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    public String generateAccessToken(UserAccount userAccount, String organizationSubdomain) {
        User user = userAccount.getUser();

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().getValue());
        claims.put("organizationId", user.getOrganizationId().getValue());
        claims.put("organizationSubdomain", organizationSubdomain);
        claims.put("fullName", user.getName().getFullName());
        claims.put("accountStatus", user.getAccountStatus().name());

        // Agregar roles y permisos
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        claims.put("roles", roles);

        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .distinct()
                .collect(Collectors.toList());
        claims.put("permissions", permissions);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail().value())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserAccount userAccount) {
        User user = userAccount.getUser();

        return Jwts.builder()
                .setSubject(user.getEmail().value())
                .claim("userId", user.getId().getValue())
                .claim("tokenType", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Object> extractClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new HashMap<>(claims);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error al extraer claims del token: {}", e.getMessage());
            throw new IllegalArgumentException("Token JWT inválido", e);
        }
    }

    @Override
    public String extractEmail(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error al extraer email del token: {}", e.getMessage());
            throw new IllegalArgumentException("Token JWT inválido", e);
        }
    }

    @Override
    public LocalDateTime getAccessTokenExpiration() {
        return LocalDateTime.now().plusSeconds(accessTokenExpirationMs / 1000);
    }

    @Override
    public LocalDateTime getRefreshTokenExpiration() {
        return LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000);
    }

    /**
     * Extrae el ID del usuario del token.
     */
    public Long extractUserId(String token) {
        Map<String, Object> claims = extractClaims(token);
        Object userId = claims.get("userId");

        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }
        throw new IllegalArgumentException("ID de usuario no encontrado en el token");
    }

    /**
     * Extrae el ID de la organización del token.
     */
    public Long extractOrganizationId(String token) {
        Map<String, Object> claims = extractClaims(token);
        Object orgId = claims.get("organizationId");

        if (orgId instanceof Number) {
            return ((Number) orgId).longValue();
        }
        throw new IllegalArgumentException("ID de organización no encontrado en el token");
    }

    /**
     * Verifica si el token ha expirado.
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true; // Si no se puede parsear, consideramos que está expirado
        }
    }
}
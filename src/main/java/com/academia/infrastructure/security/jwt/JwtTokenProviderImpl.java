package com.academia.infrastructure.security.jwt;

import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.Role;
import com.academia.domain.model.entities.User;
import com.academia.domain.ports.out.JwtTokenProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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
    private final String issuer;
    private final String audience;
    private final SignatureAlgorithm algorithm;

    public JwtTokenProviderImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration:900000}") long accessTokenExpirationMs, // 15 min
            @Value("${jwt.refresh-token-expiration:604800000}") long refreshTokenExpirationMs, // 7 días
            @Value("${jwt.issuer:academia-system}") String issuer,
            @Value("${jwt.audience:academia-api}") String audience,
            @Value("${jwt.algorithm:HS512}") String algorithmName
    ) {
        // Validar que el secret tenga suficiente entropía
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret debe tener al menos 32 caracteres (256 bits)");
        }
        
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        this.issuer = issuer;
        this.audience = audience;
        this.algorithm = SignatureAlgorithm.valueOf(algorithmName);
        
        log.info("JWT Provider configurado con algoritmo: {}, issuer: {}", algorithm, issuer);
    }

    @Override
    public String generateAccessToken(UserAccount userAccount, String organizationSubdomain) {
        User user = userAccount.getUser();

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().getValue());
        // Manejar el caso de usuarios del sistema que no tienen organizationId
        if (user.getOrganizationId() != null) {
            claims.put("organizationId", user.getOrganizationId().getValue());
        } else {
            claims.put("organizationId", null);
        }
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

        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpirationMs);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail().value())
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setId(java.util.UUID.randomUUID().toString()) // JTI único
                .signWith(secretKey, algorithm)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserAccount userAccount) {
        User user = userAccount.getUser();

        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpirationMs);
        
        return Jwts.builder()
                .setSubject(user.getEmail().value())
                .claim("userId", user.getId().getValue())
                .claim("tokenType", "refresh")
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setId(java.util.UUID.randomUUID().toString()) // JTI único
                .signWith(secretKey, algorithm)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .requireIssuer(issuer) // Validar issuer
                    .requireAudience(audience) // Validar audience
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token JWT expirado: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Token JWT malformado: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("Firma JWT inválida: {}", e.getMessage());
            return false;
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
     * Puede devolver null para usuarios del sistema.
     */
    public Long extractOrganizationId(String token) {
        Map<String, Object> claims = extractClaims(token);
        Object orgId = claims.get("organizationId");

        if (orgId == null) {
            // Para usuarios del sistema, el organizationId es null
            return null;
        }
        
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
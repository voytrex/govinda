/*
 * Govinda ERP - JWT Token Service
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for creating and validating JWT tokens.
 */
@Service
public class JwtTokenService {
    private static final int MIN_SECRET_LENGTH = 32;
    private final String secret;
    private final long expirationSeconds;
    private volatile SecretKey secretKey;

    public JwtTokenService(
        @Value("${govinda.jwt.secret:default-secret-key-change-in-production-min-256-bits}") String secret,
        @Value("${govinda.jwt.expiration-seconds:3600}") long expirationSeconds
    ) {
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            synchronized (this) {
                if (secretKey == null) {
                    byte[] keyBytes;
                    if (secret.length() < MIN_SECRET_LENGTH) {
                        keyBytes = String.format("%1$-" + MIN_SECRET_LENGTH + "s", secret)
                            .replace(' ', '0')
                            .getBytes(StandardCharsets.UTF_8);
                    } else {
                        keyBytes = secret.getBytes(StandardCharsets.UTF_8);
                    }
                    secretKey = Keys.hmacShaKeyFor(keyBytes);
                }
            }
        }
        return secretKey;
    }

    /**
     * Generates a JWT token for a user with tenant context.
     */
    public String generateToken(UUID userId, String username, UUID tenantId, List<String> permissions) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
            .subject(userId.toString())
            .claim("username", username)
            .claim("tenantId", tenantId.toString())
            .claim("permissions", permissions)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(getSecretKey())
            .compact();
    }

    /**
     * Validates and extracts claims from a JWT token.
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts user ID from token.
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims == null) {
            return null;
        }
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Extracts tenant ID from token.
     */
    public UUID getTenantIdFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims == null) {
            return null;
        }
        Object tenantIdObj = claims.get("tenantId");
        if (!(tenantIdObj instanceof String tenantIdStr)) {
            return null;
        }
        try {
            return UUID.fromString(tenantIdStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts permissions from token.
     */
    public List<String> getPermissionsFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims == null) {
            return List.of();
        }
        Object permissionsObj = claims.get("permissions");
        if (!(permissionsObj instanceof List<?> permissions)) {
            return List.of();
        }
        return permissions.stream().filter(String.class::isInstance).map(String.class::cast).toList();
    }
}

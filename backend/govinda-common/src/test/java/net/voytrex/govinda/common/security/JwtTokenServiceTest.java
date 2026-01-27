/*
 * Govinda ERP - JWT Token Service Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService(
            "test-secret-key-minimum-256-bits-required-for-hs256-algorithm",
            3600
        );
    }

    @Nested
    @DisplayName("Token Generation")
    class TokenGeneration {

        @Test
        void shouldGenerateValidJwtToken() {
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            List<String> permissions = List.of("person:read", "person:write");

            String token = jwtTokenService.generateToken(
                userId,
                "testuser",
                tenantId,
                permissions
            );

            assertThat(token).isNotNull();
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        void shouldIncludeUserIdInToken() {
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();

            String token = jwtTokenService.generateToken(
                userId,
                "testuser",
                tenantId,
                List.of()
            );

            UUID extractedUserId = jwtTokenService.getUserIdFromToken(token);
            assertThat(extractedUserId).isEqualTo(userId);
        }

        @Test
        void shouldIncludeTenantIdInToken() {
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();

            String token = jwtTokenService.generateToken(
                userId,
                "testuser",
                tenantId,
                List.of()
            );

            UUID extractedTenantId = jwtTokenService.getTenantIdFromToken(token);
            assertThat(extractedTenantId).isEqualTo(tenantId);
        }

        @Test
        void shouldIncludePermissionsInToken() {
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            List<String> permissions = List.of("person:read", "person:write", "contract:read");

            String token = jwtTokenService.generateToken(
                userId,
                "testuser",
                tenantId,
                permissions
            );

            List<String> extractedPermissions = jwtTokenService.getPermissionsFromToken(token);
            assertThat(extractedPermissions).containsExactlyInAnyOrderElementsOf(permissions);
        }
    }

    @Nested
    @DisplayName("Token Validation")
    class TokenValidation {

        @Test
        void shouldValidateValidToken() {
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();

            String token = jwtTokenService.generateToken(
                userId,
                "testuser",
                tenantId,
                List.of("person:read")
            );

            var claims = jwtTokenService.validateToken(token);
            assertThat(claims).isNotNull();
            assertThat(claims.getSubject()).isEqualTo(userId.toString());
        }

        @Test
        void shouldRejectInvalidToken() {
            String invalidToken = "invalid.token.here";

            var claims = jwtTokenService.validateToken(invalidToken);
            assertThat(claims).isNull();
        }

        @Test
        void shouldRejectEmptyToken() {
            var claims = jwtTokenService.validateToken("");
            assertThat(claims).isNull();
        }

        @Test
        void shouldRejectTokenWithWrongSignature() {
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();

            String token = jwtTokenService.generateToken(
                userId,
                "testuser",
                tenantId,
                List.of()
            );

            JwtTokenService differentService = new JwtTokenService(
                "different-secret-key-minimum-256-bits-required-for-hs256",
                3600
            );

            var claims = differentService.validateToken(token);
            assertThat(claims).isNull();
        }
    }

    @Nested
    @DisplayName("Token Extraction")
    class TokenExtraction {

        @Test
        void shouldExtractUserIdFromValidToken() {
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();

            String token = jwtTokenService.generateToken(
                userId,
                "testuser",
                tenantId,
                List.of()
            );

            UUID extractedUserId = jwtTokenService.getUserIdFromToken(token);
            assertThat(extractedUserId).isEqualTo(userId);
        }

        @Test
        void shouldReturnNullForInvalidTokenWhenExtractingUserId() {
            UUID userId = jwtTokenService.getUserIdFromToken("invalid.token");
            assertThat(userId).isNull();
        }

        @Test
        void shouldExtractTenantIdFromValidToken() {
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();

            String token = jwtTokenService.generateToken(
                userId,
                "testuser",
                tenantId,
                List.of()
            );

            UUID extractedTenantId = jwtTokenService.getTenantIdFromToken(token);
            assertThat(extractedTenantId).isEqualTo(tenantId);
        }

        @Test
        void shouldReturnNullForInvalidTokenWhenExtractingTenantId() {
            UUID tenantId = jwtTokenService.getTenantIdFromToken("invalid.token");
            assertThat(tenantId).isNull();
        }

        @Test
        void shouldExtractPermissionsFromValidToken() {
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            List<String> permissions = List.of("person:read", "person:write");

            String token = jwtTokenService.generateToken(
                userId,
                "testuser",
                tenantId,
                permissions
            );

            List<String> extractedPermissions = jwtTokenService.getPermissionsFromToken(token);
            assertThat(extractedPermissions).containsExactlyInAnyOrderElementsOf(permissions);
        }

        @Test
        void shouldReturnEmptyListForInvalidTokenWhenExtractingPermissions() {
            List<String> permissions = jwtTokenService.getPermissionsFromToken("invalid.token");
            assertThat(permissions).isEmpty();
        }

        @Test
        void shouldReturnEmptyListWhenTokenHasNoPermissions() {
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();

            String token = jwtTokenService.generateToken(
                userId,
                "testuser",
                tenantId,
                List.of()
            );

            List<String> extractedPermissions = jwtTokenService.getPermissionsFromToken(token);
            assertThat(extractedPermissions).isEmpty();
        }
    }
}

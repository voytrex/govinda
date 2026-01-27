/*
 * Govinda ERP - Tenant Context Resolver Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;
import net.voytrex.govinda.common.domain.exception.UnauthorizedTenantAccessException;
import net.voytrex.govinda.common.domain.model.Role;
import net.voytrex.govinda.common.domain.model.Tenant;
import net.voytrex.govinda.common.domain.model.User;
import net.voytrex.govinda.common.domain.model.UserTenant;
import net.voytrex.govinda.common.infrastructure.persistence.JpaUserTenantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@Tag("unit")
@Tag("fast")
@ExtendWith(MockitoExtension.class)
class TenantContextResolverTest {

    @Mock
    private JpaUserTenantRepository userTenantRepository;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Bypass")
    class Bypass {

        @Test
        void shouldBypassPublicPaths() throws Exception {
            TenantContextResolver resolver = new TenantContextResolver(userTenantRepository);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/swagger-ui/index.html");

            boolean result = resolver.preHandle(request, new MockHttpServletResponse(), new Object());

            assertThat(result).isTrue();
            verifyNoInteractions(userTenantRepository);
        }
    }

    @Nested
    @DisplayName("Tenant resolution")
    class TenantResolution {

        @Test
        void shouldUseHeaderTenantIdWhenPresent() throws Exception {
            TenantContextResolver resolver = new TenantContextResolver(userTenantRepository);
            UUID tenantId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            authenticate(userId);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/persons");
            request.addHeader("X-Tenant-Id", tenantId.toString());

            when(userTenantRepository.findUserTenantAccess(userId, tenantId))
                .thenReturn(createUserTenant(userId, tenantId));

            boolean result = resolver.preHandle(request, new MockHttpServletResponse(), new Object());

            assertThat(result).isTrue();
            assertThat(request.getAttribute("tenantId")).isEqualTo(tenantId);
            verify(userTenantRepository).findUserTenantAccess(userId, tenantId);
        }

        @Test
        void shouldUseTokenTenantIdWhenHeaderMissing() throws Exception {
            TenantContextResolver resolver = new TenantContextResolver(userTenantRepository);
            UUID tenantId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            authenticate(userId);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/persons");
            request.setAttribute("tenantId", tenantId);

            when(userTenantRepository.findUserTenantAccess(userId, tenantId))
                .thenReturn(createUserTenant(userId, tenantId));

            boolean result = resolver.preHandle(request, new MockHttpServletResponse(), new Object());

            assertThat(result).isTrue();
            assertThat(request.getAttribute("tenantId")).isEqualTo(tenantId);
        }

        @Test
        void shouldUseTokenTenantIdWhenHeaderInvalid() throws Exception {
            TenantContextResolver resolver = new TenantContextResolver(userTenantRepository);
            UUID tenantId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            authenticate(userId);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/persons");
            request.addHeader("X-Tenant-Id", "not-a-uuid");
            request.setAttribute("tenantId", tenantId);

            when(userTenantRepository.findUserTenantAccess(userId, tenantId))
                .thenReturn(createUserTenant(userId, tenantId));

            boolean result = resolver.preHandle(request, new MockHttpServletResponse(), new Object());

            assertThat(result).isTrue();
            assertThat(request.getAttribute("tenantId")).isEqualTo(tenantId);
        }
    }

    @Nested
    @DisplayName("Missing tenant")
    class MissingTenant {

        @Test
        void shouldReturnBadRequestWhenTenantMissingAndUnauthenticated() throws Exception {
            TenantContextResolver resolver = new TenantContextResolver(userTenantRepository);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/persons");
            MockHttpServletResponse response = new MockHttpServletResponse();

            boolean result = resolver.preHandle(request, response, new Object());

            assertThat(result).isFalse();
            assertThat(response.getStatus()).isEqualTo(400);
            assertThat(response.getContentAsString()).contains("X-Tenant-Id header is required");
        }

        @Test
        void shouldReturnBadRequestWhenTenantMissingAndAuthenticated() throws Exception {
            TenantContextResolver resolver = new TenantContextResolver(userTenantRepository);
            authenticate(UUID.randomUUID());
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/persons");
            MockHttpServletResponse response = new MockHttpServletResponse();

            boolean result = resolver.preHandle(request, response, new Object());

            assertThat(result).isFalse();
            assertThat(response.getStatus()).isEqualTo(400);
            assertThat(response.getContentAsString()).contains("Tenant context is required");
        }
    }

    @Nested
    @DisplayName("Access")
    class Access {

        @Test
        void shouldThrowWhenUserHasNoTenantAccess() {
            TenantContextResolver resolver = new TenantContextResolver(userTenantRepository);
            UUID tenantId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            authenticate(userId);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/persons");
            request.addHeader("X-Tenant-Id", tenantId.toString());

            when(userTenantRepository.findUserTenantAccess(userId, tenantId)).thenReturn(null);

            assertThatThrownBy(() -> resolver.preHandle(request, new MockHttpServletResponse(), new Object()))
                .isInstanceOf(UnauthorizedTenantAccessException.class);
        }

        @Test
        void shouldReturnFalseWhenPrincipalNotUuid() throws Exception {
            TenantContextResolver resolver = new TenantContextResolver(userTenantRepository);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/persons");
            request.addHeader("X-Tenant-Id", UUID.randomUUID().toString());

            TestingAuthenticationToken token = new TestingAuthenticationToken("user", null);
            token.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(token);

            boolean result = resolver.preHandle(request, new MockHttpServletResponse(), new Object());

            assertThat(result).isFalse();
        }
    }

    private void authenticate(UUID userId) {
        TestingAuthenticationToken token = new TestingAuthenticationToken(userId, null);
        token.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private UserTenant createUserTenant(UUID userId, UUID tenantId) {
        User user = new User("user", "user@example.com", "hash");
        user.setId(userId);
        Tenant tenant = new Tenant(tenantId, "TENANT", "Tenant Name");
        Role role = new Role("ROLE_USER", "User");
        return new UserTenant(user, tenant, role);
    }
}

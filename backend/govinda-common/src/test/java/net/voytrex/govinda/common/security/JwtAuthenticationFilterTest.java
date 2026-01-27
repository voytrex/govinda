/*
 * Govinda ERP - JWT Authentication Filter Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSetAuthenticationAndTenantWhenTokenIsValid() throws Exception {
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        Claims claims = org.mockito.Mockito.mock(Claims.class);
        when(claims.getSubject()).thenReturn(userId.toString());

        when(jwtTokenService.validateToken(token)).thenReturn(claims);
        when(jwtTokenService.getPermissionsFromToken(token)).thenReturn(List.of("person:read", "person:write"));
        when(jwtTokenService.getTenantIdFromToken(token)).thenReturn(tenantId);

        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        var response = new MockHttpServletResponse();

        var filter = new JwtAuthenticationFilter(jwtTokenService);

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userId);
        assertThat(authentication.getAuthorities()).extracting("authority")
            .containsExactlyInAnyOrder("person:read", "person:write");
        assertThat(request.getAttribute("tenantId")).isEqualTo(tenantId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldPassThroughWhenAuthorizationHeaderMissing() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        var filter = new JwtAuthenticationFilter(jwtTokenService);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtTokenService);
        verify(filterChain).doFilter(request, response);
    }
}

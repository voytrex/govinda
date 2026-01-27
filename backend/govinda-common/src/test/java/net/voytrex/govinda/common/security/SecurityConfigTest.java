/*
 * Govinda ERP - Security Configuration Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private TenantContextResolver tenantContextResolver;

    @Test
    void shouldProvideBcryptPasswordEncoder() {
        var config = new SecurityConfig(true, jwtAuthenticationFilter, tenantContextResolver);

        assertThat(config.passwordEncoder()).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void shouldWriteAuthenticationRequiredResponse() throws Exception {
        var config = new SecurityConfig(true, jwtAuthenticationFilter, tenantContextResolver);
        var entryPoint = config.authenticationEntryPoint();
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.setRequestURI("/api/v1/protected");

        entryPoint.commence(request, response, new BadCredentialsException("bad"));

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString()).contains("AUTHENTICATION_REQUIRED");
        assertThat(response.getContentAsString()).contains("/api/v1/protected");
    }

    @Test
    void shouldRegisterTenantContextInterceptor() {
        var config = new SecurityConfig(true, jwtAuthenticationFilter, tenantContextResolver);
        var registry = mock(InterceptorRegistry.class);

        config.addInterceptors(registry);

        verify(registry).addInterceptor(tenantContextResolver);
    }

    @Test
    void shouldBuildSecurityFilterChain() throws Exception {
        var config = new SecurityConfig(true, jwtAuthenticationFilter, tenantContextResolver);
        var httpSecurity = mock(HttpSecurity.class);
        var chain = mock(DefaultSecurityFilterChain.class);

        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.exceptionHandling(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(eq(jwtAuthenticationFilter), eq(UsernamePasswordAuthenticationFilter.class)))
            .thenReturn(httpSecurity);
        when(httpSecurity.formLogin(any())).thenReturn(httpSecurity);
        when(httpSecurity.httpBasic(any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(chain);

        SecurityFilterChain result = config.securityFilterChain(httpSecurity);

        assertThat(result).isSameAs(chain);
        verify(httpSecurity).build();
    }
}

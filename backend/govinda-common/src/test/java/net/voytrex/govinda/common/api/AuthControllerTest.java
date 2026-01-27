/*
 * Govinda ERP - Authentication Controller Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.voytrex.govinda.common.security.AuthenticationService;
import net.voytrex.govinda.common.security.UserTenantInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Test
    void shouldReturnLoginResponseWithToken() {
        String token = "token";
        LoginRequest request = new LoginRequest("user", "password", null);
        when(authenticationService.authenticate("user", "password", null)).thenReturn(token);

        AuthController controller = new AuthController(authenticationService);
        var response = controller.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isEqualTo(token);
        assertThat(response.getBody().tokenType()).isEqualTo("Bearer");
        assertThat(response.getBody().message()).contains("Bearer " + token);
    }

    @Test
    void shouldPassTenantIdToAuthenticationService() {
        UUID tenantId = UUID.randomUUID();
        LoginRequest request = new LoginRequest("user", "password", tenantId.toString());
        when(authenticationService.authenticate("user", "password", tenantId)).thenReturn("token");

        AuthController controller = new AuthController(authenticationService);
        controller.login(request);

        verify(authenticationService).authenticate("user", "password", tenantId);
    }

    @Test
    void shouldReturnUserTenantsFromService() {
        UUID userId = UUID.randomUUID();
        var authentication = org.mockito.Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId);

        List<UserTenantInfo> tenants = List.of(
            new UserTenantInfo(userId, "T1", "Tenant 1", "ROLE", "Role", true)
        );
        when(authenticationService.getUserTenants(userId)).thenReturn(tenants);

        AuthController controller = new AuthController(authenticationService);
        var result = controller.getUserTenants(authentication);

        assertThat(result).isEqualTo(tenants);
    }

    @Test
    void shouldReturnCurrentUserDetailsFromAuthentication() {
        UUID userId = UUID.randomUUID();
        var authentication = org.mockito.Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId);
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("person:read"),
            new SimpleGrantedAuthority("person:write")
        );
        org.mockito.Mockito.doReturn(authorities).when(authentication).getAuthorities();

        AuthController controller = new AuthController(authenticationService);
        Map<String, Object> result = controller.getCurrentUser(authentication);

        assertThat(result.get("userId")).isEqualTo(userId.toString());
        assertThat(result.get("authorities")).isEqualTo(List.of("person:read", "person:write"));
    }
}

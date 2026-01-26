/*
 * Govinda ERP - Authentication Controller
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.voytrex.govinda.common.security.AuthenticationService;
import net.voytrex.govinda.common.security.UserTenantInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication and authorization")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Authenticates a user and returns a JWT token. Use this token in the Authorization header as 'Bearer <token>'",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Authentication successful",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
        }
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UUID tenantId = request.tenantId() != null ? UUID.fromString(request.tenantId()) : null;
        String token = authenticationService.authenticate(request.username(), request.password(), tenantId);
        return ResponseEntity.ok(
            new LoginResponse(
                token,
                "Bearer",
                "Use this token in the Authorization header: 'Bearer " + token + "'"
            )
        );
    }

    @GetMapping("/tenants")
    @Operation(
        summary = "Get user tenants",
        description = "Returns all tenants the authenticated user can access",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<UserTenantInfo> getUserTenants(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return authenticationService.getUserTenants(userId);
    }

    @GetMapping("/me")
    @Operation(
        summary = "Get current user",
        description = "Returns information about the currently authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<String> authorities = authentication.getAuthorities().stream()
            .map(granted -> granted.getAuthority())
            .toList();
        return Map.of(
            "userId", userId.toString(),
            "authorities", authorities
        );
    }
}

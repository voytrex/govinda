/*
 * Govinda ERP - Authentication Controller
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
        description = """
            Authenticates a user and returns a JWT token.
            
            **Usage:**
            1. Send credentials in the request body
            2. Receive JWT token in response
            3. Use the token in Authorization header: `Bearer <token>`
            4. Include `X-Tenant-Id` header in subsequent requests
            
            **Note:** The tenant ID in the token must match the `X-Tenant-Id` header.
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Authentication successful",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class),
                    examples = {
                        @ExampleObject(
                            name = "Success",
                            value = """
                                {
                                  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                  "tokenType": "Bearer",
                                  "message": "Use this token in the Authorization header: 'Bearer &lt;token&gt;'"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad Request - Invalid request format",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid credentials",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(
                            name = "Invalid Credentials",
                            value = """
                                {
                                  "errorCode": "AUTHENTICATION_FAILED",
                                  "message": "Invalid username or password",
                                  "timestamp": "2024-01-15T10:30:00Z",
                                  "path": "/api/v1/auth/login"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UUID tenantId = request.tenantId() != null ? UUID.fromString(request.tenantId()) : null;
        String token = authenticationService.authenticate(request.username(), request.password(), tenantId);
        return ResponseEntity.ok(
            new LoginResponse(
                token,
                "Bearer",
                "Use this token in the Authorization header: 'Bearer "
                    + token + "'"
            )
        );
    }

    @GetMapping("/tenants")
    @Operation(
        summary = "Get user tenants",
        description = "Returns all tenants the authenticated user can access",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of accessible tenants",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Authentication required",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public List<UserTenantInfo> getUserTenants(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return authenticationService.getUserTenants(userId);
    }

    @GetMapping("/me")
    @Operation(
        summary = "Get current user",
        description = "Returns information about the currently authenticated user including user ID and authorities",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Current user information",
                content = @Content(
                    mediaType = "application/json",
                    examples = {
                        @ExampleObject(
                            name = "User Info",
                            value = """
                                {
                                  "userId": "123e4567-e89b-12d3-a456-426614174000",
                                  "authorities": ["person:read", "person:write"]
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Authentication required",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
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

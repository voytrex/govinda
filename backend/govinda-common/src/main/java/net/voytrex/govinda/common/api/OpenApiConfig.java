/*
 * Govinda ERP - OpenAPI Configuration
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration with comprehensive API documentation.
 *
 * Features:
 * - JWT authentication support
 * - Multi-tenancy header documentation
 * - Standardized error responses
 * - Server environments (local, dev, staging, prod)
 * - Contact and license information
 * - External documentation links
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(createApiInfo())
            .servers(createServers())
            .components(createComponents())
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private Info createApiInfo() {
        return new Info()
            .title("Govinda ERP API")
            .version("v1.0.0")
            .description("""
                # Govinda ERP API
                
                Open Source Enterprise Resource Planning for Swiss Health Insurance
                
                ## Overview
                
                This REST API provides comprehensive functionality for managing Swiss health insurance operations,
                including master data, products, contracts, premium calculation, and billing.
                
                ## Authentication
                
                1. Use the `/api/v1/auth/login` endpoint to authenticate
                2. Copy the JWT token from the response
                3. Click the **"Authorize"** button above and enter: `Bearer <your-token>`
                4. All subsequent requests will include the token automatically
                
                ## Multi-Tenancy
                
                All API requests require the `X-Tenant-Id` header with a valid tenant UUID.
                The tenant ID is also included in the JWT token after login.
                
                ## Error Handling
                
                The API uses standardized error responses. All errors follow this structure:
                
                - **400 Bad Request**: Validation errors (with field-level details)
                - **401 Unauthorized**: Authentication required or failed
                - **403 Forbidden**: Access denied (insufficient permissions)
                - **404 Not Found**: Resource not found
                - **409 Conflict**: Duplicate entity or concurrent modification
                - **422 Unprocessable Entity**: Business rule violations
                - **500 Internal Server Error**: Unexpected server errors
                
                ## Rate Limiting
                
                API rate limits apply per tenant. Check response headers for rate limit information.
                
                ## Internationalization
                
                The API supports multiple languages (DE, FR, IT, EN). Use the `Accept-Language` header
                to specify your preferred language. Default is German (DE).
                
                ## Versioning
                
                This API follows semantic versioning. The current version is v1. Breaking changes will
                result in a new major version.
                """.stripIndent())
            .contact(
                new Contact()
                    .name("Voytrex")
                    .url("https://www.voytrex.net/")
                    .email("info@voytrex.net")
            )
            .license(
                new License()
                    .name("Apache 2.0")
                    .url("https://opensource.org/licenses/Apache-2.0")
            );
    }

    private List<Server> createServers() {
        return List.of(
            new Server()
                .url("http://localhost:8080")
                .description("Local Development Server"),
            new Server()
                .url("https://api-dev.govinda.voytrex.net")
                .description("Development Environment"),
            new Server()
                .url("https://api-staging.govinda.voytrex.net")
                .description("Staging Environment"),
            new Server()
                .url("https://api.govinda.voytrex.net")
                .description("Production Environment")
        );
    }

    private Components createComponents() {
        Components components = new Components();
        
        // Security Schemes
        components.addSecuritySchemes(
            "bearerAuth",
            new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token obtained from /api/v1/auth/login endpoint. " +
                    "Include in Authorization header as: `Bearer <token>`")
        );
        
        // Global Headers
        components.addParameters(
            "X-Tenant-Id",
            new Parameter()
                .in("header")
                .name("X-Tenant-Id")
                .required(true)
                .description("Tenant UUID for multi-tenant isolation. " +
                    "Required for all authenticated requests. " +
                    "The tenant ID must match the tenant in the JWT token.")
                .schema(new Schema<>().type("string").format("uuid"))
                .example("550e8400-e29b-41d4-a716-446655440000")
        );
        
        components.addParameters(
            "X-User-Id",
            new Parameter()
                .in("header")
                .name("X-User-Id")
                .required(false)
                .description("User UUID for audit purposes. " +
                    "Automatically extracted from JWT token if not provided.")
                .schema(new Schema<>().type("string").format("uuid"))
                .example("123e4567-e89b-12d3-a456-426614174000")
        );
        
        components.addParameters(
            "Accept-Language",
            new Parameter()
                .in("header")
                .name("Accept-Language")
                .required(false)
                .description("Preferred language for responses. " +
                    "Supported: DE (German), FR (French), IT (Italian), EN (English). " +
                    "Default: DE")
                .schema(new Schema<String>().type("string")._default("DE")) // NOSONAR - OpenAPI uses raw types
                .example("DE")
        );
        
        // Error Response Schemas
        components.addSchemas("ErrorResponse", createErrorResponseSchema());
        components.addSchemas("ErrorDetail", createErrorDetailSchema());
        
        // Common Error Responses
        components.addResponses("BadRequest", createBadRequestResponse());
        components.addResponses("Unauthorized", createUnauthorizedResponse());
        components.addResponses("Forbidden", createForbiddenResponse());
        components.addResponses("NotFound", createNotFoundResponse());
        components.addResponses("Conflict", createConflictResponse());
        components.addResponses("UnprocessableEntity", createUnprocessableEntityResponse());
        components.addResponses("InternalServerError", createInternalServerErrorResponse());
        
        return components;
    }

    private Schema<?> createErrorResponseSchema() {
        return new Schema<>()
            .type("object")
            .description("Standard error response structure")
            .addProperty("errorCode", new Schema<>()
                .type("string")
                .description("Machine-readable error code")
                .example("ENTITY_NOT_FOUND"))
            .addProperty("message", new Schema<>()
                .type("string")
                .description("Human-readable error message (localized)")
                .example("Person with ID 550e8400-e29b-41d4-a716-446655440000 not found"))
            .addProperty("timestamp", new Schema<>()
                .type("string")
                .format("date-time")
                .description("Timestamp when the error occurred")
                .example("2024-01-15T10:30:00Z"))
            .addProperty("path", new Schema<>()
                .type("string")
                .description("API path where the error occurred")
                .example("/api/v1/masterdata/persons/550e8400-e29b-41d4-a716-446655440000"))
            .addProperty("details", new Schema<>()
                .type("array")
                .description("Optional field-level validation errors")
                .items(new Schema<Object>().$ref("#/components/schemas/ErrorDetail"))); // NOSONAR - OpenAPI uses raw types
    }

    private Schema<?> createErrorDetailSchema() {
        return new Schema<>()
            .type("object")
            .description("Field-level validation error detail")
            .addProperty("field", new Schema<>()
                .type("string")
                .description("Field name that failed validation")
                .example("ahvNr"))
            .addProperty("message", new Schema<>()
                .type("string")
                .description("Validation error message")
                .example("Invalid AHV number format"))
            .addProperty("rejectedValue", new Schema<>()
                .type("string")
                .description("The value that was rejected")
                .example("123456789"));
    }

    private ApiResponse createBadRequestResponse() {
        return new ApiResponse()
            .description("Bad Request - Validation error with field details")
            .content(new Content().addMediaType(
                "application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                    .example(new Example().value("""
                        {
                          "errorCode": "VALIDATION_ERROR",
                          "message": "Request validation failed",
                          "timestamp": "2024-01-15T10:30:00Z",
                          "path": "/api/v1/masterdata/persons",
                          "details": [
                            {
                              "field": "ahvNr",
                              "message": "Invalid AHV number format",
                              "rejectedValue": "123456789"
                            }
                          ]
                        }
                        """))
            ));
    }

    private ApiResponse createUnauthorizedResponse() {
        return new ApiResponse()
            .description("Unauthorized - Authentication required or failed")
            .content(new Content().addMediaType(
                "application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                    .example(new Example().value("""
                        {
                          "errorCode": "AUTHENTICATION_REQUIRED",
                          "message": "Authentication required",
                          "timestamp": "2024-01-15T10:30:00Z",
                          "path": "/api/v1/masterdata/persons"
                        }
                        """))
            ));
    }

    private ApiResponse createForbiddenResponse() {
        return new ApiResponse()
            .description("Forbidden - Insufficient permissions or unauthorized tenant access")
            .content(new Content().addMediaType(
                "application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                    .example(new Example().value("""
                        {
                          "errorCode": "ACCESS_DENIED",
                          "message": "Insufficient permissions",
                          "timestamp": "2024-01-15T10:30:00Z",
                          "path": "/api/v1/masterdata/persons"
                        }
                        """))
            ));
    }

    private ApiResponse createNotFoundResponse() {
        return new ApiResponse()
            .description("Not Found - Resource not found")
            .content(new Content().addMediaType(
                "application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                    .example(new Example().value("""
                        {
                          "errorCode": "ENTITY_NOT_FOUND",
                          "message": "Person with ID 550e8400-e29b-41d4-a716-446655440000 not found",
                          "timestamp": "2024-01-15T10:30:00Z",
                          "path": "/api/v1/masterdata/persons/550e8400-e29b-41d4-a716-446655440000"
                        }
                        """))
            ));
    }

    private ApiResponse createConflictResponse() {
        return new ApiResponse()
            .description("Conflict - Duplicate entity or concurrent modification")
            .content(new Content().addMediaType(
                "application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                    .example(new Example().value("""
                        {
                          "errorCode": "ENTITY_DUPLICATE",
                          "message": "Person with AHV number 756.1234.5678.90 already exists",
                          "timestamp": "2024-01-15T10:30:00Z",
                          "path": "/api/v1/masterdata/persons"
                        }
                        """))
            ));
    }

    private ApiResponse createUnprocessableEntityResponse() {
        return new ApiResponse()
            .description("Unprocessable Entity - Business rule violation")
            .content(new Content().addMediaType(
                "application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                    .example(new Example().value("""
                        {
                          "errorCode": "BUSINESS_RULE_VIOLATION",
                          "message": "Cannot change marital status: effective date must be in the future",
                          "timestamp": "2024-01-15T10:30:00Z",
                          "path": "/api/v1/masterdata/persons/550e8400-e29b-41d4-a716-446655440000/marital-status-change"
                        }
                        """))
            ));
    }

    private ApiResponse createInternalServerErrorResponse() {
        return new ApiResponse()
            .description("Internal Server Error - Unexpected server error")
            .content(new Content().addMediaType(
                "application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                    .example(new Example().value("""
                        {
                          "errorCode": "INTERNAL_ERROR",
                          "message": "An unexpected error occurred",
                          "timestamp": "2024-01-15T10:30:00Z",
                          "path": "/api/v1/masterdata/persons"
                        }
                        """))
            ));
    }
}

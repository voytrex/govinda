/*
 * Govinda ERP - OpenAPI Configuration
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration with JWT authentication support.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(
                new Info()
                    .title("Govinda ERP API")
                    .version("v1")
                    .description("""
                        Open Source Enterprise Resource Planning for Swiss Health Insurance

                        ## Authentication

                        1. Use the `/api/v1/auth/login` endpoint to authenticate
                        2. Copy the JWT token from the response
                        3. Click the "Authorize" button above and enter: `Bearer <your-token>`
                        4. All subsequent requests will include the token automatically

                        ## Multi-Tenancy

                        All API requests require the `X-Tenant-Id` header with a valid tenant UUID.
                        The tenant ID is also included in the JWT token after login.
                        """.stripIndent())
            )
            .components(
                new Components().addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token obtained from /api/v1/auth/login")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

/*
 * Govinda ERP - OpenAPI Configuration Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

    @Test
    void shouldBuildOpenApiDefinition() {
        OpenApiConfig config = new OpenApiConfig();

        var openApi = config.customOpenAPI();

        assertThat(openApi.getInfo().getTitle()).isEqualTo("Govinda ERP API");
        assertThat(openApi.getInfo().getVersion()).isEqualTo("v1.0.0");
        assertThat(openApi.getServers()).hasSize(4);
        assertThat(openApi.getSecurity()).isNotEmpty();
        assertThat(openApi.getSecurity().get(0)).containsKey("bearerAuth");
        assertThat(openApi.getComponents().getSecuritySchemes()).containsKey("bearerAuth");
        assertThat(openApi.getComponents().getParameters())
            .containsKeys("X-Tenant-Id", "X-User-Id", "Accept-Language");
        assertThat(openApi.getComponents().getSchemas()).containsKeys("ErrorResponse", "ErrorDetail");
        assertThat(openApi.getComponents().getResponses()).containsKeys(
            "BadRequest",
            "Unauthorized",
            "Forbidden",
            "NotFound",
            "Conflict",
            "UnprocessableEntity",
            "InternalServerError"
        );
    }
}

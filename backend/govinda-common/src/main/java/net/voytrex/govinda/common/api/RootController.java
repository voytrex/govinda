/*
 * Govinda ERP - Root Controller
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Root controller for the API.
 * Provides a helpful response at the root path and redirects to Swagger UI.
 */
@RestController
@Tag(name = "Root", description = "API root and information")
public class RootController {

    @GetMapping("/")
    @Operation(summary = "API root", description = "Returns API information and links")
    public ResponseEntity<ApiInfo> root() {
        return ResponseEntity.ok(
            new ApiInfo(
                "Govinda ERP API",
                "v1",
                "Open Source Enterprise Resource Planning for Swiss Health Insurance",
                "/swagger-ui.html",
                "/api-docs",
                "/actuator/health"
            )
        );
    }

    @GetMapping("/swagger-ui")
    @Operation(hidden = true)
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui.html");
    }
}

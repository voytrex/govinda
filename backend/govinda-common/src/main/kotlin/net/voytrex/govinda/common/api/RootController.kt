/*
 * Govinda ERP - Root Controller
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

/**
 * Root controller for the API.
 * Provides a helpful response at the root path and redirects to Swagger UI.
 */
@RestController
@Tag(name = "Root", description = "API root and information")
class RootController {

    @GetMapping("/")
    @Operation(summary = "API root", description = "Returns API information and links")
    fun root(): ResponseEntity<ApiInfo> {
        return ResponseEntity.ok(
            ApiInfo(
                name = "Govinda ERP API",
                version = "v1",
                description = "Open Source Enterprise Resource Planning for Swiss Health Insurance",
                documentation = "/swagger-ui.html",
                apiDocs = "/api-docs",
                health = "/actuator/health"
            )
        )
    }

    @GetMapping("/swagger-ui")
    @Operation(hidden = true)
    fun redirectToSwagger(): RedirectView {
        return RedirectView("/swagger-ui.html")
    }
}

/**
 * API information response.
 */
data class ApiInfo(
    val name: String,
    val version: String,
    val description: String,
    val documentation: String,
    val apiDocs: String,
    val health: String
)

/*
 * Govinda ERP - Security Configuration
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

/**
 * Security configuration for Govinda ERP.
 *
 * Swagger UI and API docs are open by default (no authentication required).
 * This is the recommended approach for development environments.
 *
 * For production, you can:
 * 1. Disable Swagger UI entirely via springdoc.swagger-ui.enabled=false
 * 2. Add authentication/authorization to protect Swagger UI
 * 3. Use a reverse proxy with authentication in front of the application
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${springdoc.swagger-ui.enabled:true}")
    private val swaggerUiEnabled: Boolean
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // Disable CSRF for stateless API
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authz ->
                authz
                    // Swagger UI and API docs - open for development
                    // Best practice: Open in dev, protected/disabled in production
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                    ).permitAll()
                    // Actuator health endpoint - open for monitoring
                    .requestMatchers("/actuator/health").permitAll()
                    // All other endpoints - currently open (TODO: implement authentication)
                    // API endpoints should require X-Tenant-Id header validation
                    .anyRequest().permitAll()
            }
            // Disable default login form and basic auth
            .formLogin { it.disable() }
            .httpBasic { it.disable() }

        return http.build()
    }
}

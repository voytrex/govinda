/*
 * Govinda ERP - Portal Profile REST Controller
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import net.voytrex.govinda.portal.application.PortalIdentityService;
import net.voytrex.govinda.portal.application.PortalProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portal/v1/profile")
@Tag(name = "Portal Profile", description = "Customer portal profile access")
public class PortalProfileController {
    private final PortalProfileService portalProfileService;
    private final PortalIdentityService portalIdentityService;

    public PortalProfileController(
        PortalProfileService portalProfileService,
        PortalIdentityService portalIdentityService
    ) {
        this.portalProfileService = portalProfileService;
        this.portalIdentityService = portalIdentityService;
    }

    @GetMapping
    @Operation(
        summary = "Get portal profile",
        description = "Returns the profile for the authenticated portal user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public PortalProfileResponse getProfile(
        @RequestHeader("X-Tenant-Id") UUID tenantId,
        @RequestHeader("X-Portal-Subject") String subject
    ) {
        var personId = portalIdentityService.resolvePersonId(tenantId, subject);
        var person = portalProfileService.getProfile(tenantId, personId);
        return PortalProfileMapper.toResponse(person);
    }
}

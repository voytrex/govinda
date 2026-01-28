/*
 * Govinda ERP - Portal Document REST Controller
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.portal.application.PortalDocumentService;
import net.voytrex.govinda.portal.application.PortalIdentityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portal/v1/documents")
@Tag(name = "Portal Documents", description = "Customer portal documents")
public class PortalDocumentController {
    private final PortalDocumentService portalDocumentService;
    private final PortalIdentityService portalIdentityService;

    public PortalDocumentController(
        PortalDocumentService portalDocumentService,
        PortalIdentityService portalIdentityService
    ) {
        this.portalDocumentService = portalDocumentService;
        this.portalIdentityService = portalIdentityService;
    }

    @GetMapping
    @Operation(
        summary = "List portal documents",
        description = "Returns documents for the authenticated portal user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<PortalDocumentResponse> listDocuments(
        @RequestHeader("X-Tenant-Id") UUID tenantId,
        @RequestHeader("X-Portal-Subject") String subject
    ) {
        var personId = portalIdentityService.resolvePersonId(tenantId, subject);
        return portalDocumentService.listDocuments(tenantId, personId).stream()
            .map(PortalDocumentMapper::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get portal document",
        description = "Returns document metadata by ID for the authenticated portal user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public PortalDocumentResponse getDocument(
        @RequestHeader("X-Tenant-Id") UUID tenantId,
        @RequestHeader("X-Portal-Subject") String subject,
        @PathVariable UUID id
    ) {
        var personId = portalIdentityService.resolvePersonId(tenantId, subject);
        var document = portalDocumentService.getDocument(tenantId, personId, id);
        return PortalDocumentMapper.toResponse(document);
    }
}

/*
 * Govinda ERP - Portal Case REST Controller
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.cases.application.CaseService;
import net.voytrex.govinda.cases.application.CreateCaseCommand;
import net.voytrex.govinda.portal.application.PortalIdentityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portal/v1/cases")
@Tag(name = "Portal Cases", description = "Customer portal service requests")
public class PortalCaseController {
    private final CaseService caseService;
    private final PortalIdentityService portalIdentityService;

    public PortalCaseController(
        CaseService caseService,
        PortalIdentityService portalIdentityService
    ) {
        this.caseService = caseService;
        this.portalIdentityService = portalIdentityService;
    }

    @PostMapping
    @Operation(
        summary = "Create portal case",
        description = "Creates a new service request for the authenticated portal user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PortalCaseResponse> createCase(
        @RequestHeader("X-Tenant-Id") UUID tenantId,
        @RequestHeader("X-Portal-Subject") String subject,
        @Valid @RequestBody PortalCaseCreateRequest request
    ) {
        var personId = portalIdentityService.resolvePersonId(tenantId, subject);
        var command = new CreateCaseCommand(
            tenantId,
            personId,
            request.type(),
            request.subject(),
            request.description()
        );
        var caseRecord = caseService.createCase(command);
        var response = PortalCaseMapper.toResponse(caseRecord);
        return ResponseEntity
            .created(URI.create("/api/portal/v1/cases/" + caseRecord.getId()))
            .body(response);
    }

    @GetMapping
    @Operation(
        summary = "List portal cases",
        description = "Returns all cases for the authenticated portal user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<PortalCaseResponse> listCases(
        @RequestHeader("X-Tenant-Id") UUID tenantId,
        @RequestHeader("X-Portal-Subject") String subject
    ) {
        var personId = portalIdentityService.resolvePersonId(tenantId, subject);
        return caseService.listCases(tenantId, personId).stream()
            .map(PortalCaseMapper::toResponse)
            .toList();
    }
}

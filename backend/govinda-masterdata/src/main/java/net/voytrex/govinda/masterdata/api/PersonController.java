/*
 * Govinda ERP - Person REST Controller
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.voytrex.govinda.common.api.PageResponse;
import net.voytrex.govinda.masterdata.application.ChangeMaritalStatusCommand;
import net.voytrex.govinda.masterdata.application.ChangeNameCommand;
import net.voytrex.govinda.masterdata.application.CreatePersonCommand;
import net.voytrex.govinda.masterdata.application.PersonSearchQuery;
import net.voytrex.govinda.masterdata.application.PersonService;
import net.voytrex.govinda.masterdata.application.UpdatePersonCommand;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/masterdata/persons")
@Tag(name = "Persons", description = "Person management (Versicherte)")
public class PersonController {
    private static final String BEARER_AUTH = "bearerAuth";
    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String READ_AUTHORITY = "hasAuthority('person:read')";
    private static final String WRITE_AUTHORITY = "hasAuthority('person:write')";

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    @Operation(
        summary = "List persons",
        description = "Returns paginated list of persons",
        security = @SecurityRequirement(name = BEARER_AUTH)
    )
    @PreAuthorize(READ_AUTHORITY)
    public PageResponse<PersonResponse> listPersons(
        @RequestHeader(TENANT_HEADER) UUID tenantId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "lastName") String sortBy,
        @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        var pageable = PageRequest.of(
            page,
            Math.min(size, 100),
            Sort.by(Sort.Direction.valueOf(sortDir.toUpperCase(Locale.ROOT)), sortBy)
        );
        var result = personService.listPersons(tenantId, pageable);
        return new PageResponse<>(
            result.getContent().stream().map(PersonMapper::toResponse).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.isFirst(),
            result.isLast()
        );
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search persons",
        description = "Search persons by various criteria",
        security = @SecurityRequirement(name = BEARER_AUTH)
    )
    @PreAuthorize(READ_AUTHORITY)
    // CHECKSTYLE:OFF: ParameterNumber - Search method requires multiple optional parameters
    public PageResponse<PersonResponse> searchPersons(
        @RequestHeader(TENANT_HEADER) UUID tenantId,
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) String firstName,
        @RequestParam(required = false) String ahvNr,
        @RequestParam(required = false) LocalDate dateOfBirth,
        @RequestParam(required = false) String postalCode,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        // CHECKSTYLE:ON: ParameterNumber
        var query = new PersonSearchQuery(
            tenantId,
            lastName,
            firstName,
            ahvNr,
            dateOfBirth,
            postalCode
        );
        var pageable = PageRequest.of(page, Math.min(size, 100));
        var result = personService.searchPersons(query, pageable);
        return new PageResponse<>(
            result.getContent().stream().map(PersonMapper::toResponse).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.isFirst(),
            result.isLast()
        );
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get person",
        description = "Returns a person by ID",
        security = @SecurityRequirement(name = BEARER_AUTH)
    )
    @PreAuthorize(READ_AUTHORITY)
    public PersonResponse getPerson(
        @RequestHeader(TENANT_HEADER) UUID tenantId,
        @PathVariable UUID id
    ) {
        return PersonMapper.toResponse(personService.getPerson(id, tenantId));
    }

    @PostMapping
    @Operation(
        summary = "Create person",
        description = "Creates a new person",
        security = @SecurityRequirement(name = BEARER_AUTH)
    )
    @PreAuthorize(WRITE_AUTHORITY)
    public ResponseEntity<PersonResponse> createPerson(
        @RequestHeader(TENANT_HEADER) UUID tenantId,
        @Valid @RequestBody CreatePersonRequest request
    ) {
        var command = new CreatePersonCommand(
            tenantId,
            request.ahvNr(),
            request.lastName(),
            request.firstName(),
            request.dateOfBirth(),
            request.gender(),
            request.maritalStatus(),
            request.nationality(),
            request.preferredLanguage()
        );
        var person = personService.createPerson(command);
        return ResponseEntity
            .created(URI.create("/api/v1/masterdata/persons/" + person.getId()))
            .body(PersonMapper.toResponse(person));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update person",
        description = "Updates non-historical person data",
        security = @SecurityRequirement(name = BEARER_AUTH)
    )
    @PreAuthorize(WRITE_AUTHORITY)
    public PersonResponse updatePerson(
        @RequestHeader(TENANT_HEADER) UUID tenantId,
        @PathVariable UUID id,
        @Valid @RequestBody UpdatePersonRequest request
    ) {
        var command = new UpdatePersonCommand(
            tenantId,
            id,
            request.nationality(),
            request.preferredLanguage()
        );
        return PersonMapper.toResponse(personService.updatePerson(command));
    }

    @PostMapping("/{id}/name-change")
    @Operation(
        summary = "Change name",
        description = "Changes person's name (creates history)",
        security = @SecurityRequirement(name = BEARER_AUTH)
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(WRITE_AUTHORITY)
    public PersonResponse changeName(
        @RequestHeader(TENANT_HEADER) UUID tenantId,
        @RequestHeader("X-User-Id") UUID userId,
        @PathVariable UUID id,
        @Valid @RequestBody ChangeNameRequest request
    ) {
        var command = new ChangeNameCommand(
            tenantId,
            id,
            request.newLastName(),
            request.newFirstName(),
            request.reason(),
            request.effectiveDate(),
            userId
        );
        return PersonMapper.toResponse(personService.changeName(command));
    }

    @PostMapping("/{id}/marital-status-change")
    @Operation(
        summary = "Change marital status",
        description = "Changes person's marital status (creates history)",
        security = @SecurityRequirement(name = BEARER_AUTH)
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(WRITE_AUTHORITY)
    public PersonResponse changeMaritalStatus(
        @RequestHeader(TENANT_HEADER) UUID tenantId,
        @RequestHeader("X-User-Id") UUID userId,
        @PathVariable UUID id,
        @Valid @RequestBody ChangeMaritalStatusRequest request
    ) {
        var command = new ChangeMaritalStatusCommand(
            tenantId,
            id,
            request.newStatus(),
            request.reason(),
            request.effectiveDate(),
            userId
        );
        return PersonMapper.toResponse(personService.changeMaritalStatus(command));
    }

    @GetMapping("/{id}/history")
    @Operation(
        summary = "Get person history",
        description = "Returns the change history of a person",
        security = @SecurityRequirement(name = BEARER_AUTH)
    )
    @PreAuthorize(READ_AUTHORITY)
    public List<PersonHistoryResponse> getPersonHistory(
        @RequestHeader(TENANT_HEADER) UUID tenantId,
        @PathVariable UUID id
    ) {
        return personService.getPersonHistory(id, tenantId).stream()
            .map(PersonMapper::toResponse)
            .toList();
    }

    @GetMapping("/{id}/history/at/{date}")
    @Operation(
        summary = "Get person state at date",
        description = "Returns the person's state at a specific date",
        security = @SecurityRequirement(name = BEARER_AUTH)
    )
    @PreAuthorize(READ_AUTHORITY)
    public ResponseEntity<PersonHistoryResponse> getPersonStateAt(
        @RequestHeader(TENANT_HEADER) UUID tenantId,
        @PathVariable UUID id,
        @PathVariable @Parameter(description = "Date in YYYY-MM-DD format") LocalDate date
    ) {
        return personService.getPersonStateAt(id, tenantId, date)
            .map(PersonMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

/*
 * Govinda ERP - Person REST Controller
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import net.voytrex.govinda.common.api.PageResponse
import net.voytrex.govinda.common.domain.model.AgeGroup
import net.voytrex.govinda.common.domain.model.Gender
import net.voytrex.govinda.common.domain.model.Language
import net.voytrex.govinda.common.domain.model.MaritalStatus
import net.voytrex.govinda.masterdata.application.*
import net.voytrex.govinda.masterdata.domain.model.Person
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/v1/masterdata/persons")
@Tag(name = "Persons", description = "Person management (Versicherte)")
class PersonController(
    private val personService: PersonService
) {

    @GetMapping
    @Operation(summary = "List persons", description = "Returns paginated list of persons")
    fun listPersons(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "lastName") sortBy: String,
        @RequestParam(defaultValue = "ASC") sortDir: String
    ): PageResponse<PersonResponse> {
        val pageable = PageRequest.of(
            page,
            size.coerceAtMost(100),
            Sort.by(Sort.Direction.valueOf(sortDir.uppercase()), sortBy)
        )
        val result = personService.listPersons(tenantId, pageable)
        return PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            first = result.isFirst,
            last = result.isLast
        )
    }

    @GetMapping("/search")
    @Operation(summary = "Search persons", description = "Search persons by various criteria")
    fun searchPersons(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @RequestParam(required = false) lastName: String?,
        @RequestParam(required = false) firstName: String?,
        @RequestParam(required = false) ahvNr: String?,
        @RequestParam(required = false) dateOfBirth: LocalDate?,
        @RequestParam(required = false) postalCode: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): PageResponse<PersonResponse> {
        val query = PersonSearchQuery(
            tenantId = tenantId,
            lastName = lastName,
            firstName = firstName,
            ahvNr = ahvNr,
            dateOfBirth = dateOfBirth,
            postalCode = postalCode
        )
        val pageable = PageRequest.of(page, size.coerceAtMost(100))
        val result = personService.searchPersons(query, pageable)
        return PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            first = result.isFirst,
            last = result.isLast
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get person", description = "Returns a person by ID")
    fun getPerson(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable id: UUID
    ): PersonResponse {
        return personService.getPerson(id, tenantId).toResponse()
    }

    @PostMapping
    @Operation(summary = "Create person", description = "Creates a new person")
    fun createPerson(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @Valid @RequestBody request: CreatePersonRequest
    ): ResponseEntity<PersonResponse> {
        val command = CreatePersonCommand(
            tenantId = tenantId,
            ahvNr = request.ahvNr,
            lastName = request.lastName,
            firstName = request.firstName,
            dateOfBirth = request.dateOfBirth,
            gender = request.gender,
            maritalStatus = request.maritalStatus,
            nationality = request.nationality,
            preferredLanguage = request.preferredLanguage
        )
        val person = personService.createPerson(command)
        return ResponseEntity
            .created(URI("/api/v1/masterdata/persons/${person.id}"))
            .body(person.toResponse())
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update person", description = "Updates non-historical person data")
    fun updatePerson(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdatePersonRequest
    ): PersonResponse {
        val command = UpdatePersonCommand(
            tenantId = tenantId,
            personId = id,
            nationality = request.nationality,
            preferredLanguage = request.preferredLanguage
        )
        return personService.updatePerson(command).toResponse()
    }

    @PostMapping("/{id}/name-change")
    @Operation(summary = "Change name", description = "Changes person's name (creates history)")
    @ResponseStatus(HttpStatus.OK)
    fun changeName(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @RequestHeader("X-User-Id") userId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: ChangeNameRequest
    ): PersonResponse {
        val command = ChangeNameCommand(
            tenantId = tenantId,
            personId = id,
            newLastName = request.newLastName,
            newFirstName = request.newFirstName,
            reason = request.reason,
            effectiveDate = request.effectiveDate,
            changedBy = userId
        )
        return personService.changeName(command).toResponse()
    }

    @PostMapping("/{id}/marital-status-change")
    @Operation(summary = "Change marital status", description = "Changes person's marital status (creates history)")
    @ResponseStatus(HttpStatus.OK)
    fun changeMaritalStatus(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @RequestHeader("X-User-Id") userId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: ChangeMaritalStatusRequest
    ): PersonResponse {
        val command = ChangeMaritalStatusCommand(
            tenantId = tenantId,
            personId = id,
            newStatus = request.newStatus,
            reason = request.reason,
            effectiveDate = request.effectiveDate,
            changedBy = userId
        )
        return personService.changeMaritalStatus(command).toResponse()
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get person history", description = "Returns the change history of a person")
    fun getPersonHistory(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable id: UUID
    ): List<PersonHistoryResponse> {
        return personService.getPersonHistory(id, tenantId).map { it.toResponse() }
    }

    @GetMapping("/{id}/history/at/{date}")
    @Operation(summary = "Get person state at date", description = "Returns the person's state at a specific date")
    fun getPersonStateAt(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable id: UUID,
        @PathVariable @Parameter(description = "Date in YYYY-MM-DD format") date: LocalDate
    ): PersonHistoryResponse? {
        return personService.getPersonStateAt(id, tenantId, date)?.toResponse()
    }
}

// ═══════════════════════════════════════════════════════════════
// Request DTOs
// ═══════════════════════════════════════════════════════════════

data class CreatePersonRequest(
    @field:NotBlank(message = "AHV number is required")
    val ahvNr: String,

    @field:NotBlank(message = "Last name is required")
    val lastName: String,

    @field:NotBlank(message = "First name is required")
    val firstName: String,

    @field:Past(message = "Date of birth must be in the past")
    val dateOfBirth: LocalDate,

    val gender: Gender,

    val maritalStatus: MaritalStatus? = null,

    val nationality: String? = "CHE",

    val preferredLanguage: Language? = Language.DE
)

data class UpdatePersonRequest(
    val nationality: String? = null,
    val preferredLanguage: Language? = null
)

data class ChangeNameRequest(
    @field:NotBlank(message = "New last name is required")
    val newLastName: String,

    val newFirstName: String? = null,

    @field:NotBlank(message = "Reason is required")
    val reason: String,

    val effectiveDate: LocalDate
)

data class ChangeMaritalStatusRequest(
    val newStatus: MaritalStatus,

    @field:NotBlank(message = "Reason is required")
    val reason: String,

    val effectiveDate: LocalDate
)

// ═══════════════════════════════════════════════════════════════
// Response DTOs
// ═══════════════════════════════════════════════════════════════

data class PersonResponse(
    val id: UUID,
    val ahvNr: String,
    val lastName: String,
    val firstName: String,
    val fullName: String,
    val dateOfBirth: LocalDate,
    val gender: Gender,
    val age: Int,
    val ageGroup: AgeGroup,
    val maritalStatus: MaritalStatus?,
    val nationality: String?,
    val preferredLanguage: Language,
    val status: String
)

data class PersonHistoryResponse(
    val historyId: UUID,
    val lastName: String,
    val firstName: String,
    val maritalStatus: MaritalStatus?,
    val validFrom: LocalDate,
    val validTo: LocalDate?,
    val mutationType: String,
    val mutationReason: String?,
    val changedBy: UUID,
    val recordedAt: String
)

// ═══════════════════════════════════════════════════════════════
// Mappers
// ═══════════════════════════════════════════════════════════════

fun Person.toResponse() = PersonResponse(
    id = id,
    ahvNr = ahvNr.value,
    lastName = lastName,
    firstName = firstName,
    fullName = fullName(),
    dateOfBirth = dateOfBirth,
    gender = gender,
    age = ageAt(LocalDate.now()),
    ageGroup = ageGroupAt(LocalDate.now()),
    maritalStatus = maritalStatus,
    nationality = nationality,
    preferredLanguage = preferredLanguage,
    status = status.name
)

fun PersonHistoryEntry.toResponse() = PersonHistoryResponse(
    historyId = historyId,
    lastName = lastName,
    firstName = firstName,
    maritalStatus = maritalStatus,
    validFrom = validFrom,
    validTo = validTo,
    mutationType = mutationType.name,
    mutationReason = mutationReason,
    changedBy = changedBy,
    recordedAt = recordedAt.toString()
)

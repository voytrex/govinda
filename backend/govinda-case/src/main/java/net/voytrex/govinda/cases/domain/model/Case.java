/*
 * Govinda ERP - Case Entity
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.cases.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "portal_case")
public class Case {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "tenant_id", updatable = false, nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", updatable = false, nullable = false)
    private UUID personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 40, nullable = false)
    private CaseType type;

    @Column(name = "subject", length = 200, nullable = false)
    private String subject;

    @Column(name = "description", length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private CaseStatus status = CaseStatus.OPEN;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Version
    @Column(name = "version", nullable = false)
    private long version = 0L;

    protected Case() {
    }

    public Case(
        UUID tenantId,
        UUID personId,
        CaseType type,
        String subject,
        String description
    ) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID must not be null");
        }
        if (personId == null) {
            throw new IllegalArgumentException("Person ID must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Case type must not be null");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject must not be blank");
        }
        this.tenantId = tenantId;
        this.personId = personId;
        this.type = type;
        this.subject = subject;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getPersonId() {
        return personId;
    }

    public CaseType getType() {
        return type;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

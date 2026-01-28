/*
 * Govinda ERP - Portal Document Entity
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.domain.model;

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
@Table(name = "portal_document")
public class PortalDocument {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "tenant_id", updatable = false, nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", updatable = false, nullable = false)
    private UUID personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 40, nullable = false)
    private PortalDocumentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PortalDocumentStatus status = PortalDocumentStatus.AVAILABLE;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "storage_key", length = 500, nullable = false)
    private String storageKey;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Version
    @Column(name = "version", nullable = false)
    private long version = 0L;

    protected PortalDocument() {
    }

    public PortalDocument(
        UUID tenantId,
        UUID personId,
        PortalDocumentType type,
        PortalDocumentStatus status,
        String title,
        String storageKey
    ) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID must not be null");
        }
        if (personId == null) {
            throw new IllegalArgumentException("Person ID must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Document type must not be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Document status must not be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be blank");
        }
        if (storageKey == null || storageKey.isBlank()) {
            throw new IllegalArgumentException("Storage key must not be blank");
        }
        this.tenantId = tenantId;
        this.personId = personId;
        this.type = type;
        this.status = status;
        this.title = title;
        this.storageKey = storageKey;
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

    public PortalDocumentType getType() {
        return type;
    }

    public PortalDocumentStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

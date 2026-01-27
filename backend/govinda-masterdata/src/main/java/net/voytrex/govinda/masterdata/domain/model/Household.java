/*
 * Govinda ERP - Household Entity
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.HouseholdRole;

/**
 * Household entity grouping related persons.
 *
 * A household represents a family unit for insurance purposes.
 * It contains members with different roles (primary, partner, children).
 */
@Entity
@Table(name = "household")
public class Household {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "tenant_id", updatable = false, nullable = false)
    private UUID tenantId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Version
    @Column(name = "version", nullable = false)
    private long version = 0L;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HouseholdMember> members = new ArrayList<>();

    protected Household() {
    }

    public Household(UUID tenantId, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Household name must not be blank");
        }
        this.tenantId = tenantId;
        this.name = name;
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

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Household name must not be blank");
        }
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<HouseholdMember> getMembers() {
        return List.copyOf(members);
    }

    public void setMembers(List<HouseholdMember> members) {
        this.members = members;
    }

    /**
     * Returns all currently active members.
     */
    public List<HouseholdMember> currentMembers() {
        return members.stream().filter(HouseholdMember::isCurrent).toList();
    }

    /**
     * Returns the primary member (policyholder).
     */
    public HouseholdMember primaryMember() {
        return currentMembers().stream()
            .filter(member -> member.getRole() == HouseholdRole.PRIMARY)
            .findFirst()
            .orElse(null);
    }

    /**
     * Returns true if the household has a primary member.
     */
    public boolean hasPrimary() {
        return primaryMember() != null;
    }

    /**
     * Returns the count of child members.
     */
    public int childCount() {
        return (int) currentMembers().stream().filter(member -> member.getRole() == HouseholdRole.CHILD).count();
    }

    /**
     * Adds a member to the household.
     */
    public void addMember(UUID personId, HouseholdRole role, LocalDate validFrom) {
        if (currentMembers().stream().anyMatch(member -> member.getPersonId().equals(personId))) {
            throw new IllegalStateException("Person is already a member of this household");
        }
        if (role == HouseholdRole.PRIMARY && hasPrimary()) {
            throw new IllegalStateException("Household already has a primary member");
        }

        HouseholdMember member = new HouseholdMember(
            this.id,
            personId,
            role,
            validFrom
        );
        member.setHousehold(this);
        members.add(member);
        updatedAt = Instant.now();
    }

    /**
     * Removes a member from the household by setting their end date.
     */
    public void removeMember(UUID personId, LocalDate validTo) {
        HouseholdMember member = currentMembers().stream()
            .filter(current -> current.getPersonId().equals(personId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Member not found in household"));
        member.setValidTo(validTo);
        updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Household household = (Household) other;
        return Objects.equals(id, household.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Household(id=" + id + ", name='" + name + "')";
    }
}

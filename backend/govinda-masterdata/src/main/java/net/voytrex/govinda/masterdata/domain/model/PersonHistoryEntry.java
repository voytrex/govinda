/*
 * Govinda ERP - Person History Entry
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.HistoryEntry;
import net.voytrex.govinda.common.domain.model.MaritalStatus;
import net.voytrex.govinda.common.domain.model.MutationType;

/**
 * History entry for Person changes.
 */
@Entity
@Table(name = "person_history")
public class PersonHistoryEntry extends HistoryEntry {
    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatus maritalStatus;

    protected PersonHistoryEntry() {
    }

    // CHECKSTYLE:OFF: ParameterNumber - Domain model constructor requires all history fields
    public PersonHistoryEntry(
        UUID personId,
        String lastName,
        String firstName,
        MaritalStatus maritalStatus,
        LocalDate validFrom,
        LocalDate validTo,
        MutationType mutationType,
        String mutationReason,
        UUID changedBy
    ) {
        // CHECKSTYLE:ON: ParameterNumber
        setHistoryId(UUID.randomUUID());
        setValidFrom(validFrom);
        setValidTo(validTo);
        setRecordedAt(Instant.now());
        setSupersededAt(null);
        setMutationType(mutationType);
        setMutationReason(mutationReason);
        setChangedBy(changedBy);
        this.personId = personId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.maritalStatus = maritalStatus;
    }

    public UUID getPersonId() {
        return personId;
    }

    public void setPersonId(UUID personId) {
        this.personId = personId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
}

/*
 * Govinda ERP - Person Entity
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AddressType;
import net.voytrex.govinda.common.domain.model.AgeGroup;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.Historized;
import net.voytrex.govinda.common.domain.model.Language;
import net.voytrex.govinda.common.domain.model.MaritalStatus;
import net.voytrex.govinda.common.domain.model.MutationType;
import net.voytrex.govinda.common.domain.model.PersonStatus;

/**
 * Person entity representing an insured individual.
 *
 * Supports history tracking for name and marital status changes.
 */
@Entity
@Table(name = "person")
public class Person implements Historized<PersonHistoryEntry> {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "tenant_id", updatable = false, nullable = false)
    private UUID tenantId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ahv_nr", length = 16, nullable = false))
    private AhvNumber ahvNr;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10, nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatus maritalStatus;

    @Column(name = "nationality", length = 3)
    private String nationality = "CHE";

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", length = 2)
    private Language preferredLanguage = Language.DE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PersonStatus status = PersonStatus.ACTIVE;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Version
    @Column(name = "version", nullable = false)
    private long version = 0L;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    protected Person() {
    }

    public Person(
        UUID tenantId,
        AhvNumber ahvNr,
        String lastName,
        String firstName,
        LocalDate dateOfBirth,
        Gender gender,
        MaritalStatus maritalStatus,
        String nationality,
        Language preferredLanguage
    ) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be blank");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be blank");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        this.tenantId = tenantId;
        this.ahvNr = ahvNr;
        this.lastName = lastName;
        this.firstName = firstName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        if (nationality != null) {
            this.nationality = nationality;
        }
        if (preferredLanguage != null) {
            this.preferredLanguage = preferredLanguage;
        }
    }

    @Override
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

    public AhvNumber getAhvNr() {
        return ahvNr;
    }

    public void setAhvNr(AhvNumber ahvNr) {
        this.ahvNr = ahvNr;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be blank");
        }
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be blank");
        }
        this.firstName = firstName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Language getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(Language preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public PersonStatus getStatus() {
        return status;
    }

    public void setStatus(PersonStatus status) {
        this.status = status;
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

    public List<Address> getAddresses() {
        return List.copyOf(addresses);
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    /**
     * Returns the full name (first name + last name).
     */
    public String fullName() {
        return firstName + " " + lastName;
    }

    /**
     * Calculates the age at a given date.
     */
    public int ageAt(LocalDate date) {
        return Period.between(dateOfBirth, date).getYears();
    }

    /**
     * Determines the age group for premium calculation at a given date.
     */
    public AgeGroup ageGroupAt(LocalDate date) {
        return AgeGroup.forAge(ageAt(date));
    }

    /**
     * Returns the current (active) main address.
     */
    public Address currentAddress() {
        return addresses.stream()
            .filter(address -> address.getAddressType() == AddressType.MAIN && address.isCurrent())
            .findFirst()
            .orElse(null);
    }

    /**
     * Returns the address valid at a specific date.
     */
    public Address addressAt(LocalDate date) {
        return addresses.stream()
            .filter(address -> address.getAddressType() == AddressType.MAIN && address.isValidOn(date))
            .findFirst()
            .orElse(null);
    }

    /**
     * Adds a new address, closing the previous one if exists.
     */
    public void addAddress(Address address, LocalDate closeExistingOn) {
        if (closeExistingOn != null) {
            Address current = currentAddress();
            if (current != null) {
                current.close(closeExistingOn);
            }
        }
        addresses.add(address);
        updatedAt = Instant.now();
    }

    /**
     * Changes the name (e.g., due to marriage) and creates a history entry.
     */
    public PersonHistoryEntry changeName(
        String newLastName,
        String newFirstName,
        String reason,
        LocalDate effectiveDate,
        UUID changedBy
    ) {
        if (newLastName == null || newLastName.isBlank()) {
            throw new IllegalArgumentException("New last name must not be blank");
        }
        if (newFirstName == null || newFirstName.isBlank()) {
            throw new IllegalArgumentException("New first name must not be blank");
        }

        PersonHistoryEntry historyEntry = createHistoryEntry(
            MutationType.UPDATE,
            reason,
            changedBy
        );
        historyEntry.setValidTo(effectiveDate.minusDays(1));

        this.lastName = newLastName;
        this.firstName = newFirstName;
        this.updatedAt = Instant.now();

        return historyEntry;
    }

    /**
     * Changes the marital status and creates a history entry.
     */
    public PersonHistoryEntry changeMaritalStatus(
        MaritalStatus newStatus,
        String reason,
        LocalDate effectiveDate,
        UUID changedBy
    ) {
        PersonHistoryEntry historyEntry = createHistoryEntry(
            MutationType.UPDATE,
            reason,
            changedBy
        );
        historyEntry.setValidTo(effectiveDate.minusDays(1));

        this.maritalStatus = newStatus;
        this.updatedAt = Instant.now();

        return historyEntry;
    }

    @Override
    public PersonHistoryEntry createHistoryEntry(MutationType mutationType, String reason, UUID changedBy) {
        return new PersonHistoryEntry(
            this.id,
            this.lastName,
            this.firstName,
            this.maritalStatus,
            LocalDate.now(),
            null,
            mutationType,
            reason,
            changedBy
        );
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Person person = (Person) other;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person(id=" + id + ", ahvNr=" + ahvNr + ", name='" + firstName + " " + lastName + "')";
    }
}

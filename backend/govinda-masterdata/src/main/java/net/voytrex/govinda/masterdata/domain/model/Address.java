/*
 * Govinda ERP - Address Entity
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AddressType;
import net.voytrex.govinda.common.domain.model.Canton;

/**
 * Address entity with temporal validity.
 *
 * Addresses have a validity period (validFrom/validTo) to support
 * address history tracking. When a person moves, the old address
 * is closed and a new one is created.
 *
 * The premium region is determined from the postal code and affects
 * insurance premium calculations.
 */
@Entity
@Table(name = "address")
public class Address {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", length = 20, nullable = false)
    private AddressType addressType;

    @Column(name = "street", length = 200, nullable = false)
    private String street;

    @Column(name = "house_number", length = 20)
    private String houseNumber;

    @Column(name = "additional_line", length = 200)
    private String additionalLine;

    @Column(name = "postal_code", length = 10, nullable = false)
    private String postalCode;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(name = "canton", length = 2, nullable = false)
    private Canton canton;

    @Column(name = "country", length = 3, nullable = false)
    private String country = "CHE";

    @Column(name = "premium_region_id")
    private UUID premiumRegionId;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt = Instant.now();

    @Column(name = "superseded_at")
    private Instant supersededAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", insertable = false, updatable = false)
    private Person person;

    protected Address() {
    }

    public Address(
        UUID personId,
        AddressType addressType,
        String street,
        String houseNumber,
        String additionalLine,
        String postalCode,
        String city,
        Canton canton,
        String country,
        UUID premiumRegionId,
        LocalDate validFrom,
        LocalDate validTo,
        UUID createdBy
    ) {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street must not be blank");
        }
        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("Postal code must not be blank");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City must not be blank");
        }
        this.personId = personId;
        this.addressType = addressType;
        this.street = street;
        this.houseNumber = houseNumber;
        this.additionalLine = additionalLine;
        this.postalCode = postalCode;
        this.city = city;
        this.canton = canton;
        if (country != null) {
            this.country = country;
        }
        this.premiumRegionId = premiumRegionId;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.createdBy = createdBy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPersonId() {
        return personId;
    }

    public void setPersonId(UUID personId) {
        this.personId = personId;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street must not be blank");
        }
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getAdditionalLine() {
        return additionalLine;
    }

    public void setAdditionalLine(String additionalLine) {
        this.additionalLine = additionalLine;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("Postal code must not be blank");
        }
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City must not be blank");
        }
        this.city = city;
    }

    public Canton getCanton() {
        return canton;
    }

    public void setCanton(Canton canton) {
        this.canton = canton;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public UUID getPremiumRegionId() {
        return premiumRegionId;
    }

    public void setPremiumRegionId(UUID premiumRegionId) {
        this.premiumRegionId = premiumRegionId;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Instant getSupersededAt() {
        return supersededAt;
    }

    public void setSupersededAt(Instant supersededAt) {
        this.supersededAt = supersededAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Returns true if this address is currently valid (no end date).
     */
    public boolean isCurrent() {
        return validTo == null || !validTo.isBefore(LocalDate.now());
    }

    /**
     * Returns true if this address was valid on the given date.
     */
    public boolean isValidOn(LocalDate date) {
        if (validFrom.isAfter(date)) {
            return false;
        }
        return validTo == null || !validTo.isBefore(date);
    }

    /**
     * Closes this address as of the given date.
     */
    public void close(LocalDate endDate) {
        if (endDate.isBefore(validFrom)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        this.validTo = endDate;
    }

    /**
     * Returns the formatted street line (street + house number).
     */
    public String formattedStreet() {
        return houseNumber != null ? street + " " + houseNumber : street;
    }

    /**
     * Returns the formatted city line (postal code + city).
     */
    public String formattedCity() {
        return postalCode + " " + city;
    }

    /**
     * Returns the full formatted address as multiple lines.
     */
    public List<String> formattedLines() {
        List<String> lines = new ArrayList<>();
        lines.add(formattedStreet());
        if (additionalLine != null) {
            lines.add(additionalLine);
        }
        lines.add(formattedCity());
        return lines;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Address address = (Address) other;
        return Objects.equals(id, address.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Address(id=" + id + ", type=" + addressType + ", street='" + street
            + "', city='" + city + "', validFrom=" + validFrom + ", validTo=" + validTo + ")";
    }
}

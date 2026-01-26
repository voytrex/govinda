/*
 * Govinda ERP - Address Entity
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model

import jakarta.persistence.*
import net.voytrex.govinda.common.domain.model.AddressType
import net.voytrex.govinda.common.domain.model.Canton
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

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
class Address(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "person_id", nullable = false)
    val personId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", length = 20, nullable = false)
    val addressType: AddressType,

    @Column(name = "street", length = 200, nullable = false)
    val street: String,

    @Column(name = "house_number", length = 20)
    val houseNumber: String? = null,

    @Column(name = "additional_line", length = 200)
    val additionalLine: String? = null,

    @Column(name = "postal_code", length = 10, nullable = false)
    val postalCode: String,

    @Column(name = "city", length = 100, nullable = false)
    val city: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "canton", length = 2, nullable = false)
    val canton: Canton,

    @Column(name = "country", length = 3, nullable = false)
    val country: String = "CHE",

    @Column(name = "premium_region_id")
    val premiumRegionId: UUID? = null,

    @Column(name = "valid_from", nullable = false)
    val validFrom: LocalDate,

    @Column(name = "valid_to")
    var validTo: LocalDate? = null,

    @Column(name = "recorded_at", nullable = false)
    val recordedAt: Instant = Instant.now(),

    @Column(name = "superseded_at")
    var supersededAt: Instant? = null,

    @Column(name = "created_by")
    val createdBy: UUID? = null

) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", insertable = false, updatable = false)
    var person: Person? = null

    init {
        require(street.isNotBlank()) { "Street must not be blank" }
        require(postalCode.isNotBlank()) { "Postal code must not be blank" }
        require(city.isNotBlank()) { "City must not be blank" }
    }

    // ═══════════════════════════════════════════════════════════════
    // Business Methods
    // ═══════════════════════════════════════════════════════════════

    /**
     * Returns true if this address is currently valid (no end date).
     */
    fun isCurrent(): Boolean = validTo == null || !validTo!!.isBefore(LocalDate.now())

    /**
     * Returns true if this address was valid on the given date.
     */
    fun isValidOn(date: LocalDate): Boolean {
        if (validFrom.isAfter(date)) return false
        if (validTo != null && validTo!!.isBefore(date)) return false
        return true
    }

    /**
     * Closes this address as of the given date.
     */
    fun close(endDate: LocalDate) {
        require(!endDate.isBefore(validFrom)) {
            "End date cannot be before start date"
        }
        this.validTo = endDate
    }

    /**
     * Returns the formatted street line (street + house number).
     */
    fun formattedStreet(): String = if (houseNumber != null) {
        "$street $houseNumber"
    } else {
        street
    }

    /**
     * Returns the formatted city line (postal code + city).
     */
    fun formattedCity(): String = "$postalCode $city"

    /**
     * Returns the full formatted address as multiple lines.
     */
    fun formattedLines(): List<String> = buildList {
        add(formattedStreet())
        additionalLine?.let { add(it) }
        add(formattedCity())
    }

    // ═══════════════════════════════════════════════════════════════
    // Equality
    // ═══════════════════════════════════════════════════════════════

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Address
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String =
        "Address(id=$id, type=$addressType, street='$street', city='$city', validFrom=$validFrom, validTo=$validTo)"
}

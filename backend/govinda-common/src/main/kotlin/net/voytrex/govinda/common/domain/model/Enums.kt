/*
 * Govinda ERP - Common Enumerations
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model

import java.math.BigDecimal

/**
 * Swiss national languages plus English.
 */
enum class Language(val code: String, val displayName: String) {
    DE("de", "Deutsch"),
    FR("fr", "Français"),
    IT("it", "Italiano"),
    EN("en", "English");

    companion object {
        fun fromCode(code: String): Language =
            entries.find { it.code.equals(code, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown language code: $code")
    }
}

/**
 * Gender enumeration.
 */
enum class Gender(val code: String) {
    MALE("M"),
    FEMALE("F"),
    OTHER("O");

    companion object {
        fun fromCode(code: String): Gender =
            entries.find { it.code.equals(code, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown gender code: $code")
    }
}

/**
 * Swiss cantons.
 */
enum class Canton(val code: String, val nameDe: String, val nameFr: String) {
    ZH("ZH", "Zürich", "Zurich"),
    BE("BE", "Bern", "Berne"),
    LU("LU", "Luzern", "Lucerne"),
    UR("UR", "Uri", "Uri"),
    SZ("SZ", "Schwyz", "Schwytz"),
    OW("OW", "Obwalden", "Obwald"),
    NW("NW", "Nidwalden", "Nidwald"),
    GL("GL", "Glarus", "Glaris"),
    ZG("ZG", "Zug", "Zoug"),
    FR("FR", "Freiburg", "Fribourg"),
    SO("SO", "Solothurn", "Soleure"),
    BS("BS", "Basel-Stadt", "Bâle-Ville"),
    BL("BL", "Basel-Landschaft", "Bâle-Campagne"),
    SH("SH", "Schaffhausen", "Schaffhouse"),
    AR("AR", "Appenzell Ausserrhoden", "Appenzell Rhodes-Extérieures"),
    AI("AI", "Appenzell Innerrhoden", "Appenzell Rhodes-Intérieures"),
    SG("SG", "St. Gallen", "Saint-Gall"),
    GR("GR", "Graubünden", "Grisons"),
    AG("AG", "Aargau", "Argovie"),
    TG("TG", "Thurgau", "Thurgovie"),
    TI("TI", "Tessin", "Tessin"),
    VD("VD", "Waadt", "Vaud"),
    VS("VS", "Wallis", "Valais"),
    NE("NE", "Neuenburg", "Neuchâtel"),
    GE("GE", "Genf", "Genève"),
    JU("JU", "Jura", "Jura");

    companion object {
        fun fromCode(code: String): Canton =
            entries.find { it.code.equals(code, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown canton code: $code")
    }
}

/**
 * Age groups for premium calculation.
 *
 * Swiss health insurance has three age-based premium categories:
 * - Children (0-18): Reduced premium, no franchise for KVG
 * - Young adults (19-25): Reduced premium
 * - Adults (26+): Full premium
 */
enum class AgeGroup(val minAge: Int, val maxAge: Int?) {
    CHILD(0, 18),
    YOUNG_ADULT(19, 25),
    ADULT(26, null);

    companion object {
        /**
         * Determines the age group for a given age.
         */
        fun forAge(age: Int): AgeGroup = when {
            age <= 18 -> CHILD
            age <= 25 -> YOUNG_ADULT
            else -> ADULT
        }
    }
}

/**
 * KVG insurance models (Versicherungsmodelle).
 *
 * Different models offer premium discounts in exchange for
 * restrictions on healthcare provider choice.
 */
enum class InsuranceModel(
    val code: String,
    val nameDe: String,
    val hasProviderRestriction: Boolean
) {
    /** Standard model - free choice of provider */
    STANDARD("STD", "Standard", false),

    /** HMO model - must use HMO center */
    HMO("HMO", "HMO", true),

    /** Family doctor model - must consult family doctor first */
    HAUSARZT("HAM", "Hausarzt-Modell", true),

    /** Telemedicine model - must call hotline first */
    TELMED("TLM", "Telmed", true);

    companion object {
        fun fromCode(code: String): InsuranceModel =
            entries.find { it.code.equals(code, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown insurance model code: $code")
    }
}

/**
 * KVG franchise options (Jahresfranchise).
 *
 * The franchise is the annual deductible the insured person
 * pays before the insurance covers costs.
 *
 * Higher franchise = lower premium.
 */
enum class Franchise(
    val amount: Int,
    val forChildren: Boolean = false,
    val forAdults: Boolean = true
) {
    /** CHF 0 - Only for children */
    CHF_0(0, forChildren = true, forAdults = false),

    /** CHF 100 - Only for children */
    CHF_100(100, forChildren = true, forAdults = false),

    /** CHF 200 - Only for children */
    CHF_200(200, forChildren = true, forAdults = false),

    /** CHF 300 - Minimum for adults */
    CHF_300(300, forChildren = true, forAdults = true),

    /** CHF 400 - Only for children */
    CHF_400(400, forChildren = true, forAdults = false),

    /** CHF 500 - Adults only */
    CHF_500(500, forChildren = false, forAdults = true),

    /** CHF 600 - Only for children */
    CHF_600(600, forChildren = true, forAdults = false),

    /** CHF 1000 - Adults only */
    CHF_1000(1000, forChildren = false, forAdults = true),

    /** CHF 1500 - Adults only */
    CHF_1500(1500, forChildren = false, forAdults = true),

    /** CHF 2000 - Adults only */
    CHF_2000(2000, forChildren = false, forAdults = true),

    /** CHF 2500 - Maximum, adults only */
    CHF_2500(2500, forChildren = false, forAdults = true);

    companion object {
        /**
         * Returns valid franchise options for the given age group.
         */
        fun forAgeGroup(ageGroup: AgeGroup): List<Franchise> = when (ageGroup) {
            AgeGroup.CHILD -> entries.filter { it.forChildren }
            AgeGroup.YOUNG_ADULT, AgeGroup.ADULT -> entries.filter { it.forAdults }
        }

        /**
         * Returns the default franchise for the given age group.
         */
        fun defaultFor(ageGroup: AgeGroup): Franchise = when (ageGroup) {
            AgeGroup.CHILD -> CHF_0
            AgeGroup.YOUNG_ADULT, AgeGroup.ADULT -> CHF_300
        }

        fun fromAmount(amount: Int): Franchise =
            entries.find { it.amount == amount }
                ?: throw IllegalArgumentException("Unknown franchise amount: $amount")
    }
}

/**
 * Marital status.
 */
enum class MaritalStatus(val code: String) {
    SINGLE("S"),
    MARRIED("M"),
    DIVORCED("D"),
    WIDOWED("W"),
    REGISTERED_PARTNERSHIP("P"),
    DISSOLVED_PARTNERSHIP("DP");

    companion object {
        fun fromCode(code: String): MaritalStatus =
            entries.find { it.code.equals(code, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown marital status code: $code")
    }
}

/**
 * Product types in Swiss health insurance.
 */
enum class ProductType(val code: String, val nameDe: String) {
    /** KVG - Mandatory basic health insurance */
    KVG("KVG", "Obligatorische Krankenpflegeversicherung"),

    /** VVG - Voluntary supplementary insurance */
    VVG("VVG", "Zusatzversicherung");
}

/**
 * Product categories for VVG products.
 */
enum class ProductCategory(val code: String, val nameDe: String) {
    /** Basic mandatory insurance */
    BASIC("BASIC", "Grundversicherung"),

    /** Hospital supplementary insurance */
    HOSPITAL("HOSP", "Spitalzusatzversicherung"),

    /** Dental insurance */
    DENTAL("DENT", "Zahnversicherung"),

    /** Alternative/complementary medicine */
    ALTERNATIVE("ALT", "Alternativmedizin"),

    /** Travel/abroad insurance */
    TRAVEL("TRAV", "Auslandsversicherung"),

    /** Daily sickness allowance */
    DAILY_ALLOWANCE("TAGG", "Taggeldversicherung");
}

/**
 * Policy status.
 */
enum class PolicyStatus {
    /** Quote/offer not yet accepted */
    QUOTE,

    /** Application pending review */
    PENDING,

    /** Active policy */
    ACTIVE,

    /** Temporarily suspended */
    SUSPENDED,

    /** Terminated/cancelled */
    CANCELLED
}

/**
 * Coverage status.
 */
enum class CoverageStatus {
    /** Active coverage */
    ACTIVE,

    /** Temporarily suspended */
    SUSPENDED,

    /** Terminated */
    TERMINATED
}

/**
 * Billing frequency.
 */
enum class BillingFrequency(
    val code: String,
    val periodsPerYear: Int,
    val discountPercent: BigDecimal
) {
    MONTHLY("M", 12, BigDecimal.ZERO),
    QUARTERLY("Q", 4, BigDecimal("0.5")),
    SEMI_ANNUAL("S", 2, BigDecimal("1.0")),
    ANNUAL("A", 1, BigDecimal("2.0"));

    companion object {
        fun fromCode(code: String): BillingFrequency =
            entries.find { it.code.equals(code, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown billing frequency code: $code")
    }
}

/**
 * Invoice status.
 */
enum class InvoiceStatus {
    /** Draft, not yet sent */
    DRAFT,

    /** Sent to customer */
    SENT,

    /** Fully paid */
    PAID,

    /** Partially paid */
    PARTIAL,

    /** Overdue */
    OVERDUE,

    /** Cancelled */
    CANCELLED
}

/**
 * Person status.
 */
enum class PersonStatus {
    /** Active insured person */
    ACTIVE,

    /** Deceased */
    DECEASED,

    /** Emigrated from Switzerland */
    EMIGRATED
}

/**
 * Address type.
 */
enum class AddressType {
    /** Main/residential address */
    MAIN,

    /** Correspondence address */
    CORRESPONDENCE,

    /** Billing address */
    BILLING
}

/**
 * Household role.
 */
enum class HouseholdRole {
    /** Primary policyholder */
    PRIMARY,

    /** Partner/spouse */
    PARTNER,

    /** Child */
    CHILD
}

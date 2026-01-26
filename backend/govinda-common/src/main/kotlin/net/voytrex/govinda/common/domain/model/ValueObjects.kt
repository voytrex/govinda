/*
 * Govinda ERP - Common Value Objects
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Swiss AHV number (Sozialversicherungsnummer).
 *
 * Format: 756.XXXX.XXXX.XX
 * - 756: Country code for Switzerland
 * - 13 digits total, separated by dots
 * - Last digit is a checksum
 *
 * @property value The formatted AHV number string
 */
@Embeddable
data class AhvNumber(
    @Column(name = "ahv_nr", length = 16)
    val value: String
) {
    init {
        require(isValidFormat(value)) {
            "Invalid AHV number format: $value. Expected format: 756.XXXX.XXXX.XX"
        }
    }

    /**
     * Returns the AHV number without dots.
     */
    fun toUnformatted(): String = value.replace(".", "")

    /**
     * Returns the formatted AHV number.
     */
    override fun toString(): String = value

    companion object {
        private val FORMAT_REGEX = Regex("""756\.\d{4}\.\d{4}\.\d{2}""")

        /**
         * Validates the format of an AHV number.
         */
        fun isValidFormat(value: String): Boolean = FORMAT_REGEX.matches(value)

        /**
         * Creates an AhvNumber from an unformatted string (13 digits).
         */
        fun fromUnformatted(digits: String): AhvNumber {
            require(digits.length == 13 && digits.all { it.isDigit() }) {
                "Unformatted AHV must be 13 digits"
            }
            val formatted = "${digits.substring(0, 3)}.${digits.substring(3, 7)}.${digits.substring(7, 11)}.${digits.substring(11, 13)}"
            return AhvNumber(formatted)
        }
    }
}

/**
 * Money value object with currency.
 *
 * Supports Swiss Rappen rounding (to 5 centimes).
 *
 * @property amount The decimal amount
 * @property currency The currency (default: CHF)
 */
@Embeddable
data class Money(
    @Column(name = "amount", precision = 12, scale = 2)
    val amount: BigDecimal,

    @Column(name = "currency", length = 3)
    val currency: Currency = Currency.CHF
) : Comparable<Money> {

    init {
        require(amount.scale() <= 2) {
            "Money amount must have at most 2 decimal places"
        }
    }

    /**
     * Rounds the amount to Swiss 5-Rappen increments.
     *
     * Examples:
     * - 10.12 → 10.10
     * - 10.13 → 10.15
     * - 10.17 → 10.15
     * - 10.18 → 10.20
     */
    fun roundToRappen(): Money {
        val rounded = amount
            .multiply(BigDecimal("20"))
            .setScale(0, RoundingMode.HALF_UP)
            .divide(BigDecimal("20"), 2, RoundingMode.HALF_UP)
        return copy(amount = rounded)
    }

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Cannot add different currencies" }
        return Money(amount.add(other.amount), currency)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) { "Cannot subtract different currencies" }
        return Money(amount.subtract(other.amount), currency)
    }

    operator fun times(factor: Int): Money =
        Money(amount.multiply(BigDecimal(factor)), currency)

    operator fun times(factor: BigDecimal): Money =
        Money(amount.multiply(factor).setScale(2, RoundingMode.HALF_UP), currency)

    fun negate(): Money = Money(amount.negate(), currency)

    fun isPositive(): Boolean = amount > BigDecimal.ZERO
    fun isNegative(): Boolean = amount < BigDecimal.ZERO
    fun isZero(): Boolean = amount.compareTo(BigDecimal.ZERO) == 0

    override fun compareTo(other: Money): Int {
        require(currency == other.currency) { "Cannot compare different currencies" }
        return amount.compareTo(other.amount)
    }

    override fun toString(): String = "$currency ${amount.setScale(2)}"

    companion object {
        val ZERO = Money(BigDecimal.ZERO, Currency.CHF)

        fun chf(amount: Double): Money = Money(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP))
        fun chf(amount: Int): Money = Money(BigDecimal(amount).setScale(2))
        fun chf(amount: String): Money = Money(BigDecimal(amount).setScale(2, RoundingMode.HALF_UP))
    }
}

/**
 * Supported currencies.
 */
enum class Currency {
    CHF,  // Swiss Franc
    EUR,  // Euro (for cross-border)
    USD   // US Dollar (rare, for international)
}

/**
 * Localized text supporting multiple languages.
 *
 * Used for product names, descriptions, and other user-facing text
 * that must be available in all Swiss national languages plus English.
 */
@Embeddable
data class LocalizedText(
    @Column(name = "text_de", length = 1000)
    val de: String,

    @Column(name = "text_fr", length = 1000)
    val fr: String,

    @Column(name = "text_it", length = 1000)
    val it: String,

    @Column(name = "text_en", length = 1000)
    val en: String
) {
    /**
     * Gets the text for the specified language.
     * Falls back to German if the specified language is not available.
     */
    fun get(language: Language): String = when (language) {
        Language.DE -> de
        Language.FR -> fr.ifBlank { de }
        Language.IT -> it.ifBlank { de }
        Language.EN -> en.ifBlank { de }
    }

    /**
     * Returns true if all language variants are blank.
     */
    fun isBlank(): Boolean = de.isBlank() && fr.isBlank() && it.isBlank() && en.isBlank()

    companion object {
        /**
         * Creates a LocalizedText with the same value for all languages.
         */
        fun of(text: String): LocalizedText = LocalizedText(text, text, text, text)

        /**
         * Creates an empty LocalizedText.
         */
        fun empty(): LocalizedText = LocalizedText("", "", "", "")
    }
}

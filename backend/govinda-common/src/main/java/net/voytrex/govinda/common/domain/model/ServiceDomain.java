/*
 * Govinda ERP - Service Domain
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Identifies the regulatory or business domain for products and subscriptions.
 *
 * <p>User-facing labels must be resolved via {@code MessageSource} using enum codes,
 * see i18n guidelines in {@code .cursorrules}. Enum values intentionally do not
 * contain translated names.</p>
 */
public enum ServiceDomain {

    /**
     * Swiss health insurance domain (KVG/VVG).
     */
    HEALTHCARE,

    /**
     * Swiss radio and television fee domain (RTVG).
     */
    BROADCAST,

    /**
     * Telecommunications services domain.
     */
    TELECOM,

    /**
     * Utility services domain (e.g. electricity, gas, water).
     */
    UTILITIES,

    /**
     * Generic subscription domain for custom products.
     */
    CUSTOM
}


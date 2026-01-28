/*
 * Govinda ERP - Pricing Model
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Identifies how pricing is calculated for a product.
 *
 * <p>User-facing labels must be resolved via {@code MessageSource} using enum codes,
 * see i18n guidelines in {@code .cursorrules}. Enum values intentionally do not
 * contain translated names.</p>
 */
public enum PricingModel {

    /**
     * Fixed price for all subscribers.
     */
    FIXED,

    /**
     * Price varies by subscriber's age group.
     */
    AGE_BASED,

    /**
     * Price varies by geographic region.
     */
    REGION_BASED,

    /**
     * Price varies by tier (e.g. turnover-based tiers).
     */
    TIER_BASED,

    /**
     * Price varies by subscriber type.
     */
    SUBSCRIBER_TYPE_BASED,

    /**
     * Price based on actual usage.
     */
    USAGE_BASED,

    /**
     * Combination of multiple pricing factors.
     */
    COMPOSITE
}


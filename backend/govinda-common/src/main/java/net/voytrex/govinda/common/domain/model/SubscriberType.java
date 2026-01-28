/*
 * Govinda ERP - Subscriber Type
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Identifies the type of subscriber for products and subscriptions.
 *
 * <p>User-facing labels must be resolved via {@code MessageSource} using enum codes,
 * see i18n guidelines in {@code .cursorrules}. Enum values intentionally do not
 * contain translated names.</p>
 */
public enum SubscriberType {

    /**
     * Individual person subscriber.
     */
    INDIVIDUAL,

    /**
     * Private household (family unit).
     */
    PRIVATE_HOUSEHOLD,

    /**
     * Collective household (institutional).
     */
    COLLECTIVE_HOUSEHOLD,

    /**
     * Small business subscriber.
     */
    CORPORATE_SMALL,

    /**
     * Medium-sized business subscriber.
     */
    CORPORATE_MEDIUM,

    /**
     * Large business subscriber.
     */
    CORPORATE_LARGE,

    /**
     * Non-profit organization subscriber.
     */
    NONPROFIT,

    /**
     * Public institution subscriber (e.g. schools, municipalities).
     */
    PUBLIC_INSTITUTION;

    /**
     * Returns {@code true} if this subscriber type represents a household.
     */
    public boolean isHousehold() {
        return this == PRIVATE_HOUSEHOLD || this == COLLECTIVE_HOUSEHOLD;
    }

    /**
     * Returns {@code true} if this subscriber type represents a corporate or institutional entity.
     */
    public boolean isCorporate() {
        return switch (this) {
            case CORPORATE_SMALL, CORPORATE_MEDIUM, CORPORATE_LARGE, NONPROFIT, PUBLIC_INSTITUTION -> true;
            default -> false;
        };
    }
}


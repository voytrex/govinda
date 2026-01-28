/*
 * Govinda ERP - Age Group
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Age groups for premium calculation.
 *
 * Swiss health insurance has three age-based premium categories:
 * - Children (0-18): Reduced premium, no franchise for KVG
 * - Young adults (19-25): Reduced premium
 * - Adults (26+): Full premium
 */
public enum AgeGroup {
    CHILD(0, 18),
    YOUNG_ADULT(19, 25),
    ADULT(26, null);

    private static final int CHILD_MAX_AGE = 18;
    private static final int YOUNG_ADULT_MAX_AGE = 25;

    private final int minAge;
    private final Integer maxAge;

    AgeGroup(int minAge, Integer maxAge) {
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    /**
     * Determines the age group for a given age.
     */
    public static AgeGroup forAge(int age) {
        if (age <= CHILD_MAX_AGE) {
            return CHILD;
        }
        if (age <= YOUNG_ADULT_MAX_AGE) {
            return YOUNG_ADULT;
        }
        return ADULT;
    }
}

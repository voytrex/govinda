/*
 * Govinda ERP - Franchise
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import java.util.Arrays;
import java.util.List;

/**
 * KVG franchise options (Jahresfranchise).
 *
 * The franchise is the annual deductible the insured person
 * pays before the insurance covers costs.
 *
 * Higher franchise = lower premium.
 */
public enum Franchise {
    /** CHF 0 - Only for children */
    CHF_0(0, true, false),
    /** CHF 100 - Only for children */
    CHF_100(100, true, false),
    /** CHF 200 - Only for children */
    CHF_200(200, true, false),
    /** CHF 300 - Minimum for adults */
    CHF_300(300, true, true),
    /** CHF 400 - Only for children */
    CHF_400(400, true, false),
    /** CHF 500 - Adults only */
    CHF_500(500, false, true),
    /** CHF 600 - Only for children */
    CHF_600(600, true, false),
    /** CHF 1000 - Adults only */
    CHF_1000(1000, false, true),
    /** CHF 1500 - Adults only */
    CHF_1500(1500, false, true),
    /** CHF 2000 - Adults only */
    CHF_2000(2000, false, true),
    /** CHF 2500 - Maximum, adults only */
    CHF_2500(2500, false, true);

    private final int amount;
    private final boolean forChildren;
    private final boolean forAdults;

    Franchise(int amount, boolean forChildren, boolean forAdults) {
        this.amount = amount;
        this.forChildren = forChildren;
        this.forAdults = forAdults;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isForChildren() {
        return forChildren;
    }

    public boolean isForAdults() {
        return forAdults;
    }

    /**
     * Returns valid franchise options for the given age group.
     */
    public static List<Franchise> forAgeGroup(AgeGroup ageGroup) {
        return switch (ageGroup) {
            case CHILD -> Arrays.stream(values()).filter(Franchise::isForChildren).toList();
            case YOUNG_ADULT, ADULT -> Arrays.stream(values()).filter(Franchise::isForAdults).toList();
        };
    }

    /**
     * Returns the default franchise for the given age group.
     */
    public static Franchise defaultFor(AgeGroup ageGroup) {
        return switch (ageGroup) {
            case CHILD -> CHF_0;
            case YOUNG_ADULT, ADULT -> CHF_300;
        };
    }

    public static Franchise fromAmount(int amount) {
        for (Franchise franchise : values()) {
            if (franchise.amount == amount) {
                return franchise;
            }
        }
        throw new IllegalArgumentException("Unknown franchise amount: " + amount);
    }
}

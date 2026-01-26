/*
 * Govinda ERP - Premium Calculation Exception
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

public final class PremiumCalculationException extends DomainException {
    public PremiumCalculationException(String message) {
        super(message, "PREMIUM_CALCULATION_ERROR");
    }

    public PremiumCalculationException(String message, Throwable cause) {
        super(message, "PREMIUM_CALCULATION_ERROR", cause);
    }
}

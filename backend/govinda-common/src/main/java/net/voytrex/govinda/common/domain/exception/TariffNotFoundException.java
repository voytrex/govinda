/*
 * Govinda ERP - Tariff Not Found Exception
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

import java.util.UUID;

public final class TariffNotFoundException extends DomainException {
    public TariffNotFoundException(int year, String productCode, UUID regionId) {
        super(
            "Tariff not found for year " + year + ", product " + productCode
                + (regionId != null ? ", region " + regionId : ""),
            "TARIFF_NOT_FOUND"
        );
    }
}

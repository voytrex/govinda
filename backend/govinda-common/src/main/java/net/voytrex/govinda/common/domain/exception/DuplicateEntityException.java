/*
 * Govinda ERP - Duplicate Entity Exception
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

public final class DuplicateEntityException extends DomainException {
    public DuplicateEntityException(String entityType, String fieldName, String fieldValue) {
        super(entityType + " already exists with " + fieldName + ": " + fieldValue, "DUPLICATE_ENTITY");
    }
}

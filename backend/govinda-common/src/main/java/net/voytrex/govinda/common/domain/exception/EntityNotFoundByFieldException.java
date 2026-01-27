/*
 * Govinda ERP - Entity Not Found By Field Exception
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

public final class EntityNotFoundByFieldException extends DomainException {
    public EntityNotFoundByFieldException(String entityType, String fieldName, String fieldValue) {
        super(entityType + " not found with " + fieldName + ": " + fieldValue, "ENTITY_NOT_FOUND");
    }
}

/*
 * Govinda ERP - Entity Not Found Exception
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

import java.util.UUID;

public final class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entityType, UUID id) {
        super(entityType + " not found with id: " + id, "ENTITY_NOT_FOUND");
    }
}

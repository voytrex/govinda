/*
 * Govinda ERP - Concurrent Modification Exception
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

import java.util.UUID;

public final class ConcurrentModificationException extends DomainException {
    public ConcurrentModificationException(String entityType, UUID id) {
        super(entityType + " with id " + id + " was modified by another transaction", "CONCURRENT_MODIFICATION");
    }
}

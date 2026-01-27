/*
 * Govinda ERP - User Repository Interface
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.repository;

import java.util.UUID;
import net.voytrex.govinda.common.domain.model.User;

/**
 * Repository interface for User entities.
 * 
 * Note: When implemented by Spring Data JPA repositories, findById may return
 * Optional internally, but the interface contract returns User (or null if not found).
 */
public interface UserRepository {
    /**
     * Find user by ID. Returns null if not found.
     */
    User findById(UUID id);
    
    User findByUsername(String username);
    User findByEmail(String email);
    User save(User user);
}

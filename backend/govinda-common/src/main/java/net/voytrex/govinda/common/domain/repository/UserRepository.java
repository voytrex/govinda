/*
 * Govinda ERP - User Repository Interface
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.repository;

import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.User;

/**
 * Repository interface for User entities.
 * 
 * Note: Use Optional return types for lookups that may not exist.
 */
public interface UserRepository {
    /**
     * Find user by ID.
     */
    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User save(User user);
}

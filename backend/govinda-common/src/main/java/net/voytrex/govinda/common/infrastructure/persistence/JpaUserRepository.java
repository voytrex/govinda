/*
 * Govinda ERP - JPA User Repository
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Govinda ERP - JPA User Repository
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.infrastructure.persistence;

import java.util.UUID;
import net.voytrex.govinda.common.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for User entities.
 * 
 * This is the infrastructure-layer repository. For domain services, use UserRepository
 * interface which is implemented by UserRepositoryAdapter.
 */
public interface JpaUserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    User findByEmail(String email);
}

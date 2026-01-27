/*
 * Govinda ERP - User Repository Adapter
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.infrastructure.persistence;

import java.util.UUID;
import net.voytrex.govinda.common.domain.model.User;
import net.voytrex.govinda.common.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

/**
 * Adapter that implements UserRepository domain interface by delegating to JpaUserRepository.
 * 
 * This bridges the gap between the domain interface (UserRepository) and Spring Data JPA
 * repository (JpaUserRepository), handling the Optional return type from Spring Data.
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User findById(UUID id) {
        return jpaUserRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return jpaUserRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return jpaUserRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(user);
    }
}

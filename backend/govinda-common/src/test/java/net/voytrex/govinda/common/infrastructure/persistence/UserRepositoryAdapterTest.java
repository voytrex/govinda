/*
 * Govinda ERP - User Repository Adapter Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Test
    void shouldReturnNullWhenUserNotFoundById() {
        UUID userId = UUID.randomUUID();
        when(jpaUserRepository.findById(userId)).thenReturn(Optional.empty());

        UserRepositoryAdapter adapter = new UserRepositoryAdapter(jpaUserRepository);

        assertThat(adapter.findById(userId)).isNull();
    }

    @Test
    void shouldDelegateFindByUsername() {
        User user = new User("user", "user@example.com", "hash");
        when(jpaUserRepository.findByUsername("user")).thenReturn(user);

        UserRepositoryAdapter adapter = new UserRepositoryAdapter(jpaUserRepository);

        assertThat(adapter.findByUsername("user")).isSameAs(user);
        verify(jpaUserRepository).findByUsername("user");
    }

    @Test
    void shouldDelegateFindByEmail() {
        User user = new User("user", "user@example.com", "hash");
        when(jpaUserRepository.findByEmail("user@example.com")).thenReturn(user);

        UserRepositoryAdapter adapter = new UserRepositoryAdapter(jpaUserRepository);

        assertThat(adapter.findByEmail("user@example.com")).isSameAs(user);
        verify(jpaUserRepository).findByEmail("user@example.com");
    }

    @Test
    void shouldDelegateSave() {
        User user = new User("user", "user@example.com", "hash");
        when(jpaUserRepository.save(user)).thenReturn(user);

        UserRepositoryAdapter adapter = new UserRepositoryAdapter(jpaUserRepository);

        assertThat(adapter.save(user)).isSameAs(user);
        verify(jpaUserRepository).save(user);
    }
}

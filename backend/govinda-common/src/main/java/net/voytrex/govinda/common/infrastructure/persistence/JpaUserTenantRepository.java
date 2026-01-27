/*
 * Govinda ERP - JPA UserTenant Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.UserTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for UserTenant entities.
 */
@Repository
public interface JpaUserTenantRepository extends JpaRepository<UserTenant, UUID> {
    List<UserTenant> findByUserId(UUID userId);
    Optional<UserTenant> findByUserIdAndTenantId(UUID userId, UUID tenantId);
    Optional<UserTenant> findByUserIdAndIsDefaultTrue(UUID userId);

    @Query("SELECT ut FROM UserTenant ut WHERE ut.user.id = :userId AND ut.tenant.id = :tenantId")
    Optional<UserTenant> findUserTenantAccess(@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);
}

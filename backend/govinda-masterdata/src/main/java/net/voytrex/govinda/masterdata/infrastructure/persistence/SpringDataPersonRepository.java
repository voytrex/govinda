/*
 * Govinda ERP - Spring Data Person Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.infrastructure.persistence;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.masterdata.domain.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

public interface SpringDataPersonRepository extends JpaRepository<Person, UUID> {
    Optional<Person> findByIdAndTenantId(UUID id, UUID tenantId);
    Optional<Person> findByAhvNrAndTenantId(AhvNumber ahvNr, UUID tenantId);
    Page<Person> findByTenantId(UUID tenantId, Pageable pageable);
    boolean existsByAhvNrAndTenantId(AhvNumber ahvNr, UUID tenantId);

    @Query("""
        SELECT p FROM Person p
        WHERE p.tenantId = :tenantId
        AND (:lastName IS NULL OR LOWER(p.lastName) LIKE CONCAT('%', CAST(:lastName AS string), '%'))
        AND (:firstName IS NULL OR LOWER(p.firstName) LIKE CONCAT('%', CAST(:firstName AS string), '%'))
        AND (:ahvNr IS NULL OR p.ahvNr.value LIKE CONCAT('%', CAST(:ahvNr AS string), '%'))
        AND (CAST(:dateOfBirth AS date) IS NULL OR p.dateOfBirth = CAST(:dateOfBirth AS date))
        AND (:postalCode IS NULL OR EXISTS (
            SELECT a FROM Address a WHERE a.personId = p.id
            AND a.postalCode = :postalCode AND a.validTo IS NULL
        ))
        """)
    Page<Person> search(
        @Param("tenantId") UUID tenantId,
        @Nullable @Param("lastName") String lastName,
        @Nullable @Param("firstName") String firstName,
        @Nullable @Param("ahvNr") String ahvNr,
        @Nullable @Param("dateOfBirth") LocalDate dateOfBirth,
        @Nullable @Param("postalCode") String postalCode,
        Pageable pageable
    );
}

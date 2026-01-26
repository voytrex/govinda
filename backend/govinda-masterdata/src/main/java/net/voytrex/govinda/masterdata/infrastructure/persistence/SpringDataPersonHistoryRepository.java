/*
 * Govinda ERP - Spring Data Person History Repository
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataPersonHistoryRepository extends JpaRepository<PersonHistoryEntry, UUID> {
    List<PersonHistoryEntry> findByPersonIdOrderByValidFromDesc(UUID personId);

    @Query("""
        SELECT h FROM PersonHistoryEntry h
        WHERE h.personId = :personId
        AND h.validFrom <= :date
        AND (h.validTo IS NULL OR h.validTo >= :date)
        AND h.supersededAt IS NULL
        ORDER BY h.validFrom DESC
        """)
    PersonHistoryEntry findByPersonIdAndDate(
        @Param("personId") UUID personId,
        @Param("date") LocalDate date
    );
}

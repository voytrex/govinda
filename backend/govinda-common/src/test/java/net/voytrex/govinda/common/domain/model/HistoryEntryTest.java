/*
 * Govinda ERP - History Entry Domain Model Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("fast")
class HistoryEntryTest {

    @Nested
    @DisplayName("Initialization")
    class Initialization {

        @Test
        void shouldInitializeWithBitemporalFields() {
            LocalDate validFrom = LocalDate.of(2025, 1, 1);
            LocalDate validTo = LocalDate.of(2025, 12, 31);
            UUID changedBy = UUID.randomUUID();

            TestHistoryEntry entry = new TestHistoryEntry(
                validFrom,
                validTo,
                MutationType.UPDATE,
                "correction",
                changedBy
            );

            assertThat(entry.getHistoryId()).isNotNull();
            assertThat(entry.getRecordedAt()).isNotNull();
            assertThat(entry.getValidFrom()).isEqualTo(validFrom);
            assertThat(entry.getValidTo()).isEqualTo(validTo);
            assertThat(entry.getMutationType()).isEqualTo(MutationType.UPDATE);
            assertThat(entry.getMutationReason()).isEqualTo("correction");
            assertThat(entry.getChangedBy()).isEqualTo(changedBy);
        }
    }

    @Nested
    @DisplayName("Updates")
    class Updates {

        @Test
        void shouldAllowUpdatingOptionalFields() {
            TestHistoryEntry entry = new TestHistoryEntry(
                LocalDate.of(2024, 1, 1),
                null,
                MutationType.CREATE,
                null,
                UUID.randomUUID()
            );

            LocalDate newValidTo = LocalDate.of(2024, 12, 31);
            LocalDate newValidFrom = LocalDate.of(2024, 2, 1);
            Instant supersededAt = Instant.now();
            UUID changedBy = UUID.randomUUID();
            entry.setValidTo(newValidTo);
            entry.setValidFrom(newValidFrom);
            entry.setSupersededAt(supersededAt);
            entry.setMutationReason("corrected");
            entry.setMutationType(MutationType.CORRECTION);
            entry.setChangedBy(changedBy);

            assertThat(entry.getValidTo()).isEqualTo(newValidTo);
            assertThat(entry.getValidFrom()).isEqualTo(newValidFrom);
            assertThat(entry.getSupersededAt()).isEqualTo(supersededAt);
            assertThat(entry.getMutationReason()).isEqualTo("corrected");
            assertThat(entry.getMutationType()).isEqualTo(MutationType.CORRECTION);
            assertThat(entry.getChangedBy()).isEqualTo(changedBy);
        }

        @Test
        void shouldAllowUpdatingIdentifiersAndRecordedAt() {
            TestHistoryEntry entry = new TestHistoryEntry(
                LocalDate.of(2024, 1, 1),
                null,
                MutationType.CREATE,
                null,
                UUID.randomUUID()
            );
            UUID historyId = UUID.randomUUID();
            Instant recordedAt = Instant.now().minusSeconds(30);

            entry.setHistoryIdPublic(historyId);
            entry.setRecordedAtPublic(recordedAt);

            assertThat(entry.getHistoryId()).isEqualTo(historyId);
            assertThat(entry.getRecordedAt()).isEqualTo(recordedAt);
        }
    }

    private static final class TestHistoryEntry extends HistoryEntry {
        private TestHistoryEntry(
            LocalDate validFrom,
            LocalDate validTo,
            MutationType mutationType,
            String mutationReason,
            UUID changedBy
        ) {
            super(validFrom, validTo, mutationType, mutationReason, changedBy);
        }

        private void setHistoryIdPublic(UUID historyId) {
            setHistoryId(historyId);
        }

        private void setRecordedAtPublic(Instant recordedAt) {
            setRecordedAt(recordedAt);
        }
    }
}

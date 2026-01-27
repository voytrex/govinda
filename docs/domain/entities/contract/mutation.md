# Mutation Entity

## Overview

The **Mutation** entity tracks changes to coverages over time. Every significant change to a coverage (franchise change, model change, address change, termination) is recorded as a mutation. This provides a complete audit trail and supports scheduled future changes.

> **German**: Mutation, Änderung
> **Module**: `govinda-contract` (planned)
> **Status**: ⏳ Planned

---

## Entity Definition

```java
@Entity
@Table(name = "mutations")
public class Mutation {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coverage_id", nullable = false)
    private Coverage coverage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MutationType mutationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MutationStatus status = MutationStatus.PENDING;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    private String previousValue;

    private String newValue;

    private String mutationReason;

    @Column(nullable = false)
    private UUID createdBy;

    private UUID processedBy;

    private Instant createdAt;
    private Instant processedAt;
}
```

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | UUID | ✅ | Unique identifier |
| `coverage` | Coverage | ✅ | Parent coverage |
| `mutationType` | MutationType | ✅ | Type of change |
| `status` | MutationStatus | ✅ | Processing status |
| `effectiveDate` | LocalDate | ✅ | When change takes effect |
| `previousValue` | String | ❌ | Value before change |
| `newValue` | String | ❌ | Value after change |
| `mutationReason` | String | ❌ | Reason for change |
| `createdBy` | UUID | ✅ | User who created |
| `processedBy` | UUID | ❌ | User/system who processed |
| `createdAt` | Instant | Auto | Creation timestamp |
| `processedAt` | Instant | ❌ | Processing timestamp |

---

## Mutation Types

### MutationType Enum

```java
public enum MutationType {
    NEW,                // New coverage created
    FRANCHISE_CHANGE,   // KVG franchise changed
    MODEL_CHANGE,       // KVG insurance model changed
    ADDRESS_CHANGE,     // Address changed (premium region update)
    PREMIUM_UPDATE,     // Annual premium update (new tariff)
    TERMINATION,        // Coverage terminated
    REACTIVATION,       // Coverage reactivated
    CORRECTION;         // Data correction
}
```

### Mutation Type Details

| Type | Description | KVG | VVG |
|------|-------------|-----|-----|
| `NEW` | Initial coverage creation | ✅ | ✅ |
| `FRANCHISE_CHANGE` | Annual deductible change | ✅ | ❌ |
| `MODEL_CHANGE` | Insurance model change (HMO, etc.) | ✅ | ❌ |
| `ADDRESS_CHANGE` | Move to different premium region | ✅ | ✅ |
| `PREMIUM_UPDATE` | New year tariff application | ✅ | ✅ |
| `TERMINATION` | End of coverage | ✅ | ✅ |
| `REACTIVATION` | Restart suspended coverage | ✅ | ✅ |
| `CORRECTION` | Error correction | ✅ | ✅ |

---

## Mutation Status

### MutationStatus Enum

```java
public enum MutationStatus {
    PENDING,    // Scheduled for future effective date
    PROCESSED,  // Successfully applied
    CANCELLED,  // Cancelled before processing
    FAILED;     // Processing failed
}
```

### Status Transitions

```
  PENDING ─────process()────────▶ PROCESSED
     │
     │ cancel()
     ▼
  CANCELLED

  PENDING ─────process() error──▶ FAILED
```

---

## Key Behaviors

### Create Mutation

```java
public static Mutation create(Coverage coverage, MutationType type,
                              LocalDate effectiveDate, String previousValue,
                              String newValue, String reason, UUID createdBy) {
    return Mutation.builder()
        .coverage(coverage)
        .mutationType(type)
        .status(MutationStatus.PENDING)
        .effectiveDate(effectiveDate)
        .previousValue(previousValue)
        .newValue(newValue)
        .mutationReason(reason)
        .createdBy(createdBy)
        .createdAt(Instant.now())
        .build();
}
```

### Process Mutation

```java
public void process(UUID processedBy) {
    if (this.status != MutationStatus.PENDING) {
        throw new IllegalStateException("Only PENDING mutations can be processed");
    }
    if (this.effectiveDate.isAfter(LocalDate.now())) {
        throw new IllegalStateException("Cannot process future mutation");
    }

    try {
        applyToCoverage();
        this.status = MutationStatus.PROCESSED;
        this.processedBy = processedBy;
        this.processedAt = Instant.now();
    } catch (Exception e) {
        this.status = MutationStatus.FAILED;
        throw e;
    }
}

private void applyToCoverage() {
    switch (mutationType) {
        case FRANCHISE_CHANGE:
            coverage.setFranchise(Franchise.valueOf(newValue));
            coverage.recalculatePremium();
            break;
        case MODEL_CHANGE:
            // Update product to new model
            break;
        case ADDRESS_CHANGE:
            coverage.setPremiumRegionId(UUID.fromString(newValue));
            coverage.recalculatePremium();
            break;
        case TERMINATION:
            coverage.setStatus(CoverageStatus.TERMINATED);
            break;
        case REACTIVATION:
            coverage.setStatus(CoverageStatus.ACTIVE);
            break;
        // ... other types
    }
    coverage.setUpdatedAt(Instant.now());
}
```

### Cancel Mutation

```java
public void cancel(String reason, UUID cancelledBy) {
    if (this.status != MutationStatus.PENDING) {
        throw new IllegalStateException("Only PENDING mutations can be cancelled");
    }
    this.status = MutationStatus.CANCELLED;
    this.mutationReason = this.mutationReason + " | Cancelled: " + reason;
    this.processedBy = cancelledBy;
    this.processedAt = Instant.now();
}
```

---

## Scheduled Processing

Mutations with future effective dates are processed by a scheduled job:

```java
@Service
public class MutationProcessor {

    @Scheduled(cron = "0 0 0 * * *") // Daily at midnight
    public void processScheduledMutations() {
        LocalDate today = LocalDate.now();

        List<Mutation> pending = mutationRepository
            .findByStatusAndEffectiveDateLessThanEqual(
                MutationStatus.PENDING, today);

        for (Mutation mutation : pending) {
            try {
                mutation.process(SYSTEM_USER_ID);
                mutationRepository.save(mutation);
            } catch (Exception e) {
                log.error("Failed to process mutation {}", mutation.getId(), e);
                // Mutation status is FAILED, will need manual review
            }
        }
    }
}
```

---

## Mutation Examples

### Franchise Change

```java
Mutation mutation = Mutation.create(
    coverage,
    MutationType.FRANCHISE_CHANGE,
    LocalDate.of(2026, 1, 1),  // Always Jan 1
    "CHF_300",                  // Previous franchise
    "CHF_2500",                 // New franchise
    "Customer request for lower premium",
    currentUserId
);
```

### Address Change

```java
// Person moved from Zürich to Basel
Mutation mutation = Mutation.create(
    coverage,
    MutationType.ADDRESS_CHANGE,
    LocalDate.of(2025, 6, 15),  // Move date
    "region-uuid-zh-1",         // Old region
    "region-uuid-bs-1",         // New region
    "Customer moved to Basel",
    currentUserId
);
```

### Termination

```java
Mutation mutation = Mutation.create(
    coverage,
    MutationType.TERMINATION,
    LocalDate.of(2025, 12, 31),
    null,
    null,
    "Change to competitor - CSS policy CSS-2026-123456",
    currentUserId
);
```

### Annual Premium Update

```java
// Applied automatically on January 1 when new tariff is active
Mutation mutation = Mutation.create(
    coverage,
    MutationType.PREMIUM_UPDATE,
    LocalDate.of(2026, 1, 1),
    "450.00",                   // Previous premium
    "465.50",                   // New premium
    "Annual tariff update 2026",
    SYSTEM_USER_ID
);
```

---

## API Examples

### Create Franchise Change Mutation

```http
POST /api/v1/coverages/{coverageId}/mutations
Content-Type: application/json

{
  "mutationType": "FRANCHISE_CHANGE",
  "effectiveDate": "2026-01-01",
  "newValue": "CHF_2500",
  "mutationReason": "Customer request for lower premium"
}
```

### Response

```json
{
  "id": "mutation-uuid",
  "coverageId": "coverage-uuid",
  "mutationType": "FRANCHISE_CHANGE",
  "status": "PENDING",
  "effectiveDate": "2026-01-01",
  "previousValue": "CHF_300",
  "newValue": "CHF_2500",
  "mutationReason": "Customer request for lower premium",
  "createdBy": "user-uuid",
  "createdAt": "2025-11-15T10:30:00Z"
}
```

### Get Coverage Mutations

```http
GET /api/v1/coverages/{coverageId}/mutations
```

### Response

```json
{
  "coverageId": "coverage-uuid",
  "mutations": [
    {
      "id": "mutation-1",
      "mutationType": "NEW",
      "status": "PROCESSED",
      "effectiveDate": "2025-01-01",
      "processedAt": "2025-01-01T00:00:00Z"
    },
    {
      "id": "mutation-2",
      "mutationType": "ADDRESS_CHANGE",
      "status": "PROCESSED",
      "effectiveDate": "2025-06-15",
      "previousValue": "ZH-1",
      "newValue": "BS-1",
      "processedAt": "2025-06-15T00:00:00Z"
    },
    {
      "id": "mutation-3",
      "mutationType": "FRANCHISE_CHANGE",
      "status": "PENDING",
      "effectiveDate": "2026-01-01",
      "previousValue": "CHF_300",
      "newValue": "CHF_2500"
    }
  ]
}
```

### Cancel Pending Mutation

```http
POST /api/v1/mutations/{mutationId}/cancel
Content-Type: application/json

{
  "reason": "Customer changed their mind"
}
```

---

## Mutation Timeline Example

```
┌─────────────────────────────────────────────────────────────────┐
│                    COVERAGE MUTATION TIMELINE                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  2025-01-01     2025-06-15     2025-11-15     2026-01-01       │
│       │              │              │              │            │
│       ▼              ▼              ▼              ▼            │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐     │
│  │   NEW   │    │ ADDRESS │    │FRANCHISE│    │ PREMIUM │     │
│  │         │    │ CHANGE  │    │ CHANGE  │    │ UPDATE  │     │
│  │PROCESSED│    │PROCESSED│    │ PENDING │    │ PENDING │     │
│  └─────────┘    └─────────┘    └─────────┘    └─────────┘     │
│                                                                 │
│  Coverage created  Moved to    Scheduled    Annual tariff      │
│  F300, ZH-1        Basel       F2500        2026 rates         │
│  Premium: 485      BS-1        (Jan 1)      (Jan 1)            │
│                    Premium:                                     │
│                    440                                          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Code Location (Planned)

| File | Path |
|------|------|
| Entity | `backend/govinda-contract/src/main/java/net/voytrex/govinda/contract/domain/model/Mutation.java` |
| Repository | `backend/govinda-contract/src/main/java/net/voytrex/govinda/contract/domain/repository/MutationRepository.java` |
| Processor | `backend/govinda-contract/src/main/java/net/voytrex/govinda/contract/application/service/MutationProcessor.java` |

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ Franchise on Jan 1 | Franchise changes only effective January 1 |
| ⚠️ Address immediate | Address changes effective on move date |
| ⚠️ Only PENDING cancellable | Cannot cancel processed mutations |
| ⚠️ Scheduled processing | Future mutations processed by daily job |
| ⚠️ Audit trail | All changes tracked with user and timestamp |

---

## Related Documentation

- [Coverage Entity](./coverage.md)
- [Policy Entity](./policy.md)
- [Franchise System](../../concepts/franchise-system.md)
- [Contract Use Cases](/docs/use-cases/contract-use-cases.md)

---

*Last Updated: 2026-01-26*

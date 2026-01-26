# ADR-003: Temporal Data Model (History Tracking)

## Status
**Accepted**

## Date
2024-01-01

## Context

Swiss health insurance requires:
- Complete audit trail for regulatory compliance
- Ability to query historical states ("What was the premium on date X?")
- Track business changes (name changes, address moves, coverage mutations)
- Support corrections that may affect past records

## Decision

We implement a **bitemporal data model** for critical entities:

### Two Time Dimensions

| Dimension | Description | Example |
|-----------|-------------|---------|
| **Valid Time** | When the fact is true in the real world | Address valid from 2024-01-01 |
| **Transaction Time** | When the fact was recorded in the system | Recorded on 2024-01-15 |

### Implementation Levels

#### Level 1: Full Bitemporality
For critical business data that may need historical corrections:
- Person (name, civil status)
- Address
- Coverage (franchise, model, premium)

```sql
CREATE TABLE person_history (
    history_id      UUID PRIMARY KEY,
    person_id       UUID NOT NULL,

    -- Versioned data
    last_name       VARCHAR(100),
    first_name      VARCHAR(100),
    marital_status  VARCHAR(20),

    -- Valid time (business time)
    valid_from      DATE NOT NULL,
    valid_to        DATE,

    -- Transaction time (system time)
    recorded_at     TIMESTAMP NOT NULL,
    superseded_at   TIMESTAMP,

    -- Mutation info
    mutation_type   VARCHAR(20),
    mutation_reason VARCHAR(500),
    changed_by      UUID
);
```

#### Level 2: Valid Time Only
For entities with natural versioning:
- Policy status changes
- Product/Tariff versions
- Household membership

```sql
-- valid_from/valid_to on the main entity
valid_from DATE NOT NULL,
valid_to   DATE  -- NULL = current
```

#### Level 3: Audit Log Only
For all entities (generic compliance trail):

```sql
CREATE TABLE audit_log (
    entity_type    VARCHAR(100),
    entity_id      UUID,
    action         VARCHAR(20),
    changed_fields JSONB,
    changed_at     TIMESTAMP,
    changed_by     UUID
);
```

### Query Patterns

```sql
-- Current state
SELECT * FROM person WHERE id = ?

-- State at a specific date (business time)
SELECT * FROM person_history
WHERE person_id = ?
  AND valid_from <= '2023-06-15'
  AND (valid_to IS NULL OR valid_to >= '2023-06-15')
  AND superseded_at IS NULL

-- State as known at a specific time (transaction time)
SELECT * FROM person_history
WHERE person_id = ?
  AND recorded_at <= '2023-07-01'
  AND (superseded_at IS NULL OR superseded_at > '2023-07-01')
```

### Domain Model Support

```kotlin
interface Historized<H : HistoryEntry> {
    fun createHistoryEntry(
        mutationType: MutationType,
        reason: String?,
        changedBy: UUID
    ): H
}

enum class MutationType {
    CREATE,      // Initial creation
    UPDATE,      // Normal business change
    CORRECTION,  // Error correction (may affect past)
    CANCELLATION // Retroactive cancellation
}
```

## Consequences

### Positive
- Full audit trail for compliance
- Can answer historical queries
- Supports error corrections
- Clear mutation tracking

### Negative
- More complex queries
- Increased storage requirements
- Need to manage history tables

### Risks
- Performance impact on high-volume entities
- Mitigation: Index optimization, consider partitioning

## Alternatives Considered

| Alternative | Reason for Rejection |
|-------------|---------------------|
| Event Sourcing | Too complex for MVP, consider for Phase 3 |
| PostgreSQL Temporal Tables | Not mature enough |
| Audit-only (no valid time) | Insufficient for business queries |

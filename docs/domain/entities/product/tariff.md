# Tariff Entity

## Overview

The **Tariff** entity represents a specific version of a product with temporal validity and associated premium entries. Tariffs allow products to have different pricing over time while maintaining history. Each tariff contains a premium table with entries for all required parameter combinations.

> **German**: Tarif
> **Module**: `govinda-product` (planned)
> **Status**: ⏳ Planned

---

## Entity Definition

```java
@Entity
@Table(name = "tariffs")
public class Tariff {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TariffStatus status = TariffStatus.DRAFT;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTo;

    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PremiumEntry> premiums = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
    private Instant activatedAt;

    @Version
    private long version;
}
```

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | UUID | ✅ | Unique identifier |
| `product` | Product | ✅ | Parent product |
| `version` | String | ✅ | Version identifier (e.g., "2024-V1") |
| `status` | TariffStatus | ✅ | DRAFT, ACTIVE, or INACTIVE |
| `validFrom` | LocalDate | ✅ | Validity start date |
| `validTo` | LocalDate | ✅ | Validity end date |
| `premiums` | List\<PremiumEntry\> | ❌ | Premium table entries |
| `createdAt` | Instant | Auto | Creation timestamp |
| `updatedAt` | Instant | Auto | Last modification |
| `activatedAt` | Instant | Auto | When tariff was activated |

---

## Key Behaviors

### Check Validity

```java
public boolean isValidOn(LocalDate date) {
    return (validFrom.isBefore(date) || validFrom.isEqual(date))
        && (validTo.isAfter(date) || validTo.isEqual(date));
}

public boolean isCurrent() {
    return isValidOn(LocalDate.now());
}
```

### Activate Tariff

```java
public void activate() {
    if (this.status != TariffStatus.DRAFT) {
        throw new IllegalStateException("Only DRAFT tariffs can be activated");
    }
    if (!isPremiumTableComplete()) {
        throw new IllegalStateException("Premium table is incomplete");
    }
    this.status = TariffStatus.ACTIVE;
    this.activatedAt = Instant.now();
    this.updatedAt = Instant.now();
}
```

### Deactivate Tariff

```java
public void deactivate() {
    if (this.status == TariffStatus.INACTIVE) {
        return; // Already inactive
    }
    this.status = TariffStatus.INACTIVE;
    this.updatedAt = Instant.now();
}
```

### Check Premium Table Completeness

```java
public boolean isPremiumTableComplete() {
    ProductType category = product.getCategory();

    if (category == ProductType.KVG) {
        return hasAllKvgCombinations();
    } else {
        return hasAllVvgCombinations();
    }
}

private boolean hasAllKvgCombinations() {
    // Required: regions × ageGroups × franchises × accidentOptions
    Set<String> required = generateRequiredKvgKeys();
    Set<String> existing = premiums.stream()
        .map(PremiumEntry::getKey)
        .collect(Collectors.toSet());
    return existing.containsAll(required);
}
```

### Get Premium for Parameters

```java
public Optional<PremiumEntry> getPremium(
        UUID premiumRegionId,
        AgeGroup ageGroup,
        Franchise franchise,
        boolean withAccident) {

    return premiums.stream()
        .filter(p -> p.getPremiumRegionId().equals(premiumRegionId))
        .filter(p -> p.getAgeGroup() == ageGroup)
        .filter(p -> p.getFranchise() == franchise)
        .filter(p -> p.isWithAccident() == withAccident)
        .findFirst();
}
```

---

## Tariff Status

### TariffStatus Enum

```java
public enum TariffStatus {
    DRAFT,     // Being prepared, can be edited
    ACTIVE,    // Live, used for premium calculation
    INACTIVE;  // Archived, not used
}
```

### Status Transitions

```
     ┌──────────────────────────────────────┐
     │                                      │
     ▼                                      │
  DRAFT ──────activate()────────▶ ACTIVE ───┤
     │                              │       │
     │                              │       │
     └───────deactivate()───────────┼───────┘
                                    │
                                    ▼
                               INACTIVE
```

| Transition | Allowed | Requirements |
|------------|---------|--------------|
| DRAFT → ACTIVE | ✅ | Premium table complete |
| DRAFT → INACTIVE | ✅ | None |
| ACTIVE → INACTIVE | ✅ | None |
| ACTIVE → DRAFT | ❌ | Not allowed |
| INACTIVE → ACTIVE | ❌ | Not allowed |
| INACTIVE → DRAFT | ❌ | Not allowed |

---

## Validation Rules

### Validity Period

```java
// validFrom must be before validTo
if (validFrom.isAfter(validTo)) {
    throw new IllegalArgumentException("validFrom must be before validTo");
}

// No overlap with other tariffs of same product
List<Tariff> overlapping = product.getTariffs().stream()
    .filter(t -> !t.getId().equals(this.id))
    .filter(t -> periodsOverlap(t, this))
    .collect(Collectors.toList());
if (!overlapping.isEmpty()) {
    throw new IllegalStateException("Tariff validity periods cannot overlap");
}
```

### Modification Rules

```java
// Cannot modify active or inactive tariffs
public void addPremiumEntry(PremiumEntry entry) {
    if (this.status != TariffStatus.DRAFT) {
        throw new IllegalStateException("Cannot modify non-DRAFT tariff");
    }
    this.premiums.add(entry);
    this.updatedAt = Instant.now();
}
```

---

## Relationships

### Product → Tariff (1:N)

```
Product (1) ────────────< Tariff (N)
                   ↑
            productId: UUID (FK)
```

### Tariff → PremiumEntry (1:N)

```
Tariff (1) ────────────< PremiumEntry (N)
                   ↑
            tariffId: UUID (FK)
```

### Tariff → Coverage (1:N)

```
Tariff (1) ────────────< Coverage (N)
                   ↑
            tariffId: UUID (FK)
```

---

## API Examples

### Create Tariff

```http
POST /api/v1/products/{productId}/tariffs
Content-Type: application/json

{
  "validFrom": "2025-01-01",
  "validTo": "2025-12-31",
  "version": "2025-V1"
}
```

### Response

```json
{
  "id": "tariff-uuid",
  "productId": "product-uuid",
  "version": "2025-V1",
  "status": "DRAFT",
  "validFrom": "2025-01-01",
  "validTo": "2025-12-31",
  "premiumCount": 0,
  "isComplete": false,
  "createdAt": "2025-01-15T10:30:00Z"
}
```

### Activate Tariff

```http
POST /api/v1/tariffs/{tariffId}/activate
```

### Response

```json
{
  "id": "tariff-uuid",
  "status": "ACTIVE",
  "activatedAt": "2025-01-15T10:35:00Z"
}
```

---

## Tariff Lifecycle

```
┌─────────────────────────────────────────────────────────────────┐
│                    TARIFF LIFECYCLE                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. CREATE (DRAFT)                                              │
│     └─▶ Tariff created with validity period                    │
│     └─▶ Status: DRAFT                                          │
│                                                                 │
│  2. POPULATE                                                    │
│     └─▶ Add premium entries (individual or bulk import)        │
│     └─▶ Modify entries as needed                               │
│     └─▶ Status: DRAFT (unchanged)                              │
│                                                                 │
│  3. VALIDATE                                                    │
│     └─▶ Check premium table completeness                       │
│     └─▶ All required combinations must exist                   │
│                                                                 │
│  4. ACTIVATE                                                    │
│     └─▶ Premium table must be complete                         │
│     └─▶ Status: DRAFT → ACTIVE                                 │
│     └─▶ Product auto-activated if INACTIVE                     │
│                                                                 │
│  5. USE                                                         │
│     └─▶ Tariff used for new coverages                          │
│     └─▶ Cannot be modified                                     │
│                                                                 │
│  6. RETIRE                                                      │
│     └─▶ When validity period ends or manually deactivated      │
│     └─▶ Status: ACTIVE → INACTIVE                              │
│     └─▶ Existing coverages continue to reference it            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Premium Table Requirements

### KVG Products

Required combinations:
- All premium regions × All age groups × All franchises × Accident options

```
Total entries (example for 42 regions):
42 regions × 3 age groups × 6 franchises × 2 accident = 1,512 entries
```

### VVG Products (Unisex)

Required combinations:
- All premium regions × All age groups

```
Total entries (example for 42 regions):
42 regions × 3 age groups = 126 entries
```

### VVG Products (Gender-Based)

Required combinations:
- All premium regions × All age groups × Both genders

```
Total entries (example for 42 regions):
42 regions × 3 age groups × 2 genders = 252 entries
```

---

## Code Location (Planned)

| File | Path |
|------|------|
| Entity | `backend/govinda-product/src/main/java/net/voytrex/govinda/product/domain/model/Tariff.java` |
| Repository | `backend/govinda-product/src/main/java/net/voytrex/govinda/product/domain/repository/TariffRepository.java` |

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ No overlap | Tariff validity periods cannot overlap |
| ⚠️ DRAFT only editable | Cannot modify ACTIVE/INACTIVE tariffs |
| ⚠️ Complete to activate | Premium table must be complete |
| ⚠️ One active per product | Only one active tariff at a time |
| ⚠️ No reactivation | Cannot change INACTIVE back to ACTIVE |

---

## Related Documentation

- [Product Entity](./product.md)
- [Premium Table](./premium-table.md)
- [Coverage Entity](../contract/coverage.md)
- [Product Use Cases](/docs/use-cases/product-use-cases.md)

---

*Last Updated: 2026-01-26*

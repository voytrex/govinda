# Coverage Entity

## Overview

The **Coverage** entity represents the insurance protection for a specific person under a specific product. It links an insured person to a product/tariff with all parameters needed for premium calculation. Coverages track changes through mutations and maintain history.

> **German**: Deckung, Versicherungsschutz
> **Module**: `govinda-contract` (planned)
> **Status**: ⏳ Planned

---

## Entity Definition

```java
@Entity
@Table(name = "coverages")
public class Coverage {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(nullable = false)
    private UUID insuredPersonId;  // Reference to Person

    @Column(nullable = false)
    private UUID productId;  // Reference to Product

    @Column(nullable = false)
    private UUID tariffId;  // Reference to Tariff

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoverageStatus status = CoverageStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    private LocalDate terminationDate;

    // KVG-specific fields
    @Enumerated(EnumType.STRING)
    private Franchise franchise;

    private Boolean withAccident;

    // Premium calculation fields
    @Column(nullable = false)
    private UUID premiumRegionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgeGroup ageGroup;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "monthly_premium")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money monthlyPremium;

    @OneToMany(mappedBy = "coverage", cascade = CascadeType.ALL)
    private List<Mutation> mutations = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    @Version
    private long version;
}
```

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | UUID | ✅ | Unique identifier |
| `policy` | Policy | ✅ | Parent policy |
| `insuredPersonId` | UUID | ✅ | Reference to insured person |
| `productId` | UUID | ✅ | Reference to product |
| `tariffId` | UUID | ✅ | Reference to tariff used for premium |
| `status` | CoverageStatus | ✅ | ACTIVE, SUSPENDED, TERMINATED |
| `effectiveDate` | LocalDate | ✅ | Coverage start date |
| `terminationDate` | LocalDate | ❌ | Coverage end date (null = ongoing) |
| `franchise` | Franchise | ⚠️ KVG | Annual deductible |
| `withAccident` | Boolean | ⚠️ KVG | Accident coverage included |
| `premiumRegionId` | UUID | ✅ | Premium region at effective date |
| `ageGroup` | AgeGroup | ✅ | Age group at effective date |
| `monthlyPremium` | Money | ✅ | Calculated monthly premium |
| `mutations` | List\<Mutation\> | ❌ | Coverage change history |
| `createdAt` | Instant | Auto | Creation timestamp |
| `updatedAt` | Instant | Auto | Last modification |

---

## Coverage Status

### CoverageStatus Enum

```java
public enum CoverageStatus {
    ACTIVE,      // Coverage is in effect
    SUSPENDED,   // Temporarily suspended
    TERMINATED;  // Coverage has ended
}
```

### Status Transitions

```
  ACTIVE ◄────────────────────┐
     │                        │
     │ suspend()              │ reactivate()
     ▼                        │
  SUSPENDED ──────────────────┘
     │
     │ terminate()
     ▼
  TERMINATED
```

---

## Key Behaviors

### Calculate Premium

```java
public void calculatePremium() {
    // Get person's current address for premium region
    Person person = personService.findById(insuredPersonId);
    Address address = person.currentAddress()
        .orElseThrow(() -> new IllegalStateException("Person has no current address"));

    this.premiumRegionId = address.getPremiumRegionId();
    this.ageGroup = person.ageGroupAt(effectiveDate);

    // Look up premium from tariff
    Tariff tariff = tariffRepository.findById(tariffId);
    Product product = tariff.getProduct();

    Money premium;
    if (product.getCategory() == ProductType.KVG) {
        premium = tariff.getPremium(premiumRegionId, ageGroup, franchise, withAccident)
            .map(PremiumEntry::getMonthlyAmount)
            .orElseThrow(() -> new PremiumNotFoundException());
    } else {
        // VVG - may include gender
        Gender gender = person.getGender();
        premium = tariff.getVvgPremium(premiumRegionId, ageGroup, gender)
            .map(PremiumEntry::getMonthlyAmount)
            .orElseThrow(() -> new PremiumNotFoundException());
    }

    this.monthlyPremium = premium;
    this.updatedAt = Instant.now();
}
```

### Change Franchise (KVG)

```java
public Mutation changeFranchise(Franchise newFranchise, LocalDate effectiveDate) {
    if (effectiveDate.getMonthValue() != 1 || effectiveDate.getDayOfMonth() != 1) {
        throw new IllegalArgumentException("Franchise changes only allowed on January 1");
    }

    Mutation mutation = Mutation.builder()
        .coverage(this)
        .mutationType(MutationType.FRANCHISE_CHANGE)
        .effectiveDate(effectiveDate)
        .previousValue(this.franchise.name())
        .newValue(newFranchise.name())
        .build();

    this.mutations.add(mutation);
    // Actual change applied on effective date by scheduled job

    return mutation;
}
```

### Terminate Coverage

```java
public void terminate(LocalDate terminationDate, String reason) {
    if (this.status == CoverageStatus.TERMINATED) {
        throw new IllegalStateException("Coverage already terminated");
    }

    Mutation mutation = Mutation.builder()
        .coverage(this)
        .mutationType(MutationType.TERMINATION)
        .effectiveDate(terminationDate)
        .mutationReason(reason)
        .build();

    this.mutations.add(mutation);
    this.terminationDate = terminationDate;

    // If termination is immediate or past
    if (!terminationDate.isAfter(LocalDate.now())) {
        this.status = CoverageStatus.TERMINATED;
    }

    this.updatedAt = Instant.now();
}
```

### Update for Address Change

```java
public Mutation updateForAddressChange(Address newAddress, LocalDate effectiveDate) {
    UUID oldRegionId = this.premiumRegionId;
    UUID newRegionId = newAddress.getPremiumRegionId();

    if (!oldRegionId.equals(newRegionId)) {
        // Premium region changed - recalculate
        Mutation mutation = Mutation.builder()
            .coverage(this)
            .mutationType(MutationType.ADDRESS_CHANGE)
            .effectiveDate(effectiveDate)
            .previousValue(oldRegionId.toString())
            .newValue(newRegionId.toString())
            .build();

        this.mutations.add(mutation);

        // Recalculate premium
        this.premiumRegionId = newRegionId;
        recalculatePremium();

        return mutation;
    }

    return null; // No premium region change
}
```

---

## KVG-Specific Rules

### One Active KVG per Person

```java
// Validation when creating KVG coverage
public void validateKvgCoverage() {
    Product product = productRepository.findById(productId);

    if (product.getCategory() == ProductType.KVG) {
        // Check if person already has active KVG coverage
        boolean hasActiveKvg = coverageRepository
            .findActiveKvgByPersonId(insuredPersonId)
            .filter(c -> !c.getId().equals(this.id))
            .isPresent();

        if (hasActiveKvg) {
            throw new IllegalStateException(
                "Person already has active KVG coverage. Only one KVG coverage allowed.");
        }
    }
}
```

### KVG Termination Requirements

```java
// KVG termination requires proof of new coverage
public void terminateKvg(LocalDate terminationDate,
                         String newInsurerName,
                         String newPolicyNumber) {
    if (newInsurerName == null || newPolicyNumber == null) {
        throw new IllegalArgumentException(
            "KVG termination requires proof of new coverage");
    }

    // Proceed with termination
    terminate(terminationDate,
        String.format("New coverage with %s: %s", newInsurerName, newPolicyNumber));
}
```

### Franchise Change Rules

| Rule | Requirement |
|------|-------------|
| Effective Date | January 1 only |
| Notification Deadline | November 30 of previous year |
| Age-Appropriate | Must be valid for person's age group |

---

## Premium Calculation Factors

### KVG Coverage

```
Premium = f(Region, AgeGroup, Franchise, WithAccident)

┌─────────────────────────────────────────────────────────────────┐
│   PREMIUM CALCULATION (KVG)                                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   Person: Hans Müller                                           │
│   ├── Address: Bahnhofstrasse 42, 8001 Zürich                  │
│   │   └── Premium Region: ZH-1                                 │
│   ├── Birth Date: 15.03.1985                                    │
│   │   └── Age Group: ADULT (40 years old)                      │
│   │                                                             │
│   Coverage Parameters:                                          │
│   ├── Franchise: CHF 300                                        │
│   └── With Accident: Yes                                        │
│                                                                 │
│   Premium Lookup: Tariff.getPremium(ZH-1, ADULT, CHF_300, true)│
│   └── Monthly Premium: CHF 485.20                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### VVG Coverage

```
Premium = f(Region, AgeGroup, [Gender])

┌─────────────────────────────────────────────────────────────────┐
│   PREMIUM CALCULATION (VVG)                                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   Person: Anna Müller                                           │
│   ├── Address: Bahnhofstrasse 42, 8001 Zürich                  │
│   │   └── Premium Region: ZH-1                                 │
│   ├── Birth Date: 22.07.1988                                    │
│   │   └── Age Group: ADULT                                     │
│   └── Gender: FEMALE (for gender-based products)               │
│                                                                 │
│   Premium Lookup: Tariff.getVvgPremium(ZH-1, ADULT, FEMALE)    │
│   └── Monthly Premium: CHF 92.00                               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## API Examples

### Create Coverage

```http
POST /api/v1/policies/{policyId}/coverages
Content-Type: application/json

{
  "insuredPersonId": "person-uuid",
  "productId": "product-uuid",
  "effectiveDate": "2025-01-01",
  "franchise": "CHF_300",
  "withAccident": true
}
```

### Response

```json
{
  "id": "coverage-uuid",
  "policyId": "policy-uuid",
  "insuredPerson": {
    "id": "person-uuid",
    "name": "Hans Müller"
  },
  "product": {
    "id": "product-uuid",
    "code": "KVG_STANDARD_2025",
    "name": "Grundversicherung Standard"
  },
  "status": "ACTIVE",
  "effectiveDate": "2025-01-01",
  "terminationDate": null,
  "franchise": "CHF_300",
  "withAccident": true,
  "premiumRegion": {
    "code": "ZH-1",
    "name": "Zürich Region 1"
  },
  "ageGroup": "ADULT",
  "monthlyPremium": 485.20,
  "createdAt": "2025-01-15T10:30:00Z"
}
```

### Change Franchise

```http
POST /api/v1/coverages/{coverageId}/mutations
Content-Type: application/json

{
  "mutationType": "FRANCHISE_CHANGE",
  "effectiveDate": "2026-01-01",
  "newValue": "CHF_2500"
}
```

### Terminate Coverage

```http
POST /api/v1/coverages/{coverageId}/terminate
Content-Type: application/json

{
  "terminationDate": "2025-12-31",
  "reason": "Change to competitor",
  "newInsurerName": "CSS",
  "newPolicyNumber": "CSS-2026-123456"
}
```

---

## Code Location (Planned)

| File | Path |
|------|------|
| Entity | `backend/govinda-contract/src/main/java/net/voytrex/govinda/contract/domain/model/Coverage.java` |
| Repository | `backend/govinda-contract/src/main/java/net/voytrex/govinda/contract/domain/repository/CoverageRepository.java` |

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ One KVG per person | Only one active KVG coverage allowed |
| ⚠️ Franchise on Jan 1 | KVG franchise changes only on January 1 |
| ⚠️ KVG needs proof | Termination requires proof of new coverage |
| ⚠️ Address affects premium | Moving to new region triggers recalculation |
| ⚠️ Age at effective date | Age group determined at coverage start |

---

## Related Documentation

- [Policy Entity](./policy.md)
- [Mutation Entity](./mutation.md)
- [Premium Table](../product/premium-table.md)
- [Franchise System](../../concepts/franchise-system.md)
- [Contract Use Cases](/docs/use-cases/contract-use-cases.md)

---

*Last Updated: 2026-01-26*

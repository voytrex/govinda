# Policy Entity

## Overview

The **Policy** entity represents an insurance contract between the insurer and a policyholder. It is the aggregate root for the contract bounded context, containing one or more coverages for insured persons. The policyholder is responsible for premium payments and administrative matters.

> **German**: Police, Versicherungspolice
> **Module**: `govinda-contract` (planned)
> **Status**: ⏳ Planned

---

## Entity Definition

```java
@Entity
@Table(name = "policies")
public class Policy {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false, unique = true)
    private String policyNumber;

    @Column(nullable = false)
    private UUID policyholderId;  // Reference to Person

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status = PolicyStatus.QUOTE;

    private UUID billingAddressId;  // Reference to Address

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingFrequency billingFrequency = BillingFrequency.MONTHLY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language preferredLanguage;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    private List<Coverage> coverages = new ArrayList<>();

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
| `tenantId` | UUID | ✅ | Multi-tenant isolation |
| `policyNumber` | String | ✅ | Human-readable policy number |
| `policyholderId` | UUID | ✅ | Reference to Person (must be adult) |
| `status` | PolicyStatus | ✅ | Current policy status |
| `billingAddressId` | UUID | ❌ | Reference to billing address |
| `billingFrequency` | BillingFrequency | ✅ | Payment frequency |
| `preferredLanguage` | Language | ✅ | Communication language |
| `coverages` | List\<Coverage\> | ❌ | Insurance coverages |
| `createdAt` | Instant | Auto | Creation timestamp |
| `updatedAt` | Instant | Auto | Last modification |
| `activatedAt` | Instant | Auto | When policy became active |
| `version` | long | Auto | Optimistic locking |

---

## Policy Status

### PolicyStatus Enum

```java
public enum PolicyStatus {
    QUOTE,      // Quote/offer not yet accepted
    PENDING,    // Application pending review
    ACTIVE,     // Active policy
    SUSPENDED,  // Temporarily suspended
    CANCELLED;  // Terminated/cancelled
}
```

### Status Transitions

```
     ┌────────────────────────────────────────────────────────┐
     │                                                        │
     ▼                                                        │
  QUOTE ──▶ PENDING ──▶ ACTIVE ──▶ SUSPENDED ──▶ ACTIVE      │
     │         │          │            │                      │
     │         │          │            │                      │
     └─────────┴──────────┴────────────┴───────▶ CANCELLED ───┘
```

| Transition | Trigger | Requirements |
|------------|---------|--------------|
| QUOTE → PENDING | Customer acceptance | - |
| PENDING → ACTIVE | Underwriting approval | At least one coverage |
| ACTIVE → SUSPENDED | Non-payment / Request | - |
| SUSPENDED → ACTIVE | Payment / Reactivation | - |
| Any → CANCELLED | Termination | Proper notice period |

---

## Key Behaviors

### Activate Policy

```java
public void activate() {
    if (this.status != PolicyStatus.PENDING) {
        throw new IllegalStateException("Only PENDING policies can be activated");
    }
    if (this.coverages.isEmpty()) {
        throw new IllegalStateException("Policy must have at least one coverage");
    }
    this.status = PolicyStatus.ACTIVE;
    this.activatedAt = Instant.now();
    this.updatedAt = Instant.now();
}
```

### Suspend Policy

```java
public void suspend(String reason) {
    if (this.status != PolicyStatus.ACTIVE) {
        throw new IllegalStateException("Only ACTIVE policies can be suspended");
    }
    this.status = PolicyStatus.SUSPENDED;
    this.updatedAt = Instant.now();
    // Log suspension reason
}
```

### Cancel Policy

```java
public void cancel(LocalDate terminationDate, String reason) {
    if (this.status == PolicyStatus.CANCELLED) {
        throw new IllegalStateException("Policy already cancelled");
    }
    // Terminate all coverages
    this.coverages.forEach(c -> c.terminate(terminationDate, reason));
    this.status = PolicyStatus.CANCELLED;
    this.updatedAt = Instant.now();
}
```

### Add Coverage

```java
public Coverage addCoverage(UUID insuredPersonId, UUID productId,
                            LocalDate effectiveDate, CoverageParams params) {
    Coverage coverage = Coverage.builder()
        .policy(this)
        .insuredPersonId(insuredPersonId)
        .productId(productId)
        .effectiveDate(effectiveDate)
        .franchise(params.getFranchise())
        .withAccident(params.isWithAccident())
        .build();

    // Calculate premium
    coverage.calculatePremium();

    this.coverages.add(coverage);
    this.updatedAt = Instant.now();

    return coverage;
}
```

### Get Active Coverages

```java
public List<Coverage> activeCoverages() {
    return coverages.stream()
        .filter(c -> c.getStatus() == CoverageStatus.ACTIVE)
        .collect(Collectors.toList());
}
```

### Calculate Monthly Premium

```java
public Money monthlyPremium() {
    return activeCoverages().stream()
        .map(Coverage::getMonthlyPremium)
        .reduce(Money.chf(0), Money::add);
}
```

---

## Billing Frequency

### BillingFrequency Enum

```java
public enum BillingFrequency {
    MONTHLY(12, BigDecimal.ZERO),           // 0% discount
    QUARTERLY(4, new BigDecimal("0.005")),  // 0.5% discount
    SEMI_ANNUAL(2, new BigDecimal("0.01")), // 1% discount
    ANNUAL(1, new BigDecimal("0.02"));      // 2% discount

    private final int invoicesPerYear;
    private final BigDecimal discount;

    public Money calculateInvoiceAmount(Money monthlyPremium) {
        int monthsPerPeriod = 12 / invoicesPerYear;
        Money periodAmount = monthlyPremium.multiply(monthsPerPeriod);
        return periodAmount.multiply(BigDecimal.ONE.subtract(discount));
    }
}
```

---

## Relationships

### Policy → Person (Policyholder)

```
Policy (N) ────────────> Person (1)
      ↑
policyholderId: UUID (FK)
```

### Policy → Coverage (1:N)

```
Policy (1) ────────────< Coverage (N)
                   ↑
            policyId: UUID (FK)
```

### Policy → Address (Billing)

```
Policy (N) ────────────> Address (1)
      ↑
billingAddressId: UUID (FK)
```

---

## Policy Number Format

Policy numbers follow a structured format:

```
Format: {PREFIX}-{YEAR}-{SEQUENCE}

Examples:
- POL-2025-000001
- POL-2025-000002
- POL-2025-123456

Generation:
@Service
public class PolicyNumberGenerator {
    public String generate() {
        int year = LocalDate.now().getYear();
        long sequence = sequenceRepository.nextValue("POLICY");
        return String.format("POL-%d-%06d", year, sequence);
    }
}
```

---

## Validation Rules

### Policyholder Requirements

```java
// Policyholder must be an adult (18+)
Person policyholder = personRepository.findById(policyholderId);
if (policyholder.ageAt(LocalDate.now()) < 18) {
    throw new IllegalArgumentException("Policyholder must be at least 18 years old");
}

// Policyholder must have ACTIVE status
if (policyholder.getStatus() != PersonStatus.ACTIVE) {
    throw new IllegalArgumentException("Policyholder must have ACTIVE status");
}
```

### Coverage Requirements

```java
// At least one coverage for activation
if (coverages.isEmpty() && status == PolicyStatus.PENDING) {
    throw new IllegalStateException("Cannot activate policy without coverages");
}

// KVG: only one active coverage per person
// VVG: depends on product rules
```

---

## API Examples

### Create Policy

```http
POST /api/v1/policies
Content-Type: application/json

{
  "policyholderId": "person-uuid",
  "billingAddressId": "address-uuid",
  "billingFrequency": "QUARTERLY",
  "preferredLanguage": "DE"
}
```

### Response

```json
{
  "id": "policy-uuid",
  "policyNumber": "POL-2025-000001",
  "policyholderId": "person-uuid",
  "policyholderName": "Hans Müller",
  "status": "QUOTE",
  "billingFrequency": "QUARTERLY",
  "preferredLanguage": "DE",
  "coverages": [],
  "monthlyPremium": 0.00,
  "createdAt": "2025-01-15T10:30:00Z"
}
```

### Add Coverage to Policy

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

### Get Policy with Coverages

```http
GET /api/v1/policies/{policyId}
```

### Response

```json
{
  "id": "policy-uuid",
  "policyNumber": "POL-2025-000001",
  "status": "ACTIVE",
  "policyholder": {
    "id": "person-uuid",
    "name": "Hans Müller"
  },
  "billingAddress": {
    "formatted": "Bahnhofstrasse 42, 8001 Zürich"
  },
  "billingFrequency": "QUARTERLY",
  "coverages": [
    {
      "id": "coverage-uuid",
      "insuredPerson": {
        "id": "person-uuid",
        "name": "Hans Müller"
      },
      "product": {
        "code": "KVG_STANDARD_2025",
        "name": "Grundversicherung Standard"
      },
      "status": "ACTIVE",
      "monthlyPremium": 485.20
    }
  ],
  "monthlyPremium": 485.20,
  "quarterlyAmount": 1449.09,
  "activatedAt": "2025-01-15T10:35:00Z"
}
```

---

## Code Location (Planned)

| File | Path |
|------|------|
| Entity | `backend/govinda-contract/src/main/java/net/voytrex/govinda/contract/domain/model/Policy.java` |
| Repository | `backend/govinda-contract/src/main/java/net/voytrex/govinda/contract/domain/repository/PolicyRepository.java` |
| Service | `backend/govinda-contract/src/main/java/net/voytrex/govinda/contract/application/service/PolicyService.java` |

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ Adult policyholder | Must be at least 18 years old |
| ⚠️ Unique policy number | Generated automatically |
| ⚠️ Coverage required | At least one coverage for activation |
| ⚠️ Billing discounts | Longer intervals = lower total cost |
| ⚠️ KVG termination | Requires proof of new coverage |

---

## Related Documentation

- [Coverage Entity](./coverage.md)
- [Mutation Entity](./mutation.md)
- [Person Entity](../masterdata/person.md)
- [Billing and Payments](../../concepts/billing-and-payments.md)
- [Contract Use Cases](/docs/use-cases/contract-use-cases.md)

---

*Last Updated: 2026-01-26*

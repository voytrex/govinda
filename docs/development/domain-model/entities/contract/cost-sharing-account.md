# CostSharingAccount Entity

## Overview

The **CostSharingAccount** (Kostenbeteiligungskonto) tracks a person's annual franchise and Selbstbehalt usage for KVG coverage.

> **German**: Kostenbeteiligungskonto, Franchisekonto
> **Module**: `govinda-contract`
> **Status**: Planned

**Resets**: January 1st each year (or coverage start date if later)

---

## Entity Definition

```java
@Entity
@Table(name = "cost_sharing_accounts")
public class CostSharingAccount {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    // Links
    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;

    // Period
    @Column(nullable = false)
    private Integer year;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    // Franchise Configuration
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "franchise_amount"))
    })
    private Money franchiseAmount;  // e.g., CHF 300, 500, 1000, 1500, 2000, 2500

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "franchise_used"))
    })
    private Money franchiseUsed;

    @Column(name = "franchise_exhausted")
    private boolean franchiseExhausted = false;

    @Column(name = "franchise_exhausted_date")
    private LocalDate franchiseExhaustedDate;

    // Selbstbehalt (Co-pay)
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "selbstbehalt_max"))
    })
    private Money selbstbehaltMax;  // CHF 700 adults, CHF 350 children

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "selbstbehalt_used"))
    })
    private Money selbstbehaltUsed;

    @Column(name = "selbstbehalt_exhausted")
    private boolean selbstbehaltExhausted = false;

    @Column(name = "selbstbehalt_exhausted_date")
    private LocalDate selbstbehaltExhaustedDate;

    // Summary
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_patient_share"))
    })
    private Money totalPatientShare;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_insurer_paid"))
    })
    private Money totalInsurerPaid;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private long version;
}
```

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `personId` | UUID | Required | Patient |
| `coverageId` | UUID | Required | KVG coverage |
| `year` | Integer | Required | Calendar year |
| `periodStart` | LocalDate | Required | Start of tracking period |
| `periodEnd` | LocalDate | Required | End of tracking period |
| `franchiseAmount` | Money | Required | Selected franchise (300-2500 CHF) |
| `franchiseUsed` | Money | Required | Amount applied to claims |
| `franchiseExhausted` | boolean | Required | Franchise fully used |
| `selbstbehaltMax` | Money | Required | Max co-pay (700/350 CHF) |
| `selbstbehaltUsed` | Money | Required | Co-pay applied to claims |
| `totalPatientShare` | Money | Required | Total patient responsibility |
| `totalInsurerPaid` | Money | Required | Total insurer payments |

---

## Franchise Options (KVG)

### Adults (18+)

| Franchise | Monthly Premium Impact |
|-----------|----------------------|
| CHF 300 | Base premium |
| CHF 500 | ~8% reduction |
| CHF 1,000 | ~17% reduction |
| CHF 1,500 | ~25% reduction |
| CHF 2,000 | ~32% reduction |
| CHF 2,500 | ~40% reduction |

### Children (0-17)

| Franchise | Note |
|-----------|------|
| CHF 0 | Default, no franchise |
| CHF 100 | Optional |
| CHF 200 | Optional |
| CHF 300 | Optional |
| CHF 400 | Optional |
| CHF 500 | Optional |
| CHF 600 | Maximum |

---

## Selbstbehalt Limits

```java
public class SelbstbehaltLimits {
    public static final Money ADULT_MAX = Money.chf(700.00);
    public static final Money CHILD_MAX = Money.chf(350.00);
    public static final BigDecimal RATE = new BigDecimal("0.10");  // 10%
}
```

---

## Cost Sharing Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    Approved Claim Amount                        │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │   Is Maternity/Accident?      │
              └───────────────┬───────────────┘
                   Yes │            │ No
                       ▼            ▼
           ┌───────────────┐  ┌────────────────────────┐
           │ No cost share │  │ Apply Franchise (100%) │
           │ Insurer pays  │  └───────────┬────────────┘
           │ full amount   │              │
           └───────────────┘              ▼
                              ┌─────────────────────────┐
                              │ Remaining after         │
                              │ Franchise               │
                              └───────────┬─────────────┘
                                          │
                                          ▼
                              ┌─────────────────────────┐
                              │ Apply Selbstbehalt     │
                              │ (10%, max 700/350 CHF) │
                              └───────────┬─────────────┘
                                          │
                                          ▼
                        ┌─────────────────┴─────────────────┐
                        │                                   │
                        ▼                                   ▼
              ┌─────────────────┐                ┌─────────────────┐
              │  Patient Share  │                │  Insurer Pays   │
              │  (Franchise +   │                │  (Remaining)    │
              │   Selbstbehalt) │                │                 │
              └─────────────────┘                └─────────────────┘
```

---

## Example Calculation

```java
// Person with CHF 1500 franchise, already used CHF 800
CostSharingAccount account = loadAccount(personId, 2026);
// franchiseAmount = 1500, franchiseUsed = 800
// selbstbehaltMax = 700, selbstbehaltUsed = 200

Claim claim = new Claim();
claim.setApprovedAmount(Money.chf(1000.00));

// Step 1: Apply franchise
Money franchiseRemaining = account.getFranchiseAmount()
    .subtract(account.getFranchiseUsed());  // 1500 - 800 = 700

Money franchiseApplied = Money.min(
    claim.getApprovedAmount(),  // 1000
    franchiseRemaining          // 700
);  // = 700

Money afterFranchise = claim.getApprovedAmount()
    .subtract(franchiseApplied);  // 1000 - 700 = 300

// Step 2: Apply Selbstbehalt (10%)
Money selbstbehaltRemaining = account.getSelbstbehaltMax()
    .subtract(account.getSelbstbehaltUsed());  // 700 - 200 = 500

Money tenPercent = afterFranchise.multiply(0.10);  // 300 * 0.10 = 30

Money selbstbehaltApplied = Money.min(
    tenPercent,            // 30
    selbstbehaltRemaining  // 500
);  // = 30

// Results
Money patientShare = franchiseApplied.add(selbstbehaltApplied);  // 700 + 30 = 730
Money insurerPays = claim.getApprovedAmount().subtract(patientShare);  // 1000 - 730 = 270
```

---

## Year-End Reset

```java
@Scheduled(cron = "0 0 0 1 1 *")  // Jan 1st midnight
public void resetCostSharingAccounts() {
    List<Coverage> activeCoverages = coverageRepository
        .findActiveKvgCoverages(LocalDate.now());

    for (Coverage coverage : activeCoverages) {
        CostSharingAccount newAccount = CostSharingAccount.builder()
            .tenantId(coverage.getTenantId())
            .personId(coverage.getPersonId())
            .coverageId(coverage.getId())
            .year(LocalDate.now().getYear())
            .periodStart(LocalDate.now())
            .periodEnd(LocalDate.of(LocalDate.now().getYear(), 12, 31))
            .franchiseAmount(coverage.getFranchiseAmount())
            .franchiseUsed(Money.ZERO)
            .selbstbehaltMax(calculateSelbstbehaltMax(coverage))
            .selbstbehaltUsed(Money.ZERO)
            .build();

        repository.save(newAccount);
    }
}
```

---

## Special Cases

### Maternity (Mutterschaft)

No cost sharing from 13th week of pregnancy through 8 weeks after birth:

```java
if (claim.isMaternity()) {
    claim.setFranchiseApplied(Money.ZERO);
    claim.setSelbstbehaltApplied(Money.ZERO);
    claim.setInsurerPays(claim.getApprovedAmount());
}
```

### Accident (Unfall)

Accidents are covered by UVG (employer insurance), not KVG. If no UVG coverage:

```java
if (claim.isAccident() && !hasUvgCoverage(claim.getPersonId())) {
    // Normal KVG cost sharing applies
    applyCostSharing(claim);
}
```

### Family Cap

Total Selbstbehalt for a family is capped at 2x adult maximum (CHF 1,400):

```java
Money familySelbstbehaltMax = Money.chf(1400.00);
Money familySelbstbehaltUsed = sumFamilySelbstbehalt(householdId, year);
```

---

## Related Documentation

- [Coverage](./coverage.md) - KVG coverage with franchise
- [Claim](../claims/claim.md) - Individual claim with cost sharing
- [Cost Sharing Concept](../../concepts/cost-sharing.md) - Business rules

---

*Last Updated: 2026-01-28*

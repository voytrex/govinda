# GAP-09: KVG Cost Sharing (Franchise & Selbstbehalt)

## Problem Statement

Missing tracking for KVG cost sharing components:
- **Franchise** - annual deductible (CHF 300-2500)
- **Selbstbehalt** - 10% co-payment after franchise (max CHF 700/year)
- Annual reset and carryover rules
- Special rules for children, maternity

---

## Legal Basis

- **KVG Art. 64** - Cost sharing (Kostenbeteiligung)
- **KVV Art. 103** - Franchise amounts
- **KVV Art. 104** - Selbstbehalt rules

---

## Cost Sharing Structure

### Franchise (Deductible)

| Category | Options (CHF) |
|----------|---------------|
| Adults | 300, 500, 1000, 1500, 2000, 2500 |
| Children (0-18) | 0, 100, 200, 300, 400, 500, 600 |
| Young Adults (19-25) | Same as adults |

**Rules:**
- Paid 100% by insured until reached
- Resets January 1st each year
- Can change franchise for following year (deadline: Nov 30)

### Selbstbehalt (Co-payment)

| Rule | Value |
|------|-------|
| Rate | 10% of costs after franchise |
| Annual max (adults) | CHF 700 |
| Annual max (children) | CHF 350 |
| Maternity | 0% (no Selbstbehalt) |

---

## Model

```java
@Entity
@Table(name = "cost_sharing_accounts")
public class CostSharingAccount {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;  // KVG coverage

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "account_year", nullable = false)
    private int accountYear;

    // Franchise
    @Enumerated(EnumType.STRING)
    @Column(name = "franchise_level", nullable = false)
    private Franchise franchiseLevel;  // CHF_300, CHF_500, etc.

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "franchise_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money franchiseAmount;  // Total franchise for year

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "franchise_used"))
    })
    private Money franchiseUsed;  // Amount used so far

    @Column(name = "franchise_exhausted")
    private boolean franchiseExhausted = false;

    @Column(name = "franchise_exhausted_date")
    private LocalDate franchiseExhaustedDate;

    // Selbstbehalt
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "selbstbehalt_max"))
    })
    private Money selbstbehaltMax;  // CHF 700 or 350

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "selbstbehalt_used"))
    })
    private Money selbstbehaltUsed;  // Amount used so far

    @Column(name = "selbstbehalt_exhausted")
    private boolean selbstbehaltExhausted = false;

    @Column(name = "selbstbehalt_exhausted_date")
    private LocalDate selbstbehaltExhaustedDate;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private long version;

    // Business methods
    public Money calculatePatientShare(Money treatmentCost, boolean isMaternity) {
        if (isMaternity) {
            return Money.chf(0);  // No cost sharing for maternity
        }

        Money remaining = treatmentCost;
        Money patientShare = Money.chf(0);

        // 1. Apply to franchise first
        if (!franchiseExhausted) {
            Money franchiseRemaining = franchiseAmount.subtract(franchiseUsed);
            Money franchisePortion = remaining.min(franchiseRemaining);
            patientShare = patientShare.add(franchisePortion);
            remaining = remaining.subtract(franchisePortion);
        }

        // 2. Apply Selbstbehalt to remainder
        if (remaining.isPositive() && !selbstbehaltExhausted) {
            Money selbstbehaltRemaining = selbstbehaltMax.subtract(selbstbehaltUsed);
            Money tenPercent = remaining.multiply(new BigDecimal("0.10"));
            Money selbstbehaltPortion = tenPercent.min(selbstbehaltRemaining);
            patientShare = patientShare.add(selbstbehaltPortion);
        }

        return patientShare;
    }
}

public enum AccountStatus {
    ACTIVE,
    CLOSED,      // Year ended
    TRANSFERRED  // Changed insurer mid-year
}
```

### CostSharingEntry (Transaction Log)

```java
@Entity
@Table(name = "cost_sharing_entries")
public class CostSharingEntry {

    @Id
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "treatment_date")
    private LocalDate treatmentDate;

    // Amounts
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "treatment_cost"))
    })
    private Money treatmentCost;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "franchise_applied"))
    })
    private Money franchiseApplied;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "selbstbehalt_applied"))
    })
    private Money selbstbehaltApplied;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "insurer_pays"))
    })
    private Money insurerPays;

    // Reference
    @Column(name = "claim_id")
    private UUID claimId;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "provider_name")
    private String providerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "treatment_type")
    private TreatmentType treatmentType;

    @Column(name = "is_maternity")
    private boolean isMaternity = false;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;
}

public enum TreatmentType {
    AMBULATORY,      // Ambulant
    HOSPITAL,        // Stationär
    MEDICATION,      // Medikamente
    LABORATORY,      // Labor
    PHYSIOTHERAPY,   // Physiotherapie
    MATERNITY,       // Mutterschaft
    DENTAL,          // Zahnarzt (limited KVG)
    OTHER
}
```

---

## Special Rules

### Maternity Exemption
```java
// No cost sharing for:
// - Prenatal checkups (7 standard + ultrasounds)
// - Delivery and hospital stay
// - Postnatal checkups (1 standard)
// - Breastfeeding counseling (3x)
public boolean isMaternityExempt(TreatmentType type, String code) {
    return type == TreatmentType.MATERNITY;
}
```

### Mid-Year Insurer Change
```java
// When changing insurer mid-year:
// - Franchise usage transfers (new insurer must honor)
// - Selbstbehalt usage transfers
// - Old insurer provides certificate
public CostSharingTransfer createTransferCertificate(CostSharingAccount account) {
    return new CostSharingTransfer(
        account.getPersonId(),
        account.getAccountYear(),
        account.getFranchiseUsed(),
        account.getSelbstbehaltUsed(),
        LocalDate.now()
    );
}
```

### Children in Same Household
```java
// Combined Selbstbehalt cap for children in same household:
// Max CHF 700 total (not per child)
public Money getHouseholdChildrenSelbstbehaltMax(Household household) {
    long childCount = household.getCurrentMembers().stream()
        .filter(m -> m.getRole() == HouseholdRole.CHILD)
        .count();

    // Even with multiple children, max is CHF 700 combined
    return Money.chf(700);
}
```

---

## Database Schema

```sql
CREATE TABLE cost_sharing_accounts (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    coverage_id UUID NOT NULL,
    person_id UUID NOT NULL,
    account_year INTEGER NOT NULL,
    franchise_level VARCHAR(10) NOT NULL,
    franchise_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'CHF',
    franchise_used DECIMAL(10,2) DEFAULT 0,
    franchise_exhausted BOOLEAN DEFAULT FALSE,
    franchise_exhausted_date DATE,
    selbstbehalt_max DECIMAL(10,2) NOT NULL,
    selbstbehalt_used DECIMAL(10,2) DEFAULT 0,
    selbstbehalt_exhausted BOOLEAN DEFAULT FALSE,
    selbstbehalt_exhausted_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0,
    UNIQUE(coverage_id, account_year)
);

CREATE TABLE cost_sharing_entries (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES cost_sharing_accounts(id),
    entry_date DATE NOT NULL,
    treatment_date DATE,
    treatment_cost DECIMAL(10,2) NOT NULL,
    franchise_applied DECIMAL(10,2) DEFAULT 0,
    selbstbehalt_applied DECIMAL(10,2) DEFAULT 0,
    insurer_pays DECIMAL(10,2) NOT NULL,
    claim_id UUID,
    invoice_number VARCHAR(50),
    provider_name VARCHAR(255),
    treatment_type VARCHAR(20),
    is_maternity BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_csa_person_year ON cost_sharing_accounts(person_id, account_year);
CREATE INDEX idx_cse_account ON cost_sharing_entries(account_id);
```

---

*Status: Draft*
*Priority: HIGH*

# GAP-08: Reductions, Surcharges & Payment Incentives

## Problem Statement

Missing support for financial adjustments to premiums/fees:
1. **IPV** - Individuelle Prämienverbilligung (cantonal premium subsidy)
2. **Skonto** - Prepayment discounts (annual/semi-annual)
3. **Malus** - Premium surcharge for late KVG enrollment (Art. 5 KVG)
4. Other reductions/surcharges

---

## 1. IPV - Individuelle Prämienverbilligung

### Legal Basis
- **KVG Art. 65** - Cantons must reduce premiums for persons in modest economic circumstances
- Cantons set their own rules, thresholds, and amounts

### Key Facts

| Aspect | Details |
|--------|---------|
| Funding | Canton + Federal (7.5% of gross KVG costs) |
| Total (2020) | CHF 5.5 billion |
| Average benefit | CHF 2,304 per recipient |
| Children | Min. 80% premium reduction |
| Young adults (in training) | Min. 50% premium reduction |

### Cantonal Differences

| Canton | Basis for Calculation |
|--------|----------------------|
| 12 cantons | Taxable income (steuerbares Einkommen) |
| 9 cantons | Net income (Reineinkommen) |
| 4 cantons | Net earnings (Nettoeinkünfte) |

### Model

```java
@Entity
@Table(name = "premium_subsidies")
public class PremiumSubsidy {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;  // Recipient

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Canton canton;  // Granting canton

    // Period
    @Column(name = "subsidy_year", nullable = false)
    private int subsidyYear;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo;

    // Amounts
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "monthly_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money monthlyAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "annual_amount"))
    })
    private Money annualAmount;

    // Source data
    @Column(name = "reference_income")
    private BigDecimal referenceIncome;  // Income used for calculation

    @Column(name = "household_size")
    private Integer householdSize;

    @Column(name = "decision_number")
    private String decisionNumber;  // Cantonal decision reference

    @Column(name = "decision_date")
    private LocalDate decisionDate;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubsidyStatus status = SubsidyStatus.ACTIVE;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

public enum SubsidyStatus {
    PENDING,      // Application pending
    ACTIVE,       // Currently receiving
    SUSPENDED,    // Temporarily suspended
    TERMINATED,   // Ended
    REJECTED      // Application rejected
}
```

**Source**: [BAG Prämienverbilligung](https://www.bag.admin.ch/de/krankenversicherung-praemienverbilligung)

---

## 2. Skonto - Prepayment Discount

### What Is Skonto?
Cash discount for paying premiums in advance (semi-annual or annual).

### Market Data

| Discount | Insurers |
|----------|----------|
| 2% | Assura, Sympany, Visana |
| 1% | KPT, ÖKK, Sanitas |
| 0.5% | Various (semi-annual) |
| 0% | Groupe Mutuel (no skonto) |

### Savings Example
- Average annual premium: CHF 5,584
- At 2% skonto: CHF ~112 saved
- At 1% skonto: CHF ~56 saved

### Model

```java
public enum BillingFrequency {
    MONTHLY("Monatlich", 12, BigDecimal.ZERO),
    QUARTERLY("Vierteljährlich", 4, BigDecimal.ZERO),
    SEMI_ANNUAL("Halbjährlich", 2, new BigDecimal("0.5")),  // 0.5% typical
    ANNUAL("Jährlich", 1, new BigDecimal("1.0"));           // 1-2% typical

    private final String nameDe;
    private final int periodsPerYear;
    private final BigDecimal defaultSkontoPercent;

    // ... constructor and getters
}

@Entity
@Table(name = "skonto_rates")
public class SkontoRate {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "product_id")
    private UUID productId;  // null = all products

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_frequency", nullable = false)
    private BillingFrequency billingFrequency;

    @Column(name = "skonto_percent", nullable = false)
    private BigDecimal skontoPercent;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "payment_deadline_days")
    private Integer paymentDeadlineDays;  // e.g., by Jan 3rd

    @Version
    private long version;
}
```

**Source**: [Comparis Skonto](https://en.comparis.ch/krankenkassen/praemien/praemien-rabatte-vorauszahlung)

---

## 3. Malus - Late Enrollment Surcharge

### Legal Basis
- **KVG Art. 5 Abs. 2** - Premium surcharge for inexcusable late enrollment
- **KVV Art. 8** - Surcharge rates and duration

### Rules

| Aspect | Regulation |
|--------|------------|
| Surcharge rate | **30-50%** of premium |
| Duration | **2x the delay**, max 5 years |
| Calculation base | Current monthly premiums |
| Transfer | Must be reported to new insurer on switch |

### Exceptions

| Situation | Surcharge |
|-----------|-----------|
| Social services pays premiums | **No surcharge** |
| Hardship case | **<30%** (reduced) |
| Excusable delay | **No surcharge** |

### Model

```java
@Entity
@Table(name = "enrollment_surcharges")
public class EnrollmentSurcharge {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    // Delay details
    @Column(name = "required_enrollment_date", nullable = false)
    private LocalDate requiredEnrollmentDate;  // When should have enrolled

    @Column(name = "actual_enrollment_date", nullable = false)
    private LocalDate actualEnrollmentDate;  // When actually enrolled

    @Column(name = "delay_days", nullable = false)
    private int delayDays;  // Days late

    // Surcharge
    @Column(name = "surcharge_percent", nullable = false)
    private BigDecimal surchargePercent;  // 30-50%

    @Column(name = "surcharge_start", nullable = false)
    private LocalDate surchargeStart;

    @Column(name = "surcharge_end", nullable = false)
    private LocalDate surchargeEnd;  // 2x delay, max 5 years

    @Column(name = "surcharge_months")
    private int surchargeMonths;  // Total months

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurchargeStatus status = SurchargeStatus.ACTIVE;

    @Column(name = "hardship_reduction")
    private boolean hardshipReduction = false;

    @Column(name = "waived_by_social_services")
    private boolean waivedBySocialServices = false;

    // Transfer tracking
    @Column(name = "original_insurer_id")
    private UUID originalInsurerId;  // Who assessed it

    @Column(name = "transferred_from_insurer_id")
    private UUID transferredFromInsurerId;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Column
    private String notes;

    @Version
    private long version;
}

public enum SurchargeStatus {
    ACTIVE,       // Currently being charged
    COMPLETED,    // Surcharge period ended
    WAIVED,       // Waived (social services)
    REDUCED,      // Reduced for hardship
    TRANSFERRED   // Transferred to another insurer
}
```

### Business Rules

```java
public class EnrollmentSurchargeCalculator {

    public EnrollmentSurcharge calculate(
            LocalDate requiredDate,
            LocalDate actualDate,
            boolean isHardship,
            boolean socialServicesPaysPremium) {

        if (socialServicesPaysPremium) {
            return null;  // No surcharge
        }

        long delayDays = ChronoUnit.DAYS.between(requiredDate, actualDate);
        if (delayDays <= 0) {
            return null;  // No delay
        }

        // Surcharge duration: 2x delay, max 5 years
        long surchargeDays = Math.min(delayDays * 2, 5 * 365);
        int surchargeMonths = (int) Math.ceil(surchargeDays / 30.0);

        // Surcharge rate: 30-50%
        BigDecimal rate;
        if (isHardship) {
            rate = new BigDecimal("0.25");  // Reduced for hardship
        } else if (delayDays > 365) {
            rate = new BigDecimal("0.50");  // Max rate for long delay
        } else {
            rate = new BigDecimal("0.30");  // Base rate
        }

        EnrollmentSurcharge surcharge = new EnrollmentSurcharge();
        surcharge.setDelayDays((int) delayDays);
        surcharge.setSurchargePercent(rate.multiply(new BigDecimal("100")));
        surcharge.setSurchargeStart(actualDate);
        surcharge.setSurchargeEnd(actualDate.plusDays(surchargeDays));
        surcharge.setSurchargeMonths(surchargeMonths);
        surcharge.setHardshipReduction(isHardship);

        return surcharge;
    }
}
```

**Source**: [KVG Art. 5](https://lawbrary.ch/law/art/KVG-v2021.01-de-art-5/)

---

## 4. Unified Adjustment Framework

### PremiumAdjustment Entity

Unify all adjustments (reductions and surcharges) in one framework:

```java
@Entity
@Table(name = "premium_adjustments")
public class PremiumAdjustment {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    // Target
    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;  // Which subscription

    @Column(name = "person_id")
    private UUID personId;  // Optional: specific person

    // Adjustment type
    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type", nullable = false)
    private AdjustmentType adjustmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_reason", nullable = false)
    private AdjustmentReason adjustmentReason;

    // Value
    @Column(name = "is_reduction", nullable = false)
    private boolean isReduction;  // true = discount, false = surcharge

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_method", nullable = false)
    private CalculationMethod calculationMethod;

    @Column(name = "percent_value")
    private BigDecimal percentValue;  // For percentage adjustments

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "fixed_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "fixed_currency"))
    })
    private Money fixedAmount;  // For fixed amount adjustments

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Reference
    @Column(name = "external_reference")
    private String externalReference;  // Decision number, etc.

    @Column(name = "source_entity_type")
    private String sourceEntityType;  // "CANTON", "INSURER", etc.

    @Column(name = "source_entity_id")
    private UUID sourceEntityId;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentStatus status = AdjustmentStatus.ACTIVE;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}
```

### AdjustmentType Enum

```java
public enum AdjustmentType {
    // Reductions
    IPV("IPV - Prämienverbilligung", true),
    SKONTO("Skonto - Vorauszahlung", true),
    FAMILY_DISCOUNT("Familienrabatt", true),
    LOYALTY_DISCOUNT("Treuerabatt", true),
    MODEL_DISCOUNT("Modellrabatt", true),  // HMO, Hausarzt
    ACCIDENT_EXCLUSION("UVG-Abzug", true),  // Employed, covered by UVG

    // Surcharges
    LATE_ENROLLMENT("Verspäteter Beitritt", false),
    RISK_SURCHARGE("Risikozuschlag", false),  // VVG only
    ADMINISTRATIVE_FEE("Verwaltungsgebühr", false),
    LATE_PAYMENT_FEE("Mahngebühr", false);

    private final String nameDe;
    private final boolean isReduction;

    AdjustmentType(String nameDe, boolean isReduction) {
        this.nameDe = nameDe;
        this.isReduction = isReduction;
    }

    public boolean isReduction() {
        return isReduction;
    }
}
```

### AdjustmentReason Enum

```java
public enum AdjustmentReason {
    // IPV reasons
    LOW_INCOME("Tiefes Einkommen"),
    EL_RECIPIENT("EL-Bezüger"),
    SOCIAL_WELFARE("Sozialhilfe"),
    CHILD_REDUCTION("Kinderreduktion"),
    YOUNG_ADULT_TRAINING("Junge Erwachsene in Ausbildung"),

    // Skonto reasons
    ANNUAL_PREPAYMENT("Jahresvorauszahlung"),
    SEMI_ANNUAL_PREPAYMENT("Halbjahresvorauszahlung"),

    // Surcharge reasons
    LATE_ENROLLMENT_INEXCUSABLE("Verspätung unentschuldbar"),
    LATE_ENROLLMENT_HARDSHIP("Verspätung Härtefall"),
    VVG_HEALTH_RISK("Gesundheitsrisiko VVG"),

    // Other
    PROMOTIONAL("Aktion"),
    CONTRACTUAL("Vertraglich vereinbart");

    private final String nameDe;

    AdjustmentReason(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### CalculationMethod Enum

```java
public enum CalculationMethod {
    PERCENTAGE("Prozent"),           // X% of premium
    FIXED_MONTHLY("Fester Monatsbetrag"),  // Fixed CHF/month
    FIXED_ANNUAL("Fester Jahresbetrag"),   // Fixed CHF/year
    TIERED("Gestaffelt");            // Complex calculation

    private final String nameDe;

    CalculationMethod(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 5. Database Schema

```sql
-- Premium subsidies (IPV)
CREATE TABLE premium_subsidies (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID NOT NULL REFERENCES persons(id),
    canton VARCHAR(2) NOT NULL,
    subsidy_year INTEGER NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    monthly_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'CHF',
    annual_amount DECIMAL(10,2),
    reference_income DECIMAL(12,2),
    household_size INTEGER,
    decision_number VARCHAR(50),
    decision_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Skonto rates
CREATE TABLE skonto_rates (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    product_id UUID,
    billing_frequency VARCHAR(20) NOT NULL,
    skonto_percent DECIMAL(5,2) NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE,
    payment_deadline_days INTEGER,
    version BIGINT DEFAULT 0
);

-- Enrollment surcharges (Malus)
CREATE TABLE enrollment_surcharges (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID NOT NULL REFERENCES persons(id),
    required_enrollment_date DATE NOT NULL,
    actual_enrollment_date DATE NOT NULL,
    delay_days INTEGER NOT NULL,
    surcharge_percent DECIMAL(5,2) NOT NULL,
    surcharge_start DATE NOT NULL,
    surcharge_end DATE NOT NULL,
    surcharge_months INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    hardship_reduction BOOLEAN DEFAULT FALSE,
    waived_by_social_services BOOLEAN DEFAULT FALSE,
    original_insurer_id UUID,
    transferred_from_insurer_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    version BIGINT DEFAULT 0
);

-- Generic premium adjustments
CREATE TABLE premium_adjustments (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    coverage_id UUID NOT NULL,
    person_id UUID,
    adjustment_type VARCHAR(30) NOT NULL,
    adjustment_reason VARCHAR(40) NOT NULL,
    is_reduction BOOLEAN NOT NULL,
    calculation_method VARCHAR(20) NOT NULL,
    percent_value DECIMAL(5,2),
    fixed_amount DECIMAL(10,2),
    fixed_currency VARCHAR(3),
    valid_from DATE NOT NULL,
    valid_to DATE,
    external_reference VARCHAR(50),
    source_entity_type VARCHAR(30),
    source_entity_id UUID,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_ps_person ON premium_subsidies(person_id);
CREATE INDEX idx_ps_year ON premium_subsidies(subsidy_year);
CREATE INDEX idx_es_person ON enrollment_surcharges(person_id);
CREATE INDEX idx_pa_coverage ON premium_adjustments(coverage_id);
```

---

## Sources

- [BAG Prämienverbilligung](https://www.bag.admin.ch/de/krankenversicherung-praemienverbilligung)
- [Comparis Skonto-Rabatt](https://en.comparis.ch/krankenkassen/praemien/praemien-rabatte-vorauszahlung)
- [KVG Art. 5 - Verspäteter Beitritt](https://lawbrary.ch/law/art/KVG-v2021.01-de-art-5/)
- [Kanton Zürich IPV 2026](https://www.zh.ch/de/gesundheit/praemienverbilligung_krankenversicherung.html)

---

*Status: Draft*
*Priority: HIGH*

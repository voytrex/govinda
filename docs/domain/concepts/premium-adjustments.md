# Premium Adjustments (Prämienanpassungen)

## Overview

This document describes all types of premium adjustments in Swiss health insurance, including subsidies, discounts, surcharges, and penalties. These adjustments can increase or decrease the base premium calculated from the tariff tables.

> **German**: Prämienanpassungen, Rabatte, Zuschläge
> **Legal Basis**: KVG Art. 61-65, KVV Art. 8-10

---

## Adjustment Categories

| Category | Direction | Source | Example |
|----------|-----------|--------|---------|
| **Subsidy** | Reduces | Government | IPV (cantonal premium subsidy) |
| **Discount** | Reduces | Insurer | Payment frequency, family |
| **Reduction** | Reduces | Product | Accident exclusion, model choice |
| **Surcharge** | Increases | Regulatory | Late enrollment penalty |

---

## 1. Premium Subsidies (Prämienverbilligung / IPV)

### Legal Framework

| Aspect | Details |
|--------|---------|
| **Law** | KVG Art. 65 |
| **Administration** | Cantonal (each canton has own rules) |
| **Funding** | Federal contribution + cantonal contribution |
| **Application** | Via cantonal authority (SVA, Ausgleichskasse) |

### Eligibility

Premium subsidies are means-tested based on:
- Taxable income
- Taxable assets
- Family situation
- Canton of residence

### Cantonal Variations

Each canton sets its own:
- Income thresholds
- Asset limits
- Subsidy calculation formula
- Application deadlines

#### Example: Canton Zürich (2025)

| Factor | Rate |
|--------|------|
| Personal contribution (married) | 7.0% of relevant income |
| Personal contribution (single) | 5.6% of relevant income |
| Asset limit | Varies by family size |

#### Example: Canton St. Gallen

| Factor | Limit |
|--------|-------|
| Asset limit (single) | CHF 100,000 |
| Asset limit (with children) | Up to CHF 150,000 |

### Special Categories

| Category | Minimum Reduction | Legal Basis |
|----------|-------------------|-------------|
| **Children** | 80% of premium | KVG Art. 65(1bis) |
| **Young adults in education** | 50% of premium | KVG Art. 65(1bis) |

### Business Rules

```
IF person.canton == "ZH" AND person.maritalStatus == MARRIED THEN
    personalContribution = relevantIncome * 0.07
    subsidyAmount = MAX(0, cantonalAveragePremium - personalContribution)
END IF

// Children receive at least 80% reduction
IF person.ageGroup == CHILD THEN
    subsidyAmount = MAX(subsidyAmount, premium * 0.80)
END IF

// Young adults in education receive at least 50% reduction
IF person.ageGroup == YOUNG_ADULT AND person.inEducation THEN
    subsidyAmount = MAX(subsidyAmount, premium * 0.50)
END IF
```

### Domain Model

```java
public class PremiumSubsidy {
    private UUID id;
    private UUID personId;
    private UUID coverageId;

    private String canton;              // Canton granting subsidy
    private Integer subsidyYear;        // Calendar year

    private Money monthlyAmount;        // Subsidy amount per month
    private Money annualAmount;         // Total annual subsidy

    private LocalDate validFrom;
    private LocalDate validTo;

    private SubsidyStatus status;       // PENDING, APPROVED, REJECTED, EXPIRED
    private String decisionNumber;      // Cantonal decision reference
    private LocalDate decisionDate;

    // Audit
    private Instant createdAt;
    private Instant updatedAt;
}

public enum SubsidyStatus {
    PENDING,    // Application submitted
    APPROVED,   // Subsidy granted
    REJECTED,   // Application denied
    EXPIRED,    // Past validity period
    REVOKED     // Withdrawn (e.g., income change)
}
```

---

## 2. Payment Frequency Discounts (Zahlungsrabatte)

### Overview

Many insurers offer discounts for advance premium payment:

| Frequency | Typical Discount | Notes |
|-----------|------------------|-------|
| Monthly | 0% (reference) | Most common |
| Quarterly | 0 - 0.25% | Some insurers |
| Semi-annual | 0.5 - 1% | Common |
| Annual | 1 - 2% | Most common discount |

### Legal Basis

Payment frequency discounts are **not regulated** - insurers set their own policies.

### Business Rules

```java
public enum BillingFrequency {
    MONTHLY(12, BigDecimal.ZERO),
    QUARTERLY(4, new BigDecimal("0.005")),    // 0.5%
    SEMI_ANNUAL(2, new BigDecimal("0.01")),   // 1.0%
    ANNUAL(1, new BigDecimal("0.02"));        // 2.0%

    private final int paymentsPerYear;
    private final BigDecimal discountRate;

    public Money applyDiscount(Money annualPremium) {
        Money discount = annualPremium.multiply(discountRate);
        return annualPremium.subtract(discount);
    }
}
```

### Domain Model Integration

Already exists in `BillingFrequency` enum - extend with discount rates per insurer/product.

```java
public class PaymentDiscount {
    private UUID id;
    private UUID productId;             // Or null for insurer-wide
    private UUID tariffId;

    private BillingFrequency frequency;
    private BigDecimal discountPercent; // 0-100

    private LocalDate validFrom;
    private LocalDate validTo;
}
```

---

## 3. Family Discounts (Familienrabatte)

### Overview

Discounts for families with multiple children insured with the same company.

### Types

| Type | Trigger | Typical Discount |
|------|---------|------------------|
| **Third child discount** | 3+ children | 10-25% on children's premiums |
| **All children discount** | Rare (Assura, Groupe Mutuel) | Discount on all children |

### Insurer Variations

| Insurer | Discount Structure |
|---------|-------------------|
| **Assura** | Discount from 2nd child onwards |
| **Groupe Mutuel** | Discount on all children if 3+ |
| **Most others** | Discount from 3rd child only |
| **CSS, Helsana** | No family discount |

### Requirements

- Children must be insured with same company
- Parents typically need NOT be with same company
- Some require basic + supplementary with same insurer

### Business Rules

```
// Third child discount calculation
childrenCount = household.childrenWithInsurer(insurerId)

IF childrenCount >= 3 THEN
    FOR EACH child IN household.children (sorted by age DESC)
        IF child.rank >= 3 THEN
            child.discount = FAMILY_DISCOUNT_RATE  // e.g., 15%
        END IF
    END FOR
END IF
```

### Domain Model

```java
public class FamilyDiscount {
    private UUID id;
    private UUID householdId;
    private UUID insurerId;

    private Integer qualifyingChildCount;  // Children with this insurer
    private Integer discountedChildCount;  // Children receiving discount

    private BigDecimal discountPercent;    // e.g., 15%
    private Money monthlyDiscountAmount;   // Calculated amount

    private LocalDate validFrom;
    private LocalDate validTo;

    private FamilyDiscountType type;       // THIRD_CHILD, ALL_CHILDREN
}

public enum FamilyDiscountType {
    THIRD_CHILD_ONWARDS,  // Only 3rd+ child gets discount
    ALL_CHILDREN,         // All children if threshold met
    SECOND_CHILD_ONWARDS  // Rare: Assura model
}
```

---

## 4. Accident Exclusion (Unfallausschluss)

### Overview

Employees covered by employer's UVG accident insurance can exclude accident coverage from KVG.

| Employment Status | KVG Accident | Typical Savings |
|-------------------|--------------|-----------------|
| Employed (>8h/week) | Excluded | 7-10% |
| Part-time (<8h/week) | Required* | - |
| Self-employed | Required | - |
| Unemployed | Required | - |

*Part-time workers are covered for occupational accidents only under UVG.

### Legal Basis

- KVG Art. 8: Accident coverage suspension
- UVG Art. 1a: Compulsory accident insurance

### Business Rules

```
IF person.hasEmployerUvgCoverage AND person.weeklyHours >= 8 THEN
    coverage.withAccident = FALSE
    premium = tariff.lookup(region, age, franchise, withAccident=FALSE)
ELSE
    coverage.withAccident = TRUE
    premium = tariff.lookup(region, age, franchise, withAccident=TRUE)
END IF

// Premium difference typically 7-10%
```

### Domain Model Integration

Already exists in `Coverage.withAccident` field.

---

## 5. Insurance Model Discounts (Modellrabatte)

### Overview

Alternative insurance models offer premium reductions in exchange for restricted provider choice.

| Model | Restriction | Typical Discount |
|-------|-------------|------------------|
| Standard | None | 0% (reference) |
| HMO | Must use HMO center | 10-25% |
| Hausarzt | Family doctor gatekeeper | 10-20% |
| Telmed | Call hotline first | 10-15% |

### Legal Basis

- KVG Art. 62: Alternative insurance models

### Domain Model Integration

Already exists via `InsuranceModel` enum and product/tariff structure.

---

## 6. Late Enrollment Surcharge (Prämienzuschlag)

### Legal Framework

| Aspect | Details |
|--------|---------|
| **Law** | KVG Art. 5(2) |
| **Ordinance** | KVV Art. 8 |
| **Trigger** | Inexcusable late enrollment |
| **Duration** | 2x delay period, max 5 years |
| **Rate** | 30-50% of premium |

### Timeline

```
Residence/Birth ─────────────────────────────────────────────────►
     │
     │◄─── 3 months ───►│
     │   Registration   │
     │     Deadline     │
     │                  │
     │                  │◄──── Delay Period ────►│
     │                  │                        │
     │                  │                        └── Enrollment Date
     │                  │
     │                  │◄──── 2x Delay Period (max 5 years) ────►│
     │                  │       Surcharge applies                  │
```

### Surcharge Calculation

| Delay Duration | Surcharge Duration | Maximum |
|----------------|-------------------|---------|
| 6 months | 12 months | - |
| 1 year | 2 years | - |
| 2 years | 4 years | - |
| 3+ years | 5 years | 5 years cap |

### Surcharge Rate

| Rate | Condition |
|------|-----------|
| **30-50%** | Standard range |
| **<30%** | Hardship cases |
| **0%** | Social welfare pays premiums |

### Excusable vs. Inexcusable Delays

**Excusable** (no surcharge):
- Genuine ignorance of obligation
- Administrative errors
- Force majeure

**Inexcusable** (surcharge applies):
- Knew about obligation but didn't act
- Received reminder but ignored it
- Conscious decision to delay

### Business Rules

```java
public class LateEnrollmentSurcharge {
    private UUID id;
    private UUID personId;
    private UUID coverageId;

    // Delay calculation
    private LocalDate obligationStartDate;  // When insurance became mandatory
    private LocalDate enrollmentDate;       // Actual enrollment
    private Integer delayMonths;            // Calculated delay

    // Surcharge terms
    private BigDecimal surchargePercent;    // 30-50%
    private Integer surchargeMonths;        // 2x delay, max 60
    private LocalDate surchargeEndDate;     // When surcharge ends

    // Status
    private SurchargeStatus status;
    private Boolean excusable;              // If true, no surcharge
    private String excusableReason;

    // Continuity (when changing insurers)
    private UUID previousInsurerId;
    private Integer remainingMonths;        // Months left when transferred
}

public enum SurchargeStatus {
    PENDING,      // Under review
    ACTIVE,       // Currently paying surcharge
    COMPLETED,    // Surcharge period ended
    WAIVED,       // Excusable delay
    HARDSHIP      // Reduced rate applied
}
```

### Calculation Example

```
Person arrives in Switzerland: 2024-01-01
Registration deadline:         2024-04-01 (3 months)
Actual enrollment:             2024-10-01 (6 months late)

Delay period:     6 months (Apr-Sep)
Surcharge period: 12 months (2x delay)
Surcharge rate:   30-50% (based on financial situation)

Monthly premium:  CHF 400
With 40% surcharge: CHF 560 (+CHF 160/month)
Duration: Oct 2024 - Sep 2025
```

### Insurer Transfer Rules

When the insured changes insurers:
1. Previous insurer must inform new insurer of surcharge
2. Surcharge continues with new insurer
3. Remaining duration transfers
4. Rate may be recalculated based on new premium

---

## 7. Promotional Discounts (Aktionsrabatte)

### Overview

Time-limited promotional offers from insurers.

| Type | Example | Duration |
|------|---------|----------|
| New customer | First year discount | 12 months |
| Multi-year contract | Commitment discount | Contract duration |
| Bundle | KVG + VVG together | Ongoing |
| Referral | Bring a friend | One-time |

### Legal Constraints

- Must not discriminate based on health
- Must be transparently communicated
- Cannot affect mandatory coverage terms

### Domain Model

```java
public class PromotionalDiscount {
    private UUID id;
    private UUID insurerId;
    private UUID productId;             // null = all products
    private UUID campaignId;            // Marketing campaign reference

    private String promotionCode;       // PROMO2026, WELCOME, etc.
    private LocalizedText name;
    private LocalizedText description;

    // Discount terms
    private DiscountType discountType;  // PERCENT, FIXED_AMOUNT
    private BigDecimal discountValue;   // 10 (%) or 50.00 (CHF)
    private Integer durationMonths;     // How long discount applies

    // Validity
    private LocalDate campaignStart;    // When promotion starts
    private LocalDate campaignEnd;      // When promotion ends
    private LocalDate applicationDeadline;

    // Constraints
    private Boolean newCustomersOnly;
    private Boolean requiresBundle;     // Must have KVG + VVG
    private Integer minContractMonths;  // Minimum commitment
}

public enum DiscountType {
    PERCENT,        // Percentage off premium
    FIXED_AMOUNT,   // Fixed CHF amount off
    FREE_MONTHS     // X months free
}
```

---

## Summary: Adjustment Types

### Premium Reductions (Positive for Insured)

| Adjustment | Typical Impact | Who Decides |
|------------|----------------|-------------|
| IPV Subsidy | Up to 100% | Canton |
| Payment discount | 0-2% | Insurer |
| Family discount | 10-25% (children) | Insurer |
| Accident exclusion | 7-10% | Insured (if eligible) |
| Model discount | 10-25% | Insured (choice) |
| Promotional | Variable | Insurer |

### Premium Increases (Negative for Insured)

| Adjustment | Typical Impact | Who Decides |
|------------|----------------|-------------|
| Late enrollment surcharge | 30-50% | Regulatory (KVV) |
| (No VVG malus in KVG) | - | - |

---

## Domain Model: PremiumAdjustment Entity

Generic entity for tracking all adjustments:

```java
@Entity
public class PremiumAdjustment {
    @Id
    private UUID id;
    private UUID tenantId;

    // What it applies to
    private UUID coverageId;            // Specific coverage
    private UUID personId;              // Or person-level
    private UUID householdId;           // Or household-level

    // Adjustment details
    @Enumerated(EnumType.STRING)
    private AdjustmentType type;

    @Enumerated(EnumType.STRING)
    private AdjustmentCategory category; // SUBSIDY, DISCOUNT, SURCHARGE

    @Enumerated(EnumType.STRING)
    private AdjustmentDirection direction; // REDUCTION, INCREASE

    // Value
    private BigDecimal percentValue;    // Percentage (0-100)
    @Embedded
    private Money fixedValue;           // Or fixed amount

    // Validity
    private LocalDate validFrom;
    private LocalDate validTo;

    // Source/Reference
    private String externalReference;   // Decision number, campaign code
    private String source;              // CANTON, INSURER, REGULATORY

    // Status
    @Enumerated(EnumType.STRING)
    private AdjustmentStatus status;

    // Audit
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
}

public enum AdjustmentType {
    // Subsidies
    IPV_CANTONAL,           // Cantonal premium subsidy
    IPV_FEDERAL,            // Federal contribution

    // Discounts
    PAYMENT_FREQUENCY,      // Annual/semi-annual payment
    FAMILY_THIRD_CHILD,     // Third child discount
    FAMILY_ALL_CHILDREN,    // All children discount
    ACCIDENT_EXCLUSION,     // UVG covers accidents
    MODEL_DISCOUNT,         // HMO, Hausarzt, Telmed
    PROMOTIONAL,            // Marketing promotion
    LOYALTY,                // Long-term customer
    BUNDLE,                 // KVG + VVG bundle
    REFERRAL,               // Referral program

    // Surcharges
    LATE_ENROLLMENT,        // Art. 5 KVG surcharge
    HARDSHIP_REDUCED        // Reduced surcharge for hardship
}

public enum AdjustmentCategory {
    SUBSIDY,    // Government-funded
    DISCOUNT,   // Insurer-offered
    SURCHARGE   // Regulatory penalty
}

public enum AdjustmentDirection {
    REDUCTION,  // Lowers premium
    INCREASE    // Raises premium
}

public enum AdjustmentStatus {
    PENDING,    // Awaiting approval/verification
    ACTIVE,     // Currently applied
    SUSPENDED,  // Temporarily inactive
    EXPIRED,    // Past validity
    REVOKED     // Cancelled
}
```

---

## Premium Calculation Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      PREMIUM CALCULATION FLOW                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. BASE PREMIUM (from Tariff)                                              │
│     ├── Region + Age + Franchise + Accident + Model                        │
│     └── = CHF 450.00 / month                                               │
│                                                                             │
│  2. APPLY SURCHARGES (+)                                                    │
│     ├── Late enrollment surcharge: +40% = +CHF 180.00                      │
│     └── Subtotal: CHF 630.00                                               │
│                                                                             │
│  3. APPLY DISCOUNTS (-)                                                     │
│     ├── Payment frequency (annual): -2% = -CHF 12.60                       │
│     ├── Family discount (3rd child): -15% = -CHF 94.50                     │
│     ├── Promotional (new customer): -CHF 20.00                             │
│     └── Subtotal: CHF 502.90                                               │
│                                                                             │
│  4. APPLY SUBSIDIES (-)                                                     │
│     ├── IPV (cantonal): -CHF 200.00                                        │
│     └── Final Premium: CHF 302.90                                          │
│                                                                             │
│  5. INVOICE                                                                 │
│     ├── Due from insured: CHF 302.90                                       │
│     └── Covered by IPV: CHF 200.00 (paid by canton to insurer)            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Official Resources

| Resource | URL |
|----------|-----|
| KVG Art. 5 (Late enrollment) | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/de#art_5) |
| KVV Art. 8 (Surcharge rules) | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/1995/3867_3867_3867/de#art_8) |
| KVG Art. 65 (Premium subsidies) | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/de#art_65) |
| BAG Premium subsidies | [bag.admin.ch](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemienverbilligung.html) |
| Family discounts (Comparis) | [comparis.ch](https://en.comparis.ch/krankenkassen/familie/familienrabatte) |
| Payment discounts (Moneyland) | [moneyland.ch](https://www.moneyland.ch/en/health-insurance-discounts-annual-payment) |

---

*Last Updated: 2026-01-27*

# KVG Law Requirements

## Overview

This document outlines the technical compliance requirements for implementing systems that manage **Krankenversicherungsgesetz (KVG)** - the Swiss mandatory health insurance. These requirements must be implemented to ensure legal compliance.

📋 **Law Reference**: [KVG - SR 832.10](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/de)

📋 **Ordinance Reference**: [KVV - SR 832.102](https://www.fedlex.admin.ch/eli/cc/1995/3867_3867_3867/de)

---

## Core Compliance Requirements

### 1. Insurance Obligation (Art. 3 KVG)

**Requirement**: Every person residing in Switzerland must have basic health insurance.

| Rule | Implementation |
|------|----------------|
| Coverage start | Within 3 months of residence |
| Retroactive coverage | If signed within 3 months, covers from day 1 |
| No gaps allowed | System must prevent coverage gaps |
| Canton assignment | Uninsured persons assigned by canton |

**System Requirements**:
```java
// Coverage validation
public void validateKvgCoverage(UUID personId, LocalDate date) {
    boolean hasActiveKvg = coverageRepository
        .existsActiveKvgCoverage(personId, date);

    if (!hasActiveKvg) {
        // Flag as requiring KVG coverage
        // Cannot have gaps in basic insurance
    }
}
```

### 2. Acceptance Obligation (Art. 4 KVG)

**Requirement**: Insurers must accept all applicants for basic insurance without any health checks.

| Rule | Implementation |
|------|----------------|
| No health questions | KVG enrollment without health declaration |
| No exclusions | Pre-existing conditions covered |
| No waiting periods | Coverage starts immediately |
| No rejection | Cannot refuse any applicant |

**System Requirements**:
```java
// KVG enrollment - no health checks
public Coverage enrollKvg(Person person, Product kvgProduct, LocalDate startDate) {
    // NO health declaration required for KVG
    // NO pre-existing condition checks
    // NO waiting periods

    return Coverage.builder()
        .insuredPerson(person)
        .product(kvgProduct)
        .effectiveDate(startDate)
        .status(CoverageStatus.ACTIVE)
        .build();
}
```

### 3. Unisex Premium Requirement (Art. 61 KVG)

**Requirement**: KVG premiums must be the same for men and women (unisex).

| Rule | Implementation |
|------|----------------|
| No gender factor | Premium calculation ignores gender |
| Since 2013 | Mandatory unisex premiums |
| All ages | Applies to children, young adults, adults |

**System Requirements**:
```java
// KVG PremiumEntry - NO gender field
public class KvgPremiumEntry {
    private UUID premiumRegionId;
    private AgeGroup ageGroup;
    private Franchise franchise;
    private Boolean withAccident;
    private Money monthlyAmount;
    // NOTE: No gender field - unisex by law
}

// Premium lookup - no gender parameter
public Money lookupKvgPremium(
    UUID productId,
    UUID premiumRegionId,
    AgeGroup ageGroup,
    Franchise franchise,
    boolean withAccident
) {
    // Gender is NOT a parameter
    return premiumRepository.findKvgPremium(
        productId, premiumRegionId, ageGroup, franchise, withAccident
    );
}
```

### 4. Premium Structure (Art. 61 KVG)

**Requirement**: Premiums vary by region, age group, franchise, and accident inclusion only.

| Factor | Allowed | Details |
|--------|---------|---------|
| Premium region | ✅ Yes | BAG-defined regions |
| Age group | ✅ Yes | Child, Young Adult, Adult |
| Franchise | ✅ Yes | CHF 0-600 children, CHF 300-2500 adults |
| Accident inclusion | ✅ Yes | With/without UVG accident |
| Insurance model | ✅ Yes | Standard, HMO, Hausarzt, Telmed |
| Gender | ❌ No | Unisex only |
| Health status | ❌ No | No risk selection |

### 5. Franchise Levels (Art. 64 KVG)

**Requirement**: Specific franchise options must be offered.

| Age Group | Available Franchises (CHF) |
|-----------|---------------------------|
| Children (0-18) | 0, 100, 200, 300, 400, 600 |
| Adults (19+) | 300, 500, 1000, 1500, 2000, 2500 |

**System Requirements**:
```java
public enum Franchise {
    // Children options
    CHF_0(0, true, false),
    CHF_100(100, true, false),
    CHF_200(200, true, false),
    CHF_300(300, true, true),  // Both children and adults
    CHF_400(400, true, false),
    CHF_600(600, true, false),

    // Adult-only options
    CHF_500(500, false, true),
    CHF_1000(1000, false, true),
    CHF_1500(1500, false, true),
    CHF_2000(2000, false, true),
    CHF_2500(2500, false, true);

    private final int amount;
    private final boolean availableForChildren;
    private final boolean availableForAdults;

    public static List<Franchise> forAgeGroup(AgeGroup ageGroup) {
        return Arrays.stream(values())
            .filter(f -> ageGroup == AgeGroup.CHILD
                ? f.availableForChildren
                : f.availableForAdults)
            .collect(Collectors.toList());
    }
}
```

### 6. Cost Sharing (Art. 64 KVG)

**Requirement**: Insured persons share costs through franchise and co-payment.

| Element | Rule |
|---------|------|
| Franchise | 100% until reached |
| Co-payment (Selbstbehalt) | 10% after franchise |
| Co-payment maximum | CHF 700/year adults, CHF 350/year children |
| Maternity exception | No cost sharing for maternity |

### 7. Termination Rules (Art. 7 KVG)

**Requirement**: Strict rules govern when coverage can be terminated.

| Termination Type | Deadline | Effective Date |
|-----------------|----------|----------------|
| Ordinary | November 30 | January 1 |
| Premium increase | End of month after notice | January 1 |
| Move to new region | Within 1 month | Move date |

**System Requirements**:
```java
public void validateTermination(Coverage coverage, LocalDate terminationDate) {
    // Ordinary termination: must be December 31
    if (terminationDate.getMonth() != Month.DECEMBER ||
        terminationDate.getDayOfMonth() != 31) {

        // Check for special circumstances
        if (!hasSpecialTerminationRight(coverage)) {
            throw new IllegalTerminationException(
                "KVG can only be terminated on December 31");
        }
    }

    // Must have replacement coverage
    if (!hasReplacementCoverage(coverage.getInsuredPerson(), terminationDate)) {
        throw new IllegalTerminationException(
            "KVG termination requires proof of new coverage");
    }
}
```

### 8. Change Restrictions

**Requirement**: Certain changes can only happen at specific times.

| Change Type | When Allowed |
|-------------|--------------|
| Franchise change | January 1 only |
| Model change | January 1 (some insurers allow quarterly) |
| Insurer change | January 1 (or July 1 for some cantons) |

---

## Data Requirements

### Required Person Data

| Field | Required | Purpose |
|-------|----------|---------|
| AHV Number | ✅ | Unique identification |
| Name | ✅ | Contract identification |
| Date of Birth | ✅ | Age group calculation |
| Gender | ⚠️ | Statistics only, NOT for premiums |
| Address | ✅ | Premium region determination |
| Canton | ✅ | Premium region |

### Required Coverage Data

| Field | Required | Purpose |
|-------|----------|---------|
| Product | ✅ | KVG product reference |
| Effective Date | ✅ | Coverage start |
| Franchise | ✅ | Cost sharing level |
| With Accident | ✅ | UVG coverage status |
| Premium Region | ✅ | Premium calculation |
| Monthly Premium | ✅ | Billing |

---

## Validation Rules Summary

```java
public class KvgValidationRules {

    // Rule 1: Single active KVG coverage
    public void validateSingleKvg(UUID personId, LocalDate date) {
        long activeCount = coverageRepository
            .countActiveKvgCoverages(personId, date);
        if (activeCount > 1) {
            throw new ValidationException("Person can only have one active KVG coverage");
        }
    }

    // Rule 2: No coverage gaps
    public void validateNoCoverageGaps(UUID personId) {
        List<Coverage> history = coverageRepository.findKvgHistory(personId);
        for (int i = 1; i < history.size(); i++) {
            LocalDate prevEnd = history.get(i-1).getTerminationDate();
            LocalDate nextStart = history.get(i).getEffectiveDate();
            if (prevEnd.plusDays(1).isBefore(nextStart)) {
                throw new ValidationException("KVG coverage gap detected");
            }
        }
    }

    // Rule 3: Valid franchise for age group
    public void validateFranchise(Franchise franchise, AgeGroup ageGroup) {
        List<Franchise> validOptions = Franchise.forAgeGroup(ageGroup);
        if (!validOptions.contains(franchise)) {
            throw new ValidationException(
                "Franchise " + franchise + " not valid for " + ageGroup);
        }
    }

    // Rule 4: Franchise change only on Jan 1
    public void validateFranchiseChangeDate(LocalDate effectiveDate) {
        if (effectiveDate.getMonth() != Month.JANUARY ||
            effectiveDate.getDayOfMonth() != 1) {
            throw new ValidationException(
                "Franchise changes only effective January 1");
        }
    }
}
```

---

## Reporting Requirements

### BAG Statistics

| Report | Frequency | Content |
|--------|-----------|---------|
| Insured count | Annual | By region, age, franchise |
| Premium income | Annual | Aggregated by product |
| Claims data | Annual | By category, anonymized |

### SASIS Reporting

| Data | Purpose |
|------|---------|
| Cover card data | Proof of insurance |
| Change notifications | Insurer changes |

---

## Penalties for Non-Compliance

| Violation | Consequence |
|-----------|-------------|
| Charging unapproved premiums | BAG sanctions, refund obligation |
| Gender-based pricing | BAG sanctions |
| Refusing applicants | BAG sanctions |
| Coverage gaps allowed | Administrative issues |

---

## Implementation Checklist

- [ ] Unisex premium calculation (no gender)
- [ ] Correct franchise levels per age group
- [ ] Premium region integration
- [ ] Single KVG coverage per person
- [ ] No coverage gaps validation
- [ ] Franchise change on January 1 only
- [ ] Termination with replacement validation
- [ ] Acceptance without health checks
- [ ] Cost sharing calculation
- [ ] Maternity exemption from cost sharing

---

## Official Resources

| Resource | URL |
|----------|-----|
| KVG Full Text | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/de) |
| KVV Ordinance | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/1995/3867_3867_3867/de) |
| BAG KVG Overview | [bag.admin.ch](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung.html) |
| Priminfo | [priminfo.admin.ch](https://www.priminfo.admin.ch) |

---

## Related Documentation

- [BAG - Federal Office](./bag-federal-office.md)
- [KVG Concept](../concepts/kvg-mandatory-insurance.md)
- [Franchise System](../concepts/franchise-system.md)
- [Coverage Entity](../entities/contract/coverage.md)

---

*Last Updated: 2026-01-26*

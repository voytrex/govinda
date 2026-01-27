# VVG Law Requirements

## Overview

This document outlines the technical compliance requirements for implementing systems that manage **Versicherungsvertragsgesetz (VVG)** - the Swiss Insurance Contract Act governing supplementary health insurance. Unlike KVG, VVG operates under private law with different rules.

📋 **Law Reference**: [VVG - SR 221.229.1](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/de)

---

## Key Differences from KVG

| Aspect | KVG (Basic) | VVG (Supplementary) |
|--------|-------------|---------------------|
| Legal basis | Public law | Private law |
| Mandatory | Yes | No |
| Health check | Not allowed | Required |
| Risk selection | Prohibited | Allowed |
| Gender pricing | Prohibited | Allowed |
| Exclusions | Not allowed | Allowed |
| Waiting periods | Not allowed | Allowed |
| Rejection | Not allowed | Allowed |

---

## Core Compliance Requirements

### 1. Disclosure Obligation (Art. 4-8 VVG)

**Requirement**: Applicants must truthfully answer all questions relevant to risk assessment.

| Rule | Implementation |
|------|----------------|
| Written questions | Insurer must ask specific questions |
| Material facts | Answers must be complete and truthful |
| Consequence of breach | Contract can be voided |
| Time limit | Insurer must act within 4 weeks |

**System Requirements**:
```java
public class VvgEnrollment {

    // Health declaration required for VVG
    private HealthDeclaration healthDeclaration;
    private LocalDate declarationDate;
    private UUID declarationSignedBy;

    public void validateDeclaration() {
        if (healthDeclaration == null) {
            throw new ValidationException(
                "VVG enrollment requires health declaration");
        }
        if (!healthDeclaration.isComplete()) {
            throw new ValidationException(
                "All health questions must be answered");
        }
    }
}

public class HealthDeclaration {
    private List<HealthQuestion> questions;
    private List<HealthAnswer> answers;
    private boolean signedByApplicant;
    private LocalDate signatureDate;

    public boolean isComplete() {
        return questions.stream()
            .allMatch(q -> hasAnswer(q.getId()));
    }
}
```

### 2. Risk Assessment Rights (Art. 4 VVG)

**Requirement**: Insurers may assess risk and make underwriting decisions.

| Decision | Description | Implementation |
|----------|-------------|----------------|
| Accept | Full coverage granted | Standard enrollment |
| Accept with exclusions | Coverage with limitations | Store exclusions |
| Accept with surcharge | Higher premium for risk | Premium adjustment |
| Reject | Coverage denied | Record rejection reason |

**System Requirements**:
```java
public enum UnderwritingDecision {
    ACCEPTED,
    ACCEPTED_WITH_EXCLUSIONS,
    ACCEPTED_WITH_SURCHARGE,
    REJECTED,
    PENDING_REVIEW
}

public class VvgApplication {
    private UUID applicationId;
    private UUID personId;
    private UUID productId;
    private HealthDeclaration healthDeclaration;
    private UnderwritingDecision decision;
    private List<CoverageExclusion> exclusions;
    private BigDecimal premiumSurchargePercent;
    private String rejectionReason;
    private UUID reviewedBy;
    private Instant reviewedAt;
}

public class CoverageExclusion {
    private UUID coverageId;
    private String exclusionCode;        // e.g., "BACK_PROBLEMS"
    private LocalizedText description;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;       // null = permanent
}
```

### 3. Gender-Based Pricing (Allowed)

**Requirement**: Unlike KVG, VVG products may use gender-differentiated premiums.

| Rule | Implementation |
|------|----------------|
| Optional | Not required, but allowed |
| Actuarial basis | Must be based on risk data |
| Transparency | Customer must be informed |

**System Requirements**:
```java
// VVG PremiumEntry CAN include gender
public class VvgPremiumEntry {
    private UUID premiumRegionId;
    private AgeGroup ageGroup;
    private Gender gender;         // Nullable - if null, unisex product
    private Money monthlyAmount;
}

// Premium lookup for VVG
public Money lookupVvgPremium(
    UUID productId,
    UUID premiumRegionId,
    AgeGroup ageGroup,
    Gender gender            // May be used for pricing
) {
    // First try gender-specific premium
    Optional<Money> genderPremium = premiumRepository
        .findVvgPremium(productId, premiumRegionId, ageGroup, gender);

    if (genderPremium.isPresent()) {
        return genderPremium.get();
    }

    // Fall back to unisex premium
    return premiumRepository
        .findVvgPremiumUnisex(productId, premiumRegionId, ageGroup)
        .orElseThrow(() -> new PremiumNotFoundException());
}
```

### 4. Contract Terms (Art. 35-41 VVG)

**Requirement**: Contract duration and termination rules must be clearly defined.

| Aspect | Typical Terms |
|--------|---------------|
| Contract duration | 1-5 years, auto-renewal |
| Notice period | 3 months before term end |
| Premium adjustment | Allowed with notice |
| Cancellation by insurer | Limited (see below) |

**System Requirements**:
```java
public class VvgContractTerms {
    private int contractDurationYears;
    private int noticePeriodMonths;
    private boolean autoRenewal;
    private boolean insurerCanTerminate;
    private boolean premiumAdjustmentAllowed;

    // Validate termination date
    public void validateTermination(LocalDate requestDate, LocalDate terminationDate) {
        LocalDate earliestTermination = getContractEndDate();
        LocalDate noticeDeadline = earliestTermination
            .minusMonths(noticePeriodMonths);

        if (requestDate.isAfter(noticeDeadline)) {
            throw new ValidationException(
                "Notice period of " + noticePeriodMonths +
                " months not met. Next possible termination: " +
                earliestTermination.plusYears(contractDurationYears));
        }
    }
}
```

### 5. Waiting Periods (Allowed)

**Requirement**: VVG products may impose waiting periods before benefits are payable.

| Product Type | Typical Waiting Period |
|--------------|----------------------|
| Hospital | None or 30 days |
| Dental | 6-12 months |
| Maternity | 9-12 months |
| Alternative medicine | 3-6 months |

**System Requirements**:
```java
public class WaitingPeriod {
    private UUID productId;
    private String benefitCategory;
    private int waitingDays;
    private LocalDate coverageStart;

    public boolean isWaitingPeriodComplete(LocalDate serviceDate) {
        LocalDate waitingEnd = coverageStart.plusDays(waitingDays);
        return !serviceDate.isBefore(waitingEnd);
    }
}
```

### 6. Claims After Termination (Art. 39 VVG)

**Requirement**: Rules for claims submitted after coverage ends.

| Rule | Description |
|------|-------------|
| Service during coverage | Covered even if claimed later |
| Service after termination | Not covered |
| Late submission deadline | Usually 5 years (statute of limitations) |

---

## Data Requirements

### Application Data

| Field | Required | Purpose |
|-------|----------|---------|
| Person details | ✅ | Identification |
| Health declaration | ✅ | Risk assessment |
| Signature/consent | ✅ | Contract validity |
| Declaration date | ✅ | Audit trail |

### Contract Data

| Field | Required | Purpose |
|-------|----------|---------|
| Product | ✅ | Coverage terms |
| Contract start | ✅ | Duration calculation |
| Contract duration | ✅ | Termination rules |
| Premium | ✅ | Billing |
| Exclusions | If applicable | Claims processing |
| Surcharge | If applicable | Premium calculation |

---

## Privacy and Consent

### Health Data Handling

| Requirement | Implementation |
|-------------|----------------|
| Explicit consent | Written consent for health data processing |
| Purpose limitation | Use only for underwriting and claims |
| Retention limits | Delete when no longer needed |
| Access rights | Customer can request their data |

**System Requirements**:
```java
public class HealthDataConsent {
    private UUID personId;
    private ConsentType consentType;
    private boolean granted;
    private LocalDate consentDate;
    private String consentText;          // Full text shown to customer
    private ConsentChannel channel;      // ONLINE, PAPER, PHONE
    private UUID recordedBy;

    public enum ConsentType {
        HEALTH_DATA_PROCESSING,
        HEALTH_DATA_SHARING_REINSURER,
        HEALTH_DATA_RETENTION
    }
}
```

---

## Validation Rules Summary

```java
public class VvgValidationRules {

    // Rule 1: Health declaration required
    public void validateHealthDeclaration(VvgApplication application) {
        if (application.getHealthDeclaration() == null) {
            throw new ValidationException(
                "VVG application requires health declaration");
        }
    }

    // Rule 2: Underwriting decision required
    public void validateUnderwritingDecision(VvgApplication application) {
        if (application.getDecision() == UnderwritingDecision.PENDING_REVIEW) {
            throw new ValidationException(
                "Application must be underwritten before enrollment");
        }
    }

    // Rule 3: Exclusions must be documented
    public void validateExclusions(Coverage coverage) {
        if (coverage.hasExclusions() && coverage.getExclusions().isEmpty()) {
            throw new ValidationException(
                "Exclusions indicated but not documented");
        }
    }

    // Rule 4: Notice period validation
    public void validateTerminationNotice(
            Coverage coverage,
            LocalDate terminationDate,
            LocalDate requestDate) {
        VvgContractTerms terms = coverage.getProduct().getContractTerms();
        terms.validateTermination(requestDate, terminationDate);
    }

    // Rule 5: Consent for health data
    public void validateHealthDataConsent(UUID personId) {
        boolean hasConsent = consentRepository
            .hasActiveConsent(personId, ConsentType.HEALTH_DATA_PROCESSING);
        if (!hasConsent) {
            throw new ValidationException(
                "Health data processing consent required");
        }
    }
}
```

---

## Product Configuration

### VVG Product Settings

```java
public class VvgProductConfiguration {
    private UUID productId;

    // Pricing model
    private boolean genderBasedPricing;
    private boolean ageBasedPricing;

    // Underwriting
    private boolean healthDeclarationRequired;
    private List<String> healthQuestionSet;
    private boolean allowExclusions;
    private boolean allowSurcharge;

    // Contract terms
    private int standardContractYears;
    private int noticePeriodMonths;
    private boolean autoRenewal;

    // Waiting periods
    private Map<String, Integer> waitingPeriodDays;

    // Benefits
    private List<BenefitLimit> benefitLimits;
}

public class BenefitLimit {
    private String benefitCategory;
    private Money maxAmountPerYear;
    private Money maxAmountPerClaim;
    private Integer maxClaimsPerYear;
    private BigDecimal coveragePercent;  // e.g., 50%, 75%
}
```

---

## Termination Scenarios

### By Insured Person

| Scenario | Notice Required | Effective Date |
|----------|-----------------|----------------|
| Ordinary end of term | 3 months | Contract anniversary |
| Premium increase | Usually 30 days | Before new premium |
| Benefit reduction | Per terms | Before reduction |

### By Insurer

| Scenario | Allowed | Notes |
|----------|---------|-------|
| End of term | Usually no | Most VVG products are "lifelong" |
| After claim | Some products | Check specific terms |
| Non-payment | Yes | After proper warning |
| Fraud | Yes | Void contract |

---

## Supervision and Disputes

### FINMA Supervision

VVG insurers are supervised by FINMA for:
- Financial solvency
- Reserve adequacy
- Fair treatment of customers

### Dispute Resolution

| Option | Description |
|--------|-------------|
| Insurer complaint | Internal complaint process |
| Ombudsman | [ombudsman-assurance.ch](https://www.ombudsman-assurance.ch) |
| Civil court | For significant disputes |

---

## Implementation Checklist

- [ ] Health declaration support
- [ ] Underwriting workflow
- [ ] Exclusion management
- [ ] Premium surcharge calculation
- [ ] Gender-based pricing option
- [ ] Waiting period tracking
- [ ] Contract term management
- [ ] Notice period validation
- [ ] Health data consent
- [ ] Rejection reason tracking

---

## Official Resources

| Resource | URL |
|----------|-----|
| VVG Full Text | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/de) |
| FINMA | [finma.ch](https://www.finma.ch) |
| Ombudsman | [ombudsman-assurance.ch](https://www.ombudsman-assurance.ch) |

---

## Related Documentation

- [VVG Concept](../concepts/vvg-supplementary-insurance.md)
- [Data Protection](./data-protection.md)
- [Coverage Entity](../entities/contract/coverage.md)

---

*Last Updated: 2026-01-26*

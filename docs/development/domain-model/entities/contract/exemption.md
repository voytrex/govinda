# Exemption Entity

## Overview

The **Exemption** (Befreiung) entity tracks exemptions from mandatory fees or insurance obligations.

> **German**: Befreiung, Gebührenbefreiung
> **Module**: `govinda-contract`
> **Status**: Planned

**Common Uses**: RTVG (broadcast fee) exemptions for EL recipients, deaf/blind persons, diplomatic immunity

---

## Entity Definition

```java
@Entity
@Table(name = "exemptions")
public class Exemption {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    // Target
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ExemptionTargetType targetType;

    @Column(name = "person_id")
    private UUID personId;

    @Column(name = "household_id")
    private UUID householdId;

    // Exemption details
    @Enumerated(EnumType.STRING)
    @Column(name = "exemption_type", nullable = false)
    private ExemptionType exemptionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private FeeType feeType;

    @Column(name = "reason_code")
    private String reasonCode;

    @Column(name = "reason_description")
    private String reasonDescription;

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Verification
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "verified_by")
    private String verifiedBy;

    // Evidence
    @Column(name = "document_reference")
    private String documentReference;

    @Column(name = "external_reference")
    private String externalReference;  // e.g., EL decision number

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExemptionStatus status = ExemptionStatus.PENDING;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revocation_reason")
    private String revocationReason;

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
| `targetType` | ExemptionTargetType | Required | PERSON or HOUSEHOLD |
| `personId` | UUID | Conditional | Required if target is PERSON |
| `householdId` | UUID | Conditional | Required if target is HOUSEHOLD |
| `exemptionType` | ExemptionType | Required | Type of exemption |
| `feeType` | FeeType | Required | Fee being exempted |
| `reasonCode` | String | Optional | Structured reason code |
| `validFrom` | LocalDate | Required | When exemption starts |
| `validTo` | LocalDate | Optional | When exemption ends (null = indefinite) |
| `verificationStatus` | VerificationStatus | Required | Verification state |
| `documentReference` | String | Optional | Supporting document ID |
| `status` | ExemptionStatus | Required | Current status |

---

## Related Enums

### ExemptionTargetType

```java
public enum ExemptionTargetType {
    PERSON("Person"),
    HOUSEHOLD("Household");
}
```

### ExemptionType

```java
public enum ExemptionType {
    // RTVG Exemptions
    EL_RECIPIENT("EL-Bezüger"),              // Ergänzungsleistungen recipient
    DEAF("Gehörlos"),                         // Deaf person (TV only)
    BLIND("Blind"),                           // Blind person (TV only)
    DEAF_BLIND("Taubblind"),                 // Both conditions
    DIPLOMATIC_IMMUNITY("Diplomatische Immunität"),
    INSTITUTIONAL_LIVING("Institutioneller Wohnsitz"),  // Nursing home, etc.

    // KVG Exemptions
    MILITARY_SERVICE("Militärdienst"),        // Covered by military
    FOREIGN_COVERAGE("Ausländische Deckung"), // Equivalent foreign coverage
    CROSS_BORDER_WORKER("Grenzgänger"),       // EU/EFTA worker exemption

    // General
    SOCIAL_HARDSHIP("Soziale Härte"),
    OTHER("Andere");
}
```

### FeeType

```java
public enum FeeType {
    RTVG("RTVG-Gebühr"),           // Broadcast fee
    KVG("KVG-Prämie"),             // Health insurance premium
    VVG("VVG-Prämie"),             // Supplementary insurance
    TELECOM("Telecom-Abonnement"); // Telecom subscription
}
```

### ExemptionStatus

```java
public enum ExemptionStatus {
    PENDING("Ausstehend"),
    ACTIVE("Aktiv"),
    EXPIRED("Abgelaufen"),
    REVOKED("Widerrufen"),
    REJECTED("Abgelehnt");
}
```

### VerificationStatus

```java
public enum VerificationStatus {
    UNVERIFIED("Nicht verifiziert"),
    PENDING_VERIFICATION("Verifizierung ausstehend"),
    VERIFIED("Verifiziert"),
    VERIFICATION_FAILED("Verifizierung fehlgeschlagen"),
    EXPIRED("Abgelaufen");
}
```

---

## RTVG Exemption Rules

### Automatic Household Exemption

```
IF any household member has:
   - EL_RECIPIENT status (verified)
   - DIPLOMATIC_IMMUNITY status
THEN
   Entire household is exempt from RTVG fee
```

### Partial Exemptions (TV portion only)

```
IF person has:
   - DEAF status (verified)
   - BLIND status (verified)
THEN
   Person exempt from TV portion only
   Radio portion still applies
```

### Institutional Living

```
IF household type is:
   - NURSING_HOME (Pflegeheim)
   - ELDER_HOME (Altersheim)
   - ASSISTED_LIVING (Betreutes Wohnen)
AND
   Institution pays collective fee
THEN
   Individual residents exempt
```

---

## State Transitions

```
                    ┌──────────────────┐
                    │     PENDING      │
                    └────────┬─────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼
┌────────────────┐  ┌────────────────┐  ┌────────────────┐
│    ACTIVE      │  │    REJECTED    │  │(needs more info)│
└───────┬────────┘  └────────────────┘  └────────────────┘
        │
        ├────────────────────────────────────┐
        │                                    │
        ▼                                    ▼
┌────────────────┐                  ┌────────────────┐
│    EXPIRED     │                  │    REVOKED     │
│ (date passed)  │                  │  (cancelled)   │
└────────────────┘                  └────────────────┘
```

---

## Use Cases

### EL Recipient Exemption

```java
// Create exemption when EL status is confirmed
Exemption exemption = Exemption.builder()
    .tenantId(tenantId)
    .targetType(ExemptionTargetType.HOUSEHOLD)
    .householdId(household.getId())
    .exemptionType(ExemptionType.EL_RECIPIENT)
    .feeType(FeeType.RTVG)
    .reasonCode("EL_AHV")
    .validFrom(elDecision.getEffectiveDate())
    .validTo(null)  // Until EL status changes
    .verificationStatus(VerificationStatus.VERIFIED)
    .verifiedAt(Instant.now())
    .externalReference(elDecision.getDecisionNumber())
    .status(ExemptionStatus.ACTIVE)
    .build();

exemptionRepository.save(exemption);
```

### Deaf/Blind Partial Exemption

```java
Exemption exemption = Exemption.builder()
    .targetType(ExemptionTargetType.PERSON)
    .personId(person.getId())
    .exemptionType(ExemptionType.DEAF)
    .feeType(FeeType.RTVG)
    .validFrom(medicalCertificate.getDate())
    .documentReference(medicalCertificate.getId())
    .verificationStatus(VerificationStatus.VERIFIED)
    .status(ExemptionStatus.ACTIVE)
    .build();

// Note: Only TV portion exempt, radio still applies
```

### Check Exemption Status

```java
public boolean isExemptFromFee(UUID householdId, FeeType feeType, LocalDate asOfDate) {
    return exemptionRepository.findActiveExemption(
        householdId, feeType, asOfDate
    ).isPresent();
}

public Optional<Exemption> findActiveExemption(
        UUID householdId, FeeType feeType, LocalDate asOfDate) {
    return repository.findAll().stream()
        .filter(e -> e.getHouseholdId().equals(householdId))
        .filter(e -> e.getFeeType() == feeType)
        .filter(e -> e.getStatus() == ExemptionStatus.ACTIVE)
        .filter(e -> !e.getValidFrom().isAfter(asOfDate))
        .filter(e -> e.getValidTo() == null || !e.getValidTo().isBefore(asOfDate))
        .findFirst();
}
```

---

## Integration Points

### EL Status Updates (External)

```java
@EventListener
public void onElStatusChanged(ElStatusChangedEvent event) {
    if (event.isNowReceivingEl()) {
        createRtvgExemption(event.getHouseholdId(), event.getEffectiveDate());
    } else {
        revokeRtvgExemption(event.getHouseholdId(), event.getEffectiveDate());
    }
}
```

### Billing Integration

```java
@Service
public class RtvgBillingService {

    public Money calculateRtvgFee(UUID householdId, LocalDate billingPeriod) {
        if (exemptionService.isExemptFromFee(householdId, FeeType.RTVG, billingPeriod)) {
            return Money.ZERO;
        }

        // Check for partial exemptions (deaf/blind - TV only)
        Optional<Exemption> partialExemption = exemptionService
            .findPartialExemption(householdId, FeeType.RTVG, billingPeriod);

        if (partialExemption.isPresent()) {
            return calculateRadioOnlyFee();
        }

        return calculateFullRtvgFee();
    }
}
```

---

## Related Documentation

- [PersonCircumstance](../masterdata/person-circumstance.md) - Source circumstances
- [Household](../masterdata/household.md) - Household entity
- [RTVG Concept](../../concepts/broadcast-fees.md) - Broadcast fee rules

---

*Last Updated: 2026-01-28*

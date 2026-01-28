# Suspension Entity

## Overview

The **Suspension** entity represents a temporary pause of insurance coverage or billing.

> **German**: Sistierung
> **Module**: `govinda-contract`
> **Status**: ⏳ Planned

**Common Scenarios**:
- Military service (Militärdienst)
- Study abroad (Auslandstudium)
- Extended foreign travel
- Moving/relocation transition

---

## Entity Definition

```java
@Entity
@Table(name = "suspensions")
public class Suspension {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    // Reason
    @Enumerated(EnumType.STRING)
    @Column(name = "suspension_reason", nullable = false)
    private SuspensionReason suspensionReason;

    @Column(name = "reason_detail")
    private String reasonDetail;

    // Type
    @Enumerated(EnumType.STRING)
    @Column(name = "suspension_type", nullable = false)
    private SuspensionType suspensionType;

    // Dates
    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "requested_duration_days")
    private Integer requestedDurationDays;

    // Billing
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_treatment", nullable = false)
    private BillingTreatment billingTreatment;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuspensionStatus status = SuspensionStatus.REQUESTED;

    // Reactivation
    @Column(name = "auto_reactivate")
    private boolean autoReactivate = true;

    @Column(name = "reactivation_date")
    private LocalDate reactivationDate;

    @Column(name = "reactivated_at")
    private Instant reactivatedAt;

    // Documentation
    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "certificate_number")
    private String certificateNumber;

    // Approval
    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}
```

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | UUID | ✅ | Unique identifier |
| `coverageId` | UUID | ✅ | FK to Coverage |
| `personId` | UUID | ✅ | FK to Person |
| `suspensionReason` | SuspensionReason | ✅ | Why suspended |
| `suspensionType` | SuspensionType | ✅ | Full or partial |
| `effectiveFrom` | LocalDate | ✅ | Suspension start |
| `effectiveTo` | LocalDate | ❌ | Suspension end (null = open-ended) |
| `billingTreatment` | BillingTreatment | ✅ | How to handle billing |
| `status` | SuspensionStatus | ✅ | Workflow state |
| `autoReactivate` | boolean | ✅ | Auto-resume at end date |

---

## Related Enums

### SuspensionReason

```java
public enum SuspensionReason {
    // Military & Civil
    MILITARY_SERVICE("Militärdienst", true, 365),
    CIVIL_PROTECTION("Zivilschutz", true, 90),
    CIVIL_SERVICE("Zivildienst", true, 365),

    // Relocation
    MOVING_DOMESTIC("Umzug Inland", false, 30),
    MOVING_ABROAD_TEMPORARY("Auslandaufenthalt temporär", true, 365),

    // Health
    HOSPITALIZATION("Spitalaufenthalt", true, 180),
    LONG_TERM_CARE("Langzeitpflege", true, null),

    // Education
    STUDY_ABROAD("Auslandstudium", true, 365),
    EXCHANGE_PROGRAM("Austauschprogramm", true, 365),

    // Life
    SABBATICAL("Sabbatical", true, 365),
    UNPAID_LEAVE("Unbezahlter Urlaub", true, 180);

    private final String nameDe;
    private final boolean requiresDocumentation;
    private final Integer defaultMaxDays;
}
```

### SuspensionType

```java
public enum SuspensionType {
    FULL("Vollständig"),
    PARTIAL("Teilweise"),
    COVERAGE_ONLY("Nur Deckung"),
    BILLING_ONLY("Nur Faktura");
}
```

### BillingTreatment

```java
public enum BillingTreatment {
    NO_BILLING("Keine Fakturierung"),
    REDUCED_BILLING("Reduzierte Faktura"),
    FULL_BILLING("Volle Faktura"),
    DEFERRED_BILLING("Aufgeschoben"),
    CREDIT_ON_RETURN("Gutschrift");
}
```

### SuspensionStatus

```java
public enum SuspensionStatus {
    REQUESTED("Beantragt"),
    PENDING_DOCS("Dokumente ausstehend"),
    UNDER_REVIEW("In Prüfung"),
    APPROVED("Genehmigt"),
    ACTIVE("Aktiv"),
    ENDING_SOON("Endet bald"),
    ENDED("Beendet"),
    REJECTED("Abgelehnt"),
    CANCELLED("Storniert");
}
```

---

## State Diagram

```
REQUESTED → PENDING_DOCS → UNDER_REVIEW → APPROVED → ACTIVE
                                              ↓
                                         REJECTED

ACTIVE → ENDING_SOON → ENDED
   ↓
CANCELLED
```

---

## Business Rules

### Maximum Duration

| Reason | Max Duration |
|--------|--------------|
| Military Service | 1 year |
| Study Abroad | 1 year |
| Sabbatical | 1 year |
| Moving Domestic | 30 days |
| Long-term Care | Unlimited |

### Documentation Requirements

```
IF reason.requiresDocumentation == true THEN
    status = PENDING_DOCS until document uploaded
END IF

IF reason == MILITARY_SERVICE THEN
    require: Marschbefehl or Militär-Ausweis
ELSE IF reason == STUDY_ABROAD THEN
    require: Immatrikulationsbescheinigung
END IF
```

---

## Use Cases

### Military Service Suspension

```java
Suspension suspension = new Suspension();
suspension.setCoverageId(coverage.getId());
suspension.setPersonId(person.getId());
suspension.setSuspensionReason(SuspensionReason.MILITARY_SERVICE);
suspension.setReasonDetail("Rekrutenschule, Kaserne Thun");
suspension.setSuspensionType(SuspensionType.FULL);
suspension.setEffectiveFrom(LocalDate.of(2026, 7, 1));
suspension.setEffectiveTo(LocalDate.of(2026, 10, 31));
suspension.setBillingTreatment(BillingTreatment.NO_BILLING);
suspension.setAutoReactivate(true);
```

---

## Related Documentation

- [Coverage](./coverage.md) - Parent entity
- [PersonCircumstance](../masterdata/person-circumstance.md) - Circumstances

---

*Last Updated: 2026-01-28*

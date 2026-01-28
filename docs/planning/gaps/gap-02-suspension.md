# GAP-02: Suspension / Sistierung Framework

## Problem Statement

The current domain model has no mechanism to temporarily **pause** or **suspend** subscriptions/coverages. Real-world scenarios require:

- Military service suspension (Militärsistierung)
- Civil protection service (Zivilschutz, Zivildienst)
- Moving/relocation transitions
- Extended hospitalization
- Extended foreign travel (>3 months)
- Study abroad programs
- Imprisonment
- Maternity/paternity leave (for some products)

## Regulatory Background

### Healthcare (KVG)

**KVG Art. 3**: Coverage is mandatory for all persons domiciled in Switzerland.

**Exceptions allowing suspension**:
- Persons posted abroad by Swiss employer (KVG Art. 3.2)
- Extended military service (>60 consecutive days)
- Temporary residence abroad (special agreement required)

**Note**: VVG supplementary insurance may have different rules per product.

### Broadcast Fee (RTVG)

**Generally no suspension** - fee is household-based, not person-based.

**Exceptions**:
- Complete household dissolution (emigration)
- Death of sole household member

### Telecom

**Commercial rules** - suspension typically available:
- Contract pause during military service
- Moving pause (limited duration)
- Hardship cases

---

## Proposed Model

### Suspension Entity

```java
@Entity
@Table(name = "suspensions")
public class Suspension {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    // What is suspended
    @Column(name = "subscription_id", nullable = false)
    private UUID subscriptionId;  // FK to Coverage/Subscription

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false)
    private SubscriptionType subscriptionType;  // COVERAGE, POLICY, FEE

    // Who requested
    @Column(name = "person_id")
    private UUID personId;  // FK to Person (if person-specific)

    // Suspension details
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private SuspensionReason reason;

    @Column(name = "reason_detail")
    private String reasonDetail;  // Free text explanation

    @Enumerated(EnumType.STRING)
    @Column(name = "suspension_type", nullable = false)
    private SuspensionType suspensionType;

    // Duration
    @Column(name = "requested_from", nullable = false)
    private LocalDate requestedFrom;

    @Column(name = "requested_to")
    private LocalDate requestedTo;  // null = indefinite

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;  // Actual start (may differ from requested)

    @Column(name = "effective_to")
    private LocalDate effectiveTo;  // Actual end

    @Column(name = "max_duration_days")
    private Integer maxDurationDays;  // Rule-based limit

    // Financial impact
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_treatment", nullable = false)
    private BillingTreatment billingTreatment;

    @Column(name = "reduction_percent")
    private BigDecimal reductionPercent;  // If partial suspension

    // Documentation
    @Column(name = "reference_number")
    private String referenceNumber;  // Military order number, etc.

    @Column(name = "document_id")
    private UUID documentId;  // FK to uploaded document

    // Workflow
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuspensionStatus status = SuspensionStatus.REQUESTED;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    // Reactivation
    @Column(name = "reactivation_notice_sent")
    private LocalDate reactivationNoticeSent;

    @Column(name = "auto_reactivate")
    private boolean autoReactivate = true;

    // Audit
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private long version;
}
```

### SuspensionReason Enum

```java
public enum SuspensionReason {

    // Military & Civil Service
    MILITARY_SERVICE("Militärdienst", true, 365),
    CIVIL_PROTECTION("Zivilschutz", true, 90),
    CIVIL_SERVICE("Zivildienst", true, 365),

    // Relocation
    MOVING_DOMESTIC("Umzug Inland", false, 30),
    MOVING_ABROAD_TEMPORARY("Auslandaufenthalt temporär", true, 365),
    MOVING_ABROAD_PERMANENT("Auswanderung", false, null),

    // Health
    HOSPITALIZATION("Spitalaufenthalt", true, 180),
    REHABILITATION("Rehabilitation", true, 90),
    LONG_TERM_CARE("Langzeitpflege", true, null),

    // Education
    STUDY_ABROAD("Auslandstudium", true, 365),
    EXCHANGE_PROGRAM("Austauschprogramm", true, 365),

    // Legal
    IMPRISONMENT("Haft", true, null),
    ASYLUM_PROCEDURE("Asylverfahren", true, null),

    // Life Events
    MATERNITY_LEAVE("Mutterschaftsurlaub", true, 120),
    PATERNITY_LEAVE("Vaterschaftsurlaub", true, 30),
    UNPAID_LEAVE("Unbezahlter Urlaub", true, 180),
    SABBATICAL("Sabbatical", true, 365),

    // Other
    FINANCIAL_HARDSHIP("Finanzielle Härte", true, 90),
    DISPUTE("Streitfall", false, null),
    ADMINISTRATIVE("Administrativ", false, null),
    OTHER("Andere", true, 90);

    private final String nameDe;
    private final boolean requiresDocumentation;
    private final Integer defaultMaxDays;  // null = unlimited

    SuspensionReason(String nameDe, boolean requiresDocumentation, Integer defaultMaxDays) {
        this.nameDe = nameDe;
        this.requiresDocumentation = requiresDocumentation;
        this.defaultMaxDays = defaultMaxDays;
    }

    public boolean requiresDocumentation() {
        return requiresDocumentation;
    }

    public Integer getDefaultMaxDays() {
        return defaultMaxDays;
    }

    /**
     * Check if this reason applies to a specific service domain.
     */
    public boolean appliesTo(ServiceDomain domain) {
        return switch (this) {
            case MILITARY_SERVICE, CIVIL_PROTECTION, CIVIL_SERVICE,
                 MOVING_ABROAD_TEMPORARY, MOVING_ABROAD_PERMANENT,
                 STUDY_ABROAD, EXCHANGE_PROGRAM ->
                domain == ServiceDomain.HEALTHCARE || domain == ServiceDomain.TELECOM;

            case HOSPITALIZATION, REHABILITATION, LONG_TERM_CARE,
                 IMPRISONMENT, ASYLUM_PROCEDURE ->
                domain == ServiceDomain.HEALTHCARE;

            case MOVING_DOMESTIC, FINANCIAL_HARDSHIP, SABBATICAL ->
                domain == ServiceDomain.TELECOM;

            default -> true;
        };
    }
}
```

### SuspensionType Enum

```java
public enum SuspensionType {

    FULL("Vollständig"),           // Complete suspension, no coverage
    PARTIAL("Teilweise"),          // Reduced coverage/fees
    COVERAGE_ONLY("Nur Deckung"),  // No coverage but policy exists
    BILLING_ONLY("Nur Faktura");   // No billing but coverage continues

    private final String nameDe;

    SuspensionType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### BillingTreatment Enum

```java
public enum BillingTreatment {

    NO_BILLING("Keine Fakturierung"),       // No fees during suspension
    REDUCED_BILLING("Reduzierte Faktura"),  // Reduced fees
    FULL_BILLING("Volle Faktura"),          // Continue full billing
    DEFERRED_BILLING("Aufgeschoben"),       // Bill later
    CREDIT_ON_RETURN("Gutschrift");         // Credit when reactivated

    private final String nameDe;

    BillingTreatment(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### SuspensionStatus Enum

```java
public enum SuspensionStatus {

    REQUESTED("Beantragt"),      // Request submitted
    PENDING_DOCS("Dokumente ausstehend"), // Awaiting documentation
    UNDER_REVIEW("In Prüfung"),  // Being reviewed
    APPROVED("Genehmigt"),       // Approved, pending activation
    ACTIVE("Aktiv"),             // Currently suspended
    ENDING_SOON("Endet bald"),   // Approaching end date
    ENDED("Beendet"),            // Suspension completed
    REJECTED("Abgelehnt"),       // Request denied
    CANCELLED("Storniert");      // Cancelled by requester

    private final String nameDe;

    SuspensionStatus(String nameDe) {
        this.nameDe = nameDe;
    }

    public boolean isActive() {
        return this == ACTIVE || this == ENDING_SOON;
    }

    public boolean isTerminal() {
        return this == ENDED || this == REJECTED || this == CANCELLED;
    }
}
```

---

## Example Use Cases

### 1. Military Service Suspension

```java
Suspension military = new Suspension();
military.setSubscriptionId(vvgCoverage.getId());
military.setPersonId(person.getId());
military.setReason(SuspensionReason.MILITARY_SERVICE);
military.setReasonDetail("RS Inf 4, Kaserne Thun");
military.setSuspensionType(SuspensionType.FULL);
military.setRequestedFrom(LocalDate.of(2026, 7, 1));
military.setRequestedTo(LocalDate.of(2026, 10, 31));
military.setBillingTreatment(BillingTreatment.NO_BILLING);
military.setReferenceNumber("MIL-2026-123456");
military.setAutoReactivate(true);
```

### 2. Study Abroad Suspension

```java
Suspension study = new Suspension();
study.setSubscriptionId(telecomContract.getId());
study.setPersonId(person.getId());
study.setReason(SuspensionReason.STUDY_ABROAD);
study.setReasonDetail("Erasmus exchange, University of Barcelona");
study.setSuspensionType(SuspensionType.FULL);
study.setRequestedFrom(LocalDate.of(2026, 9, 1));
study.setRequestedTo(LocalDate.of(2027, 6, 30));
study.setBillingTreatment(BillingTreatment.REDUCED_BILLING);
study.setReductionPercent(new BigDecimal("50"));  // 50% of normal fee
study.setMaxDurationDays(365);
```

### 3. Moving Pause (Telecom)

```java
Suspension moving = new Suspension();
moving.setSubscriptionId(internetContract.getId());
moving.setReason(SuspensionReason.MOVING_DOMESTIC);
moving.setSuspensionType(SuspensionType.BILLING_ONLY);
moving.setRequestedFrom(LocalDate.of(2026, 3, 15));
moving.setRequestedTo(LocalDate.of(2026, 4, 15));
moving.setBillingTreatment(BillingTreatment.NO_BILLING);
moving.setMaxDurationDays(30);
moving.setAutoReactivate(true);
```

---

## Business Rules

### Rule: Maximum Duration by Reason

```java
public class SuspensionRules {

    public int getMaxDurationDays(SuspensionReason reason, ServiceDomain domain) {
        // Domain-specific overrides
        if (domain == ServiceDomain.HEALTHCARE) {
            return switch (reason) {
                case MILITARY_SERVICE -> 365;  // Max 1 year
                case MOVING_ABROAD_TEMPORARY -> 730;  // Max 2 years
                case STUDY_ABROAD -> 1095;  // Max 3 years
                default -> reason.getDefaultMaxDays() != null ?
                    reason.getDefaultMaxDays() : 365;
            };
        }

        if (domain == ServiceDomain.TELECOM) {
            return switch (reason) {
                case MILITARY_SERVICE -> 180;  // Max 6 months
                case MOVING_DOMESTIC -> 30;    // Max 1 month
                case SABBATICAL -> 180;        // Max 6 months
                default -> 90;  // Default max
            };
        }

        return 0;  // No suspension allowed (e.g., broadcast fee)
    }
}
```

### Rule: Documentation Requirements

```java
public class SuspensionValidator {

    public ValidationResult validate(Suspension suspension) {
        List<String> errors = new ArrayList<>();

        if (suspension.getReason().requiresDocumentation() &&
            suspension.getDocumentId() == null) {
            errors.add("Dokumentation erforderlich für " +
                suspension.getReason().getNameDe());
        }

        if (suspension.getRequestedTo() != null) {
            long days = ChronoUnit.DAYS.between(
                suspension.getRequestedFrom(),
                suspension.getRequestedTo()
            );

            Integer maxDays = suspension.getMaxDurationDays();
            if (maxDays != null && days > maxDays) {
                errors.add("Maximale Dauer überschritten: " +
                    days + " > " + maxDays + " Tage");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
```

---

## Database Schema

```sql
CREATE TABLE suspensions (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    subscription_id UUID NOT NULL,
    subscription_type VARCHAR(20) NOT NULL,
    person_id UUID REFERENCES persons(id),
    reason VARCHAR(30) NOT NULL,
    reason_detail TEXT,
    suspension_type VARCHAR(20) NOT NULL,
    requested_from DATE NOT NULL,
    requested_to DATE,
    effective_from DATE,
    effective_to DATE,
    max_duration_days INTEGER,
    billing_treatment VARCHAR(20) NOT NULL,
    reduction_percent DECIMAL(5,2),
    reference_number VARCHAR(50),
    document_id UUID,
    status VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    approved_by UUID,
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    reactivation_notice_sent DATE,
    auto_reactivate BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_susp_subscription ON suspensions(subscription_id);
CREATE INDEX idx_susp_person ON suspensions(person_id);
CREATE INDEX idx_susp_status ON suspensions(status);
CREATE INDEX idx_susp_dates ON suspensions(effective_from, effective_to);
CREATE INDEX idx_susp_reason ON suspensions(reason);
```

---

## Integration Points

1. **Coverage/Subscription**: Check for active suspensions before processing claims
2. **Billing**: Apply billing treatment during suspension period
3. **Notifications**: Send reactivation notices before suspension ends
4. **Reporting**: Track suspensions by reason for analytics
5. **Document Management**: Store supporting documentation

---

## Edge Cases

1. **Overlapping suspensions**: Only one active suspension per subscription
2. **Early termination**: Allow ending suspension early
3. **Extension**: Allow extending suspension (within limits)
4. **Retroactive**: Some suspensions may be applied retroactively
5. **Auto-expiry**: System automatically ends suspensions at effective_to

---

*Status: Draft*
*Priority: HIGH*

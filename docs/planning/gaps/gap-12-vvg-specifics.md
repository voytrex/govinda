# GAP-12: VVG-Specific Features

## Problem Statement

Supplementary insurance (VVG) has different rules than KVG:
- **No acceptance obligation** - insurer can decline
- **Health questionnaire** - risk assessment
- **Reservations** (Vorbehalte) - exclusion of pre-existing conditions
- **Waiting periods** - before coverage begins
- **Age/gender pricing** - allowed in VVG

---

## 1. Health Questionnaire (Gesundheitserklärung)

### Purpose
Risk assessment for underwriting decision.

### Typical Questions
- Current health conditions
- Past illnesses (5-10 years)
- Medications
- Hospitalizations
- Planned treatments
- Height/Weight (BMI)
- Smoking status
- Occupation

### Model

```java
@Entity
@Table(name = "health_declarations")
public class HealthDeclaration {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "application_id", nullable = false)
    private UUID applicationId;  // VVG application

    @Column(name = "declaration_date", nullable = false)
    private LocalDate declarationDate;

    // Physical data
    @Column(name = "height_cm")
    private Integer heightCm;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "bmi")
    private BigDecimal bmi;

    // Lifestyle
    @Column(name = "is_smoker")
    private Boolean isSmoker;

    @Column(name = "cigarettes_per_day")
    private Integer cigarettesPerDay;

    @Column(name = "alcohol_weekly_units")
    private Integer alcoholWeeklyUnits;

    // Employment
    @Column(name = "occupation")
    private String occupation;

    @Column(name = "occupation_risk_class")
    private String occupationRiskClass;

    // Questions answered
    @OneToMany(mappedBy = "declarationId", cascade = CascadeType.ALL)
    private List<HealthQuestion> questions = new ArrayList<>();

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeclarationStatus status = DeclarationStatus.SUBMITTED;

    // Underwriting result
    @Enumerated(EnumType.STRING)
    @Column(name = "underwriting_decision")
    private UnderwritingDecision underwritingDecision;

    @Column(name = "decision_date")
    private LocalDate decisionDate;

    @Column(name = "decision_by")
    private UUID decisionBy;

    @Column(name = "decision_notes")
    private String decisionNotes;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

@Entity
@Table(name = "health_questions")
public class HealthQuestion {

    @Id
    private UUID id;

    @Column(name = "declaration_id", nullable = false)
    private UUID declarationId;

    @Column(name = "question_code", nullable = false)
    private String questionCode;

    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Column(name = "answer_type", nullable = false)
    private String answerType;  // BOOLEAN, TEXT, DATE, NUMBER

    @Column(name = "answer_boolean")
    private Boolean answerBoolean;

    @Column(name = "answer_text")
    private String answerText;

    @Column(name = "answer_date")
    private LocalDate answerDate;

    @Column(name = "answer_number")
    private BigDecimal answerNumber;

    @Column(name = "requires_detail")
    private boolean requiresDetail;

    @Column(name = "detail_text")
    private String detailText;  // If yes, explain...
}

public enum DeclarationStatus {
    DRAFT,
    SUBMITTED,
    UNDER_REVIEW,
    ADDITIONAL_INFO_REQUESTED,
    COMPLETED
}

public enum UnderwritingDecision {
    ACCEPTED,              // Normal acceptance
    ACCEPTED_WITH_LOADING, // Higher premium (Zuschlag)
    ACCEPTED_WITH_RESERVATION, // With exclusions
    ACCEPTED_WITH_BOTH,    // Loading + Reservation
    DEFERRED,              // Decision postponed
    DECLINED               // Application rejected
}
```

---

## 2. Reservations (Vorbehalte)

### Definition
Exclusion of coverage for specific pre-existing conditions.

### Rules

| Aspect | Rule |
|--------|------|
| Max duration | 5 years (typically) |
| Scope | Specific condition only |
| Transferability | New insurer can impose new reservation |
| Documentation | Must be in writing |

### Model

```java
@Entity
@Table(name = "coverage_reservations")
public class CoverageReservation {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    // What is excluded
    @Column(name = "condition_code")
    private String conditionCode;  // ICD-10 or internal code

    @Column(name = "condition_description", nullable = false)
    private String conditionDescription;

    @Column(name = "body_part")
    private String bodyPart;  // e.g., "Knie links"

    // Scope
    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_scope", nullable = false)
    private ReservationScope scope;

    @Column(name = "scope_details")
    private String scopeDetails;

    // Duration
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;  // null = permanent

    @Column(name = "duration_years")
    private Integer durationYears;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Column(name = "lifted_date")
    private LocalDate liftedDate;

    @Column(name = "lifted_reason")
    private String liftedReason;

    // Source
    @Column(name = "declaration_id")
    private UUID declarationId;  // Health declaration that triggered this

    @Column(name = "notification_sent")
    private LocalDate notificationSent;

    @Column(name = "accepted_by_insured")
    private Boolean acceptedByInsured;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Version
    private long version;
}

public enum ReservationScope {
    TREATMENT("Behandlung"),       // No coverage for treatment
    HOSPITALIZATION("Spitalaufenthalt"), // No hospital coverage
    MEDICATION("Medikamente"),     // No medication coverage
    FULL("Vollständig");           // Complete exclusion

    private final String nameDe;

    ReservationScope(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum ReservationStatus {
    PENDING,     // Awaiting insured acceptance
    ACTIVE,      // Currently in effect
    EXPIRED,     // Duration ended
    LIFTED,      // Removed early
    DECLINED     // Insured declined → coverage cancelled
}
```

---

## 3. Waiting Periods (Wartefristen)

### Common Waiting Periods

| Product Type | Typical Wait |
|--------------|--------------|
| Hospital supplementary | 0-12 months |
| Dental | 12-24 months |
| Maternity | 12-24 months |
| Alternative medicine | 0-6 months |

### Model

```java
@Entity
@Table(name = "waiting_periods")
public class WaitingPeriod {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    // What is restricted
    @Enumerated(EnumType.STRING)
    @Column(name = "benefit_type", nullable = false)
    private BenefitType benefitType;

    @Column(name = "benefit_description")
    private String benefitDescription;

    // Period
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "duration_months")
    private int durationMonths;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitingPeriodStatus status = WaitingPeriodStatus.ACTIVE;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    // Waiver (if previous coverage)
    @Column(name = "waived")
    private boolean waived = false;

    @Column(name = "waiver_reason")
    private String waiverReason;

    @Column(name = "previous_insurer")
    private String previousInsurer;

    @Column(name = "previous_coverage_end")
    private LocalDate previousCoverageEnd;

    @Version
    private long version;
}

public enum BenefitType {
    HOSPITAL_GENERAL("Spital allgemein"),
    HOSPITAL_SEMI_PRIVATE("Spital halbprivat"),
    HOSPITAL_PRIVATE("Spital privat"),
    DENTAL("Zahnarzt"),
    DENTAL_CORRECTION("Zahnkorrektur"),
    MATERNITY("Mutterschaft"),
    ALTERNATIVE_MEDICINE("Alternativmedizin"),
    GLASSES_CONTACTS("Brillen/Kontaktlinsen"),
    FITNESS("Fitness"),
    ABROAD("Ausland"),
    TRANSPORT("Transport/Rettung");

    private final String nameDe;

    BenefitType(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum WaitingPeriodStatus {
    ACTIVE,      // Currently waiting
    COMPLETED,   // Wait finished
    WAIVED       // Waiting period waived
}
```

### Waiting Period Waiver Service

```java
public class WaitingPeriodService {

    /**
     * Check if waiting period can be waived due to previous coverage.
     */
    public boolean canWaive(
            Person person,
            UUID newCoverageId,
            BenefitType benefitType) {

        // Find previous VVG coverage with same benefit type
        Coverage previousCoverage = findPreviousCoverage(
            person, benefitType);

        if (previousCoverage == null) {
            return false;
        }

        // Check gap - usually max 3 months gap allowed
        LocalDate previousEnd = previousCoverage.getEndDate();
        LocalDate newStart = coverageRepository.findById(newCoverageId)
            .getEffectiveDate();

        long gapMonths = ChronoUnit.MONTHS.between(previousEnd, newStart);

        return gapMonths <= 3;  // Typical: max 3 months gap
    }
}
```

---

## 4. Premium Loading (Prämienzuschlag)

### Reasons for Loading
- Health risks (based on declaration)
- Occupation risk
- Lifestyle factors (smoking, BMI)

### Model

```java
@Entity
@Table(name = "premium_loadings")
public class PremiumLoading {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    // Loading details
    @Enumerated(EnumType.STRING)
    @Column(name = "loading_reason", nullable = false)
    private LoadingReason loadingReason;

    @Column(name = "reason_detail")
    private String reasonDetail;

    @Column(name = "loading_percent", nullable = false)
    private BigDecimal loadingPercent;  // e.g., 25 for 25%

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "loading_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money loadingAmount;  // Alternative: fixed amount

    // Duration
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;  // null = permanent

    @Column(name = "review_date")
    private LocalDate reviewDate;  // When to reassess

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoadingStatus status = LoadingStatus.ACTIVE;

    // Source
    @Column(name = "declaration_id")
    private UUID declarationId;

    @Column(name = "accepted_by_insured")
    private Boolean acceptedByInsured;

    @Version
    private long version;
}

public enum LoadingReason {
    HEALTH_CONDITION("Gesundheitszustand"),
    OCCUPATION_RISK("Berufsrisiko"),
    SMOKING("Rauchen"),
    BMI("Übergewicht"),
    SPORTS_RISK("Risikosport"),
    AGE_AT_ENTRY("Eintrittsalter"),
    OTHER("Andere");

    private final String nameDe;

    LoadingReason(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum LoadingStatus {
    PENDING,
    ACTIVE,
    EXPIRED,
    REMOVED,
    DECLINED  // Insured declined → coverage cancelled
}
```

---

## 5. VVG Application Process

### States

```java
public enum VvgApplicationStatus {
    DRAFT,                    // Application started
    DECLARATION_PENDING,      // Awaiting health declaration
    DECLARATION_SUBMITTED,    // Declaration received
    UNDERWRITING,             // Being assessed
    ADDITIONAL_INFO_NEEDED,   // More information requested
    OFFER_GENERATED,          // Offer with conditions
    OFFER_SENT,               // Sent to applicant
    OFFER_ACCEPTED,           // Applicant accepted
    OFFER_DECLINED,           // Applicant declined
    POLICY_ISSUED,            // Coverage active
    APPLICATION_DECLINED,     // Insurer declined
    APPLICATION_WITHDRAWN     // Applicant withdrew
}
```

### Application Entity

```java
@Entity
@Table(name = "vvg_applications")
public class VvgApplication {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "requested_start_date")
    private LocalDate requestedStartDate;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VvgApplicationStatus status = VvgApplicationStatus.DRAFT;

    // Health declaration
    @Column(name = "declaration_id")
    private UUID declarationId;

    // Underwriting result
    @Column(name = "base_premium")
    private Money basePremium;

    @Column(name = "loading_percent")
    private BigDecimal loadingPercent;

    @Column(name = "final_premium")
    private Money finalPremium;

    @Column(name = "has_reservations")
    private boolean hasReservations;

    @Column(name = "has_waiting_periods")
    private boolean hasWaitingPeriods;

    // Offer
    @Column(name = "offer_valid_until")
    private LocalDate offerValidUntil;

    @Column(name = "offer_accepted_date")
    private LocalDate offerAcceptedDate;

    // Result
    @Column(name = "coverage_id")
    private UUID coverageId;  // Created coverage if accepted

    @Column(name = "decline_reason")
    private String declineReason;

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

## Database Schema

```sql
CREATE TABLE health_declarations (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID NOT NULL,
    application_id UUID NOT NULL,
    declaration_date DATE NOT NULL,
    height_cm INTEGER,
    weight_kg DECIMAL(5,2),
    bmi DECIMAL(4,1),
    is_smoker BOOLEAN,
    cigarettes_per_day INTEGER,
    alcohol_weekly_units INTEGER,
    occupation VARCHAR(100),
    occupation_risk_class VARCHAR(10),
    status VARCHAR(30) DEFAULT 'SUBMITTED',
    underwriting_decision VARCHAR(30),
    decision_date DATE,
    decision_by UUID,
    decision_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE health_questions (
    id UUID PRIMARY KEY,
    declaration_id UUID NOT NULL REFERENCES health_declarations(id),
    question_code VARCHAR(20) NOT NULL,
    question_text TEXT NOT NULL,
    answer_type VARCHAR(10) NOT NULL,
    answer_boolean BOOLEAN,
    answer_text TEXT,
    answer_date DATE,
    answer_number DECIMAL(10,2),
    requires_detail BOOLEAN DEFAULT FALSE,
    detail_text TEXT
);

CREATE TABLE coverage_reservations (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    coverage_id UUID NOT NULL,
    person_id UUID NOT NULL,
    condition_code VARCHAR(20),
    condition_description TEXT NOT NULL,
    body_part VARCHAR(100),
    reservation_scope VARCHAR(20) NOT NULL,
    scope_details TEXT,
    valid_from DATE NOT NULL,
    valid_to DATE,
    duration_years INTEGER,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    lifted_date DATE,
    lifted_reason TEXT,
    declaration_id UUID,
    notification_sent DATE,
    accepted_by_insured BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    version BIGINT DEFAULT 0
);

CREATE TABLE waiting_periods (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    coverage_id UUID NOT NULL,
    person_id UUID NOT NULL,
    benefit_type VARCHAR(30) NOT NULL,
    benefit_description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    duration_months INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    completed_date DATE,
    waived BOOLEAN DEFAULT FALSE,
    waiver_reason TEXT,
    previous_insurer VARCHAR(100),
    previous_coverage_end DATE,
    version BIGINT DEFAULT 0
);

CREATE TABLE premium_loadings (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    coverage_id UUID NOT NULL,
    person_id UUID NOT NULL,
    loading_reason VARCHAR(30) NOT NULL,
    reason_detail TEXT,
    loading_percent DECIMAL(5,2),
    loading_amount DECIMAL(10,2),
    currency VARCHAR(3),
    valid_from DATE NOT NULL,
    valid_to DATE,
    review_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    declaration_id UUID,
    accepted_by_insured BOOLEAN,
    version BIGINT DEFAULT 0
);

CREATE TABLE vvg_applications (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID NOT NULL,
    product_id UUID NOT NULL,
    application_date DATE NOT NULL,
    requested_start_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    declaration_id UUID,
    base_premium DECIMAL(10,2),
    loading_percent DECIMAL(5,2),
    final_premium DECIMAL(10,2),
    has_reservations BOOLEAN DEFAULT FALSE,
    has_waiting_periods BOOLEAN DEFAULT FALSE,
    offer_valid_until DATE,
    offer_accepted_date DATE,
    coverage_id UUID,
    decline_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);
```

---

*Status: Draft*
*Priority: HIGH*

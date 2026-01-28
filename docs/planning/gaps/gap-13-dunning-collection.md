# GAP-13: Dunning & Collection Process

## Problem Statement

Missing support for payment collection workflow:
- Payment reminders (Mahnungen)
- Debt collection (Betreibung)
- Loss certificates (Verlustscheine)
- KVG-specific rules (2025 changes)

---

## Legal Framework

### KVG Changes 2025

| Date | Change |
|------|--------|
| 2025-01-01 | Max 2 debt collection proceedings per year |
| 2025-07-01 | Cantons can take over loss certificates |

**KVG Art. 64a** - Premium collection rules

---

## Dunning Process

### Standard Flow

```
Invoice Due → Reminder 1 → Reminder 2 → Final Notice → Debt Collection
    ↓             ↓            ↓             ↓              ↓
  Day 0        +30 days     +45 days      +60 days       +75 days
```

### Model

```java
@Entity
@Table(name = "dunning_cases")
public class DunningCase {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "debtor_id", nullable = false)
    private UUID debtorId;  // Person or Organization

    @Enumerated(EnumType.STRING)
    @Column(name = "debtor_type", nullable = false)
    private DebtorType debtorType;

    @Column(name = "case_number", unique = true)
    private String caseNumber;

    // Amounts
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "original_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money originalAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "fees_amount"))
    })
    private Money feesAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "interest_amount"))
    })
    private Money interestAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_outstanding"))
    })
    private Money totalOutstanding;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "paid_amount"))
    })
    private Money paidAmount;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DunningStatus status = DunningStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_level", nullable = false)
    private DunningLevel currentLevel = DunningLevel.INVOICE_DUE;

    // Dates
    @Column(name = "opened_date", nullable = false)
    private LocalDate openedDate;

    @Column(name = "last_action_date")
    private LocalDate lastActionDate;

    @Column(name = "next_action_date")
    private LocalDate nextActionDate;

    @Column(name = "closed_date")
    private LocalDate closedDate;

    // Related invoices
    @OneToMany(mappedBy = "dunningCaseId")
    private List<DunningInvoice> invoices = new ArrayList<>();

    // Actions taken
    @OneToMany(mappedBy = "dunningCaseId", cascade = CascadeType.ALL)
    private List<DunningAction> actions = new ArrayList<>();

    // KVG specific
    @Column(name = "is_kvg")
    private boolean isKvg = false;

    @Column(name = "betreibung_count_this_year")
    private int betreibungCountThisYear = 0;

    @Column(name = "canton")
    private Canton canton;  // For loss certificate handling

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

public enum DebtorType {
    PERSON,
    ORGANIZATION,
    HOUSEHOLD
}

public enum DunningStatus {
    OPEN,               // Active case
    PAYMENT_PLAN,       // Payment arrangement
    DEBT_COLLECTION,    // At Betreibungsamt
    LOSS_CERTIFICATE,   // Verlustschein issued
    PAID,               // Fully paid
    WRITTEN_OFF,        // Written off
    TRANSFERRED         // Transferred to canton
}

public enum DunningLevel {
    INVOICE_DUE("Rechnung fällig", 0),
    REMINDER_1("1. Mahnung", 30),
    REMINDER_2("2. Mahnung", 45),
    FINAL_NOTICE("Letzte Mahnung", 60),
    DEBT_COLLECTION("Betreibung", 75),
    CONTINUATION("Fortsetzung", 90),
    LOSS_CERTIFICATE("Verlustschein", 120);

    private final String nameDe;
    private final int typicalDaysFromDue;

    DunningLevel(String nameDe, int typicalDaysFromDue) {
        this.nameDe = nameDe;
        this.typicalDaysFromDue = typicalDaysFromDue;
    }
}
```

### Dunning Action Log

```java
@Entity
@Table(name = "dunning_actions")
public class DunningAction {

    @Id
    private UUID id;

    @Column(name = "dunning_case_id", nullable = false)
    private UUID dunningCaseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private DunningActionType actionType;

    @Column(name = "action_date", nullable = false)
    private LocalDate actionDate;

    // Level change
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_level")
    private DunningLevel previousLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_level")
    private DunningLevel newLevel;

    // Financial
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "fee_charged"))
    })
    private Money feeCharged;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "payment_received"))
    })
    private Money paymentReceived;

    // Communication
    @Column(name = "letter_sent")
    private boolean letterSent;

    @Column(name = "document_id")
    private UUID documentId;

    // Debt collection specific
    @Column(name = "betreibung_number")
    private String betreibungNumber;

    @Column(name = "betreibungsamt")
    private String betreibungsamt;

    @Column(name = "objection_received")
    private Boolean objectionReceived;  // Rechtsvorschlag

    // Notes
    @Column
    private String notes;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private Instant createdAt;
}

public enum DunningActionType {
    CASE_OPENED,
    REMINDER_SENT,
    FINAL_NOTICE_SENT,
    PAYMENT_RECEIVED,
    PARTIAL_PAYMENT,
    PAYMENT_PLAN_CREATED,
    PAYMENT_PLAN_UPDATED,
    BETREIBUNG_INITIATED,
    RECHTSVORSCHLAG_RECEIVED,
    CONTINUATION_REQUESTED,
    LOSS_CERTIFICATE_RECEIVED,
    LOSS_CERTIFICATE_TRANSFERRED,
    CASE_CLOSED,
    WRITTEN_OFF,
    LEVEL_ESCALATED,
    FEE_CHARGED,
    NOTE_ADDED
}
```

---

## Payment Plans

```java
@Entity
@Table(name = "payment_plans")
public class PaymentPlan {

    @Id
    private UUID id;

    @Column(name = "dunning_case_id", nullable = false)
    private UUID dunningCaseId;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount"))
    })
    private Money totalAmount;

    @Column(name = "installment_count")
    private int installmentCount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "installment_amount"))
    })
    private Money installmentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private BillingFrequency frequency;

    @Column(name = "first_installment_date")
    private LocalDate firstInstallmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentPlanStatus status = PaymentPlanStatus.ACTIVE;

    @OneToMany(mappedBy = "paymentPlanId", cascade = CascadeType.ALL)
    private List<PaymentPlanInstallment> installments = new ArrayList<>();

    @Column(name = "missed_payments_count")
    private int missedPaymentsCount = 0;

    @Column(name = "max_missed_before_cancel")
    private int maxMissedBeforeCancel = 2;

    @Version
    private long version;
}

@Entity
@Table(name = "payment_plan_installments")
public class PaymentPlanInstallment {

    @Id
    private UUID id;

    @Column(name = "payment_plan_id", nullable = false)
    private UUID paymentPlanId;

    @Column(name = "installment_number")
    private int installmentNumber;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallmentStatus status = InstallmentStatus.PENDING;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "paid_amount"))
    })
    private Money paidAmount;
}

public enum PaymentPlanStatus {
    DRAFT,
    ACTIVE,
    COMPLETED,
    CANCELLED,
    DEFAULTED
}

public enum InstallmentStatus {
    PENDING,
    PAID,
    PARTIAL,
    OVERDUE,
    CANCELLED
}
```

---

## Loss Certificates (Verlustscheine)

```java
@Entity
@Table(name = "loss_certificates")
public class LossCertificate {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "dunning_case_id", nullable = false)
    private UUID dunningCaseId;

    @Column(name = "debtor_id", nullable = false)
    private UUID debtorId;

    @Column(name = "certificate_number", nullable = false)
    private String certificateNumber;

    @Column(name = "betreibungsamt", nullable = false)
    private String betreibungsamt;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "original_claim"))
    })
    private Money originalClaim;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "certificate_amount"))
    })
    private Money certificateAmount;

    // Interest continues to accrue
    @Column(name = "interest_rate")
    private BigDecimal interestRate;  // Typically 5%

    // Validity
    @Column(name = "expires_date")
    private LocalDate expiresDate;  // 20 years from issue

    // Canton transfer (KVG 2025)
    @Column(name = "transferred_to_canton")
    private boolean transferredToCanton = false;

    @Column(name = "transfer_date")
    private LocalDate transferDate;

    @Column(name = "canton")
    private Canton canton;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "canton_compensation"))
    })
    private Money cantonCompensation;  // 85% of claim

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LossCertificateStatus status = LossCertificateStatus.ACTIVE;

    @Version
    private long version;
}

public enum LossCertificateStatus {
    ACTIVE,           // Outstanding
    PARTIALLY_PAID,   // Some recovery
    PAID,             // Fully recovered
    TRANSFERRED,      // Transferred to canton
    EXPIRED,          // 20 years passed
    WRITTEN_OFF       // No longer pursued
}
```

---

## Dunning Fees

```java
@Entity
@Table(name = "dunning_fee_schedules")
public class DunningFeeSchedule {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dunning_level", nullable = false)
    private DunningLevel dunningLevel;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "fee_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money feeAmount;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "is_kvg")
    private boolean isKvg;  // KVG may have regulated fees
}

// Typical fees
// Reminder 1: CHF 10-20
// Reminder 2: CHF 20-30
// Final Notice: CHF 30-50
// Betreibung: Actual costs (Betreibungsamt fees)
```

---

## KVG-Specific Rules

```java
public class KvgDunningService {

    /**
     * KVG Art. 64a - Max 2 Betreibungen per year (from 2025)
     */
    public boolean canInitiateBetreibung(DunningCase dunningCase) {
        if (!dunningCase.isKvg()) {
            return true;  // No limit for VVG
        }

        int countThisYear = dunningCase.getBetreibungCountThisYear();
        return countThisYear < 2;
    }

    /**
     * Canton can take over loss certificates (from July 2025)
     */
    public void transferToCanton(LossCertificate certificate) {
        // Canton pays 85% of claim
        Money compensation = certificate.getCertificateAmount()
            .multiply(new BigDecimal("0.85"));

        certificate.setTransferredToCanton(true);
        certificate.setTransferDate(LocalDate.now());
        certificate.setCantonCompensation(compensation);
        certificate.setStatus(LossCertificateStatus.TRANSFERRED);

        // Canton then pursues debtor directly
    }

    /**
     * Coverage suspension for non-payment (KVG)
     */
    public void handleKvgNonPayment(DunningCase dunningCase) {
        // KVG: Coverage cannot be terminated for non-payment
        // But: Listed on cantonal "säumige Versicherte" list
        // Treatment limited to emergencies

        notifyCanton(dunningCase);
        addToDefaulterList(dunningCase.getDebtorId());
    }
}
```

---

## Database Schema

```sql
CREATE TABLE dunning_cases (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    debtor_id UUID NOT NULL,
    debtor_type VARCHAR(20) NOT NULL,
    case_number VARCHAR(30) UNIQUE,
    original_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'CHF',
    fees_amount DECIMAL(10,2) DEFAULT 0,
    interest_amount DECIMAL(10,2) DEFAULT 0,
    total_outstanding DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    current_level VARCHAR(20) NOT NULL DEFAULT 'INVOICE_DUE',
    opened_date DATE NOT NULL,
    last_action_date DATE,
    next_action_date DATE,
    closed_date DATE,
    is_kvg BOOLEAN DEFAULT FALSE,
    betreibung_count_this_year INTEGER DEFAULT 0,
    canton VARCHAR(2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE dunning_actions (
    id UUID PRIMARY KEY,
    dunning_case_id UUID NOT NULL REFERENCES dunning_cases(id),
    action_type VARCHAR(30) NOT NULL,
    action_date DATE NOT NULL,
    previous_level VARCHAR(20),
    new_level VARCHAR(20),
    fee_charged DECIMAL(10,2),
    payment_received DECIMAL(10,2),
    letter_sent BOOLEAN DEFAULT FALSE,
    document_id UUID,
    betreibung_number VARCHAR(30),
    betreibungsamt VARCHAR(100),
    objection_received BOOLEAN,
    notes TEXT,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_plans (
    id UUID PRIMARY KEY,
    dunning_case_id UUID NOT NULL REFERENCES dunning_cases(id),
    plan_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    installment_count INTEGER NOT NULL,
    installment_amount DECIMAL(10,2) NOT NULL,
    frequency VARCHAR(20),
    first_installment_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    missed_payments_count INTEGER DEFAULT 0,
    max_missed_before_cancel INTEGER DEFAULT 2,
    version BIGINT DEFAULT 0
);

CREATE TABLE loss_certificates (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    dunning_case_id UUID NOT NULL,
    debtor_id UUID NOT NULL,
    certificate_number VARCHAR(50) NOT NULL,
    betreibungsamt VARCHAR(100) NOT NULL,
    issue_date DATE NOT NULL,
    original_claim DECIMAL(10,2) NOT NULL,
    certificate_amount DECIMAL(10,2) NOT NULL,
    interest_rate DECIMAL(4,2) DEFAULT 5.00,
    expires_date DATE,
    transferred_to_canton BOOLEAN DEFAULT FALSE,
    transfer_date DATE,
    canton VARCHAR(2),
    canton_compensation DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_dc_debtor ON dunning_cases(debtor_id);
CREATE INDEX idx_dc_status ON dunning_cases(status);
CREATE INDEX idx_da_case ON dunning_actions(dunning_case_id);
CREATE INDEX idx_lc_debtor ON loss_certificates(debtor_id);
```

---

*Status: Draft*
*Priority: MEDIUM*

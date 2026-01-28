# GAP-17: Claims Processing & Invoice Management

## Problem Statement

Missing support for:
- **Forum Datenaustausch** - electronic invoice exchange
- **Sumex** and other validation systems
- **Leistungen** (benefits/claims) processing
- **Tiers Payant** vs **Tiers Garant** payment models
- Cost sharing application (Franchise, Selbstbehalt)
- Patient billing

---

## 1. Payment Models: Tiers Payant vs Tiers Garant

### Tiers Garant (Patient Pays)

```
Provider → Invoice → Patient → Submits → Insurer → Reimburses → Patient
```

- Patient receives invoice from provider
- Patient pays provider
- Patient submits invoice to insurer
- Insurer reimburses patient (minus cost sharing)

### Tiers Payant (Insurer Pays)

```
Provider → Invoice → Insurer → Pays Provider → Patient pays cost sharing
```

- Provider sends invoice directly to insurer
- Insurer pays provider (after validation)
- Patient only pays cost sharing portion

### Model

```java
public enum PaymentModel {
    TIERS_GARANT("Tiers Garant", "Patient zahlt"),
    TIERS_PAYANT("Tiers Payant", "Versicherer zahlt");

    private final String nameDe;
    private final String description;

    PaymentModel(String nameDe, String description) {
        this.nameDe = nameDe;
        this.description = description;
    }
}
```

---

## 2. Forum Datenaustausch

### What is Forum Datenaustausch?

The **Forum Datenaustausch** (eForum) defines Swiss healthcare electronic data exchange standards:
- XML invoice formats
- Response messages
- Error codes

Website: [forum-datenaustausch.ch](https://www.forum-datenaustausch.ch/)

### Message Types

| Type | Purpose |
|------|---------|
| Invoice Request (4.5) | Electronic invoice submission |
| Invoice Response | Acceptance/rejection |
| Reminder | Payment reminder |
| Reminder Response | Reminder reply |

### Invoice XML Versions

| Version | Status |
|---------|--------|
| 4.4 | Deprecated |
| 4.5 | Current |
| 5.0 | Future |

### Integration Model

```java
@Entity
@Table(name = "forum_messages")
public class ForumMessage {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private MessageDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private ForumMessageType messageType;

    @Column(name = "format_version", nullable = false)
    private String formatVersion;  // e.g., "4.5"

    // Identifiers
    @Column(name = "message_id", unique = true)
    private String messageId;

    @Column(name = "reference_message_id")
    private String referenceMessageId;

    // Parties
    @Column(name = "sender_gln", nullable = false)
    private String senderGln;

    @Column(name = "receiver_gln", nullable = false)
    private String receiverGln;

    // Related entities
    @Column(name = "invoice_id")
    private UUID invoiceId;

    @Column(name = "claim_id")
    private UUID claimId;

    // Content
    @Column(name = "xml_payload", columnDefinition = "TEXT")
    private String xmlPayload;

    // Transport
    @Enumerated(EnumType.STRING)
    @Column(name = "transport_method")
    private TransportMethod transportMethod;

    @Column(name = "transport_reference")
    private String transportReference;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ForumMessageStatus status = ForumMessageStatus.CREATED;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "received_at")
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    // Errors
    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

public enum ForumMessageType {
    INVOICE_REQUEST("Rechnungsanfrage"),
    INVOICE_RESPONSE("Rechnungsantwort"),
    REMINDER("Mahnung"),
    REMINDER_RESPONSE("Mahnungsantwort"),
    COST_GUARANTEE_REQUEST("Kostengutsprache-Anfrage"),
    COST_GUARANTEE_RESPONSE("Kostengutsprache-Antwort"),
    CLAIM_RESPONSE("Leistungsabrechnung");

    private final String nameDe;

    ForumMessageType(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum TransportMethod {
    DIRECT("Direktanbindung"),
    TRUSTCENTER("Trustcenter"),
    HIN("HIN"),
    SEDEX("Sedex"),
    EMAIL("E-Mail");

    private final String nameDe;

    TransportMethod(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum ForumMessageStatus {
    CREATED,
    VALIDATED,
    QUEUED,
    SENT,
    DELIVERED,
    ACCEPTED,
    REJECTED,
    PROCESSING,
    PROCESSED,
    FAILED
}
```

---

## 3. Invoice Validation (Sumex)

### What is Sumex?

**Sumex** provides:
- Invoice validation software
- Claims processing integration
- Cost sharing calculations

### Validation Process

```
Invoice XML → Sumex Validator → Validation Result
                     ↓
            [VALID] → Process
            [ERROR] → Reject with error codes
```

### Validation Model

```java
@Entity
@Table(name = "invoice_validations")
public class InvoiceValidation {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "validator_type", nullable = false)
    private ValidatorType validatorType;

    @Column(name = "validator_version")
    private String validatorVersion;

    // Result
    @Enumerated(EnumType.STRING)
    @Column(name = "validation_result", nullable = false)
    private ValidationResult validationResult;

    @Column(name = "validated_at", nullable = false)
    private Instant validatedAt;

    // Errors/Warnings
    @OneToMany(mappedBy = "validationId", cascade = CascadeType.ALL)
    private List<ValidationIssue> issues = new ArrayList<>();

    @Column(name = "error_count")
    private int errorCount = 0;

    @Column(name = "warning_count")
    private int warningCount = 0;

    // Processing recommendation
    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation")
    private ProcessingRecommendation recommendation;

    @Version
    private long version;
}

@Entity
@Table(name = "validation_issues")
public class ValidationIssue {

    @Id
    private UUID id;

    @Column(name = "validation_id", nullable = false)
    private UUID validationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private IssueSeverity severity;

    @Column(name = "error_code", nullable = false)
    private String errorCode;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @Column(name = "field_path")
    private String fieldPath;  // XPath to field

    @Column(name = "record_reference")
    private String recordReference;  // Position number

    @Column(name = "suggested_fix")
    private String suggestedFix;
}

public enum ValidatorType {
    SUMEX("Sumex"),
    INTERNAL("Intern"),
    BAG("BAG Validator"),
    CUSTOM("Kundenspezifisch");

    private final String nameDe;

    ValidatorType(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum ValidationResult {
    VALID("Gültig"),
    VALID_WITH_WARNINGS("Gültig mit Warnungen"),
    INVALID("Ungültig"),
    ERROR("Fehler");

    private final String nameDe;

    ValidationResult(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum IssueSeverity {
    ERROR("Fehler"),
    WARNING("Warnung"),
    INFO("Information");

    private final String nameDe;

    IssueSeverity(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum ProcessingRecommendation {
    PROCESS_AUTOMATICALLY("Automatisch verarbeiten"),
    MANUAL_REVIEW("Manuelle Prüfung"),
    REJECT("Ablehnen"),
    REQUEST_CORRECTION("Korrektur anfordern");

    private final String nameDe;

    ProcessingRecommendation(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 4. Provider Invoice (Leistungserbringer-Rechnung)

### Invoice from Provider

```java
@Entity
@Table(name = "provider_invoices")
public class ProviderInvoice {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    // Provider
    @Column(name = "provider_gln", nullable = false)
    private String providerGln;

    @Column(name = "provider_zsr")
    private String providerZsr;

    @Column(name = "provider_name")
    private String providerName;

    // Patient
    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;

    // Invoice details
    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false)
    private ProviderInvoiceType invoiceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_model", nullable = false)
    private PaymentModel paymentModel;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "treatment_date_from")
    private LocalDate treatmentDateFrom;

    @Column(name = "treatment_date_to")
    private LocalDate treatmentDateTo;

    // Diagnosis
    @ElementCollection
    @CollectionTable(name = "provider_invoice_diagnoses")
    private List<String> diagnosisCodes = new ArrayList<>();  // ICD-10

    // Amounts
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "gross_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money grossAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "vat_amount"))
    })
    private Money vatAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "net_amount"))
    })
    private Money netAmount;

    // Line items
    @OneToMany(mappedBy = "invoiceId", cascade = CascadeType.ALL)
    private List<ProviderInvoiceLine> lines = new ArrayList<>();

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderInvoiceStatus status = ProviderInvoiceStatus.RECEIVED;

    // Forum message
    @Column(name = "forum_message_id")
    private UUID forumMessageId;

    @Column(name = "original_xml_id")
    private UUID originalXmlId;  // Stored XML

    // Processing
    @Column(name = "received_at")
    private Instant receivedAt;

    @Column(name = "validated_at")
    private Instant validatedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "processed_by")
    private UUID processedBy;  // null = automatic

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

@Entity
@Table(name = "provider_invoice_lines")
public class ProviderInvoiceLine {

    @Id
    private UUID id;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    // Service
    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type", nullable = false)
    private TariffType tariffType;

    @Column(name = "tariff_version")
    private String tariffVersion;

    @Column(name = "position_code", nullable = false)
    private String positionCode;

    @Column(name = "position_description")
    private String positionDescription;

    // Service date
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    // Provider
    @Column(name = "provider_gln")
    private String providerGln;  // If different from invoice

    // Quantity
    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit")
    private String unit;

    // Pricing (TARMED)
    @Column(name = "tax_points_medical")
    private BigDecimal taxPointsMedical;

    @Column(name = "tax_points_technical")
    private BigDecimal taxPointsTechnical;

    @Column(name = "taxpunkt_value")
    private BigDecimal taxpunktValue;

    // Amount
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "unit_price"))
    })
    private Money unitPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "line_amount"))
    })
    private Money lineAmount;

    @Column(name = "vat_rate")
    private BigDecimal vatRate;

    // Validation
    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status")
    private LineValidationStatus validationStatus;

    @Column(name = "validation_message")
    private String validationMessage;
}

public enum ProviderInvoiceType {
    KVG("KVG-Rechnung"),
    VVG("VVG-Rechnung"),
    UVG("UVG-Rechnung"),
    MVG("MVG-Rechnung"),
    IV("IV-Rechnung"),
    SELF_PAY("Selbstzahler");

    private final String nameDe;

    ProviderInvoiceType(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum ProviderInvoiceStatus {
    RECEIVED("Empfangen"),
    VALIDATING("In Validierung"),
    VALIDATION_FAILED("Validierung fehlgeschlagen"),
    PENDING_REVIEW("Manuelle Prüfung"),
    APPROVED("Genehmigt"),
    PARTIALLY_APPROVED("Teilweise genehmigt"),
    REJECTED("Abgelehnt"),
    PROCESSING("In Verarbeitung"),
    PROCESSED("Verarbeitet"),
    PAID("Bezahlt");

    private final String nameDe;

    ProviderInvoiceStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum LineValidationStatus {
    VALID("Gültig"),
    WARNING("Warnung"),
    ERROR("Fehler"),
    ADJUSTED("Angepasst"),
    REJECTED("Abgelehnt");

    private final String nameDe;

    LineValidationStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 5. Claim (Leistung)

### From Invoice to Claim

```
ProviderInvoice → Validation → Approval → Claim(s) → Cost Sharing → Payment
```

### Claim Model

```java
@Entity
@Table(name = "claims")
public class Claim {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "claim_number", unique = true)
    private String claimNumber;

    // Source
    @Column(name = "provider_invoice_id")
    private UUID providerInvoiceId;

    @Column(name = "provider_invoice_line_id")
    private UUID providerInvoiceLineId;

    // Patient
    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;

    // Provider
    @Column(name = "provider_gln", nullable = false)
    private String providerGln;

    @Column(name = "provider_name")
    private String providerName;

    // Service details
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type")
    private TariffType tariffType;

    @Column(name = "tariff_code")
    private String tariffCode;

    @Column(name = "service_description")
    private String serviceDescription;

    // Diagnosis
    @Column(name = "diagnosis_code")
    private String diagnosisCode;

    @Column(name = "diagnosis_description")
    private String diagnosisDescription;

    // Amounts - Invoiced
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "invoiced_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money invoicedAmount;

    // Amounts - Approved
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "approved_amount"))
    })
    private Money approvedAmount;

    // Amounts - Cost sharing applied
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
        @AttributeOverride(name = "amount", column = @Column(name = "patient_share"))
    })
    private Money patientShare;

    // Amounts - Insurer pays
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "insurer_pays"))
    })
    private Money insurerPays;

    // Payment model
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_model", nullable = false)
    private PaymentModel paymentModel;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;

    // Flags
    @Column(name = "is_maternity")
    private boolean isMaternity = false;  // No cost sharing

    @Column(name = "is_accident")
    private boolean isAccident = false;   // UVG applicable?

    // Processing
    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "processed_by")
    private UUID processedBy;  // null = automatic

    @Column(name = "adjustment_reason")
    private String adjustmentReason;

    // Cost sharing account
    @Column(name = "cost_sharing_account_id")
    private UUID costSharingAccountId;

    // Payment tracking
    @Column(name = "provider_payment_id")
    private UUID providerPaymentId;  // For Tiers Payant

    @Column(name = "patient_reimbursement_id")
    private UUID patientReimbursementId;  // For Tiers Garant

    @Column(name = "patient_invoice_id")
    private UUID patientInvoiceId;  // Patient bill

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

public enum ClaimStatus {
    PENDING("Ausstehend"),
    PROCESSING("In Bearbeitung"),
    APPROVED("Genehmigt"),
    ADJUSTED("Angepasst"),
    REJECTED("Abgelehnt"),
    COST_SHARING_APPLIED("Kostenbeteiligung angewendet"),
    READY_FOR_PAYMENT("Bereit für Zahlung"),
    PAID("Bezahlt"),
    CLOSED("Abgeschlossen");

    private final String nameDe;

    ClaimStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 6. Patient Invoice (Rückforderung / Kostenbeteiligung)

### Patient Billing

```java
@Entity
@Table(name = "patient_invoices")
public class PatientInvoice {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;

    // Patient
    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;

    // Invoice type
    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false)
    private PatientInvoiceType invoiceType;

    // Dates
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "period_from")
    private LocalDate periodFrom;

    @Column(name = "period_to")
    private LocalDate periodTo;

    // Lines
    @OneToMany(mappedBy = "patientInvoiceId", cascade = CascadeType.ALL)
    private List<PatientInvoiceLine> lines = new ArrayList<>();

    // Amounts
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money totalAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "paid_amount"))
    })
    private Money paidAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "outstanding_amount"))
    })
    private Money outstandingAmount;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatientInvoiceStatus status = PatientInvoiceStatus.DRAFT;

    // Delivery
    @Column(name = "sent_at")
    private Instant sentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method")
    private DeliveryMethod deliveryMethod;

    // Payment
    @Column(name = "payment_reference")
    private String paymentReference;  // QR reference

    @Column(name = "qr_iban")
    private String qrIban;

    // Dunning link
    @Column(name = "dunning_case_id")
    private UUID dunningCaseId;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

@Entity
@Table(name = "patient_invoice_lines")
public class PatientInvoiceLine {

    @Id
    private UUID id;

    @Column(name = "patient_invoice_id", nullable = false)
    private UUID patientInvoiceId;

    @Column(name = "line_number")
    private int lineNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "line_type", nullable = false)
    private PatientInvoiceLineType lineType;

    @Column(name = "description", nullable = false)
    private String description;

    // Reference
    @Column(name = "claim_id")
    private UUID claimId;

    @Column(name = "service_date")
    private LocalDate serviceDate;

    @Column(name = "provider_name")
    private String providerName;

    // Amount
    @Embedded
    private Money amount;
}

public enum PatientInvoiceType {
    COST_SHARING("Kostenbeteiligung"),
    REIMBURSEMENT_DEDUCTION("Rückerstattung Abzug"),
    PREMIUM("Prämie"),
    REMINDER_FEE("Mahngebühr"),
    OTHER("Andere");

    private final String nameDe;

    PatientInvoiceType(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum PatientInvoiceLineType {
    FRANCHISE("Franchise"),
    SELBSTBEHALT("Selbstbehalt"),
    NON_COVERED("Nicht gedeckt"),
    PREMIUM("Prämie"),
    FEE("Gebühr"),
    ADJUSTMENT("Anpassung");

    private final String nameDe;

    PatientInvoiceLineType(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum PatientInvoiceStatus {
    DRAFT("Entwurf"),
    ISSUED("Ausgestellt"),
    SENT("Versandt"),
    PARTIALLY_PAID("Teilweise bezahlt"),
    PAID("Bezahlt"),
    OVERDUE("Überfällig"),
    IN_DUNNING("Im Mahnwesen"),
    CANCELLED("Storniert");

    private final String nameDe;

    PatientInvoiceStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum DeliveryMethod {
    POST("Post"),
    EMAIL("E-Mail"),
    PORTAL("Kundenportal"),
    EBILL("eBill");

    private final String nameDe;

    DeliveryMethod(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 7. Provider Payment (Tiers Payant)

```java
@Entity
@Table(name = "provider_payments")
public class ProviderPayment {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "payment_number", unique = true)
    private String paymentNumber;

    // Provider
    @Column(name = "provider_gln", nullable = false)
    private String providerGln;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "provider_iban")
    private String providerIban;

    // Claims included
    @OneToMany(mappedBy = "providerPaymentId")
    private List<Claim> claims = new ArrayList<>();

    @Column(name = "claim_count")
    private int claimCount;

    // Amounts
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money totalAmount;

    // Payment
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "bank_reference")
    private String bankReference;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

public enum PaymentStatus {
    PENDING("Ausstehend"),
    APPROVED("Genehmigt"),
    SCHEDULED("Geplant"),
    EXECUTED("Ausgeführt"),
    CONFIRMED("Bestätigt"),
    FAILED("Fehlgeschlagen"),
    CANCELLED("Storniert");

    private final String nameDe;

    PaymentStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 8. Claims Processing Flow

```java
public interface ClaimsProcessingService {

    /**
     * Receive invoice from Forum Datenaustausch
     */
    ProviderInvoice receiveInvoice(ForumMessage message);

    /**
     * Validate invoice (Sumex or internal)
     */
    InvoiceValidation validateInvoice(UUID invoiceId);

    /**
     * Manual review/approval
     */
    ProviderInvoice reviewInvoice(UUID invoiceId, ReviewDecision decision);

    /**
     * Process approved invoice to claims
     */
    List<Claim> processInvoiceToClaims(UUID invoiceId);

    /**
     * Apply cost sharing to claims
     */
    void applyCostSharing(List<Claim> claims);

    /**
     * Create patient invoice for cost sharing
     */
    PatientInvoice createPatientInvoice(UUID personId, List<Claim> claims);

    /**
     * Create provider payment (Tiers Payant)
     */
    ProviderPayment createProviderPayment(List<Claim> claims);

    /**
     * Send response via Forum Datenaustausch
     */
    ForumMessage sendInvoiceResponse(UUID invoiceId);
}
```

---

## Database Schema

```sql
-- Forum Messages
CREATE TABLE forum_messages (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    direction VARCHAR(10) NOT NULL,
    message_type VARCHAR(30) NOT NULL,
    format_version VARCHAR(10) NOT NULL,
    message_id VARCHAR(50) UNIQUE,
    reference_message_id VARCHAR(50),
    sender_gln VARCHAR(13) NOT NULL,
    receiver_gln VARCHAR(13) NOT NULL,
    invoice_id UUID,
    claim_id UUID,
    xml_payload TEXT,
    transport_method VARCHAR(20),
    transport_reference VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    sent_at TIMESTAMP,
    received_at TIMESTAMP,
    processed_at TIMESTAMP,
    error_code VARCHAR(20),
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Provider Invoices
CREATE TABLE provider_invoices (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    invoice_number VARCHAR(50) NOT NULL,
    provider_gln VARCHAR(13) NOT NULL,
    provider_zsr VARCHAR(10),
    provider_name VARCHAR(255),
    person_id UUID NOT NULL,
    coverage_id UUID NOT NULL,
    invoice_type VARCHAR(20) NOT NULL,
    payment_model VARCHAR(20) NOT NULL,
    invoice_date DATE NOT NULL,
    treatment_date_from DATE,
    treatment_date_to DATE,
    gross_amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'CHF',
    vat_amount DECIMAL(12,2),
    net_amount DECIMAL(12,2),
    status VARCHAR(30) NOT NULL DEFAULT 'RECEIVED',
    forum_message_id UUID,
    original_xml_id UUID,
    received_at TIMESTAMP,
    validated_at TIMESTAMP,
    processed_at TIMESTAMP,
    processed_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Claims
CREATE TABLE claims (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    claim_number VARCHAR(30) UNIQUE,
    provider_invoice_id UUID,
    provider_invoice_line_id UUID,
    person_id UUID NOT NULL,
    coverage_id UUID NOT NULL,
    provider_gln VARCHAR(13) NOT NULL,
    provider_name VARCHAR(255),
    service_date DATE NOT NULL,
    tariff_type VARCHAR(20),
    tariff_code VARCHAR(20),
    service_description TEXT,
    diagnosis_code VARCHAR(10),
    diagnosis_description VARCHAR(255),
    invoiced_amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'CHF',
    approved_amount DECIMAL(12,2),
    franchise_applied DECIMAL(12,2) DEFAULT 0,
    selbstbehalt_applied DECIMAL(12,2) DEFAULT 0,
    patient_share DECIMAL(12,2) DEFAULT 0,
    insurer_pays DECIMAL(12,2),
    payment_model VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    is_maternity BOOLEAN DEFAULT FALSE,
    is_accident BOOLEAN DEFAULT FALSE,
    processed_at TIMESTAMP,
    processed_by UUID,
    adjustment_reason TEXT,
    cost_sharing_account_id UUID,
    provider_payment_id UUID,
    patient_reimbursement_id UUID,
    patient_invoice_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Patient Invoices
CREATE TABLE patient_invoices (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    invoice_number VARCHAR(30) UNIQUE,
    person_id UUID NOT NULL,
    coverage_id UUID NOT NULL,
    invoice_type VARCHAR(30) NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE NOT NULL,
    period_from DATE,
    period_to DATE,
    total_amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'CHF',
    paid_amount DECIMAL(12,2) DEFAULT 0,
    outstanding_amount DECIMAL(12,2),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    sent_at TIMESTAMP,
    delivery_method VARCHAR(20),
    payment_reference VARCHAR(30),
    qr_iban VARCHAR(30),
    dunning_case_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Provider Payments
CREATE TABLE provider_payments (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    payment_number VARCHAR(30) UNIQUE,
    provider_gln VARCHAR(13) NOT NULL,
    provider_name VARCHAR(255),
    provider_iban VARCHAR(30),
    claim_count INTEGER,
    total_amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'CHF',
    payment_date DATE,
    value_date DATE,
    bank_reference VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_fm_invoice ON forum_messages(invoice_id);
CREATE INDEX idx_pi_person ON provider_invoices(person_id);
CREATE INDEX idx_pi_status ON provider_invoices(status);
CREATE INDEX idx_cl_person ON claims(person_id);
CREATE INDEX idx_cl_provider ON claims(provider_gln);
CREATE INDEX idx_pti_person ON patient_invoices(person_id);
CREATE INDEX idx_pp_provider ON provider_payments(provider_gln);
```

---

## References

- [Forum Datenaustausch](https://www.forum-datenaustausch.ch/)
- [Sumex AG](https://www.sumex.ch/)
- [Invoice XML 4.5 Specification](https://www.forum-datenaustausch.ch/de/referenzmaterial/)

---

*Status: Draft*
*Priority: HIGH*

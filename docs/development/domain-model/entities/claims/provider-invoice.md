# ProviderInvoice Entity

## Overview

The **ProviderInvoice** (Rechnungseingang) represents an invoice received from a healthcare provider for services rendered.

> **German**: Rechnungseingang, Leistungserbringerrechnung
> **Module**: `govinda-claims`
> **Status**: Planned

**Flow**: Provider submits invoice (XML/EDI) -> Validation -> Approval -> Claims generated

---

## Entity Definition

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

    @Column(name = "provider_name")
    private String providerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type")
    private ProviderType providerType;

    // Patient
    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "coverage_id")
    private UUID coverageId;

    @Column(name = "insurance_card_number")
    private String insuranceCardNumber;

    // Invoice details
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "treatment_start", nullable = false)
    private LocalDate treatmentStart;

    @Column(name = "treatment_end", nullable = false)
    private LocalDate treatmentEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "treatment_type", nullable = false)
    private TreatmentType treatmentType;

    // Diagnosis
    @Column(name = "primary_diagnosis")
    private String primaryDiagnosis;  // ICD-10

    @ElementCollection
    @CollectionTable(name = "invoice_diagnoses")
    private List<String> secondaryDiagnoses = new ArrayList<>();

    // Amounts
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount"))
    })
    private Money totalAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "vat_amount"))
    })
    private Money vatAmount;

    // Payment model
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_model", nullable = false)
    private PaymentModel paymentModel;

    // Submission
    @Enumerated(EnumType.STRING)
    @Column(name = "submission_method", nullable = false)
    private SubmissionMethod submissionMethod;

    @Column(name = "xml_reference")
    private String xmlReference;  // Forum Datenaustausch XML ID

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    // Processing
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.RECEIVED;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "processor_id")
    private UUID processorId;  // Sumex or other engine

    // Validation
    @Column(name = "validation_passed")
    private boolean validationPassed;

    @Column(name = "validation_errors", columnDefinition = "TEXT")
    private String validationErrors;

    // One-to-many: invoice lines
    @OneToMany(mappedBy = "providerInvoice", cascade = CascadeType.ALL)
    private List<ProviderInvoiceLine> lines = new ArrayList<>();

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

## ProviderInvoiceLine Entity

```java
@Entity
@Table(name = "provider_invoice_lines")
public class ProviderInvoiceLine {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "provider_invoice_id", nullable = false)
    private ProviderInvoice providerInvoice;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    // Service
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type", nullable = false)
    private TariffType tariffType;

    @Column(name = "tariff_code", nullable = false)
    private String tariffCode;

    @Column(name = "tariff_version")
    private String tariffVersion;

    @Column(name = "service_description")
    private String serviceDescription;

    // Quantity & Pricing
    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "tax_point")
    private BigDecimal taxPoint;  // TARMED: Taxpunktwert

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "line_amount"))
    })
    private Money lineAmount;

    // Validation
    @Enumerated(EnumType.STRING)
    @Column(name = "line_status")
    private LineStatus lineStatus = LineStatus.PENDING;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "approved_amount"))
    })
    private Money approvedAmount;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    // Link to generated claim
    @Column(name = "claim_id")
    private UUID claimId;
}
```

---

## Field Reference

### ProviderInvoice

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `invoiceNumber` | String | Required | Provider's invoice number |
| `providerGln` | String | Required | Provider GLN (13 digits) |
| `personId` | UUID | Required | Patient |
| `invoiceDate` | LocalDate | Required | Date invoice was issued |
| `treatmentStart` | LocalDate | Required | First treatment date |
| `treatmentEnd` | LocalDate | Required | Last treatment date |
| `treatmentType` | TreatmentType | Required | AMBULATORY, INPATIENT, etc. |
| `totalAmount` | Money | Required | Invoice total |
| `paymentModel` | PaymentModel | Required | TIERS_PAYANT or TIERS_GARANT |
| `submissionMethod` | SubmissionMethod | Required | How invoice was received |
| `status` | InvoiceStatus | Required | Processing status |

### ProviderInvoiceLine

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `lineNumber` | Integer | Required | Position on invoice |
| `serviceDate` | LocalDate | Required | When service rendered |
| `tariffType` | TariffType | Required | TARMED, SwissDRG, etc. |
| `tariffCode` | String | Required | Tariff position code |
| `quantity` | BigDecimal | Required | Units/points |
| `lineAmount` | Money | Required | Line total |
| `approvedAmount` | Money | Optional | Approved after validation |

---

## Related Enums

### TreatmentType

```java
public enum TreatmentType {
    AMBULATORY("Ambulant"),
    INPATIENT("Stationär"),
    SEMI_INPATIENT("Teilstationär"),
    EMERGENCY("Notfall"),
    HOME_CARE("Spitex");
}
```

### TariffType

```java
public enum TariffType {
    TARMED("TARMED"),
    TARDOC("TARDOC"),           // Replacing TARMED 2026+
    SWISS_DRG("SwissDRG"),      // Inpatient flat rates
    MIGEL("MiGeL"),             // Medical devices
    ALK("ALK"),                 // Lab analysis
    DRUG("Medikamente"),        // Medications
    PHYSIOTHERAPY("Physio"),
    OTHER("Andere");
}
```

### InvoiceStatus

```java
public enum InvoiceStatus {
    RECEIVED("Eingegangen"),
    VALIDATING("In Validierung"),
    VALIDATION_FAILED("Validierung fehlgeschlagen"),
    PENDING_REVIEW("Prüfung ausstehend"),
    IN_REVIEW("In Prüfung"),
    APPROVED("Genehmigt"),
    PARTIALLY_APPROVED("Teilweise genehmigt"),
    REJECTED("Abgelehnt"),
    CLAIMS_GENERATED("Leistungen generiert"),
    PAID("Bezahlt"),
    DISPUTED("Angefochten");
}
```

### SubmissionMethod

```java
public enum SubmissionMethod {
    FORUM_XML("Forum Datenaustausch XML"),
    EDI("EDI"),
    PDF_SCAN("PDF Scan"),
    MANUAL_ENTRY("Manuelle Erfassung"),
    API("API");
}
```

### LineStatus

```java
public enum LineStatus {
    PENDING("Ausstehend"),
    APPROVED("Genehmigt"),
    ADJUSTED("Angepasst"),
    REJECTED("Abgelehnt");
}
```

---

## Invoice Processing Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          INVOICE RECEIVED                               │
│                    (Forum XML, PDF, Manual)                             │
└─────────────────────────────┬───────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      TECHNICAL VALIDATION                               │
│  • XML schema validation                                                │
│  • GLN verification (provider exists)                                   │
│  • Patient matching (VeKa number, AHV)                                  │
│  • Coverage active on treatment dates                                   │
└─────────────────────────────┬───────────────────────────────────────────┘
                              │
            ┌─────────────────┴─────────────────┐
            │ FAILED                            │ PASSED
            ▼                                   ▼
┌───────────────────────┐         ┌────────────────────────────────────────┐
│  VALIDATION_FAILED    │         │           CONTENT VALIDATION           │
│  • Return to provider │         │  • Tariff code validity                │
│  • Error details      │         │  • Price verification (Tarifpool)      │
└───────────────────────┘         │  • Medical plausibility (Sumex)        │
                                  │  • Duplicate check                     │
                                  └─────────────────┬──────────────────────┘
                                                    │
                              ┌─────────────────────┼─────────────────────┐
                              │                     │                     │
                              ▼                     ▼                     ▼
                    ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
                    │    APPROVED     │   │    ADJUSTED     │   │    REJECTED     │
                    │  (auto/manual)  │   │  (price change) │   │                 │
                    └────────┬────────┘   └────────┬────────┘   └─────────────────┘
                             │                     │
                             └──────────┬──────────┘
                                        │
                                        ▼
                    ┌─────────────────────────────────────────┐
                    │          CLAIMS GENERATION              │
                    │  • Create Claim per approved line       │
                    │  • Apply cost sharing                   │
                    │  • Update CostSharingAccount            │
                    └─────────────────────────────────────────┘
```

---

## Forum Datenaustausch Integration

```java
@Service
public class ForumDataExchangeService {

    public ProviderInvoice importXml(String xmlContent) {
        // Parse Forum Datenaustausch XML (generalInvoiceRequest)
        GeneralInvoiceRequest request = xmlParser.parse(xmlContent);

        ProviderInvoice invoice = new ProviderInvoice();
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setProviderGln(request.getBiller().getGln());
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setTreatmentType(mapTreatmentType(request.getTreatment()));
        invoice.setSubmissionMethod(SubmissionMethod.FORUM_XML);
        invoice.setXmlReference(request.getRequestId());

        // Map services to lines
        for (Service service : request.getServices()) {
            ProviderInvoiceLine line = mapToLine(service);
            invoice.addLine(line);
        }

        invoice.setTotalAmount(calculateTotal(invoice.getLines()));
        return invoiceRepository.save(invoice);
    }
}
```

---

## Sumex Integration

```java
@Service
public class SumexValidationService {

    public ValidationResult validate(ProviderInvoice invoice) {
        // Call Sumex validation engine
        SumexRequest request = mapToSumexRequest(invoice);
        SumexResponse response = sumexClient.validate(request);

        ValidationResult result = new ValidationResult();
        result.setPassed(response.isValid());

        for (SumexError error : response.getErrors()) {
            result.addError(error.getLineNumber(), error.getCode(), error.getMessage());
        }

        // Update line statuses based on Sumex response
        for (ProviderInvoiceLine line : invoice.getLines()) {
            SumexLineResult lineResult = response.getLineResult(line.getLineNumber());
            if (lineResult.isApproved()) {
                line.setLineStatus(LineStatus.APPROVED);
                line.setApprovedAmount(Money.chf(lineResult.getApprovedAmount()));
            } else {
                line.setLineStatus(LineStatus.REJECTED);
                line.setRejectionReason(lineResult.getRejectionReason());
            }
        }

        return result;
    }
}
```

---

## Use Cases

### Tiers Payant Invoice (Provider paid directly)

```java
ProviderInvoice invoice = new ProviderInvoice();
invoice.setProviderGln("7601000123456");
invoice.setPersonId(patient.getId());
invoice.setCoverageId(kvgCoverage.getId());
invoice.setPaymentModel(PaymentModel.TIERS_PAYANT);
invoice.setTreatmentType(TreatmentType.AMBULATORY);
invoice.setTreatmentStart(LocalDate.of(2026, 1, 15));
invoice.setTreatmentEnd(LocalDate.of(2026, 1, 15));

ProviderInvoiceLine line = new ProviderInvoiceLine();
line.setTariffType(TariffType.TARMED);
line.setTariffCode("00.0010");
line.setServiceDescription("Konsultation, erste 5 Min.");
line.setQuantity(BigDecimal.ONE);
line.setTaxPoint(new BigDecimal("0.89"));  // Taxpunktwert
line.setUnitPrice(new BigDecimal("9.57"));
line.setLineAmount(Money.chf(9.57));

invoice.addLine(line);
invoice.setTotalAmount(Money.chf(9.57));
```

### Tiers Garant Invoice (Patient reimbursement)

```java
// Patient already paid provider, submits for reimbursement
ProviderInvoice invoice = new ProviderInvoice();
invoice.setPaymentModel(PaymentModel.TIERS_GARANT);
// ... rest is similar

// After approval, reimbursement goes to patient (minus cost sharing)
```

---

## Related Documentation

- [Claim](./claim.md) - Generated claims
- [PatientInvoice](./patient-invoice.md) - Cost sharing invoice to patient
- [Claims Processing Concept](../../concepts/claims-processing.md) - Business rules

---

*Last Updated: 2026-01-28*

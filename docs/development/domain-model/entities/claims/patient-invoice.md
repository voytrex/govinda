# PatientInvoice Entity

## Overview

The **PatientInvoice** (Patientenrechnung) represents a bill sent to the patient for their cost sharing (franchise and Selbstbehalt).

> **German**: Patientenrechnung, Kostenbeteiligung
> **Module**: `govinda-claims`
> **Status**: Planned

**Flow**: Claims with patient share -> PatientInvoice generated -> Payment or Dunning

---

## Entity Definition

```java
@Entity
@Table(name = "patient_invoices")
public class PatientInvoice {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    // Patient
    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;

    // Invoice details
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "period_from", nullable = false)
    private LocalDate periodFrom;

    @Column(name = "period_to", nullable = false)
    private LocalDate periodTo;

    // Amounts
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "franchise_total"))
    })
    private Money franchiseTotal;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "selbstbehalt_total"))
    })
    private Money selbstbehaltTotal;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount"))
    })
    private Money totalAmount;

    // Payments
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "paid_amount"))
    })
    private Money paidAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "open_amount"))
    })
    private Money openAmount;

    // Payment details
    @Column(name = "payment_reference")
    private String paymentReference;  // ESR/QR reference

    @Column(name = "qr_iban")
    private String qrIban;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatientInvoiceStatus status = PatientInvoiceStatus.DRAFT;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method")
    private DeliveryMethod deliveryMethod;

    // Dunning
    @Column(name = "dunning_level")
    private Integer dunningLevel = 0;

    @Column(name = "last_dunning_date")
    private LocalDate lastDunningDate;

    // Related claims
    @OneToMany(mappedBy = "patientInvoice")
    private List<PatientInvoiceLine> lines = new ArrayList<>();

    // Third-party payer (IPV, employer, etc.)
    @Column(name = "third_party_payer_id")
    private UUID thirdPartyPayerId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "third_party_amount"))
    })
    private Money thirdPartyAmount;

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

## PatientInvoiceLine Entity

```java
@Entity
@Table(name = "patient_invoice_lines")
public class PatientInvoiceLine {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_invoice_id", nullable = false)
    private PatientInvoice patientInvoice;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    // Source claim
    @Column(name = "claim_id", nullable = false)
    private UUID claimId;

    // Service info
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "service_description")
    private String serviceDescription;

    // Amounts
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "approved_amount"))
    })
    private Money approvedAmount;

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
}
```

---

## Field Reference

### PatientInvoice

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `invoiceNumber` | String | Required | Unique invoice number |
| `personId` | UUID | Required | Patient |
| `coverageId` | UUID | Required | KVG coverage |
| `invoiceDate` | LocalDate | Required | Invoice date |
| `dueDate` | LocalDate | Required | Payment due date |
| `periodFrom` | LocalDate | Required | Service period start |
| `periodTo` | LocalDate | Required | Service period end |
| `franchiseTotal` | Money | Required | Total franchise billed |
| `selbstbehaltTotal` | Money | Required | Total Selbstbehalt billed |
| `totalAmount` | Money | Required | Total patient share |
| `paidAmount` | Money | Required | Amount received |
| `openAmount` | Money | Required | Outstanding balance |
| `paymentReference` | String | Required | QR/ESR reference |
| `status` | PatientInvoiceStatus | Required | Invoice status |
| `dunningLevel` | Integer | Required | Current dunning stage |

---

## Related Enums

### PatientInvoiceStatus

```java
public enum PatientInvoiceStatus {
    DRAFT("Entwurf"),
    PENDING_REVIEW("Prüfung ausstehend"),
    APPROVED("Genehmigt"),
    SENT("Versendet"),
    PARTIALLY_PAID("Teilweise bezahlt"),
    PAID("Bezahlt"),
    OVERDUE("Überfällig"),
    IN_DUNNING("Im Mahnwesen"),
    IN_COLLECTION("Im Inkasso"),
    WRITTEN_OFF("Abgeschrieben"),
    CANCELLED("Storniert");
}
```

### DeliveryMethod

```java
public enum DeliveryMethod {
    POSTAL("Post"),
    EMAIL("E-Mail"),
    PORTAL("Kundenportal"),
    EBILL("eBill");
}
```

---

## Invoice Generation Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                   CLAIMS WITH PATIENT SHARE                     │
│     (franchise_applied > 0 OR selbstbehalt_applied > 0)         │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    INVOICE GENERATION                           │
│  • Group claims by person and period                            │
│  • Calculate totals                                             │
│  • Generate QR payment reference                                │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    THIRD-PARTY CHECK                            │
│  • Check for IPV (Prämienverbilligung)                          │
│  • Check for employer subsidy                                   │
│  • Check for social services coverage                           │
└─────────────────────────────┬───────────────────────────────────┘
                              │
            ┌─────────────────┴─────────────────┐
            │                                   │
            ▼                                   ▼
┌─────────────────────────┐        ┌─────────────────────────────┐
│   FULL PATIENT PAY      │        │    SPLIT PAYMENT            │
│   (no third party)      │        │    (IPV/employer pays part) │
└───────────┬─────────────┘        └─────────────┬───────────────┘
            │                                     │
            └────────────────┬────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                       SEND INVOICE                              │
│  • Postal mail                                                  │
│  • eBill                                                        │
│  • Customer portal                                              │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PAYMENT MONITORING                           │
│  • Match incoming payments (CAMT.054)                           │
│  • Update paid/open amounts                                     │
│  • Trigger dunning if overdue                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## Dunning Process

```
                    ┌──────────────────────────────────────┐
                    │           INVOICE SENT               │
                    │         (dunning_level = 0)          │
                    └─────────────────┬────────────────────┘
                                      │
                                      │ Payment due date +
                                      │ grace period passed
                                      ▼
                    ┌──────────────────────────────────────┐
                    │         REMINDER (Level 1)           │
                    │         (Zahlungserinnerung)         │
                    │         No fees added                │
                    └─────────────────┬────────────────────┘
                                      │
                                      │ +14 days, no payment
                                      ▼
                    ┌──────────────────────────────────────┐
                    │        1ST DUNNING (Level 2)         │
                    │        (1. Mahnung)                  │
                    │        + CHF 20 dunning fee          │
                    └─────────────────┬────────────────────┘
                                      │
                                      │ +14 days, no payment
                                      ▼
                    ┌──────────────────────────────────────┐
                    │        2ND DUNNING (Level 3)         │
                    │        (2. Mahnung)                  │
                    │        + CHF 30 dunning fee          │
                    └─────────────────┬────────────────────┘
                                      │
                                      │ +14 days, no payment
                                      ▼
                    ┌──────────────────────────────────────┐
                    │       FINAL WARNING (Level 4)        │
                    │       (Letzte Mahnung)               │
                    │       + CHF 40 fee                   │
                    │       Warning of legal action        │
                    └─────────────────┬────────────────────┘
                                      │
                                      │ +10 days, no payment
                                      ▼
                    ┌──────────────────────────────────────┐
                    │       COLLECTION (Level 5)           │
                    │       (Inkasso / Betreibung)         │
                    │       Transfer to collection agency  │
                    └──────────────────────────────────────┘
```

---

## Use Cases

### Generate Monthly Invoice

```java
@Service
public class PatientInvoiceService {

    public PatientInvoice generateInvoice(UUID personId, UUID coverageId,
                                          LocalDate periodFrom, LocalDate periodTo) {
        // Find claims with patient share
        List<Claim> claims = claimRepository.findByPersonAndPeriod(
            personId, periodFrom, periodTo, ClaimStatus.COST_SHARING_APPLIED
        );

        if (claims.isEmpty()) {
            return null;  // No invoice needed
        }

        PatientInvoice invoice = new PatientInvoice();
        invoice.setPersonId(personId);
        invoice.setCoverageId(coverageId);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setPeriodFrom(periodFrom);
        invoice.setPeriodTo(periodTo);

        Money franchiseTotal = Money.ZERO;
        Money selbstbehaltTotal = Money.ZERO;

        for (Claim claim : claims) {
            PatientInvoiceLine line = new PatientInvoiceLine();
            line.setClaimId(claim.getId());
            line.setServiceDate(claim.getServiceDate());
            line.setProviderName(claim.getProviderName());
            line.setServiceDescription(claim.getServiceDescription());
            line.setApprovedAmount(claim.getApprovedAmount());
            line.setFranchiseApplied(claim.getFranchiseApplied());
            line.setSelbstbehaltApplied(claim.getSelbstbehaltApplied());
            line.setPatientShare(claim.getPatientShare());

            franchiseTotal = franchiseTotal.add(claim.getFranchiseApplied());
            selbstbehaltTotal = selbstbehaltTotal.add(claim.getSelbstbehaltApplied());

            invoice.addLine(line);
        }

        invoice.setFranchiseTotal(franchiseTotal);
        invoice.setSelbstbehaltTotal(selbstbehaltTotal);
        invoice.setTotalAmount(franchiseTotal.add(selbstbehaltTotal));
        invoice.setOpenAmount(invoice.getTotalAmount());
        invoice.setPaidAmount(Money.ZERO);

        // Generate QR payment reference
        invoice.setPaymentReference(generateQrReference(invoice));

        return invoiceRepository.save(invoice);
    }
}
```

### Process Payment

```java
public void processPayment(String paymentReference, Money amount) {
    PatientInvoice invoice = invoiceRepository
        .findByPaymentReference(paymentReference)
        .orElseThrow();

    Money newPaid = invoice.getPaidAmount().add(amount);
    invoice.setPaidAmount(newPaid);
    invoice.setOpenAmount(invoice.getTotalAmount().subtract(newPaid));

    if (invoice.getOpenAmount().isZero()) {
        invoice.setStatus(PatientInvoiceStatus.PAID);
    } else if (invoice.getPaidAmount().isPositive()) {
        invoice.setStatus(PatientInvoiceStatus.PARTIALLY_PAID);
    }

    invoiceRepository.save(invoice);
}
```

### Apply IPV (Premium Subsidy)

```java
public void applyIpv(PatientInvoice invoice, BusinessPartner ipvProvider, Money ipvAmount) {
    invoice.setThirdPartyPayerId(ipvProvider.getId());
    invoice.setThirdPartyAmount(ipvAmount);

    // Reduce patient's open amount
    Money patientOwes = invoice.getTotalAmount().subtract(ipvAmount);
    invoice.setOpenAmount(patientOwes);

    // Create separate receivable for IPV provider
    createThirdPartyReceivable(ipvProvider, invoice, ipvAmount);
}
```

---

## QR-Bill Integration

```java
public String generateQrBillData(PatientInvoice invoice) {
    QrBill qrBill = QrBill.builder()
        .creditor(insurerCreditor)
        .debtor(Debtor.from(invoice.getPerson()))
        .amount(invoice.getOpenAmount().getAmount())
        .currency("CHF")
        .reference(SwissQrCode.createQrReference(invoice.getInvoiceNumber()))
        .additionalInfo(String.format("Kostenbeteiligung %s - %s",
            invoice.getPeriodFrom(), invoice.getPeriodTo()))
        .build();

    return qrBillGenerator.generatePaymentPart(qrBill);
}
```

---

## Related Documentation

- [Claim](./claim.md) - Source claims
- [CostSharingAccount](../contract/cost-sharing-account.md) - Franchise tracking
- [BusinessPartner](../masterdata/business-partner.md) - IPV payers
- [Dunning Concept](../../concepts/dunning-collection.md) - Collection process

---

*Last Updated: 2026-01-28*

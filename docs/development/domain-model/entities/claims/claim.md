# Claim Entity

## Overview

The **Claim** (Leistung) entity represents an approved healthcare service with cost sharing applied.

> **German**: Leistung, Leistungsabrechnung
> **Module**: `govinda-claims`
> **Status**: ⏳ Planned

**Flow**: Provider Invoice → Validation → Approval → **Claim** → Cost Sharing → Payment

---

## Entity Definition

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

    // Service
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
    private String diagnosisCode;  // ICD-10

    // Amounts - Invoiced
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "invoiced_amount"))
    })
    private Money invoicedAmount;

    // Amounts - Approved
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "approved_amount"))
    })
    private Money approvedAmount;

    // Cost Sharing
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

    // Insurer pays
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
    private boolean isMaternity = false;

    @Column(name = "is_accident")
    private boolean isAccident = false;

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
| `claimNumber` | String | ✅ | Unique claim identifier |
| `providerInvoiceId` | UUID | ❌ | Source invoice |
| `personId` | UUID | ✅ | Patient |
| `coverageId` | UUID | ✅ | KVG coverage |
| `providerGln` | String | ✅ | Provider GLN |
| `serviceDate` | LocalDate | ✅ | When service was provided |
| `tariffType` | TariffType | ❌ | TARMED, SwissDRG, etc. |
| `tariffCode` | String | ❌ | Tariff position code |
| `invoicedAmount` | Money | ✅ | Amount billed by provider |
| `approvedAmount` | Money | ❌ | Amount approved |
| `franchiseApplied` | Money | ❌ | Deductible applied |
| `selbstbehaltApplied` | Money | ❌ | Co-pay (10%) applied |
| `patientShare` | Money | ❌ | Total patient responsibility |
| `insurerPays` | Money | ❌ | Insurer reimbursement |
| `paymentModel` | PaymentModel | ✅ | Tiers Payant or Garant |
| `isMaternity` | boolean | ✅ | No cost sharing if true |

---

## Related Enums

### PaymentModel

```java
public enum PaymentModel {
    TIERS_GARANT("Tiers Garant"),   // Patient pays provider, gets reimbursed
    TIERS_PAYANT("Tiers Payant");   // Insurer pays provider directly
}
```

### ClaimStatus

```java
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
}
```

---

## Cost Sharing Calculation

```
IF claim.isMaternity THEN
    franchiseApplied = 0
    selbstbehaltApplied = 0
ELSE
    franchiseRemaining = account.franchiseAmount - account.franchiseUsed

    // Apply franchise first (100%)
    franchiseApplied = MIN(approvedAmount, franchiseRemaining)
    afterFranchise = approvedAmount - franchiseApplied

    // Apply Selbstbehalt (10%, max CHF 700/year)
    selbstbehaltRemaining = account.selbstbehaltMax - account.selbstbehaltUsed
    tenPercent = afterFranchise * 0.10
    selbstbehaltApplied = MIN(tenPercent, selbstbehaltRemaining)
END IF

patientShare = franchiseApplied + selbstbehaltApplied
insurerPays = approvedAmount - patientShare
```

---

## Flow Diagram

```
┌─────────────────┐     ┌───────────┐     ┌─────────┐
│ ProviderInvoice │ ──► │ Validation│ ──► │ Approval│
└─────────────────┘     └───────────┘     └────┬────┘
                                               │
                                               ▼
                                         ┌─────────┐
                                         │  Claim  │
                                         └────┬────┘
                                               │
                        ┌──────────────────────┼──────────────────────┐
                        ▼                      ▼                      ▼
                 ┌────────────┐        ┌─────────────┐        ┌──────────────┐
                 │ Franchise  │        │ Selbstbehalt│        │ Insurer Pays │
                 │  Applied   │        │   Applied   │        │              │
                 └────────────┘        └─────────────┘        └──────────────┘
                        │                      │                      │
                        └──────────┬───────────┘                      │
                                   ▼                                  ▼
                        ┌────────────────┐                  ┌─────────────────┐
                        │ Patient Invoice│                  │ Provider Payment│
                        │ (Cost Sharing) │                  │ (Tiers Payant)  │
                        └────────────────┘                  └─────────────────┘
```

---

## Use Cases

### Standard Ambulatory Claim (Tiers Payant)

```java
Claim claim = new Claim();
claim.setPersonId(patient.getId());
claim.setCoverageId(kvgCoverage.getId());
claim.setProviderGln("7601000123456");
claim.setServiceDate(LocalDate.of(2026, 3, 15));
claim.setTariffType(TariffType.TARMED);
claim.setTariffCode("00.0010");
claim.setInvoicedAmount(Money.chf(150.00));
claim.setApprovedAmount(Money.chf(150.00));
claim.setPaymentModel(PaymentModel.TIERS_PAYANT);

// After cost sharing
claim.setFranchiseApplied(Money.chf(150.00));  // If franchise not exhausted
claim.setSelbstbehaltApplied(Money.chf(0));
claim.setPatientShare(Money.chf(150.00));
claim.setInsurerPays(Money.chf(0));
```

### Maternity Claim (No Cost Sharing)

```java
Claim claim = new Claim();
claim.setIsMaternity(true);
claim.setInvoicedAmount(Money.chf(5000.00));
claim.setApprovedAmount(Money.chf(5000.00));

// No cost sharing for maternity
claim.setFranchiseApplied(Money.chf(0));
claim.setSelbstbehaltApplied(Money.chf(0));
claim.setPatientShare(Money.chf(0));
claim.setInsurerPays(Money.chf(5000.00));
```

---

## Related Documentation

- [ProviderInvoice](./provider-invoice.md) - Source invoice
- [CostSharingAccount](../contract/cost-sharing-account.md) - Franchise tracking
- [PatientInvoice](./patient-invoice.md) - Patient bill
- [Cost Sharing Concept](../../concepts/cost-sharing.md) - Business rules

---

*Last Updated: 2026-01-28*

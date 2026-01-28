# Claims Processing (Leistungsabrechnung)

## Overview

**Claims processing** covers the end-to-end workflow from receiving a healthcare provider invoice to paying benefits and billing the patient for their cost sharing.

> **German**: Leistungsabrechnung, Leistungsverarbeitung
> **French**: Traitement des prestations
> **Italian**: Elaborazione delle prestazioni

---

## High-Level Process Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         CLAIMS PROCESSING OVERVIEW                          │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│   RECEIVE   │──▶│   VALIDATE  │──▶│   APPROVE   │──▶│    CLAIM    │
│   Invoice   │   │   Invoice   │   │   Services  │   │   Created   │
└─────────────┘   └─────────────┘   └─────────────┘   └─────────────┘
                                                             │
                       ┌─────────────────────────────────────┘
                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          COST SHARING APPLICATION                           │
└─────────────────────────────────────────────────────────────────────────────┘
        │                        │                        │
        ▼                        ▼                        ▼
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│    Franchise    │      │  Selbstbehalt   │      │  Insurer Pays   │
│    Applied      │      │    Applied      │      │                 │
└─────────────────┘      └─────────────────┘      └─────────────────┘
        │                        │                        │
        └────────────┬───────────┘                        │
                     ▼                                    ▼
       ┌─────────────────────────┐          ┌─────────────────────────┐
       │    Patient Invoice      │          │   Provider Payment      │
       │   (Cost Sharing Bill)   │          │   (Tiers Payant)        │
       └─────────────────────────┘          └─────────────────────────┘
```

---

## Payment Models

### Tiers Payant (Third-Party Payer)

The insurer pays the provider directly:

```
┌──────────┐    Service     ┌──────────┐
│ Provider │ ─────────────▶ │ Patient  │
└────┬─────┘                └──────────┘
     │                             ▲
     │ Invoice                     │ Cost sharing
     ▼                             │ invoice
┌──────────┐    Payment     ┌──────┴─────┐
│ Insurer  │ ────────────▶  │  (same)    │
└──────────┘                └────────────┘
```

**Advantages:**
- Patient doesn't need to pre-pay large amounts
- Provider gets guaranteed payment from insurer
- Simpler for hospitals and expensive treatments

### Tiers Garant (Patient Guarantor)

The patient pays the provider, then gets reimbursed:

```
┌──────────┐    Service     ┌──────────┐
│ Provider │ ─────────────▶ │ Patient  │
└────┬─────┘                └────┬─────┘
     │                           │
     │ Invoice + Payment         │ Submits invoice
     │ ◀─────────────────        │ for reimbursement
     │                           ▼
     │                    ┌──────────┐
     │                    │ Insurer  │
     │                    └────┬─────┘
     │                         │
     │        Reimburses       │
     │  ◀──────────────────────┘
     │   (minus cost sharing)
```

**Advantages:**
- Traditional Swiss model
- Patient has more control over payment timing
- Works well for smaller ambulatory claims

---

## Invoice Channels

### Forum Datenaustausch (XML)

The industry standard for electronic invoice exchange in Swiss healthcare:

```xml
<!-- Simplified generalInvoiceRequest structure -->
<invoice:request
    xmlns:invoice="http://www.forum-datenaustausch.ch/invoice"
    role="physician">

    <invoice:header>
        <invoice:sender ean_party="7601000000001"/>
        <invoice:recipient ean_party="7601000000002"/>
    </invoice:header>

    <invoice:body>
        <invoice:patient gender="male" birthdate="1980-05-15">
            <invoice:card card_id="80756000000000000001"/>
        </invoice:patient>

        <invoice:services>
            <invoice:service
                tariff_type="001"
                code="00.0010"
                session="1"
                quantity="1"
                date_begin="2026-01-15"
                amount="12.50"/>
        </invoice:services>

        <invoice:balance
            amount="125.00"
            amount_due="125.00"
            currency="CHF"/>
    </invoice:body>
</invoice:request>
```

### EDI (Electronic Data Interchange)

Legacy format still used by some providers:

- EDIFACT messages
- Batch processing
- Being replaced by XML

### Manual Entry

For paper invoices or corrections:

- PDF/paper invoice scanned
- Manual data entry
- Additional review required

---

## Validation Stages

### Stage 1: Technical Validation

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        TECHNICAL VALIDATION                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  □ XML schema validation                                                │
│  □ Required fields present                                              │
│  □ Data types correct                                                   │
│  □ GLN format valid (13 digits, checksum)                               │
│  □ AHV number format valid (756.XXXX.XXXX.XX)                           │
│  □ Dates logical (treatment_start <= treatment_end)                     │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Stage 2: Business Validation

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        BUSINESS VALIDATION                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  □ Provider GLN exists in Refdata/MedReg                                │
│  □ Provider is authorized for tariff type                               │
│  □ Patient exists in system (VeKa lookup)                               │
│  □ Coverage active on treatment dates                                   │
│  □ Tariff codes valid (Tarifpool lookup)                                │
│  □ Prices within allowed limits                                         │
│  □ No duplicate invoice submission                                      │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Stage 3: Medical Plausibility (Sumex)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      MEDICAL PLAUSIBILITY                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Sumex rules engine checks:                                             │
│                                                                         │
│  □ Service combinations valid                                           │
│  □ Quantity limits respected                                            │
│  □ Age/gender appropriate                                               │
│  □ Diagnosis-procedure matching                                         │
│  □ Cumulation rules (services that can/cannot be combined)              │
│  □ Time-based rules (intervals between services)                        │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Tariff Systems

### TARMED (until 2026)

Ambulatory medical services tariff:

| Component | Description |
|-----------|-------------|
| **Code** | 5-digit + 2-digit position (e.g., 00.0010) |
| **AL** | Medical component (Arztleistung) |
| **TL** | Technical component (Technische Leistung) |
| **Taxpunktwert** | Canton-specific point value (CHF 0.83-1.00) |

```
Amount = (AL_points × AL_factor + TL_points × TL_factor) × Taxpunktwert
```

### TARDOC (from 2026)

Replacement for TARMED with updated structure:

- New service definitions
- Revised pricing model
- Better alignment with modern medicine

### SwissDRG

Diagnosis-Related Groups for inpatient care:

- Flat-rate per case
- Based on diagnosis, procedures, length of stay
- Annually updated cost weights

---

## External Integrations

### SASIS AG

VeKa (Versichertenkarte) validation:

```java
public class SasisService {
    public VekaValidationResult validateCard(String cardNumber) {
        // Query SASIS database
        // Returns: isValid, personId, coverageInfo
    }
}
```

### Refdata

Provider master data:

```java
public class RefdataService {
    public Provider lookupByGln(String gln) {
        // Query Refdata
        // Returns: provider name, type, authorizations
    }
}
```

### Tarifpool

Tariff validation:

```java
public class TarifpoolService {
    public TariffPosition validateCode(TariffType type, String code, LocalDate date) {
        // Query Tarifpool
        // Returns: isValid, maxPrice, restrictions
    }
}
```

### Datenpool

Industry statistics and benchmarks:

- Anonymous claims data
- Used for cost analysis
- Quality metrics

---

## Claim Generation

After invoice approval, individual claims are created:

```java
@Service
public class ClaimGenerationService {

    @Transactional
    public List<Claim> generateClaims(ProviderInvoice invoice) {
        List<Claim> claims = new ArrayList<>();

        for (ProviderInvoiceLine line : invoice.getApprovedLines()) {
            Claim claim = new Claim();
            claim.setTenantId(invoice.getTenantId());
            claim.setProviderInvoiceId(invoice.getId());
            claim.setProviderInvoiceLineId(line.getId());
            claim.setPersonId(invoice.getPersonId());
            claim.setCoverageId(invoice.getCoverageId());
            claim.setProviderGln(invoice.getProviderGln());
            claim.setServiceDate(line.getServiceDate());
            claim.setTariffType(line.getTariffType());
            claim.setTariffCode(line.getTariffCode());
            claim.setInvoicedAmount(line.getLineAmount());
            claim.setApprovedAmount(line.getApprovedAmount());
            claim.setPaymentModel(invoice.getPaymentModel());
            claim.setStatus(ClaimStatus.PENDING);

            claims.add(claimRepository.save(claim));
        }

        // Apply cost sharing to each claim
        for (Claim claim : claims) {
            costSharingService.applyCostSharing(claim);
        }

        return claims;
    }
}
```

---

## Payment Processing

### Provider Payment (Tiers Payant)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        PROVIDER PAYMENT                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  1. Aggregate approved claims per provider                              │
│  2. Calculate total amount due                                          │
│  3. Generate payment file (pain.001 ISO 20022)                          │
│  4. Submit to bank                                                      │
│  5. Receive confirmation (pain.002)                                     │
│  6. Mark claims as paid                                                 │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Patient Reimbursement (Tiers Garant)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      PATIENT REIMBURSEMENT                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  1. Sum of approved amounts                                             │
│  2. Subtract cost sharing (franchise + Selbstbehalt)                    │
│  3. Generate payment to patient's bank account                          │
│  4. Patient still owes provider full amount                             │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Status Flow

### ProviderInvoice Status

```
RECEIVED
    │
    ▼
VALIDATING ─────────▶ VALIDATION_FAILED
    │                        │
    │                        ▼
    │                   (returned to provider)
    ▼
PENDING_REVIEW
    │
    ├──────────────────┬────────────────────┐
    ▼                  ▼                    ▼
APPROVED        PARTIALLY_APPROVED      REJECTED
    │                  │
    └────────┬─────────┘
             ▼
    CLAIMS_GENERATED
             │
             ▼
          PAID
```

### Claim Status

```
PENDING
    │
    ▼
PROCESSING
    │
    ├──────────────────┬────────────────────┐
    ▼                  ▼                    ▼
APPROVED           ADJUSTED            REJECTED
    │                  │
    └────────┬─────────┘
             ▼
COST_SHARING_APPLIED
             │
             ▼
    READY_FOR_PAYMENT
             │
             ▼
          PAID
             │
             ▼
         CLOSED
```

---

## Error Handling

### Common Validation Errors

| Error Code | Description | Resolution |
|------------|-------------|------------|
| `GLN_INVALID` | Provider GLN not found | Check Refdata, contact provider |
| `PATIENT_NOT_FOUND` | VeKa number unknown | Manual patient matching |
| `COVERAGE_INACTIVE` | No active coverage on date | Check policy dates |
| `TARIFF_INVALID` | Unknown tariff code | Use valid code from Tarifpool |
| `PRICE_EXCEEDED` | Price exceeds tariff max | Adjust or reject |
| `DUPLICATE` | Invoice already processed | Reject as duplicate |

### Dispute Handling

```java
public enum DisputeReason {
    PRICE_DISAGREEMENT("Preis-Uneinigkeit"),
    SERVICE_NOT_RENDERED("Leistung nicht erbracht"),
    DUPLICATE_BILLING("Doppelte Abrechnung"),
    COVERAGE_DISPUTE("Deckungsstreit"),
    PATIENT_DISPUTE("Patientenreklamation");
}
```

---

## Reporting

### Key Metrics

| Metric | Description |
|--------|-------------|
| **Claims volume** | Total claims processed per period |
| **Auto-approval rate** | % of claims approved without manual review |
| **Average processing time** | Days from receipt to payment |
| **Rejection rate** | % of claims rejected |
| **Cost per claim** | Processing cost per claim |

---

## Related Documentation

- [ProviderInvoice Entity](../entities/claims/provider-invoice.md) - Invoice details
- [Claim Entity](../entities/claims/claim.md) - Claim details
- [PatientInvoice Entity](../entities/claims/patient-invoice.md) - Patient billing
- [Cost Sharing](./cost-sharing.md) - Cost sharing rules
- [CostSharingAccount Entity](../entities/contract/cost-sharing-account.md) - Tracking

---

*Last Updated: 2026-01-28*

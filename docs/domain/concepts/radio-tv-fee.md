# Swiss Radio and Television Fee (RTVG)

## Overview

The Swiss radio and television fee (Radio- und Fernsehgebühr) is a mandatory contribution for funding public broadcasting (SRG SSR), regulated under the RTVG (Radio- und Fernsehgesetz). This is a federal obligation for all Swiss households and qualifying businesses.

> **German**: Radio- und Fernsehgebühr (RaFe)
> **French**: Redevance de radio-télévision
> **Italian**: Canone radiotelevisivo
> **Legal Name**: Abgabe für Radio und Fernsehen

---

## Regulatory Framework

| Aspect | Details |
|--------|---------|
| **Constitutional Basis** | Art. 93 (Radio and Television), Art. 69b (Media Fee) |
| **Primary Law** | **RTVG** - Bundesgesetz über Radio und Fernsehen (SR 784.40) |
| **Ordinance** | **RTVO** - Radio- und Fernsehverordnung (SR 784.401) |
| **Supervising Authority** | **BAKOM** - Bundesamt für Kommunikation |
| **Household Fee Collector** | Contracted collection agency (currently Serafe AG) |
| **Business Fee Collector** | **ESTV** - Eidgenössische Steuerverwaltung |

### Regulatory History

| Date | Change |
|------|--------|
| 2019-01-01 | Device-independent fee introduced (replaces device-based) |
| 2021-01-01 | Current fee amounts effective |
| 2024-01-01 | Opting-out for device-free households abolished |

> **Note**: The collection agency is a contracted service provider, not the regulatory authority. BAKOM sets the rules; the collection agency executes billing.

---

## Fee Categories

### Private Households (Haushaltsabgabe)

All private households in Switzerland are liable for the fee, regardless of device ownership.

| Category | Annual Fee (CHF) | Billing |
|----------|------------------|---------|
| **Standard Household** | 335.00 | Annual default; quarterly option available |
| **Collective Household** | 670.00 | Annual default; quarterly option available |

**Collective Households** (Kollektivhaushalte) are billed at the collective rate.

**Quarterly option**: Serafe offers quarterly invoices for private households; an administration fee applies per invoice.

### Businesses (Unternehmensabgabe)

Collected by ESTV (Federal Tax Administration).

**Threshold**: VAT-registered businesses with annual turnover >= CHF 500,000 (turnover without VAT).

| Tier | Turnover Range (CHF) | Annual Fee (CHF) |
|------|----------------------|------------------|
| 1 | 500,000 - 749,999 | 160 |
| 2 | 750,000 - 1,199,999 | 235 |
| 3 | 1,200,000 - 1,699,999 | 325 |
| 4 | 1,700,000 - 2,499,999 | 460 |
| 5 | 2,500,000 - 3,599,999 | 645 |
| 6 | 3,600,000 - 5,099,999 | 905 |
| 7 | 5,100,000 - 7,299,999 | 1,270 |
| 8 | 7,300,000 - 10,399,999 | 1,785 |
| 9 | 10,400,000 - 14,999,999 | 2,505 |
| 10 | 15,000,000 - 22,999,999 | 3,315 |
| 11 | 23,000,000 - 32,999,999 | 4,935 |
| 12 | 33,000,000 - 49,999,999 | 6,925 |
| 13 | 50,000,000 - 89,999,999 | 9,725 |
| 14 | 90,000,000 - 179,999,999 | 13,665 |
| 15 | 180,000,000 - 399,999,999 | 19,170 |
| 16 | 400,000,000 - 699,999,999 | 26,915 |
| 17 | 700,000,000 - 999,999,999 | 37,790 |
| 18 | 1,000,000,000+ | 49,925 |

**Notes**:
- Turnover = total without VAT, including exports and exempt supplies
- Sole proprietors pay household fee separately, plus business fee if applicable
- Fee liability begins year after first exceeding threshold

---

## Exemptions (Befreiungen)

### Full Exemptions

| Category | Requirement | Duration | Legal Basis |
|----------|-------------|----------|-------------|
| **EL Recipients** | Receives annual Ergänzungsleistungen (AHV/IV supplements) and applies | While eligible (verification per authority) | RTVO Art. 61 |
| **Deaf-Blind Persons** | Deaf-blind person applies and **no other fee-liable person lives in the same household** | While condition applies | RTVO Art. 61 |
| **Diplomatic Staff** | FDFA data exchange (Ordipro), no application required | Duration of diplomatic status | RTVO Art. 61 |

### Exemption Application Process

1. **EL Recipients**: Submit confirmation of supplementary benefits to Serafe
2. **Deaf-Blind**: Submit medical certificate + application form; only if no other fee-liable person lives in the household
3. **Diplomatic**: Automatic via FDFA/Ordipro data exchange

---

## Household Definition

### Private Household (Privathaushalt)

A private household consists of:
- All persons registered at the same address in municipal registers
- All adult members are jointly liable for payment
- One invoice per household (not per person)

### Shared Housing (Wohngemeinschaft)

- Treated as single household
- All adult roommates listed on invoice
- Internal cost splitting is private matter

### Multi-Generational Living

- Separate households if separate entrances and facilities
- Combined if sharing main living facilities

> **Note**: Household boundary rules should be verified against official register guidance before implementation.

### Collective Households (Kollektivhaushalte)

Collective households are residential institutions housing multiple unrelated persons and are billed at the collective household fee.

---

## Comparison: Healthcare vs. Broadcast Fee

| Aspect | Health Insurance (KVG) | Broadcast Fee (RTVG) |
|--------|------------------------|----------------------|
| **Legal Basis** | KVG (SR 832.10) | RTVG (SR 784.40) |
| **Regulator** | BAG | BAKOM |
| **Mandatory** | Yes, all residents | Yes, all households |
| **Subscriber Unit** | Individual person | Household or business |
| **Pricing Model** | Region + Age + Franchise + Model | Flat (household) or Tiered (business) |
| **Regional Variation** | Yes (premium regions) | No (nationwide uniform) |
| **Age Variation** | Yes (3 age groups) | No |
| **Gender Variation** | No (KVG) / Yes (VVG) | No |
| **Full Exemptions** | None | EL recipients, deaf-blind, diplomatic |
| **Reductions Available** | Franchise choice, model choice | None for households |
| **Billing Frequency** | Monthly/Quarterly/Semi/Annual | Annual (quarterly installments) |
| **Appeals** | Cantonal insurance courts | Federal Administrative Court |

---

## Business Rules for Implementation

### Household Fee

```
IF household.type == PRIVATE THEN
    fee = CHF 335.00 / year
ELSE IF household.type == COLLECTIVE THEN
    fee = CHF 670.00 / year
END IF

// Check exemptions
IF ANY household.member has EL_RECIPIENT status AND application approved THEN
    fee = CHF 0.00
ELSE IF ANY household.member is DEAF_BLIND AND NO OTHER fee-liable person lives in household THEN
    fee = CHF 0.00
ELSE IF ANY household.member has DIPLOMATIC_STATUS THEN
    fee = CHF 0.00
END IF
```

### Business Fee

```
IF organization.vatRegistered == FALSE THEN
    fee = CHF 0.00
ELSE IF organization.annualTurnover < CHF 500,000 THEN
    fee = CHF 0.00
ELSE
    tier = determineTier(organization.annualTurnover)
    fee = TIER_TABLE[tier].annualFee
END IF

// Special case: sole proprietor
IF organization.type == SOLE_PROPRIETORSHIP THEN
    // Also pays household fee separately
    totalFee = businessFee + householdFee
END IF
```

### Fee Determination Timing

```
// Business fee liability begins year after first exceeding threshold
IF year(now) == year(firstExceededThreshold) + 1 THEN
    beginFeeLiability()
END IF

// Household fee begins with residency registration
IF person.registeredInMunicipality == TRUE THEN
    beginHouseholdFeeLiability()
END IF
```

---

## Data Integration

### Household Fee

The collection agency receives data from:
- Cantonal and municipal resident registers (Einwohnerregister)
- Automatic matching of household members

### Business Fee (ESTV)

ESTV determines liability from:
- VAT register (MWST-Register)
- Business turnover declarations
- Commercial register (Handelsregister)

---

## Enforcement

### Non-Payment Consequences

| Stage | Action |
|-------|--------|
| 1. Reminder | Payment reminder sent |
| 2. Final Notice | Formal demand with deadline |
| 3. Debt Collection | Betreibungsamt (debt enforcement) |
| 4. Legal Action | Court proceedings if disputed |

---

## Domain Model Mapping

| RTVG Concept | Domain Entity | Notes |
|--------------|---------------|-------|
| Privathaushalt | `Household` (type=PRIVATE) | Standard household |
| Kollektivhaushalt | `Household` (type=COLLECTIVE) | Institutions |
| Unternehmen | `Organization` | Businesses |
| Haushaltsabgabe | `Subscription` (domain=BROADCAST) | Household fee |
| Unternehmensabgabe | `Subscription` (domain=BROADCAST) | Business fee |
| EL-Befreiung | `Exemption` (reason=AHV_IV_SUPPLEMENT) | Supplementary benefits exemption |
| Taubblind-Befreiung | `Exemption` (reason=DEAF_BLIND) | Medical exemption |

---

## Official Resources

| Resource | URL |
|----------|-----|
| BAKOM Fee Overview | https://www.bakom.admin.ch/bakom/en/homepage/electronic-media/radio-and-television-fee.html |
| Serafe Fee Overview | https://www.serafe.ch/en/the-fee/fee-overview/ |
| Serafe EL Exemption | https://www.serafe.ch/en/exemption-from-the-fee/people-receiving-supplementary-benefits/ |
| Serafe Deaf-Blind Exemption | https://www.serafe.ch/en/exemption-from-the-fee/deaf-blind-households/ |
| Serafe Diplomatic Exemption | https://www.serafe.ch/en/exemption-from-the-fee/diplomat-households/ |
| Serafe Opt-out End (2024) | https://www.serafe.ch/en/exemption-from-the-fee/households-with-no-means-of-receiving-radio-or-television/ |
| ESTV Corporate Fee Overview | https://www.estv.admin.ch/estv/en/home/federal-taxes/corporate-fee-for-radio-and-television.html |
| ESTV Tariff Categories | https://www.estv.admin.ch/estv/en/home/federal-taxes/corporate-fee-for-radio-and-television/tariff-categories.html |

---

## Scope Assumptions (To Verify)

- The internal list of collective household subtypes (elderly homes, prisons, etc.) should be verified against BAKOM/OFS definitions before coding.
- Any duration or re-verification cadence for exemptions must be sourced from RTVO or official guidance before implementation.

---

*Last Updated: 2026-01-28*

# VVG - Supplementary Insurance

## Overview

**Versicherungsvertragsgesetz (VVG)** supplementary insurance provides coverage beyond the mandatory KVG basic insurance. Unlike KVG, VVG products are governed by private law, allowing insurers to set their own terms, pricing, and acceptance criteria.

> **German**: Zusatzversicherung
> **French**: Assurance complémentaire
> **Italian**: Assicurazione complementare

---

## Key Differences from KVG

| Aspect | KVG (Basic) | VVG (Supplementary) |
|--------|-------------|---------------------|
| **Legal Basis** | Public law (KVG) | Private law (VVG) |
| **Requirement** | Mandatory | Voluntary |
| **Coverage** | Standardized by law | Varies by product |
| **Acceptance** | Must accept everyone | Can refuse applicants |
| **Health Questions** | Not allowed | Required |
| **Waiting Periods** | Not allowed | Common |
| **Exclusions** | Not allowed | Pre-existing conditions |
| **Gender Pricing** | ❌ Unisex only | ✅ Allowed |
| **Cancellation** | Highly restricted | Per contract terms |

---

## Legal Basis

📋 **Law**: [Bundesgesetz über den Versicherungsvertrag (VVG)](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/de)

**Key Provisions**:
- Art. 3-8 VVG: Disclosure obligations (Anzeigepflicht)
- Art. 35-41 VVG: Contract termination
- Art. 98-101 VVG: Claims handling

---

## Product Categories

### Hospital Supplementary (Spitalzusatzversicherung)

| Product | Ward Type | Coverage |
|---------|-----------|----------|
| **Spital Allgemein CH** | General | All Swiss hospitals |
| **Spital Halbprivat** | Semi-private | 2-bed room, chief physician |
| **Spital Privat** | Private | Single room, choice of doctor |
| **Spital Flex** | Flexible | Choose ward at admission |

**Benefits**:
- Free choice of hospital throughout Switzerland
- Private or semi-private rooms
- Treatment by chief physician
- Additional comfort services

### Dental Insurance (Zahnversicherung)

| Coverage Type | Typical Benefits |
|---------------|------------------|
| **Basic Dental** | Check-ups, cleaning, fillings (50-75%) |
| **Comprehensive** | Crowns, bridges, implants (50-75%) |
| **Orthodontics** | Braces for children (50-75%) |

⚠️ **Note**: Usually requires signing up before age 3-5 for orthodontic coverage without health check.

### Alternative Medicine (Alternativmedizin/Komplementärmedizin)

Covers therapies not fully included in KVG:
- Acupuncture
- Homeopathy
- Traditional Chinese Medicine (TCM)
- Naturopathy
- Osteopathy

### Travel/Abroad Insurance (Auslandversicherung)

- Emergency treatment beyond KVG limits
- Repatriation costs
- Extended coverage for long stays abroad
- Search and rescue operations

### Daily Allowance (Taggeldversicherung)

- Income replacement during illness
- Typically 80% of salary
- After waiting period (e.g., 30, 60, 90 days)
- Duration: usually 720-730 days

---

## Underwriting Process

### Health Declaration (Gesundheitserklärung)

Applicants must answer health questions honestly:

```
┌─────────────────────────────────────────────────────────────┐
│              HEALTH DECLARATION PROCESS                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Application ──▶ Health Questions ──▶ Insurer Review       │
│                                              │              │
│                                              ▼              │
│                          ┌─────────────────────────────┐   │
│                          │ DECISION                    │   │
│                          ├─────────────────────────────┤   │
│                          │ ✅ Accept                   │   │
│                          │ ⚠️ Accept with exclusions   │   │
│                          │ ⚠️ Accept with surcharge    │   │
│                          │ ❌ Reject                   │   │
│                          └─────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Disclosure Obligation (Anzeigepflicht)

📋 **Art. 6 VVG**: Applicant must truthfully answer all questions relevant to risk assessment.

**Consequences of Non-Disclosure**:
- Within 4 weeks of discovery: Insurer can void contract
- Claims may be denied
- No premium refund

### Common Exclusions

| Type | Example |
|------|---------|
| **Pre-existing conditions** | Back problems, mental health |
| **Waiting periods** | 9-12 months for maternity |
| **Age limits** | Some products not available after 60 |

---

## Premium Structure

### Premium Factors (VVG)

| Factor | Impact | Notes |
|--------|--------|-------|
| **Age** | Higher premiums with age | Age at entry matters |
| **Gender** | Different rates allowed | Based on actuarial data |
| **Premium Region** | Regional differences | Similar to KVG |
| **Health Status** | Via exclusions/surcharges | At underwriting |
| **Product Choice** | Varies by coverage level | Private > Semi-private > General |

### Gender-Based Pricing

Unlike KVG, VVG allows gender-differentiated premiums based on actuarial risk:

```
Example: Hospital Supplementary (Adult, Region ZH-1)

Female, Age 35: CHF  85/month
Male, Age 35:   CHF  72/month
                ─────────────
Difference:     CHF  13/month (15% higher for female)

Reason: Statistically higher healthcare utilization
        (including maternity-related hospitalizations)
```

⚠️ **Note**: Gender pricing is product-specific. Some insurers offer unisex VVG products.

### Premium Entry Dimensions (VVG)

```java
PremiumEntry (VVG)
├── premiumRegionId    // Regional pricing
├── ageGroup           // CHILD, YOUNG_ADULT, ADULT
├── gender             // MALE, FEMALE (optional, for risk-based pricing)
└── monthlyAmount      // Premium in CHF
// Note: NO franchise (VVG products typically don't have deductibles)
```

---

## Contract Terms

### Contract Duration

| Type | Duration | Renewal |
|------|----------|---------|
| **Standard** | 1-5 years | Auto-renewal |
| **Lifelong** | Until death | Cannot be cancelled by insurer |

### Termination

**By Insured Person**:
- Notice period: Usually 3 months before term end
- Some products: Monthly termination possible

**By Insurer**:
- End of contract period (rare)
- After claim (some products allow)
- Cannot terminate lifelong products

### Premium Adjustments

Insurers can adjust VVG premiums:
- Due to general cost increases
- At contract renewal
- Must notify in advance (usually 30-60 days)

⚠️ **Right to Terminate**: Premium increases usually trigger special termination right.

---

## VVG + KVG Relationship

### Independent Products

Most VVG products are independent of KVG:
- Can be with different insurer than KVG
- Own contract terms and premiums
- Separate claims handling

### Linked Products

Some insurers offer combined KVG/VVG packages:
- Discounts for bundling
- Single invoice option
- Linked termination clauses

⚠️ **Caution**: Changing KVG insurer may affect linked VVG products.

---

## Common VVG Products Summary

| Code | Category | Description |
|------|----------|-------------|
| `HOSP` | Hospital | Spital supplementary (general/semi-private/private) |
| `DENT` | Dental | Dental treatments and orthodontics |
| `ALT` | Alternative | Complementary medicine |
| `TRAV` | Travel | Extended coverage abroad |
| `TAGG` | Daily Allowance | Sick pay (Krankentaggeld) |

---

## Code References

| Concept | Implementation |
|---------|----------------|
| Product Type | `ProductType.VVG` |
| Categories | `ProductCategory.HOSPITAL`, `.DENTAL`, `.ALTERNATIVE`, `.TRAVEL`, `.DAILY_ALLOWANCE` |
| Gender | `Gender.MALE`, `Gender.FEMALE` (in PremiumEntry) |
| Age Groups | `AgeGroup.CHILD`, `AgeGroup.YOUNG_ADULT`, `AgeGroup.ADULT` |

### Product Entity

```java
Product (VVG)
├── code               // e.g., "SPITAL_PRIVAT_2024"
├── category           // ProductCategory.HOSPITAL
├── insuranceModel     // null (VVG doesn't use models)
├── name               // LocalizedText
└── description        // LocalizedText
```

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ Health declaration required | Must answer questions truthfully |
| ⚠️ Exclusions possible | Pre-existing conditions can be excluded |
| ⚠️ Gender pricing allowed | Premiums may differ by gender |
| ⚠️ No franchise | VVG typically has no annual deductible |
| ⚠️ Waiting periods | Common for specific benefits |
| ⚠️ Can be refused | Insurers may reject applications |

---

## Official Resources

| Resource | URL |
|----------|-----|
| VVG Full Text | [fedlex.admin.ch/eli/cc/24/719_735_717/de](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/de) |
| FINMA (Supervision) | [finma.ch](https://www.finma.ch) |
| Ombudsman | [ombudsman-assurance.ch](https://www.ombudsman-assurance.ch) |

---

## Related Documentation

- [KVG - Mandatory Insurance](./kvg-mandatory-insurance.md)
- [Swiss Healthcare System](./swiss-healthcare-system.md)
- [Premium Regions](./premium-regions.md)
- [Age Groups](./age-groups.md)

---

*Last Updated: 2026-01-26*

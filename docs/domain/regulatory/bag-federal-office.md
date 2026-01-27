# BAG - Federal Office of Public Health

## Overview

The **Bundesamt für Gesundheit (BAG)** is the Swiss federal authority responsible for public health, including the regulation and supervision of mandatory health insurance (KVG). Understanding BAG's role is essential for compliance in health insurance software systems.

> **German**: Bundesamt für Gesundheit (BAG)
> **French**: Office fédéral de la santé publique (OFSP)
> **Italian**: Ufficio federale della sanità pubblica (UFSP)
> **English**: Federal Office of Public Health (FOPH)

📋 **Official Website**: [bag.admin.ch](https://www.bag.admin.ch)

---

## Key Responsibilities

### 1. Premium Approval

BAG approves all KVG premiums before they can be charged to customers.

```
┌─────────────────────────────────────────────────────────────────┐
│              PREMIUM APPROVAL PROCESS                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   April-June: Insurers submit proposed premiums                │
│        │                                                        │
│        ▼                                                        │
│   July-August: BAG reviews calculations                        │
│        │      - Actuarial soundness                            │
│        │      - Reserve requirements                           │
│        │      - Regional consistency                           │
│        ▼                                                        │
│   Late September: BAG approves final premiums                  │
│        │                                                        │
│        ▼                                                        │
│   October: Insurers announce approved premiums                 │
│        │                                                        │
│        ▼                                                        │
│   January 1: New premiums take effect                          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Key Rules**:
- Insurers cannot charge more than approved amounts
- Premiums must be actuarially sound
- Cross-subsidization between products limited

### 2. Premium Region Definition

BAG defines the geographic premium regions for all of Switzerland.

| Aspect | Details |
|--------|---------|
| **Coverage** | All 26 cantons |
| **Regions per canton** | 1 to 3 |
| **Basis** | Healthcare cost differences |
| **Update frequency** | Rarely changed |

**Data Provided**:
- Region codes (e.g., ZH-1, BE-2)
- Postal code to region mapping
- Region names (multilingual)

### 3. Benefits Catalog Management

BAG maintains the lists of covered services and products.

| Catalog | Purpose | Update Frequency |
|---------|---------|------------------|
| **Spezialitätenliste (SL)** | Covered medications | Monthly |
| **MiGeL** | Medical aids and devices | Quarterly |
| **Analysenliste (AL)** | Laboratory tests | Annually |

### 4. Insurer Supervision

Together with FINMA, BAG supervises health insurers:

| BAG Responsibility | FINMA Responsibility |
|-------------------|----------------------|
| KVG compliance | Financial solvency |
| Premium adequacy | Reserve requirements |
| Service quality | Risk management |
| Statistics collection | Audit compliance |

---

## Data Feeds and Interfaces

### BAG Data Publications

| Data | Format | Frequency | URL |
|------|--------|-----------|-----|
| Premium regions | PDF/Excel | Annual | [bag.admin.ch](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html) |
| Approved premiums | Online database | Annual | [priminfo.admin.ch](https://www.priminfo.admin.ch) |
| Insurer list | PDF | Updated as needed | BAG website |
| Statistics | Excel/PDF | Annual | BAG statistics section |

### Priminfo API

The official premium comparison portal provides data access:

📋 **Portal**: [priminfo.admin.ch](https://www.priminfo.admin.ch)

**Features**:
- Search premiums by parameters
- Compare insurers
- Download data exports

### Integration Considerations

```java
/**
 * BAG data integration points for Govinda
 */
public class BagDataIntegration {

    // Premium Region Management
    // - Import BAG PLZ-to-Region mapping annually
    // - Validate addresses against BAG regions
    // - Update when BAG publishes changes

    // Premium Validation
    // - Imported tariffs should match BAG-approved premiums
    // - Validate premium amounts are within approved limits

    // Catalog Integration (future)
    // - SL import for medication coverage checking
    // - MiGeL import for medical aids
}
```

---

## Reporting Requirements

### Mandatory Statistics

Insurers must report various statistics to BAG:

| Report | Content | Frequency |
|--------|---------|-----------|
| **SASIS KVG** | Insured population, premiums | Annual |
| **Cost statistics** | Claims data by category | Annual |
| **Reserve reporting** | Financial reserves | Quarterly |

### Data Format Standards

| Standard | Purpose |
|----------|---------|
| **eCH-0021** | Person identification |
| **eCH-0010** | Postal address format |
| **Sumex XML** | Claims data exchange |

---

## Compliance Requirements

### For Software Systems

| Requirement | Description | Implementation |
|-------------|-------------|----------------|
| **Unisex premiums** | Art. 61 KVG - no gender pricing | `PremiumEntry` has no gender for KVG |
| **Correct franchise levels** | Art. 64 KVG | `Franchise` enum with valid options |
| **Regional pricing** | Art. 61 KVG | `PremiumRegion` from BAG data |
| **Age categories** | Art. 61 KVG | `AgeGroup` with correct age ranges |
| **Acceptance obligation** | Art. 4 KVG | No health checks for KVG |

### Audit Trail Requirements

```
┌─────────────────────────────────────────────────────────────────┐
│              AUDIT REQUIREMENTS                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   WHAT TO LOG:                                                  │
│   ✅ All coverage changes                                       │
│   ✅ Premium calculations                                       │
│   ✅ Terminations and new enrollments                          │
│   ✅ Address changes affecting premium region                   │
│   ✅ User actions on sensitive data                            │
│                                                                 │
│   RETENTION:                                                    │
│   📋 Financial records: 10 years                               │
│   📋 Coverage history: Duration of coverage + 10 years         │
│   📋 Audit logs: 5 years minimum                               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## BAG Contact and Resources

### Official Channels

| Purpose | Contact |
|---------|---------|
| **General inquiries** | [bag.admin.ch/kontakt](https://www.bag.admin.ch/bag/de/home/das-bag/kontakt.html) |
| **Premium questions** | krankenversicherung@bag.admin.ch |
| **Statistics** | statistics-kvg@bag.admin.ch |

### Key Publications

| Publication | Description | URL |
|-------------|-------------|-----|
| **KVG Law Text** | Official law | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/de) |
| **KVV Ordinance** | Implementation rules | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/1995/3867_3867_3867/de) |
| **Annual Report** | BAG statistics | BAG website |
| **Circulars** | Implementation guidance | BAG website |

### News and Updates

📋 **Announcements**: [bag.admin.ch/aktuell](https://www.bag.admin.ch/bag/de/home/das-bag/aktuell/medienmitteilungen.html)

Subscribe to BAG newsletters for:
- Premium approval announcements
- Law changes
- Catalog updates
- Statistics releases

---

## Integration Roadmap

### Current Implementation

| Feature | Status |
|---------|--------|
| Premium regions (Canton enum) | ✅ Implemented |
| Age groups | ✅ Implemented |
| Franchise levels | ✅ Implemented |
| Unisex KVG pricing | ✅ Implemented |

### Planned Integration

| Feature | Priority | Notes |
|---------|----------|-------|
| PLZ-to-Region mapping | P1 | Import BAG data |
| Premium validation | P2 | Validate against approved rates |
| SL integration | P3 | Future - benefits module |
| Statistics export | P3 | Future - reporting module |

---

## Code References

| Concept | Implementation |
|---------|----------------|
| Premium Regions | `Canton` enum, `PremiumRegion` entity (planned) |
| Age Groups | `AgeGroup` enum |
| Franchise | `Franchise` enum |
| Unisex Pricing | `PremiumEntry` - no gender for KVG |

---

## Related Documentation

- [KVG Law Requirements](./kvg-law-requirements.md)
- [Premium Regions Concept](../concepts/premium-regions.md)
- [Franchise System](../concepts/franchise-system.md)
- [Swiss Healthcare System](../concepts/swiss-healthcare-system.md)

---

*Last Updated: 2026-01-26*

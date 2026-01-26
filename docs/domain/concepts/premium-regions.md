# Premium Regions (Prämienregionen)

## Overview

**Premium Regions** are geographic zones defined by the BAG (Federal Office of Public Health) that determine health insurance premium levels. Switzerland is divided into premium regions, with urban areas typically having higher premiums than rural areas.

> **German**: Prämienregion
> **French**: Région de primes
> **Italian**: Regione di premio

---

## Why Regions Affect Premiums

Healthcare costs vary significantly by location due to:

| Factor | Urban Areas | Rural Areas |
|--------|-------------|-------------|
| **Provider density** | More doctors, specialists | Fewer providers |
| **Hospital costs** | Higher (university hospitals) | Lower |
| **Service utilization** | Higher | Lower |
| **Cost of living** | Higher | Lower |

Result: Premiums in Zürich city can be **30-50% higher** than in rural areas.

---

## Region Structure

### Regions per Canton

Each of the 26 Swiss cantons has **1 to 3 premium regions**:

| # Regions | Cantons |
|-----------|---------|
| **3 regions** | ZH, BE, LU, SG, AG, VD, GE |
| **2 regions** | SZ, ZG, FR, SO, BS, BL, SH, GR, TG, TI, VS, NE |
| **1 region** | UR, OW, NW, GL, AR, AI, JU |

### Region Numbering

Within multi-region cantons:
- **Region 1**: Typically urban centers (highest premiums)
- **Region 2**: Suburban/mixed areas
- **Region 3**: Rural areas (lowest premiums)

---

## Premium Region Map

```
┌─────────────────────────────────────────────────────────────────────┐
│                    SWISS PREMIUM REGIONS                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│          Basel  ●───────────●  Zürich                              │
│           (BS)              │   (ZH)                                │
│                    Aargau   │                                       │
│                     (AG)    │     ● Winterthur                      │
│          ●───────────────●  │                                       │
│         Bern               ●│                                       │
│         (BE)            Luzern                                      │
│                          (LU)                                       │
│      ●                                          ● St.Gallen        │
│   Lausanne    ● Fribourg                         (SG)              │
│    (VD)        (FR)                                                 │
│                              ● Graubünden (GR)                      │
│      ● Genève                                                       │
│       (GE)        ● Valais                    ● Ticino             │
│                    (VS)                         (TI)                │
│                                                                     │
│   Legend:  ● = Major city (usually Region 1)                       │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Region Codes

### Format

Region codes follow the pattern: `{CANTON}-{NUMBER}`

Examples:
- `ZH-1` = Zürich Region 1 (city of Zürich)
- `ZH-2` = Zürich Region 2 (suburban)
- `ZH-3` = Zürich Region 3 (rural)
- `BE-1` = Bern Region 1 (city of Bern)

### All Cantons and Regions

| Canton | Code | # Regions | Region Codes |
|--------|------|-----------|--------------|
| Zürich | ZH | 3 | ZH-1, ZH-2, ZH-3 |
| Bern | BE | 3 | BE-1, BE-2, BE-3 |
| Luzern | LU | 3 | LU-1, LU-2, LU-3 |
| Uri | UR | 1 | UR-1 |
| Schwyz | SZ | 2 | SZ-1, SZ-2 |
| Obwalden | OW | 1 | OW-1 |
| Nidwalden | NW | 1 | NW-1 |
| Glarus | GL | 1 | GL-1 |
| Zug | ZG | 2 | ZG-1, ZG-2 |
| Fribourg | FR | 2 | FR-1, FR-2 |
| Solothurn | SO | 2 | SO-1, SO-2 |
| Basel-Stadt | BS | 2 | BS-1, BS-2 |
| Basel-Landschaft | BL | 2 | BL-1, BL-2 |
| Schaffhausen | SH | 2 | SH-1, SH-2 |
| Appenzell Ausserrhoden | AR | 1 | AR-1 |
| Appenzell Innerrhoden | AI | 1 | AI-1 |
| St. Gallen | SG | 3 | SG-1, SG-2, SG-3 |
| Graubünden | GR | 2 | GR-1, GR-2 |
| Aargau | AG | 3 | AG-1, AG-2, AG-3 |
| Thurgau | TG | 2 | TG-1, TG-2 |
| Ticino | TI | 2 | TI-1, TI-2 |
| Vaud | VD | 3 | VD-1, VD-2, VD-3 |
| Valais | VS | 2 | VS-1, VS-2 |
| Neuchâtel | NE | 2 | NE-1, NE-2 |
| Genève | GE | 3 | GE-1, GE-2, GE-3 |
| Jura | JU | 1 | JU-1 |

---

## Postal Code to Region Mapping

Premium regions are determined by **postal code (PLZ)**:

```
Address                          Premium Region
────────────────────────────     ──────────────
Bahnhofstrasse 1, 8001 Zürich    ZH-1
Dorfstrasse 5, 8307 Effretikon   ZH-2
Hauptstrasse 10, 8477 Oberstammheim   ZH-3
```

### Lookup Process

```
┌─────────────────────────────────────────────────────────────────┐
│              PREMIUM REGION DETERMINATION                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   Address Entry                                                 │
│        │                                                        │
│        ▼                                                        │
│   Extract Postal Code (PLZ)                                     │
│        │                                                        │
│        ▼                                                        │
│   Lookup in BAG PLZ-Region Table                               │
│        │                                                        │
│        ▼                                                        │
│   Return Premium Region ID                                      │
│        │                                                        │
│        ▼                                                        │
│   Use for Premium Calculation                                   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Premium Differences by Region

### Example: Adult, CHF 300 Franchise, Standard Model (2025)

| Region | City/Area | Approx. Monthly Premium |
|--------|-----------|------------------------|
| **GE-1** | Geneva City | CHF 500-550 |
| **ZH-1** | Zürich City | CHF 450-500 |
| **BS-1** | Basel City | CHF 440-480 |
| **VD-1** | Lausanne | CHF 430-470 |
| **BE-1** | Bern City | CHF 400-440 |
| **LU-1** | Luzern City | CHF 390-420 |
| **SG-3** | Rural St. Gallen | CHF 340-370 |
| **AI-1** | Appenzell I.Rh. | CHF 300-340 |
| **NW-1** | Nidwalden | CHF 310-350 |

⚠️ **Note**: Actual premiums vary by insurer and year. Use [priminfo.admin.ch](https://www.priminfo.admin.ch) for current rates.

---

## Address Changes and Premium Regions

### Moving to Different Region

When an insured person moves:

1. **Immediate effect**: Premium region changes immediately
2. **Premium adjustment**: Starts from move date
3. **Pro-rata calculation**: Month is split proportionally

### Example

```
Move from Zürich (ZH-1) to rural Aargau (AG-3) on March 15:

March 1-14:  Premium based on ZH-1 (higher)
March 15-31: Premium based on AG-3 (lower)

Insurer calculates: (14 days × ZH-1 rate) + (17 days × AG-3 rate)
```

### Special Termination Right

Moving to a different premium region triggers:
- Right to switch insurers mid-year
- Must notify within 1 month of move

---

## Code Reference

### Canton Enum

```java
public enum Canton {
    ZH("Zürich", Language.DE),
    BE("Bern", Language.DE),
    LU("Luzern", Language.DE),
    // ... all 26 cantons
    TI("Ticino", Language.IT),
    VD("Vaud", Language.FR),
    GE("Genève", Language.FR);

    private final String name;
    private final Language officialLanguage;
}
```

### PremiumRegion Entity

```java
public class PremiumRegion {
    private UUID id;
    private String code;           // e.g., "ZH-1"
    private Canton canton;         // ZH
    private Integer regionNumber;  // 1
    private LocalizedText name;    // "Zürich Region 1"
    private List<String> postalCodes;  // ["8001", "8002", ...]
}
```

### Address Entity

```java
public class Address {
    // ...
    private Canton canton;
    private UUID premiumRegionId;  // Links to PremiumRegion
    // ...
}
```

---

## BAG Region Updates

The BAG occasionally updates premium region definitions:

### Update Frequency
- Typically stable for years
- Major changes are rare
- Postal code reassignments happen

### Handling Updates

1. BAG publishes new PLZ-Region mappings
2. Import updated mapping table
3. Recalculate affected persons' regions
4. Adjust premiums from effective date

### Data Source

📋 **Official BAG Data**: Premium region definitions are published annually as part of the premium approval process.

| Resource | URL |
|----------|-----|
| Premium Region Info | [bag.admin.ch/regionen](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html) |
| PLZ Database | [post.ch/plz](https://www.post.ch/de/geschaeftlich/themen-a-z/adressen-pflegen-und-geodaten-nutzen/adress-und-geodatenservices/strassenverzeichnis) |

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ PLZ determines region | Premium region based on postal code |
| ⚠️ Immediate effect | Region changes immediately with address change |
| ⚠️ Pro-rata calculation | Monthly premium split on move date |
| ⚠️ Special termination | Moving allows mid-year insurer switch |
| ⚠️ Canton has 1-3 regions | No canton has more than 3 regions |

---

## Related Documentation

- [KVG - Mandatory Insurance](./kvg-mandatory-insurance.md)
- [Swiss Healthcare System](./swiss-healthcare-system.md)
- [Address Entity](../entities/masterdata/address.md)

---

*Last Updated: 2026-01-26*

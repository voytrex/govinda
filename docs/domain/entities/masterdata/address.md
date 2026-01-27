# Address Entity

## Overview

The **Address** entity represents a temporal address for a person. Addresses have validity periods and support bitemporal tracking for audit compliance. Each address is linked to a BAG premium region for premium calculation.

> **German**: Adresse
> **Module**: `govinda-masterdata`
> **Status**: ✅ Implemented

---

## Entity Definition

```java
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID personId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AddressType addressType;

    @Column(nullable = false)
    private String street;

    private String houseNumber;

    private String additionalLine;

    @Column(nullable = false, length = 10)
    private String postalCode;

    @Column(nullable = false)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Canton canton;

    @Column(length = 3)
    private String country = "CHE";

    private UUID premiumRegionId;

    // Temporal validity (business time)
    @Column(nullable = false)
    private LocalDate validFrom;

    private LocalDate validTo;

    // Bitemporal tracking (transaction time)
    @Column(nullable = false)
    private Instant recordedAt;

    private Instant supersededAt;

    private UUID createdBy;
}
```

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | UUID | ✅ | Unique identifier |
| `personId` | UUID | ✅ | Reference to Person |
| `addressType` | AddressType | ✅ | MAIN, CORRESPONDENCE, BILLING |
| `street` | String | ✅ | Street name |
| `houseNumber` | String | ❌ | House/building number |
| `additionalLine` | String | ❌ | c/o, apartment, etc. |
| `postalCode` | String(10) | ✅ | Swiss PLZ (e.g., "8001") |
| `city` | String | ✅ | City/locality name |
| `canton` | Canton | ✅ | Swiss canton (26 values) |
| `country` | String(3) | ❌ | ISO 3166-1 alpha-3 (default: CHE) |
| `premiumRegionId` | UUID | ❌ | Link to BAG premium region |
| `validFrom` | LocalDate | ✅ | Business validity start |
| `validTo` | LocalDate | ❌ | Business validity end (null = current) |
| `recordedAt` | Instant | ✅ | When recorded in system |
| `supersededAt` | Instant | ❌ | When replaced by correction |
| `createdBy` | UUID | ❌ | User who created the record |

---

## Key Behaviors

### Is Current

```java
public boolean isCurrent() {
    LocalDate today = LocalDate.now();
    return (validFrom.isBefore(today) || validFrom.isEqual(today))
        && (validTo == null || validTo.isAfter(today));
}
```

### Is Valid On Date

```java
public boolean isValidOn(LocalDate date) {
    return (validFrom.isBefore(date) || validFrom.isEqual(date))
        && (validTo == null || validTo.isAfter(date) || validTo.isEqual(date));
}
```

### Close Address

```java
public void close(LocalDate endDate) {
    if (this.validTo != null) {
        throw new IllegalStateException("Address already closed");
    }
    if (endDate.isBefore(this.validFrom)) {
        throw new IllegalArgumentException("End date cannot be before start date");
    }
    this.validTo = endDate;
}
```

### Formatted Output

```java
public String formattedStreet() {
    if (houseNumber != null && !houseNumber.isBlank()) {
        return street + " " + houseNumber;
    }
    return street;
}

public String formattedCity() {
    return postalCode + " " + city;
}

public List<String> formattedLines() {
    List<String> lines = new ArrayList<>();
    if (additionalLine != null && !additionalLine.isBlank()) {
        lines.add(additionalLine);
    }
    lines.add(formattedStreet());
    lines.add(formattedCity());
    return lines;
}
```

**Example Output:**
```
c/o Meier
Bahnhofstrasse 42
8001 Zürich
```

---

## Address Types

```java
public enum AddressType {
    MAIN,           // Primary residence (Wohnadresse)
    CORRESPONDENCE, // Mailing address (Korrespondenzadresse)
    BILLING;        // Invoice address (Rechnungsadresse)
}
```

| Type | Purpose | Premium Region Link |
|------|---------|---------------------|
| `MAIN` | Primary residence, determines premium region | ✅ Yes |
| `CORRESPONDENCE` | Where to send letters | ❌ No |
| `BILLING` | Where to send invoices | ❌ No |

---

## Swiss Cantons

```java
public enum Canton {
    ZH("Zürich", Language.DE),
    BE("Bern", Language.DE),
    LU("Luzern", Language.DE),
    UR("Uri", Language.DE),
    SZ("Schwyz", Language.DE),
    OW("Obwalden", Language.DE),
    NW("Nidwalden", Language.DE),
    GL("Glarus", Language.DE),
    ZG("Zug", Language.DE),
    FR("Fribourg", Language.FR),
    SO("Solothurn", Language.DE),
    BS("Basel-Stadt", Language.DE),
    BL("Basel-Landschaft", Language.DE),
    SH("Schaffhausen", Language.DE),
    AR("Appenzell Ausserrhoden", Language.DE),
    AI("Appenzell Innerrhoden", Language.DE),
    SG("St. Gallen", Language.DE),
    GR("Graubünden", Language.DE),
    AG("Aargau", Language.DE),
    TG("Thurgau", Language.DE),
    TI("Ticino", Language.IT),
    VD("Vaud", Language.FR),
    VS("Valais", Language.FR),
    NE("Neuchâtel", Language.FR),
    GE("Genève", Language.FR),
    JU("Jura", Language.FR);
}
```

---

## Premium Region Linkage

The MAIN address determines the person's premium region:

```
Address (PLZ: 8001)
        │
        ▼
┌─────────────────────┐
│  PLZ Lookup Table   │
│  8001 → ZH-1        │
└─────────────────────┘
        │
        ▼
PremiumRegion (id: uuid, code: "ZH-1")
```

### Premium Region Resolution

```java
// When saving a MAIN address:
if (address.getAddressType() == AddressType.MAIN) {
    PremiumRegion region = premiumRegionService.findByPostalCode(address.getPostalCode());
    address.setPremiumRegionId(region.getId());
}
```

---

## Temporal Model

### Business Time (Valid Time)

When the address is valid in the real world:

```
┌──────────────────────────────────────────────────────────────┐
│                    ADDRESS HISTORY                           │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  2020-01-01                    2023-06-15              Now   │
│       │                             │                   │    │
│       ▼                             ▼                   ▼    │
│  ┌────────────────────────┐    ┌──────────────────────────┐ │
│  │ Bahnhofstrasse 1       │    │ Seestrasse 42            │ │
│  │ 8001 Zürich            │    │ 8008 Zürich              │ │
│  │                        │    │                          │ │
│  │ validFrom: 2020-01-01  │    │ validFrom: 2023-06-15    │ │
│  │ validTo: 2023-06-14    │    │ validTo: null (current)  │ │
│  └────────────────────────┘    └──────────────────────────┘ │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### Transaction Time (System Time)

When the address was recorded/corrected:

```
Original recording:
  recordedAt: 2020-01-01T10:00:00Z
  supersededAt: null (current)

After correction:
  recordedAt: 2020-01-01T10:00:00Z
  supersededAt: 2020-01-05T14:30:00Z (replaced!)

New corrected record:
  recordedAt: 2020-01-05T14:30:00Z
  supersededAt: null (current)
```

---

## Address Change Flow

### Moving to New Address

```
┌─────────────────────────────────────────────────────────────────┐
│                   ADDRESS CHANGE PROCESS                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   1. Create new address                                         │
│      ├── validFrom: move date                                   │
│      └── premiumRegionId: lookup from PLZ                       │
│                                                                 │
│   2. Close old address                                          │
│      └── validTo: move date - 1 day                            │
│                                                                 │
│   3. Trigger premium recalculation                              │
│      └── If premium region changed → new premium from move date│
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Validation Rules

### Swiss Address Format

| Field | Validation |
|-------|------------|
| `postalCode` | 4 digits for Switzerland (e.g., "8001") |
| `canton` | Must match postal code's canton |
| `country` | ISO 3166-1 alpha-3 (CHE for Switzerland) |

### Business Rules

```java
// validFrom must be before or equal to validTo
if (validTo != null && validFrom.isAfter(validTo)) {
    throw new IllegalArgumentException("validFrom must be before validTo");
}

// Cannot have overlapping addresses of same type
List<Address> overlapping = existingAddresses.stream()
    .filter(a -> a.getAddressType() == this.addressType)
    .filter(a -> periodsOverlap(a, this))
    .collect(Collectors.toList());
if (!overlapping.isEmpty()) {
    throw new IllegalStateException("Address periods overlap");
}
```

---

## API Examples

### Create Address

```http
POST /api/v1/persons/{personId}/addresses
Content-Type: application/json

{
  "addressType": "MAIN",
  "street": "Bahnhofstrasse",
  "houseNumber": "42",
  "postalCode": "8001",
  "city": "Zürich",
  "canton": "ZH",
  "validFrom": "2024-01-01"
}
```

### Response

```json
{
  "id": "address-uuid",
  "personId": "person-uuid",
  "addressType": "MAIN",
  "street": "Bahnhofstrasse",
  "houseNumber": "42",
  "postalCode": "8001",
  "city": "Zürich",
  "canton": "ZH",
  "country": "CHE",
  "premiumRegion": {
    "id": "region-uuid",
    "code": "ZH-1",
    "name": "Zürich Region 1"
  },
  "validFrom": "2024-01-01",
  "validTo": null,
  "isCurrent": true,
  "formattedLines": [
    "Bahnhofstrasse 42",
    "8001 Zürich"
  ]
}
```

### Get Address History

```http
GET /api/v1/persons/{personId}/addresses?includeHistory=true
```

---

## Code Location

| File | Path |
|------|------|
| Entity | `backend/govinda-masterdata/src/main/java/net/voytrex/govinda/masterdata/domain/model/Address.java` |
| Canton Enum | `backend/govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/Canton.java` |
| AddressType Enum | `backend/govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/AddressType.java` |
| Tests | `backend/govinda-masterdata/src/test/java/net/voytrex/govinda/masterdata/domain/model/AddressTest.java` |

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ Premium region link | MAIN address determines premium region |
| ⚠️ Temporal validity | Addresses have validFrom/validTo |
| ⚠️ No overlaps | Same type addresses cannot overlap |
| ⚠️ Bitemporal tracking | Corrections tracked separately |
| ⚠️ Canton consistency | Canton must match postal code |

---

## Related Documentation

- [Person Entity](./person.md)
- [Premium Regions Concept](../../concepts/premium-regions.md)
- [Canton Reference](../../glossary/glossary-de.md)

---

*Last Updated: 2026-01-26*

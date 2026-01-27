# Premium Table (PremiumEntry)

## Overview

The **PremiumEntry** entity represents a single premium amount for a specific combination of parameters within a tariff. Together, all premium entries for a tariff form the **Premium Table**. The required parameters differ between KVG and VVG products.

> **German**: Prämientabelle, Prämieneintrag
> **Module**: `govinda-product` (planned)
> **Status**: ⏳ Planned

---

## Entity Definition

```java
@Entity
@Table(name = "premium_entries")
public class PremiumEntry {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(nullable = false)
    private UUID premiumRegionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgeGroup ageGroup;

    // KVG only
    @Enumerated(EnumType.STRING)
    private Franchise franchise;

    // KVG only
    private Boolean withAccident;

    // VVG only (for gender-based pricing)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "monthly_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money monthlyAmount;

    private Instant createdAt;
}
```

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | UUID | ✅ | Unique identifier |
| `tariff` | Tariff | ✅ | Parent tariff |
| `premiumRegionId` | UUID | ✅ | Reference to premium region |
| `ageGroup` | AgeGroup | ✅ | CHILD, YOUNG_ADULT, ADULT |
| `franchise` | Franchise | ⚠️ KVG | CHF_300 to CHF_2500 |
| `withAccident` | Boolean | ⚠️ KVG | Accident coverage included |
| `gender` | Gender | ⚠️ VVG | MALE, FEMALE (optional) |
| `monthlyAmount` | Money | ✅ | Monthly premium in CHF |
| `createdAt` | Instant | Auto | Creation timestamp |

---

## Premium Dimensions

### KVG Premium Entry

```
┌─────────────────────────────────────────────────────────────────┐
│                    KVG PREMIUM ENTRY                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   Premium = f(Region, AgeGroup, Franchise, WithAccident)        │
│                                                                 │
│   ┌─────────────────┐                                           │
│   │ premiumRegionId │──▶ ZH-1 (Zürich Region 1)                │
│   │ ageGroup        │──▶ ADULT                                  │
│   │ franchise       │──▶ CHF_300                                │
│   │ withAccident    │──▶ true                                   │
│   │ monthlyAmount   │──▶ CHF 485.20                             │
│   └─────────────────┘                                           │
│                                                                 │
│   Note: NO gender - KVG uses unisex premiums (Art. 61 KVG)     │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### VVG Premium Entry (Unisex)

```
┌─────────────────────────────────────────────────────────────────┐
│                    VVG PREMIUM ENTRY (UNISEX)                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   Premium = f(Region, AgeGroup)                                 │
│                                                                 │
│   ┌─────────────────┐                                           │
│   │ premiumRegionId │──▶ ZH-1 (Zürich Region 1)                │
│   │ ageGroup        │──▶ ADULT                                  │
│   │ gender          │──▶ null (unisex product)                  │
│   │ monthlyAmount   │──▶ CHF 85.00                              │
│   └─────────────────┘                                           │
│                                                                 │
│   Note: No franchise for VVG products                          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### VVG Premium Entry (Gender-Based)

```
┌─────────────────────────────────────────────────────────────────┐
│                    VVG PREMIUM ENTRY (GENDER-BASED)             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   Premium = f(Region, AgeGroup, Gender)                         │
│                                                                 │
│   ┌─────────────────┐     ┌─────────────────┐                  │
│   │ premiumRegionId │     │ premiumRegionId │                  │
│   │ ageGroup: ADULT │     │ ageGroup: ADULT │                  │
│   │ gender: FEMALE  │     │ gender: MALE    │                  │
│   │ monthlyAmount:  │     │ monthlyAmount:  │                  │
│   │   CHF 92.00     │     │   CHF 78.00     │                  │
│   └─────────────────┘     └─────────────────┘                  │
│                                                                 │
│   Note: Gender-based pricing allowed for VVG (risk-based)      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Unique Key Generation

Each premium entry has a unique key within its tariff:

```java
public String getKey() {
    ProductType category = tariff.getProduct().getCategory();

    if (category == ProductType.KVG) {
        return String.format("%s_%s_%s_%s",
            premiumRegionId,
            ageGroup,
            franchise,
            withAccident);
    } else {
        // VVG
        if (gender != null) {
            return String.format("%s_%s_%s",
                premiumRegionId,
                ageGroup,
                gender);
        } else {
            return String.format("%s_%s",
                premiumRegionId,
                ageGroup);
        }
    }
}
```

---

## Premium Lookup

### KVG Premium Lookup

```java
public Optional<Money> lookupKvgPremium(
        UUID productId,
        LocalDate effectiveDate,
        String postalCode,
        LocalDate birthDate,
        Franchise franchise,
        boolean withAccident) {

    // 1. Get product and active tariff
    Product product = productRepository.findById(productId);
    Tariff tariff = product.tariffAt(effectiveDate)
        .orElseThrow(() -> new TariffNotFoundException());

    // 2. Determine premium region from postal code
    PremiumRegion region = premiumRegionService.findByPostalCode(postalCode);

    // 3. Calculate age group
    AgeGroup ageGroup = AgeGroup.forAge(calculateAge(birthDate, effectiveDate));

    // 4. Lookup premium entry
    return tariff.getPremium(region.getId(), ageGroup, franchise, withAccident)
        .map(PremiumEntry::getMonthlyAmount);
}
```

### VVG Premium Lookup

```java
public Optional<Money> lookupVvgPremium(
        UUID productId,
        LocalDate effectiveDate,
        String postalCode,
        LocalDate birthDate,
        Gender gender) {  // gender may be null for unisex products

    // 1. Get product and active tariff
    Product product = productRepository.findById(productId);
    Tariff tariff = product.tariffAt(effectiveDate)
        .orElseThrow(() -> new TariffNotFoundException());

    // 2. Determine premium region from postal code
    PremiumRegion region = premiumRegionService.findByPostalCode(postalCode);

    // 3. Calculate age group
    AgeGroup ageGroup = AgeGroup.forAge(calculateAge(birthDate, effectiveDate));

    // 4. Lookup premium entry
    return tariff.getVvgPremium(region.getId(), ageGroup, gender)
        .map(PremiumEntry::getMonthlyAmount);
}
```

---

## Premium Region Entity

```java
@Entity
@Table(name = "premium_regions")
public class PremiumRegion {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;  // e.g., "ZH-1"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Canton canton;

    @Column(nullable = false)
    private Integer regionNumber;  // 1, 2, or 3

    @Embedded
    private LocalizedText name;

    @ElementCollection
    @CollectionTable(name = "premium_region_postal_codes")
    private List<String> postalCodes;
}
```

---

## Bulk Import

### Import Format

```json
{
  "entries": [
    {
      "premiumRegionCode": "ZH-1",
      "ageGroup": "ADULT",
      "franchise": "CHF_300",
      "withAccident": true,
      "monthlyAmount": 485.20
    },
    {
      "premiumRegionCode": "ZH-1",
      "ageGroup": "ADULT",
      "franchise": "CHF_300",
      "withAccident": false,
      "monthlyAmount": 450.00
    },
    ...
  ]
}
```

### CSV Import Format

```csv
premiumRegionCode,ageGroup,franchise,withAccident,monthlyAmount
ZH-1,ADULT,CHF_300,true,485.20
ZH-1,ADULT,CHF_300,false,450.00
ZH-1,ADULT,CHF_500,true,465.00
ZH-1,ADULT,CHF_500,false,430.00
...
```

### Import Validation

```java
public ImportResult importPremiumTable(UUID tariffId, List<PremiumEntryDto> entries) {
    Tariff tariff = tariffRepository.findById(tariffId)
        .orElseThrow(() -> new TariffNotFoundException());

    if (tariff.getStatus() != TariffStatus.DRAFT) {
        throw new IllegalStateException("Cannot import to non-DRAFT tariff");
    }

    // Validate all entries first
    List<ValidationError> errors = validateEntries(entries);
    if (!errors.isEmpty()) {
        throw new ValidationException(errors);
    }

    // Clear existing and import new (transactional)
    tariff.clearPremiums();
    entries.forEach(dto -> {
        PremiumEntry entry = mapToEntity(dto, tariff);
        tariff.addPremiumEntry(entry);
    });

    tariffRepository.save(tariff);

    return new ImportResult(entries.size());
}
```

---

## Required Entry Count

### KVG Formula

```
Total = Regions × AgeGroups × Franchises × AccidentOptions

Example (Switzerland-wide):
- 42 premium regions
- 3 age groups (CHILD, YOUNG_ADULT, ADULT)
- 6 franchise levels (CHF_0 to CHF_2500)
- 2 accident options (with/without)

Total = 42 × 3 × 6 × 2 = 1,512 entries

Note: Not all franchise levels available for all age groups
- Children: 6 options (CHF_0 to CHF_600)
- Young Adults: 6 options (CHF_300 to CHF_2500)
- Adults: 6 options (CHF_300 to CHF_2500)
```

### VVG Formula (Unisex)

```
Total = Regions × AgeGroups

Example:
Total = 42 × 3 = 126 entries
```

### VVG Formula (Gender-Based)

```
Total = Regions × AgeGroups × Genders

Example:
Total = 42 × 3 × 2 = 252 entries
```

---

## API Examples

### Add Single Entry

```http
POST /api/v1/tariffs/{tariffId}/premiums
Content-Type: application/json

{
  "premiumRegionId": "region-uuid",
  "ageGroup": "ADULT",
  "franchise": "CHF_300",
  "withAccident": true,
  "monthlyAmount": 485.20
}
```

### Bulk Import

```http
POST /api/v1/tariffs/{tariffId}/premiums/import
Content-Type: application/json

{
  "entries": [
    { ... },
    { ... }
  ]
}
```

### Get Premium

```http
GET /api/v1/products/{productId}/premium?postalCode=8001&birthDate=1985-03-15&franchise=CHF_300&withAccident=true
```

### Response

```json
{
  "productId": "product-uuid",
  "tariffId": "tariff-uuid",
  "tariffVersion": "2025-V1",
  "premiumRegion": {
    "code": "ZH-1",
    "name": "Zürich Region 1"
  },
  "ageGroup": "ADULT",
  "franchise": "CHF_300",
  "withAccident": true,
  "monthlyAmount": 485.20,
  "annualAmount": 5822.40
}
```

---

## Code Location (Planned)

| File | Path |
|------|------|
| PremiumEntry | `backend/govinda-product/src/main/java/net/voytrex/govinda/product/domain/model/PremiumEntry.java` |
| PremiumRegion | `backend/govinda-product/src/main/java/net/voytrex/govinda/product/domain/model/PremiumRegion.java` |

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ KVG: no gender | KVG premiums are unisex by law |
| ⚠️ VVG: optional gender | VVG can have gender-based pricing |
| ⚠️ Complete table required | All combinations must exist before activation |
| ⚠️ Positive amounts | Premium amounts must be positive |
| ⚠️ Transactional import | Bulk import is all-or-nothing |

---

## Related Documentation

- [Product Entity](./product.md)
- [Tariff Entity](./tariff.md)
- [Premium Regions Concept](../../concepts/premium-regions.md)
- [Age Groups Concept](../../concepts/age-groups.md)
- [Franchise System](../../concepts/franchise-system.md)
- [Product Use Cases](/docs/use-cases/product-use-cases.md)

---

*Last Updated: 2026-01-26*

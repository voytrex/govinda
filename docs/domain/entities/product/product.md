# Product Entity

## Overview

The **Product** entity represents a subscription product offered by the provider. Products may be healthcare (KVG/VVG), broadcast fees, telecom plans, or other recurring services. Each product can have multiple tariffs over time, with only one tariff active at any given moment.

> **German**: Produkt
> **Module**: `govinda-product` (planned)
> **Status**: ⏳ Planned

---

## Entity Definition

```java
@Entity
@Table(name = "products")
public class Product {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceDomain serviceDomain; // HEALTHCARE, BROADCAST, TELECOM, CUSTOM

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType category;        // KVG/VVG (healthcare only)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingModel pricingModel;   // FIXED, TIERED, USAGE_BASED, COMPOSITE

    @ElementCollection
    private Set<SubscriberType> eligibleSubscriberTypes;

    @Enumerated(EnumType.STRING)
    private InsuranceModel insuranceModel;  // KVG only

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.INACTIVE;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "de", column = @Column(name = "name_de")),
        @AttributeOverride(name = "fr", column = @Column(name = "name_fr")),
        @AttributeOverride(name = "it", column = @Column(name = "name_it")),
        @AttributeOverride(name = "en", column = @Column(name = "name_en"))
    })
    private LocalizedText name;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "de", column = @Column(name = "desc_de")),
        @AttributeOverride(name = "fr", column = @Column(name = "desc_fr")),
        @AttributeOverride(name = "it", column = @Column(name = "desc_it")),
        @AttributeOverride(name = "en", column = @Column(name = "desc_en"))
    })
    private LocalizedText description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Tariff> tariffs = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    @Version
    private long version;
}
```

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | UUID | ✅ | Unique identifier |
| `tenantId` | UUID | ✅ | Multi-tenant isolation |
| `code` | String | ✅ | Unique product code (e.g., "KVG_STANDARD_2024") |
| `serviceDomain` | ServiceDomain | ✅ | HEALTHCARE, BROADCAST, TELECOM, CUSTOM |
| `category` | ProductType | ⚠️ | KVG/VVG (healthcare only) |
| `pricingModel` | PricingModel | ✅ | How pricing is calculated |
| `eligibleSubscriberTypes` | Set\<SubscriberType\> | ✅ | Allowed subscriber types |
| `insuranceModel` | InsuranceModel | ⚠️ | Required for KVG, null for VVG |
| `status` | ProductStatus | ✅ | ACTIVE or INACTIVE |
| `name` | LocalizedText | ✅ | Product name in 4 languages |
| `description` | LocalizedText | ❌ | Product description |
| `tariffs` | List\<Tariff\> | ❌ | Associated tariffs |
| `createdAt` | Instant | Auto | Creation timestamp |
| `updatedAt` | Instant | Auto | Last modification |
| `version` | long | Auto | Optimistic locking |

---

## Key Behaviors

### Get Current Tariff

```java
public Optional<Tariff> currentTariff() {
    LocalDate today = LocalDate.now();
    return tariffs.stream()
        .filter(t -> t.getStatus() == TariffStatus.ACTIVE)
        .filter(t -> t.isValidOn(today))
        .findFirst();
}
```

### Get Tariff for Date

```java
public Optional<Tariff> tariffAt(LocalDate date) {
    return tariffs.stream()
        .filter(t -> t.getStatus() == TariffStatus.ACTIVE)
        .filter(t -> t.isValidOn(date))
        .findFirst();
}
```

### Activate Product

```java
public void activate() {
    if (this.tariffs.stream().noneMatch(t -> t.getStatus() == TariffStatus.ACTIVE)) {
        throw new IllegalStateException("Cannot activate product without active tariff");
    }
    this.status = ProductStatus.ACTIVE;
    this.updatedAt = Instant.now();
}
```

### Deactivate Product

```java
public void deactivate() {
    // Deactivate all tariffs
    this.tariffs.forEach(Tariff::deactivate);
    this.status = ProductStatus.INACTIVE;
    this.updatedAt = Instant.now();
}
```

---

## Product Types

### ServiceDomain Enum

```java
public enum ServiceDomain {
    HEALTHCARE,
    BROADCAST,
    TELECOM,
    UTILITIES,
    CUSTOM
}
```

### ProductType Enum

```java
public enum ProductType {
    KVG("KVG"),  // Mandatory basic insurance (Grundversicherung)
    VVG("VVG");  // Supplementary insurance (Zusatzversicherung)

    private final String code;
}
```

### ProductCategory Enum (VVG Sub-Categories)

```java
public enum ProductCategory {
    BASIC("BASIC"),           // KVG basic insurance
    HOSPITAL("HOSP"),         // Hospital supplementary
    DENTAL("DENT"),           // Dental insurance
    ALTERNATIVE("ALT"),       // Complementary medicine
    TRAVEL("TRAV"),           // Travel/abroad insurance
    DAILY_ALLOWANCE("TAGG");  // Daily sickness allowance

    private final String code;
}
```

### ProductStatus Enum

```java
public enum ProductStatus {
    ACTIVE,    // Available for new contracts
    INACTIVE;  // Not available for new contracts
}
```

---

## Insurance Models (KVG Only)

```java
public enum InsuranceModel {
    STANDARD("STD", false),
    HMO("HMO", true),
    HAUSARZT("HAM", true),
    TELMED("TLM", true);

    private final String code;
    private final boolean hasProviderRestriction;
}
```

> **i18n note**: Enum values are code-only. User-facing labels must be resolved via `MessageSource`.

---

## Pricing Models (Cross-Domain)

```java
public enum PricingModel {
    FIXED,
    TIERED,
    REGION_AGE,
    USAGE_BASED,
    PROMOTIONAL,
    COMPOSITE
}
```

```java
public enum SubscriberType {
    INDIVIDUAL,
    PRIVATE_HOUSEHOLD,
    COLLECTIVE_HOUSEHOLD,
    CORPORATE
}
```

---

## Validation Rules

### Business Invariants

```java
// Service domain is required
if (serviceDomain == null) {
    throw new IllegalArgumentException("Service domain is required");
}

// Healthcare products must declare KVG/VVG category
if (serviceDomain == ServiceDomain.HEALTHCARE && category == null) {
    throw new IllegalArgumentException("Healthcare products require category");
}

// KVG products must have an insurance model
if (category == ProductType.KVG && insuranceModel == null) {
    throw new IllegalArgumentException("KVG products require insurance model");
}

// VVG products must not have an insurance model
if (category == ProductType.VVG && insuranceModel != null) {
    throw new IllegalArgumentException("VVG products cannot have insurance model");
}

// Eligible subscriber types must be provided
if (eligibleSubscriberTypes == null || eligibleSubscriberTypes.isEmpty()) {
    throw new IllegalArgumentException("Eligible subscriber types are required");
}

// Code must be unique within tenant
// Enforced by unique constraint
```

### Name Requirements

- Must be provided in all 4 languages (DE, FR, IT, EN)
- Maximum 200 characters per language

---

## Relationships

### Product → Tariff (1:N)

```
Product (1) ────────────< Tariff (N)
                   ↑
            productId: UUID (FK)
```

- A product can have multiple tariffs over time
- Each tariff has a validity period
- Only one tariff can be ACTIVE at any time

### Product → Coverage (1:N)

```
Product (1) ────────────< Coverage (N)
                   ↑
            productId: UUID (FK)
```

- Coverages reference the product
- Coverage also references specific tariff for premium

---

## API Examples

### Create KVG Product

```http
POST /api/v1/products
Content-Type: application/json

{
  "code": "KVG_STANDARD_2024",
  "category": "KVG",
  "insuranceModel": "STANDARD",
  "name": {
    "de": "Grundversicherung Standard",
    "fr": "Assurance de base Standard",
    "it": "Assicurazione base Standard",
    "en": "Basic Insurance Standard"
  },
  "description": {
    "de": "Freie Arztwahl, alle Leistungen gemäss KVG",
    "fr": "Libre choix du médecin, toutes les prestations LAMal",
    "it": "Libera scelta del medico, tutte le prestazioni LAMal",
    "en": "Free choice of doctor, all HIA benefits"
  }
}
```

### Create VVG Product

```http
POST /api/v1/products
Content-Type: application/json

{
  "code": "SPITAL_HALBPRIVAT_2024",
  "category": "VVG",
  "productSubCategory": "HOSPITAL",
  "name": {
    "de": "Spital Halbprivat",
    "fr": "Hospitalisation semi-privée",
    "it": "Ospedalizzazione semiprivata",
    "en": "Semi-Private Hospital"
  }
}
```

### Response

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "KVG_STANDARD_2024",
  "category": "KVG",
  "insuranceModel": "STANDARD",
  "status": "INACTIVE",
  "name": {
    "de": "Grundversicherung Standard",
    "fr": "Assurance de base Standard",
    "it": "Assicurazione base Standard",
    "en": "Basic Insurance Standard"
  },
  "currentTariff": null,
  "createdAt": "2025-01-15T10:30:00Z"
}
```

---

## Example Products

### KVG Products

| Code | Model | Description |
|------|-------|-------------|
| `KVG_STANDARD_2024` | STANDARD | Free choice of provider |
| `KVG_HMO_2024` | HMO | HMO center model |
| `KVG_HAUSARZT_2024` | HAUSARZT | Family doctor model |
| `KVG_TELMED_2024` | TELMED | Telemedicine model |

### VVG Products

| Code | Category | Description |
|------|----------|-------------|
| `SPITAL_ALLGEMEIN_2024` | HOSPITAL | General ward, free hospital choice |
| `SPITAL_HALBPRIVAT_2024` | HOSPITAL | Semi-private room |
| `SPITAL_PRIVAT_2024` | HOSPITAL | Private room |
| `DENTAL_BASIC_2024` | DENTAL | Basic dental coverage |
| `ALTERNATIV_PLUS_2024` | ALTERNATIVE | Complementary medicine |

---

## Code Location (Planned)

| File | Path |
|------|------|
| Entity | `backend/govinda-product/src/main/java/net/voytrex/govinda/product/domain/model/Product.java` |
| Repository | `backend/govinda-product/src/main/java/net/voytrex/govinda/product/domain/repository/ProductRepository.java` |
| Service | `backend/govinda-product/src/main/java/net/voytrex/govinda/product/application/service/ProductService.java` |

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ Unique code | Product code unique per tenant |
| ⚠️ Domain required | ServiceDomain must be set |
| ⚠️ Pricing model | PricingModel must be set |
| ⚠️ KVG needs model | KVG products require insurance model |
| ⚠️ VVG no model | VVG products must not have model |
| ⚠️ Eligible types | Eligible subscriber types must be defined |
| ⚠️ One active tariff | Only one tariff active per product at a time |
| ⚠️ Activation requires tariff | Cannot activate product without active tariff |

---

## Related Documentation

- [Tariff Entity](./tariff.md)
- [Premium Table](./premium-table.md)
- [KVG Mandatory Insurance](../../concepts/kvg-mandatory-insurance.md)
- [VVG Supplementary Insurance](../../concepts/vvg-supplementary-insurance.md)
- [Insurance Models](../../concepts/insurance-models.md)
- [Generic Subscription Model](../../concepts/generic-subscription.md)
- [Product Use Cases](/docs/use-cases/product-use-cases.md)

---

*Last Updated: 2026-01-28*

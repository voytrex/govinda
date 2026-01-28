# Organization Entity

## Overview

The **Organization** entity represents businesses, institutions, and other legal entities that can be subscribers to various services. It supports the tiered pricing model required for business subscriptions like the Serafe corporate radio/TV fee.

> **German**: Organisation / Unternehmen
> **Module**: `govinda-masterdata`
> **Status**: Planned

---

## Entity Definition

```java
@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    // Identification
    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String uid;                     // Swiss UID (CHE-xxx.xxx.xxx)

    @Embedded
    private LocalizedText legalName;        // Formal name in all languages

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationStatus status = OrganizationStatus.ACTIVE;

    // Business Metrics (for tiered pricing)
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "annual_turnover"))
    private Money annualTurnover;

    @Column
    private Integer employeeCount;

    @Column(nullable = false)
    private Boolean vatRegistered = false;

    @Column
    private String vatNumber;               // CHE-xxx.xxx.xxx MWST

    // Relationships
    @Column
    private UUID primaryContactId;          // FK to Person

    @Column
    private UUID billingAddressId;          // FK to Address

    @Column
    private UUID headquartersAddressId;     // FK to Address

    // Audit
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private long version;
}
```

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | UUID | Auto | Unique identifier |
| `tenantId` | UUID | Yes | Multi-tenant isolation |
| `name` | String | Yes | Common/trading name |
| `uid` | String | No | Swiss UID number (CHE-xxx.xxx.xxx) |
| `legalName` | LocalizedText | No | Official registered name |
| `type` | OrganizationType | Yes | Legal structure |
| `status` | OrganizationStatus | Yes | Current status |
| `annualTurnover` | Money | No | For tiered pricing |
| `employeeCount` | Integer | No | For tiered pricing |
| `vatRegistered` | Boolean | Yes | VAT registration status |
| `vatNumber` | String | No | Swiss VAT number |
| `primaryContactId` | UUID | No | Main contact person |
| `billingAddressId` | UUID | No | Invoice address |
| `headquartersAddressId` | UUID | No | Registered office |
| `createdAt` | Instant | Auto | Creation timestamp |
| `updatedAt` | Instant | Auto | Last modification |
| `version` | long | Auto | Optimistic locking |

---

## OrganizationType Enum

```java
public enum OrganizationType {

    // Private sector
    SOLE_PROPRIETORSHIP,
    GENERAL_PARTNERSHIP,
    LIMITED_PARTNERSHIP,
    LIMITED_COMPANY,
    STOCK_CORPORATION,
    COOPERATIVE,

    // Non-profit
    ASSOCIATION,
    FOUNDATION,

    // Public sector
    PUBLIC_INSTITUTION,
    MUNICIPALITY,
    CANTON,

    // Other
    BRANCH_OFFICE,
    FOREIGN_ENTITY
}
```

> **i18n note**: Enum values are code-only. User-facing translations must be resolved via `MessageSource` using translation keys (per project i18n rules).

| Type | Code | Description |
|------|------|-------------|
| `SOLE_PROPRIETORSHIP` | Einzelunternehmen | Single owner, unlimited liability |
| `GENERAL_PARTNERSHIP` | Kollektivgesellschaft | Partnership, joint liability |
| `LIMITED_PARTNERSHIP` | Kommanditgesellschaft | Partnership with limited partners |
| `LIMITED_COMPANY` | GmbH | Limited liability company |
| `STOCK_CORPORATION` | AG | Corporation with share capital |
| `COOPERATIVE` | Genossenschaft | Member-owned cooperative |
| `ASSOCIATION` | Verein | Non-profit association |
| `FOUNDATION` | Stiftung | Foundation |
| `PUBLIC_INSTITUTION` | Öffentlich-rechtlich | Government entity |
| `MUNICIPALITY` | Gemeinde | Municipal government |
| `CANTON` | Kanton | Cantonal government |
| `BRANCH_OFFICE` | Zweigniederlassung | Branch of another entity |
| `FOREIGN_ENTITY` | Ausländische | Foreign company in Switzerland |

---

## OrganizationStatus Enum

```java
public enum OrganizationStatus {
    ACTIVE,         // Operating normally
    INACTIVE,       // Temporarily inactive
    LIQUIDATING,    // In liquidation process
    DISSOLVED,      // No longer exists
    MERGED          // Merged into another entity
}
```

---

## Key Behaviors

### Swiss UID Validation

```java
public static boolean isValidUid(String uid) {
    // Format: CHE-xxx.xxx.xxx (9 digits with check digit)
    if (uid == null) return false;
    return uid.matches("CHE-\\d{3}\\.\\d{3}\\.\\d{3}");
}
```

### VAT Number Validation

```java
public static boolean isValidVatNumber(String vatNumber) {
    // Format: CHE-xxx.xxx.xxx MWST
    if (vatNumber == null) return false;
    return vatNumber.matches("CHE-\\d{3}\\.\\d{3}\\.\\d{3} MWST");
}
```

### Turnover Tier Determination

```java
public int getTurnoverTier() {
    if (!vatRegistered || annualTurnover == null) {
        return 0; // No fee liability
    }

    BigDecimal amount = annualTurnover.getAmount();

    if (amount.compareTo(new BigDecimal("500000")) < 0) return 0;
    if (amount.compareTo(new BigDecimal("750000")) < 0) return 1;
    if (amount.compareTo(new BigDecimal("1200000")) < 0) return 2;
    if (amount.compareTo(new BigDecimal("1700000")) < 0) return 3;
    if (amount.compareTo(new BigDecimal("2500000")) < 0) return 4;
    if (amount.compareTo(new BigDecimal("3600000")) < 0) return 5;
    if (amount.compareTo(new BigDecimal("5100000")) < 0) return 6;
    if (amount.compareTo(new BigDecimal("7300000")) < 0) return 7;
    if (amount.compareTo(new BigDecimal("10400000")) < 0) return 8;
    if (amount.compareTo(new BigDecimal("15000000")) < 0) return 9;
    if (amount.compareTo(new BigDecimal("23000000")) < 0) return 10;
    if (amount.compareTo(new BigDecimal("33000000")) < 0) return 11;
    if (amount.compareTo(new BigDecimal("50000000")) < 0) return 12;
    if (amount.compareTo(new BigDecimal("90000000")) < 0) return 13;
    if (amount.compareTo(new BigDecimal("180000000")) < 0) return 14;
    if (amount.compareTo(new BigDecimal("400000000")) < 0) return 15;
    if (amount.compareTo(new BigDecimal("700000000")) < 0) return 16;
    if (amount.compareTo(new BigDecimal("1000000000")) < 0) return 17;
    return 18;
}
```

### Update Turnover

```java
public void updateTurnover(Money newTurnover, Integer fiscalYear) {
    Objects.requireNonNull(newTurnover, "Turnover must not be null");

    this.annualTurnover = newTurnover;
    this.updatedAt = Instant.now();

    // Could emit domain event for tier recalculation
}
```

---

## Relationships

### Organization → Person (Primary Contact)

```
Organization (N) ──────────> Person (1)
       ↑
primaryContactId: UUID (FK)
```

### Organization → Address (Multiple)

```
Organization (1) ──────────> Address (N)
       ↑
billingAddressId: UUID (FK)
headquartersAddressId: UUID (FK)
```

### Organization → Household (Collective)

For institutions that operate collective households:

```
Organization (1) <───────── Household (N)
                    ↑
            institutionId: UUID (FK)
```

---

## Business Rules

### UID Uniqueness

```java
// Swiss UID must be unique across tenants (national identifier)
@Column(unique = true)
private String uid;
```

### VAT Threshold

```java
// Organizations become VAT-liable at CHF 100,000 turnover
// But broadcast fee threshold is CHF 500,000
public boolean isBroadcastFeeLiable() {
    return vatRegistered &&
           annualTurnover != null &&
           annualTurnover.getAmount().compareTo(new BigDecimal("500000")) >= 0;
}
```

### Sole Proprietor Special Case

```java
// Sole proprietors pay both household fee (via Serafe)
// AND business fee (via ESTV) if turnover >= 500k
public boolean paysBothFees() {
    return type == OrganizationType.SOLE_PROPRIETORSHIP &&
           isBroadcastFeeLiable();
}
```

---

## API Examples

### Create Organization

```http
POST /api/v1/organizations
Content-Type: application/json

{
  "name": "Muster AG",
  "uid": "CHE-123.456.789",
  "type": "STOCK_CORPORATION",
  "vatRegistered": true,
  "vatNumber": "CHE-123.456.789 MWST",
  "annualTurnover": {
    "amount": 2500000,
    "currency": "CHF"
  },
  "employeeCount": 45,
  "primaryContactId": "person-uuid",
  "billingAddressId": "address-uuid"
}
```

### Response

```json
{
  "id": "org-uuid",
  "name": "Muster AG",
  "uid": "CHE-123.456.789",
  "type": "STOCK_CORPORATION",
  "status": "ACTIVE",
  "vatRegistered": true,
  "vatNumber": "CHE-123.456.789 MWST",
  "annualTurnover": {
    "amount": 2500000.00,
    "currency": "CHF"
  },
  "employeeCount": 45,
  "broadcastFeeTier": 5,
  "broadcastFeeAmount": {
    "amount": 645.00,
    "currency": "CHF"
  },
  "createdAt": "2026-01-27T10:30:00Z"
}
```

### Update Turnover

```http
PATCH /api/v1/organizations/{id}/turnover
Content-Type: application/json

{
  "annualTurnover": {
    "amount": 5500000,
    "currency": "CHF"
  },
  "fiscalYear": 2025
}
```

---

## Database Schema

```sql
CREATE TABLE organizations (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    uid VARCHAR(20) UNIQUE,
    legal_name_de VARCHAR(500),
    legal_name_fr VARCHAR(500),
    legal_name_it VARCHAR(500),
    legal_name_en VARCHAR(500),
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    annual_turnover DECIMAL(18, 2),
    employee_count INTEGER,
    vat_registered BOOLEAN NOT NULL DEFAULT FALSE,
    vat_number VARCHAR(25),
    primary_contact_id UUID REFERENCES persons(id),
    billing_address_id UUID REFERENCES addresses(id),
    headquarters_address_id UUID REFERENCES addresses(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_uid_format CHECK (uid IS NULL OR uid ~ 'CHE-[0-9]{3}\.[0-9]{3}\.[0-9]{3}'),
    CONSTRAINT chk_vat_format CHECK (vat_number IS NULL OR vat_number ~ 'CHE-[0-9]{3}\.[0-9]{3}\.[0-9]{3} MWST')
);

CREATE INDEX idx_organizations_tenant ON organizations(tenant_id);
CREATE INDEX idx_organizations_uid ON organizations(uid);
CREATE INDEX idx_organizations_status ON organizations(status);
CREATE INDEX idx_organizations_type ON organizations(type);
```

---

## Test Cases

### Creation Tests

```java
@Test
@DisplayName("should create organization with valid UID")
void should_createOrganization_when_validUidProvided() {
    // Arrange
    var command = CreateOrganizationCommand.builder()
        .name("Muster AG")
        .uid("CHE-123.456.789")
        .type(OrganizationType.STOCK_CORPORATION)
        .vatRegistered(true)
        .build();

    // Act
    var result = organizationService.create(command);

    // Assert
    assertThat(result.getUid()).isEqualTo("CHE-123.456.789");
    assertThat(result.getType()).isEqualTo(OrganizationType.STOCK_CORPORATION);
}

@Test
@DisplayName("should reject invalid UID format")
void should_throwException_when_uidFormatInvalid() {
    // Arrange
    var command = CreateOrganizationCommand.builder()
        .name("Muster AG")
        .uid("INVALID-UID")
        .type(OrganizationType.STOCK_CORPORATION)
        .build();

    // Act & Assert
    assertThatThrownBy(() -> organizationService.create(command))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("UID format");
}
```

### Tier Calculation Tests

```java
@Test
@DisplayName("should calculate tier 5 for turnover 2.5M")
void should_returnTier5_when_turnoverIs2_5Million() {
    // Arrange
    var org = OrganizationFixture.createWithTurnover(Money.chf(2_500_000));
    org.setVatRegistered(true);

    // Act
    int tier = org.getTurnoverTier();

    // Assert
    assertThat(tier).isEqualTo(5);
}

@Test
@DisplayName("should return tier 0 for non-VAT-registered")
void should_returnTier0_when_notVatRegistered() {
    // Arrange
    var org = OrganizationFixture.createWithTurnover(Money.chf(5_000_000));
    org.setVatRegistered(false);

    // Act
    int tier = org.getTurnoverTier();

    // Assert
    assertThat(tier).isEqualTo(0);
}
```

---

## Related Documentation

- [Person Entity](./person.md)
- [Household Entity](./household.md)
- [Address Entity](./address.md)
- [Radio/TV Fee (RTVG)](../../concepts/radio-tv-fee.md)

---

*Last Updated: 2026-01-28*

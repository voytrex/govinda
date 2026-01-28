# BusinessPartner Entity

## Overview

The **BusinessPartner** entity represents third parties who interact with the insurance system as payers, intermediaries, or service providers.

> **German**: Geschäftspartner
> **Module**: `govinda-masterdata`
> **Status**: ⏳ Planned

**Examples**:
- Cantons paying premium subsidies (IPV)
- Social services (Sozialhilfe) paying premiums
- Employers contributing to employee premiums
- Brokers and agents

---

## Entity Definition

```java
@Entity
@Table(name = "business_partners")
public class BusinessPartner {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "partner_number", unique = true)
    private String partnerNumber;

    // Type & Category
    @Enumerated(EnumType.STRING)
    @Column(name = "partner_type", nullable = false)
    private PartnerType partnerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "partner_category", nullable = false)
    private PartnerCategory partnerCategory;

    // Identity
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "uid")
    private String uid;  // Swiss UID

    @Column(name = "gln")
    private String gln;  // Global Location Number

    // Contact
    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Embedded
    private Address address;

    // Banking
    @Column(name = "iban")
    private String iban;

    @Column(name = "bank_name")
    private String bankName;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerStatus status = PartnerStatus.ACTIVE;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
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
| `partnerNumber` | String | ❌ | Business partner number |
| `partnerType` | PartnerType | ✅ | Type of partner (Canton, Employer, etc.) |
| `partnerCategory` | PartnerCategory | ✅ | Role (Payer, Intermediary, etc.) |
| `name` | String | ✅ | Full legal name |
| `shortName` | String | ❌ | Abbreviated name |
| `uid` | String | ❌ | Swiss UID (CHE-xxx.xxx.xxx) |
| `gln` | String | ❌ | Global Location Number (13 digits) |
| `email` | String | ❌ | Primary email |
| `phone` | String | ❌ | Primary phone |
| `address` | Address | ❌ | Embedded address |
| `iban` | String | ❌ | Bank account for payments |
| `status` | PartnerStatus | ✅ | Lifecycle status |

---

## Related Enums

### PartnerType

```java
public enum PartnerType {
    CANTON("Kanton"),
    COMMUNE("Gemeinde"),
    FEDERAL_OFFICE("Bundesamt"),
    SOCIAL_SERVICES("Sozialdienst"),
    EMPLOYER("Arbeitgeber"),
    INSTITUTION("Institution"),
    INSURANCE_COMPANY("Versicherung"),
    BROKER("Makler"),
    AGENT("Agent"),
    HEALTHCARE_PROVIDER("Leistungserbringer"),
    COLLECTION_AGENCY("Inkasso"),
    FAMILY_PAYER("Familienzahler"),
    OTHER("Andere");
}
```

### PartnerCategory

```java
public enum PartnerCategory {
    PAYER("Zahler"),
    SUBSIDIZER("Subventionierer"),
    GUARANTOR("Garant"),
    INTERMEDIARY("Vermittler"),
    PROVIDER("Anbieter"),
    CREDITOR("Gläubiger"),
    DEBTOR("Schuldner");
}
```

### PartnerStatus

```java
public enum PartnerStatus {
    PROSPECT("Interessent"),
    ACTIVE("Aktiv"),
    SUSPENDED("Sistiert"),
    TERMINATED("Beendet"),
    BLOCKED("Gesperrt");
}
```

---

## Relationships

```
BusinessPartner (1) ─────────────< PaymentArrangement (N)
        │
        │ pays for
        ▼
   Coverage / Person
```

---

## Use Cases

### Canton as IPV Payer

```java
BusinessPartner cantonZH = new BusinessPartner();
cantonZH.setPartnerType(PartnerType.CANTON);
cantonZH.setPartnerCategory(PartnerCategory.SUBSIDIZER);
cantonZH.setName("Kanton Zürich - Prämienverbilligung");
cantonZH.setShortName("IPV ZH");
```

### Employer Contribution

```java
BusinessPartner employer = new BusinessPartner();
employer.setPartnerType(PartnerType.EMPLOYER);
employer.setPartnerCategory(PartnerCategory.PAYER);
employer.setName("Beispiel AG");
employer.setUid("CHE-123.456.789");
employer.setIban("CH93 0076 2011 6238 5295 7");
```

### Social Services Full Payment

```java
BusinessPartner sozialamt = new BusinessPartner();
sozialamt.setPartnerType(PartnerType.SOCIAL_SERVICES);
sozialamt.setPartnerCategory(PartnerCategory.PAYER);
sozialamt.setName("Sozialamt Stadt Zürich");
```

---

## Code Location

| Component | Path |
|-----------|------|
| Entity | `govinda-masterdata/.../domain/model/BusinessPartner.java` |
| Repository | `govinda-masterdata/.../domain/repository/BusinessPartnerRepository.java` |
| Enums | `govinda-common/.../domain/model/PartnerType.java` |

---

## Related Documentation

- [PaymentArrangement](../contract/payment-arrangement.md) - Payment setup
- [PremiumSubsidy](../contract/premium-subsidy.md) - IPV tracking
- [Person](./person.md) - Insured person

---

*Last Updated: 2026-01-28*

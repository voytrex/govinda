# GAP-01: Business Partner / Payer Entity

## Problem Statement

The current domain model has no entity to represent **third parties** who:
- Pay fees on behalf of subscribers (Zahlungspflichtige Dritte)
- Have contractual relationships with the organization
- Act as intermediaries (brokers, agents)
- Provide guarantees or subsidies

## Real-World Scenarios

### Healthcare Insurance (KVG/VVG)

| Scenario | Payer | Subscriber | Notes |
|----------|-------|------------|-------|
| Premium Subsidy | Canton (Prämienverbilligung) | Person | Partial payment |
| Employer Insurance | Employer | Employee | Full/partial payment |
| Family Payment | Parent/Spouse | Child/Partner | Invoice routing |
| Social Welfare | Sozialhilfe (commune) | Welfare recipient | Full payment |
| Refugee Support | Canton/SEM | Asylum seeker | Special regime |

### Broadcast Fee (RTVG)

| Scenario | Payer | Subscriber | Notes |
|----------|-------|------------|-------|
| Institution | Elderly home operator | Collective household | Institution pays |
| Employer | Company | Employee housing | Part of benefits |
| Social Welfare | Sozialhilfe | Welfare recipient | Included in welfare calc |

### Telecom

| Scenario | Payer | Subscriber | Notes |
|----------|-------|------------|-------|
| Corporate Plan | Employer | Employee | Business mobile |
| Family Plan | Account holder | Family members | Multi-line discount |
| Reseller | Reseller company | End customer | White-label |

---

## Proposed Model

### BusinessPartner Entity

```java
@Entity
@Table(name = "business_partners")
public class BusinessPartner {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    // Partner identification
    @Column(name = "partner_number", unique = true)
    private String partnerNumber;  // Internal reference

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "partner_type", nullable = false)
    private PartnerType partnerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "partner_category")
    private PartnerCategory partnerCategory;

    // External identifiers
    @Column(name = "uid")
    private String uid;  // Swiss UID (CHE-xxx.xxx.xxx)

    @Column(name = "zsr_number")
    private String zsrNumber;  // ZSR for healthcare providers

    @Column(name = "gln_number")
    private String glnNumber;  // GLN (Global Location Number)

    // Contact
    @Column(name = "contact_person_id")
    private UUID contactPersonId;  // FK to Person

    @Embedded
    private Address billingAddress;

    @Embedded
    private Address correspondenceAddress;

    @Column
    private String email;

    @Column
    private String phone;

    // Financial
    @Column(name = "iban")
    private String iban;

    @Column(name = "vat_number")
    private String vatNumber;

    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays = 30;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerStatus status = PartnerStatus.ACTIVE;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Audit
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private long version;
}
```

### PartnerType Enum

```java
public enum PartnerType {

    // Government / Public
    CANTON("Kanton"),                    // Cantonal authority
    COMMUNE("Gemeinde"),                 // Municipal authority
    FEDERAL_OFFICE("Bundesamt"),         // Federal office
    SOCIAL_SERVICES("Sozialdienst"),     // Social welfare office

    // Institutional
    EMPLOYER("Arbeitgeber"),             // Employer paying for employees
    INSTITUTION("Institution"),          // Elderly home, hospital, etc.
    ASSOCIATION("Verband"),              // Industry association

    // Financial
    INSURANCE_COMPANY("Versicherung"),   // Other insurance company
    REINSURER("Rückversicherer"),        // Reinsurance company
    BANK("Bank"),                        // Banking institution

    // Intermediaries
    BROKER("Makler"),                    // Insurance broker
    AGENT("Agent"),                      // Sales agent
    RESELLER("Wiederverkäufer"),         // Reseller/white-label

    // Service Providers
    HEALTHCARE_PROVIDER("Leistungserbringer"), // Doctor, hospital
    COLLECTION_AGENCY("Inkasso"),        // Debt collection

    // Other
    FAMILY_PAYER("Familienzahler"),      // Family member paying
    OTHER("Andere");                     // Other partner type

    private final String nameDe;

    PartnerType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### PartnerCategory Enum

```java
public enum PartnerCategory {

    PAYER("Zahler"),           // Pays on behalf of subscribers
    SUBSIDIZER("Subventionierer"), // Provides partial subsidies
    GUARANTOR("Garant"),       // Guarantees payment
    INTERMEDIARY("Vermittler"), // Broker/agent role
    PROVIDER("Anbieter"),      // Service provider
    CREDITOR("Gläubiger"),     // We owe them
    DEBTOR("Schuldner");       // They owe us

    private final String nameDe;

    PartnerCategory(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### PartnerStatus Enum

```java
public enum PartnerStatus {
    PROSPECT("Interessent"),   // Potential partner
    ACTIVE("Aktiv"),           // Active relationship
    SUSPENDED("Sistiert"),     // Temporarily inactive
    TERMINATED("Beendet"),     // Relationship ended
    BLOCKED("Gesperrt");       // Blocked (e.g., non-payment)

    private final String nameDe;

    PartnerStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## Related Entity: PaymentArrangement

Tracks WHO pays for WHAT subscription:

```java
@Entity
@Table(name = "payment_arrangements")
public class PaymentArrangement {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    // What is being paid for
    @Column(name = "subscription_id", nullable = false)
    private UUID subscriptionId;  // FK to Coverage/Subscription

    // Who pays
    @Enumerated(EnumType.STRING)
    @Column(name = "payer_type", nullable = false)
    private PayerType payerType;

    @Column(name = "payer_id")
    private UUID payerId;  // FK to Person, Organization, or BusinessPartner

    // Payment details
    @Enumerated(EnumType.STRING)
    @Column(name = "arrangement_type", nullable = false)
    private ArrangementType arrangementType;

    @Column(name = "coverage_percent")
    private BigDecimal coveragePercent;  // null = 100%

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "fixed_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "fixed_amount_currency"))
    })
    private Money fixedAmount;  // Alternative to percent

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "max_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "max_amount_currency"))
    })
    private Money maxAmount;  // Cap on coverage

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Reference
    @Column(name = "external_reference")
    private String externalReference;  // Contract number, decision number

    @Column
    private String notes;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArrangementStatus status = ArrangementStatus.ACTIVE;

    // Audit
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Version
    private long version;
}
```

### PayerType Enum

```java
public enum PayerType {
    SUBSCRIBER("Versicherter"),    // Subscriber pays themselves
    PERSON("Person"),              // Another person pays
    ORGANIZATION("Organisation"),  // Organization pays
    BUSINESS_PARTNER("Partner"),   // Business partner pays
    CANTON("Kanton"),              // Cantonal subsidy
    COMMUNE("Gemeinde");           // Municipal payment

    private final String nameDe;

    PayerType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### ArrangementType Enum

```java
public enum ArrangementType {
    FULL_PAYMENT("Vollzahlung"),       // Payer covers 100%
    PARTIAL_SUBSIDY("Teilsubvention"), // Payer covers percentage
    FIXED_CONTRIBUTION("Festbeitrag"), // Payer covers fixed amount
    REMAINDER("Restbetrag"),           // Payer covers whatever's left
    GUARANTEE("Bürgschaft");           // Payer guarantees payment

    private final String nameDe;

    ArrangementType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### ArrangementStatus Enum

```java
public enum ArrangementStatus {
    PENDING("Ausstehend"),    // Awaiting activation
    ACTIVE("Aktiv"),          // Currently active
    SUSPENDED("Sistiert"),    // Temporarily paused
    EXPIRED("Abgelaufen"),    // Past validity
    TERMINATED("Beendet"),    // Ended early
    REJECTED("Abgelehnt");    // Application rejected

    private final String nameDe;

    ArrangementStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## Example Use Cases

### 1. Premium Subsidy (Prämienverbilligung)

```java
PaymentArrangement subsidy = new PaymentArrangement();
subsidy.setSubscriptionId(healthCoverage.getId());
subsidy.setPayerType(PayerType.CANTON);
subsidy.setPayerId(cantonZurich.getId());
subsidy.setArrangementType(ArrangementType.FIXED_CONTRIBUTION);
subsidy.setFixedAmount(Money.chf(200));  // CHF 200/month
subsidy.setValidFrom(LocalDate.of(2026, 1, 1));
subsidy.setValidTo(LocalDate.of(2026, 12, 31));
subsidy.setExternalReference("PV-2026-123456");
```

### 2. Social Welfare Full Payment

```java
PaymentArrangement welfare = new PaymentArrangement();
welfare.setSubscriptionId(healthCoverage.getId());
welfare.setPayerType(PayerType.BUSINESS_PARTNER);
welfare.setPayerId(socialServicesZurich.getId());
welfare.setArrangementType(ArrangementType.FULL_PAYMENT);
welfare.setValidFrom(LocalDate.of(2026, 1, 1));
welfare.setExternalReference("SOZ-2026-7890");
welfare.setNotes("Sozialhilfe-Empfänger, Klient-Nr. 12345");
```

### 3. Employer Health Insurance

```java
PaymentArrangement employer = new PaymentArrangement();
employer.setSubscriptionId(healthCoverage.getId());
employer.setPayerType(PayerType.ORGANIZATION);
employer.setPayerId(employerAG.getId());
employer.setArrangementType(ArrangementType.PARTIAL_SUBSIDY);
employer.setCoveragePercent(new BigDecimal("50"));  // 50%
employer.setMaxAmount(Money.chf(300));  // Max CHF 300/month
employer.setValidFrom(employmentStart);
```

---

## Database Schema

```sql
CREATE TABLE business_partners (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    partner_number VARCHAR(20) UNIQUE,
    name VARCHAR(255) NOT NULL,
    partner_type VARCHAR(30) NOT NULL,
    partner_category VARCHAR(20),
    uid VARCHAR(20),
    zsr_number VARCHAR(20),
    gln_number VARCHAR(20),
    contact_person_id UUID REFERENCES persons(id),
    billing_street VARCHAR(255),
    billing_postal_code VARCHAR(10),
    billing_city VARCHAR(100),
    billing_country VARCHAR(3) DEFAULT 'CH',
    correspondence_street VARCHAR(255),
    correspondence_postal_code VARCHAR(10),
    correspondence_city VARCHAR(100),
    correspondence_country VARCHAR(3),
    email VARCHAR(255),
    phone VARCHAR(30),
    iban VARCHAR(34),
    vat_number VARCHAR(20),
    payment_terms_days INTEGER DEFAULT 30,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    valid_from DATE,
    valid_to DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_bp_tenant ON business_partners(tenant_id);
CREATE INDEX idx_bp_type ON business_partners(partner_type);
CREATE INDEX idx_bp_status ON business_partners(status);

CREATE TABLE payment_arrangements (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    subscription_id UUID NOT NULL,
    payer_type VARCHAR(20) NOT NULL,
    payer_id UUID,
    arrangement_type VARCHAR(20) NOT NULL,
    coverage_percent DECIMAL(5,2),
    fixed_amount DECIMAL(10,2),
    fixed_amount_currency VARCHAR(3),
    max_amount DECIMAL(10,2),
    max_amount_currency VARCHAR(3),
    valid_from DATE NOT NULL,
    valid_to DATE,
    external_reference VARCHAR(50),
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_pa_subscription ON payment_arrangements(subscription_id);
CREATE INDEX idx_pa_payer ON payment_arrangements(payer_type, payer_id);
CREATE INDEX idx_pa_valid ON payment_arrangements(valid_from, valid_to);
```

---

## Implementation Notes

1. **Module**: BusinessPartner in `govinda-masterdata`, PaymentArrangement in `govinda-contract`

2. **Search**: Support search by name, UID, type, status

3. **Validation**:
   - UID format validation
   - IBAN validation
   - At least one address required

4. **History**: Consider bitemporality for payment arrangements (regulatory compliance)

5. **Integration**:
   - Link to billing module for invoice generation
   - Link to accounting for payment tracking

---

*Status: Draft*
*Priority: HIGH*

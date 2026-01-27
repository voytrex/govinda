# GAP-11: Cross-Border Cases

## Problem Statement

Missing support for special cross-border scenarios:
- **Grenzgänger** (cross-border commuters)
- **Fürstentum Liechtenstein** (special KVG agreement)
- **Enclaves**: Büsingen (DE), Campione d'Italia (IT)
- **Posted workers** (Entsandte)
- **Foreign students**

---

## 1. Grenzgänger (Cross-Border Commuters)

### Definition
Person who lives in one country and works in another, returning home at least weekly.

### Relevant Countries
- Germany (DE)
- France (FR)
- Italy (IT)
- Austria (AT)

### Insurance Options

| Residence | Work | Options |
|-----------|------|---------|
| EU/EFTA | Switzerland | KVG **or** residence country |
| Switzerland | EU/EFTA | KVG mandatory |

### Choice Right (Optionsrecht)

Grenzgänger from DE/FR/IT/AT working in CH can choose:
1. Swiss KVG
2. Home country public insurance
3. German private insurance (DE residents only)

**Exercise deadline**: 3 months from employment start

### Model

```java
@Entity
@Table(name = "cross_border_statuses")
public class CrossBorderStatus {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_type", nullable = false)
    private CrossBorderType statusType;

    // Residence
    @Enumerated(EnumType.STRING)
    @Column(name = "residence_country", nullable = false)
    private Country residenceCountry;

    @Column(name = "residence_address")
    private String residenceAddress;

    // Work
    @Enumerated(EnumType.STRING)
    @Column(name = "work_country", nullable = false)
    private Country workCountry;

    @Column(name = "employer_name")
    private String employerName;

    @Column(name = "employer_uid")
    private String employerUid;

    // Insurance choice (for Grenzgänger)
    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_choice")
    private InsuranceChoice insuranceChoice;

    @Column(name = "choice_made_date")
    private LocalDate choiceMadeDate;

    @Column(name = "choice_deadline")
    private LocalDate choiceDeadline;

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Documentation
    @Column(name = "permit_type")
    private String permitType;  // G permit for Grenzgänger

    @Column(name = "permit_number")
    private String permitNumber;

    @Column(name = "e106_certificate")
    private String e106Certificate;  // EU coordination form

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CrossBorderStatusState state = CrossBorderStatusState.ACTIVE;

    @Version
    private long version;
}

public enum CrossBorderType {
    GRENZGAENGER("Grenzgänger"),           // Cross-border commuter
    POSTED_WORKER("Entsandter"),            // Posted worker
    FOREIGN_STUDENT("Ausländischer Student"),
    LIECHTENSTEIN_RESIDENT("Liechtensteiner"),
    ENCLAVE_RESIDENT("Enklave-Bewohner"),
    SWISS_ABROAD("Auslandschweizer");

    private final String nameDe;

    CrossBorderType(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum InsuranceChoice {
    SWISS_KVG("Schweizer KVG"),
    RESIDENCE_COUNTRY("Wohnsitzland"),
    GERMAN_PRIVATE("Deutsche PKV");  // DE residents only

    private final String nameDe;

    InsuranceChoice(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum Country {
    CH("Schweiz"),
    DE("Deutschland"),
    FR("Frankreich"),
    IT("Italien"),
    AT("Österreich"),
    LI("Liechtenstein"),
    // ... other countries
    OTHER("Andere");

    private final String nameDe;

    Country(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 2. Fürstentum Liechtenstein

### Special Agreement
Liechtenstein has a **customs and currency union** with Switzerland and participates in the Swiss healthcare system.

### Key Rules

| Aspect | Rule |
|--------|------|
| KVG application | Yes, same as Swiss residents |
| Insurer | Swiss insurers or FL-licensed |
| Premium region | Liechtenstein = own region |
| BAG supervision | Joint CH-FL oversight |
| AHV/IV | Integrated system |

### Premium Region

```java
// Extend Canton enum or create separate
public enum LiechtensteinMunicipality {
    VADUZ,
    SCHAAN,
    BALZERS,
    TRIESEN,
    TRIESENBERG,
    ESCHEN,
    MAUREN,
    GAMPRIN,
    RUGGELL,
    SCHELLENBERG,
    PLANKEN;

    public String getPremiumRegion() {
        return "FL";  // Single premium region
    }
}

// In address handling
public PremiumRegion getPremiumRegion(Address address) {
    if (address.getCountry() == Country.LI) {
        return new PremiumRegion("FL", "Liechtenstein");
    }
    // Swiss cantons
    return premiumRegionService.forCanton(address.getCanton());
}
```

### Model Extension

```java
// Extend Address to support Liechtenstein
public class Address {
    // ... existing fields ...

    @Enumerated(EnumType.STRING)
    @Column(name = "country")
    private Country country = Country.CH;

    // For Liechtenstein
    @Column(name = "li_municipality")
    private String liMunicipality;

    public boolean isLiechtenstein() {
        return country == Country.LI;
    }

    public boolean isSwissOrLiechtenstein() {
        return country == Country.CH || country == Country.LI;
    }
}
```

---

## 3. Enclaves: Büsingen & Campione

### Büsingen am Hochrhein (Germany)

| Aspect | Details |
|--------|---------|
| Country | Germany (legally) |
| Location | Completely surrounded by CH (Canton SH) |
| Currency | CHF (de facto) |
| Customs | Swiss customs area |
| KVG | **Optional** - can choose CH or DE |
| Population | ~1,500 |

### Campione d'Italia (Italy)

| Aspect | Details |
|--------|---------|
| Country | Italy (legally) |
| Location | Surrounded by CH (Canton TI) |
| Currency | CHF (since 2019) |
| Customs | Swiss customs area (since 2020) |
| KVG | **Optional** - can choose CH or IT |
| Population | ~2,000 |

### Model

```java
public enum EnclaveType {
    BUESINGEN("Büsingen am Hochrhein", Country.DE, Canton.SH),
    CAMPIONE("Campione d'Italia", Country.IT, Canton.TI);

    private final String name;
    private final Country legalCountry;
    private final Canton surroundingCanton;

    EnclaveType(String name, Country legalCountry, Canton surroundingCanton) {
        this.name = name;
        this.legalCountry = legalCountry;
        this.surroundingCanton = surroundingCanton;
    }

    public Canton getSurroundingCanton() {
        return surroundingCanton;
    }
}

// Address extension
public class Address {
    // ... existing fields ...

    @Enumerated(EnumType.STRING)
    @Column(name = "enclave_type")
    private EnclaveType enclaveType;

    public boolean isEnclave() {
        return enclaveType != null;
    }

    /**
     * For premium calculation, use surrounding canton
     */
    public Canton getEffectiveCanton() {
        if (isEnclave()) {
            return enclaveType.getSurroundingCanton();
        }
        return canton;
    }
}

// Special handling for enclave residents
public class EnclaveInsuranceService {

    public boolean hasInsuranceChoice(Person person) {
        Address addr = person.currentAddress();
        return addr != null && addr.isEnclave();
    }

    public List<InsuranceChoice> getAvailableChoices(EnclaveType enclave) {
        return switch (enclave) {
            case BUESINGEN -> List.of(
                InsuranceChoice.SWISS_KVG,
                InsuranceChoice.RESIDENCE_COUNTRY,  // German
                InsuranceChoice.GERMAN_PRIVATE
            );
            case CAMPIONE -> List.of(
                InsuranceChoice.SWISS_KVG,
                InsuranceChoice.RESIDENCE_COUNTRY   // Italian
            );
        };
    }
}
```

---

## 4. Posted Workers (Entsandte)

### Definition
Employee temporarily sent abroad by employer, remaining in home country's social security.

### EU Coordination

| Document | Purpose |
|----------|---------|
| A1 certificate | Proves social security membership |
| E106/S1 | Healthcare entitlement in residence country |
| EHIC | Emergency care in EU |

### Duration Limits

| Situation | Max Duration |
|-----------|--------------|
| Standard posting | 24 months |
| Extension possible | Yes, with agreement |
| Multiple postings | Rules on aggregation |

### Model

```java
@Entity
@Table(name = "posting_certificates")
public class PostingCertificate {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type", nullable = false)
    private CertificateType certificateType;

    @Column(name = "certificate_number", nullable = false)
    private String certificateNumber;

    // Countries
    @Enumerated(EnumType.STRING)
    @Column(name = "issuing_country", nullable = false)
    private Country issuingCountry;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination_country", nullable = false)
    private Country destinationCountry;

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo;

    // Employer
    @Column(name = "employer_name")
    private String employerName;

    @Column(name = "employer_country")
    private Country employerCountry;

    // Document
    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "issued_at")
    private LocalDate issuedAt;

    @Column(name = "issuing_authority")
    private String issuingAuthority;

    @Version
    private long version;
}

public enum CertificateType {
    A1("A1 - Sozialversicherung"),
    E106("E106 - Krankenversicherung"),
    S1("S1 - Krankenversicherung neu"),
    EHIC("EHIC - Europäische Krankenversicherungskarte");

    private final String nameDe;

    CertificateType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 5. Foreign Students

### Rules for Foreign Students in CH

| Duration | Insurance Rule |
|----------|----------------|
| < 3 months | Travel insurance sufficient |
| ≥ 3 months | KVG mandatory |
| Exchange program | May keep home insurance (with proof) |

### Exemption Possibility

Students can be exempt from KVG if they have equivalent foreign coverage:
- Must prove comparable coverage
- Request exemption from cantonal authority
- Valid for study duration

### Model

```java
public class StudentInsuranceExemption {

    @Id
    private UUID id;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "university_name")
    private String universityName;

    @Column(name = "program_type")
    private String programType;  // Bachelor, Master, PhD, Exchange

    @Column(name = "program_start")
    private LocalDate programStart;

    @Column(name = "program_end")
    private LocalDate programEnd;

    // Foreign insurance
    @Column(name = "foreign_insurer")
    private String foreignInsurer;

    @Column(name = "foreign_policy_number")
    private String foreignPolicyNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_country")
    private Country insuranceCountry;

    // Exemption
    @Enumerated(EnumType.STRING)
    @Column(name = "exemption_status")
    private ExemptionDecision exemptionStatus;

    @Column(name = "decision_date")
    private LocalDate decisionDate;

    @Column(name = "decision_authority")
    private String decisionAuthority;  // Cantonal health dept

    @Column(name = "exemption_valid_to")
    private LocalDate exemptionValidTo;
}

public enum ExemptionDecision {
    PENDING,
    GRANTED,
    DENIED,
    EXPIRED
}
```

---

## Database Schema

```sql
CREATE TABLE cross_border_statuses (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID NOT NULL,
    status_type VARCHAR(30) NOT NULL,
    residence_country VARCHAR(3) NOT NULL,
    residence_address TEXT,
    work_country VARCHAR(3) NOT NULL,
    employer_name VARCHAR(255),
    employer_uid VARCHAR(20),
    insurance_choice VARCHAR(20),
    choice_made_date DATE,
    choice_deadline DATE,
    valid_from DATE NOT NULL,
    valid_to DATE,
    permit_type VARCHAR(10),
    permit_number VARCHAR(50),
    e106_certificate VARCHAR(50),
    state VARCHAR(20) DEFAULT 'ACTIVE',
    version BIGINT DEFAULT 0
);

CREATE TABLE posting_certificates (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID NOT NULL,
    certificate_type VARCHAR(10) NOT NULL,
    certificate_number VARCHAR(50) NOT NULL,
    issuing_country VARCHAR(3) NOT NULL,
    destination_country VARCHAR(3) NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    employer_name VARCHAR(255),
    employer_country VARCHAR(3),
    document_id UUID,
    issued_at DATE,
    issuing_authority VARCHAR(255),
    version BIGINT DEFAULT 0
);

-- Add enclave support to addresses
ALTER TABLE addresses ADD COLUMN enclave_type VARCHAR(20);
ALTER TABLE addresses ADD COLUMN country VARCHAR(3) DEFAULT 'CH';
ALTER TABLE addresses ADD COLUMN li_municipality VARCHAR(50);
```

---

## References

- [BAG - Grenzgänger](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-im-ausland/versicherungspflicht-grenzgaenger.html)
- [EU Coordination Regulations](https://ec.europa.eu/social/main.jsp?catId=849)
- [Liechtenstein Health Insurance](https://www.llv.li/inhalt/11342/amtsstellen/krankenversicherung)

---

*Status: Draft*
*Priority: MEDIUM*

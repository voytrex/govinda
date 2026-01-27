# GAP-15: Reference Data & Industry Master Data

## Problem Statement

Missing integration with Swiss healthcare reference data systems:
- **GLN** (Global Location Number) - participant identification
- **Refdata** - medication and participant master data
- **SASIS Datenpool** - industry data exchange
- **Tarifpool** - tariff reference data
- **MedReg** - medical professional register
- **Other catalogs** - ICD, CHOP, DRG, etc.

---

## 1. GLN (Global Location Number)

### What is GLN?

**GLN** is the global standard for identifying:
- Healthcare providers (doctors, hospitals, pharmacies)
- Health insurers
- Other participants in healthcare

### GLN Structure

| Part | Length | Description |
|------|--------|-------------|
| GS1 Prefix | 3 | Country (Switzerland = 760, 761, 762) |
| Company | 4-9 | Organization code |
| Location | 0-5 | Specific location |
| Check digit | 1 | Validation |

**Total: 13 digits**

### GLN Types in Healthcare

| Type | Example | Used for |
|------|---------|----------|
| Organization | 7601001xxxxxx | Insurer, hospital |
| Location | 7601001xxxxxx | Branch, department |
| Person | 7601000xxxxxx | Individual provider |

### Model

```java
@Entity
@Table(name = "gln_registry")
public class GlnEntry {

    @Id
    private UUID id;

    @Column(name = "gln", unique = true, nullable = false)
    private String gln;  // 13 digits

    @Enumerated(EnumType.STRING)
    @Column(name = "gln_type", nullable = false)
    private GlnType glnType;

    // Identification
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_additional")
    private String nameAdditional;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_type")
    private ParticipantType participantType;

    // For persons
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "title")
    private String title;

    // Other identifiers
    @Column(name = "zsr_number")
    private String zsrNumber;  // Zahlstellenregister

    @Column(name = "uid")
    private String uid;  // Swiss UID

    @Column(name = "bag_number")
    private String bagNumber;  // For insurers

    @Column(name = "medreg_number")
    private String medregNumber;  // MedReg ID

    // Contact
    @Embedded
    private Address address;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    // Status
    @Column(name = "active")
    private boolean active = true;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Sync
    @Column(name = "last_sync_at")
    private Instant lastSyncAt;

    @Column(name = "source")
    private String source;  // REFDATA, SASIS, etc.

    @Version
    private long version;
}

public enum GlnType {
    ORGANIZATION("Organisation"),
    LOCATION("Standort"),
    PERSON("Person");

    private final String nameDe;

    GlnType(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum ParticipantType {
    // Insurers
    HEALTH_INSURER("Krankenversicherer"),
    ACCIDENT_INSURER("Unfallversicherer"),
    REINSURER("Rückversicherer"),

    // Providers
    HOSPITAL("Spital"),
    CLINIC("Klinik"),
    MEDICAL_PRACTICE("Arztpraxis"),
    GROUP_PRACTICE("Gruppenpraxis"),
    PHARMACY("Apotheke"),
    DRUGSTORE("Drogerie"),
    LABORATORY("Labor"),
    PHYSIOTHERAPY("Physiotherapie"),
    DENTIST("Zahnarzt"),
    NURSING_HOME("Pflegeheim"),
    SPITEX("Spitex"),
    AMBULANCE("Rettungsdienst"),

    // Other
    AUTHORITY("Behörde"),
    ASSOCIATION("Verband"),
    OTHER("Andere");

    private final String nameDe;

    ParticipantType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 2. Refdata

### What is Refdata?

**Refdata AG** operates the official Swiss healthcare reference data:
- Partner database (healthcare participants)
- GTIN/Pharmacode database (medications)
- Product database

Website: [refdata.ch](https://www.refdata.ch/)

### Refdata Services

| Service | Content |
|---------|---------|
| PARTNER | Healthcare participants with GLN |
| ARTICLE | Medications with GTIN, Pharmacode |
| PRODUCT | Product master data |

### Partner Data Model

```java
@Entity
@Table(name = "refdata_partners")
public class RefdataPartner {

    @Id
    private UUID id;

    @Column(name = "gln", unique = true, nullable = false)
    private String gln;

    @Column(name = "refdata_id")
    private String refdataId;

    // Names
    @Column(name = "name_1")
    private String name1;

    @Column(name = "name_2")
    private String name2;

    @Column(name = "name_3")
    private String name3;

    // Address
    @Column(name = "street")
    private String street;

    @Column(name = "house_number")
    private String houseNumber;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "city")
    private String city;

    @Column(name = "canton")
    private String canton;

    @Column(name = "country")
    private String country;

    // Classification
    @Enumerated(EnumType.STRING)
    @Column(name = "partner_type")
    private RefdataPartnerType partnerType;

    @Column(name = "specialization")
    private String specialization;

    // Identifiers
    @Column(name = "zsr_number")
    private String zsrNumber;

    @Column(name = "uid")
    private String uid;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RefdataStatus status;

    // Sync metadata
    @Column(name = "refdata_version")
    private String refdataVersion;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Column(name = "synced_at")
    private Instant syncedAt;
}

public enum RefdataPartnerType {
    ARZT("Arzt"),
    APOTHEKE("Apotheke"),
    DROGERIE("Drogerie"),
    SPITAL("Spital"),
    PFLEGEHEIM("Pflegeheim"),
    LABOR("Labor"),
    PHYSIOTHERAPIE("Physiotherapie"),
    ZAHNARZT("Zahnarzt"),
    CHIROPRAKTIK("Chiropraktik"),
    HEBAMME("Hebamme"),
    PSYCHOTHERAPIE("Psychotherapie"),
    ERNAEHRUNGSBERATUNG("Ernährungsberatung"),
    LOGOPAEDIE("Logopädie"),
    ERGOTHERAPIE("Ergotherapie"),
    SPITEX("Spitex"),
    OTHER("Andere");

    private final String nameDe;

    RefdataPartnerType(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum RefdataStatus {
    ACTIVE("Aktiv"),
    INACTIVE("Inaktiv"),
    DELETED("Gelöscht");

    private final String nameDe;

    RefdataStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### Article Data (Medications)

```java
@Entity
@Table(name = "refdata_articles")
public class RefdataArticle {

    @Id
    private UUID id;

    // Identifiers
    @Column(name = "gtin", unique = true)
    private String gtin;  // Global Trade Item Number (13 digits)

    @Column(name = "pharmacode")
    private String pharmacode;  // Swiss Pharmacode (7 digits)

    @Column(name = "swissmedic_number")
    private String swissmedicNumber;  // Zulassungsnummer

    // Product info
    @Column(name = "description_de")
    private String descriptionDe;

    @Column(name = "description_fr")
    private String descriptionFr;

    @Column(name = "description_it")
    private String descriptionIt;

    @Column(name = "atc_code")
    private String atcCode;  // Anatomical Therapeutic Chemical

    // Pricing
    @Column(name = "public_price")
    private BigDecimal publicPrice;  // Publikumspreis

    @Column(name = "ex_factory_price")
    private BigDecimal exFactoryPrice;  // Fabrikabgabepreis

    @Column(name = "price_valid_from")
    private LocalDate priceValidFrom;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "sl_status")
    private SlStatus slStatus;  // Spezialitätenliste status

    @Column(name = "sl_entry_date")
    private LocalDate slEntryDate;

    // Packaging
    @Column(name = "pack_size")
    private String packSize;

    @Column(name = "unit")
    private String unit;

    // Manufacturer
    @Column(name = "manufacturer_gln")
    private String manufacturerGln;

    @Column(name = "manufacturer_name")
    private String manufacturerName;

    // Sync
    @Column(name = "synced_at")
    private Instant syncedAt;
}

public enum SlStatus {
    LISTED("Gelistet"),           // On Spezialitätenliste
    NOT_LISTED("Nicht gelistet"),
    DELETED("Gelöscht");

    private final String nameDe;

    SlStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 3. SASIS Datenpool

### What is SASIS Datenpool?

SASIS AG operates the **Datenpool** - a central data exchange platform:
- Industry statistics
- Anonymized claims data
- Reference data distribution

### Datenpool Services

| Service | Content |
|---------|---------|
| VERSICHERTE | Insured population statistics |
| LEISTUNGEN | Service/claims data |
| PRAEMIEN | Premium data |
| KOSTEN | Cost data |

### Integration Model

```java
public interface SasisDatapoolService {

    /**
     * Submit claims data for statistics
     */
    void submitClaimsData(ClaimsDataSubmission submission);

    /**
     * Query industry benchmarks
     */
    BenchmarkData getBenchmarks(BenchmarkQuery query);

    /**
     * Get population statistics
     */
    PopulationStatistics getPopulationStats(Canton canton, int year);
}

@Entity
@Table(name = "datapool_submissions")
public class DatapoolSubmission {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_type", nullable = false)
    private DatapoolSubmissionType submissionType;

    @Column(name = "period_year", nullable = false)
    private int periodYear;

    @Column(name = "period_quarter")
    private Integer periodQuarter;

    @Column(name = "submission_date", nullable = false)
    private LocalDate submissionDate;

    @Column(name = "record_count")
    private Integer recordCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @Column(name = "sasis_reference")
    private String sasisReference;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Version
    private long version;
}

public enum DatapoolSubmissionType {
    CLAIMS("Leistungsdaten"),
    COSTS("Kostendaten"),
    PREMIUMS("Prämiendaten"),
    POPULATION("Versichertendaten");

    private final String nameDe;

    DatapoolSubmissionType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 4. Tarifpool / Tariff Data

### Swiss Healthcare Tariffs

| Tariff | Domain | Description |
|--------|--------|-------------|
| TARMED | Ambulatory | Medical services tariff |
| SwissDRG | Hospital | Diagnosis Related Groups |
| ST Reha | Rehabilitation | Rehab tariff |
| TARPSY | Psychiatry | Psychiatric tariff |
| Analysenliste | Laboratory | Lab test tariff |
| Mittel- und Gegenständeliste | Materials | Medical supplies |
| Arzneimittelliste | Medications | Drug prices (SL) |

### Tariff Data Model

```java
@Entity
@Table(name = "tariff_positions")
public class TariffPosition {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type", nullable = false)
    private TariffType tariffType;

    @Column(name = "tariff_version", nullable = false)
    private String tariffVersion;

    @Column(name = "position_code", nullable = false)
    private String positionCode;

    // Description
    @Column(name = "description_de")
    private String descriptionDe;

    @Column(name = "description_fr")
    private String descriptionFr;

    @Column(name = "description_it")
    private String descriptionIt;

    // TARMED specific
    @Column(name = "tax_points_medical")
    private BigDecimal taxPointsMedical;  // AL (Ärztliche Leistung)

    @Column(name = "tax_points_technical")
    private BigDecimal taxPointsTechnical;  // TL (Technische Leistung)

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // DRG specific
    @Column(name = "cost_weight")
    private BigDecimal costWeight;

    @Column(name = "mean_stay_days")
    private BigDecimal meanStayDays;

    // General
    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Restrictions
    @Column(name = "requires_qualification")
    private String requiresQualification;

    @Column(name = "cumulation_rules")
    private String cumulationRules;

    // Sync
    @Column(name = "synced_at")
    private Instant syncedAt;
}

public enum TariffType {
    TARMED("TARMED - Ambulant"),
    SWISSDRG("SwissDRG - Akutsomatik"),
    ST_REHA("ST Reha - Rehabilitation"),
    TARPSY("TARPSY - Psychiatrie"),
    ANALYSENLISTE("AL - Labor"),
    MIGEL("MiGeL - Hilfsmittel"),
    SL("SL - Spezialitätenliste"),
    PHYSIO("Physio-Tarif"),
    DENTAL("SSO-Tarif - Zahnarzt");

    private final String nameDe;

    TariffType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### Taxpunkt Values

```java
@Entity
@Table(name = "taxpunkt_values")
public class TaxpunktValue {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type", nullable = false)
    private TariffType tariffType;

    @Column(name = "canton")
    private Canton canton;  // null = national

    @Column(name = "taxpunkt_value", nullable = false)
    private BigDecimal taxpunktValue;  // CHF per point

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "contract_partner")
    private String contractPartner;  // Which agreement

    @Version
    private long version;
}

// Example: TARMED taxpunkt in Zürich ≈ CHF 0.89
```

---

## 5. MedReg (Medical Register)

### What is MedReg?

**MedReg** - official Swiss register of medical professionals:
- Doctors, pharmacists, dentists, etc.
- Diplomas and specializations
- Practice authorizations

Website: [medreg.admin.ch](https://www.medreg.admin.ch/)

### MedReg Data

```java
@Entity
@Table(name = "medreg_professionals")
public class MedRegProfessional {

    @Id
    private UUID id;

    @Column(name = "medreg_id", unique = true, nullable = false)
    private String medregId;

    @Column(name = "gln")
    private String gln;

    // Personal
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    private String gender;

    // Profession
    @Enumerated(EnumType.STRING)
    @Column(name = "profession")
    private MedicalProfession profession;

    @Column(name = "diploma")
    private String diploma;

    @Column(name = "diploma_date")
    private LocalDate diplomaDate;

    @Column(name = "diploma_country")
    private String diplomaCountry;

    // Specializations
    @ElementCollection
    @CollectionTable(name = "medreg_specializations")
    private List<String> specializations = new ArrayList<>();

    // Practice authorization
    @Column(name = "authorization_canton")
    private String authorizationCanton;

    @Column(name = "authorization_valid_from")
    private LocalDate authorizationValidFrom;

    @Column(name = "authorization_valid_to")
    private LocalDate authorizationValidTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "authorization_status")
    private AuthorizationStatus authorizationStatus;

    // Sync
    @Column(name = "synced_at")
    private Instant syncedAt;
}

public enum MedicalProfession {
    ARZT("Arzt/Ärztin"),
    ZAHNARZT("Zahnarzt/Zahnärztin"),
    APOTHEKER("Apotheker/in"),
    CHIROPRAKTOR("Chiropraktor/in"),
    TIERARZT("Tierarzt/Tierärztin");

    private final String nameDe;

    MedicalProfession(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum AuthorizationStatus {
    ACTIVE("Aktiv"),
    SUSPENDED("Suspendiert"),
    REVOKED("Entzogen"),
    EXPIRED("Abgelaufen");

    private final String nameDe;

    AuthorizationStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 6. Classification Catalogs

### ICD-10 (Diagnoses)

```java
@Entity
@Table(name = "icd10_codes")
public class Icd10Code {

    @Id
    private UUID id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;  // e.g., "J06.9"

    @Column(name = "code_without_dot")
    private String codeWithoutDot;  // e.g., "J069"

    @Column(name = "description_de")
    private String descriptionDe;

    @Column(name = "description_fr")
    private String descriptionFr;

    @Column(name = "chapter")
    private String chapter;

    @Column(name = "block")
    private String block;

    @Column(name = "category")
    private String category;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "version")
    private String catalogVersion;  // e.g., "ICD-10-GM 2024"
}
```

### CHOP (Procedures)

```java
@Entity
@Table(name = "chop_codes")
public class ChopCode {

    @Id
    private UUID id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;  // e.g., "39.61.11"

    @Column(name = "description_de")
    private String descriptionDe;

    @Column(name = "description_fr")
    private String descriptionFr;

    @Column(name = "chapter")
    private String chapter;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "version")
    private String catalogVersion;  // e.g., "CHOP 2024"
}
```

---

## 7. Sync Service

```java
public interface ReferenceDataSyncService {

    /**
     * Sync GLN/Partner data from Refdata
     */
    SyncResult syncRefdataPartners();

    /**
     * Sync medication data from Refdata
     */
    SyncResult syncRefdataArticles();

    /**
     * Sync tariff data (TARMED, DRG, etc.)
     */
    SyncResult syncTariffs(TariffType tariffType);

    /**
     * Sync MedReg professionals
     */
    SyncResult syncMedReg();

    /**
     * Sync ICD-10 catalog
     */
    SyncResult syncIcd10(String version);

    /**
     * Sync CHOP catalog
     */
    SyncResult syncChop(String version);

    /**
     * Get sync status
     */
    SyncStatus getSyncStatus(String dataType);
}

@Entity
@Table(name = "reference_data_sync_log")
public class ReferenceDataSyncLog {

    @Id
    private UUID id;

    @Column(name = "data_type", nullable = false)
    private String dataType;

    @Column(name = "sync_started_at", nullable = false)
    private Instant syncStartedAt;

    @Column(name = "sync_completed_at")
    private Instant syncCompletedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncResultStatus status;

    @Column(name = "records_processed")
    private Integer recordsProcessed;

    @Column(name = "records_added")
    private Integer recordsAdded;

    @Column(name = "records_updated")
    private Integer recordsUpdated;

    @Column(name = "records_deleted")
    private Integer recordsDeleted;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "source_version")
    private String sourceVersion;
}

public enum SyncResultStatus {
    SUCCESS,
    PARTIAL,
    FAILED
}
```

---

## Database Schema

```sql
-- GLN Registry
CREATE TABLE gln_registry (
    id UUID PRIMARY KEY,
    gln VARCHAR(13) UNIQUE NOT NULL,
    gln_type VARCHAR(20) NOT NULL,
    name VARCHAR(255) NOT NULL,
    name_additional VARCHAR(255),
    participant_type VARCHAR(30),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    title VARCHAR(50),
    zsr_number VARCHAR(20),
    uid VARCHAR(20),
    bag_number VARCHAR(10),
    medreg_number VARCHAR(20),
    street VARCHAR(255),
    postal_code VARCHAR(10),
    city VARCHAR(100),
    canton VARCHAR(2),
    country VARCHAR(3),
    email VARCHAR(255),
    phone VARCHAR(30),
    active BOOLEAN DEFAULT TRUE,
    valid_from DATE,
    valid_to DATE,
    last_sync_at TIMESTAMP,
    source VARCHAR(50),
    version BIGINT DEFAULT 0
);

-- Refdata Articles (Medications)
CREATE TABLE refdata_articles (
    id UUID PRIMARY KEY,
    gtin VARCHAR(13) UNIQUE,
    pharmacode VARCHAR(7),
    swissmedic_number VARCHAR(10),
    description_de TEXT,
    description_fr TEXT,
    description_it TEXT,
    atc_code VARCHAR(10),
    public_price DECIMAL(10,2),
    ex_factory_price DECIMAL(10,2),
    price_valid_from DATE,
    sl_status VARCHAR(20),
    sl_entry_date DATE,
    pack_size VARCHAR(50),
    unit VARCHAR(20),
    manufacturer_gln VARCHAR(13),
    manufacturer_name VARCHAR(255),
    synced_at TIMESTAMP
);

-- Tariff Positions
CREATE TABLE tariff_positions (
    id UUID PRIMARY KEY,
    tariff_type VARCHAR(20) NOT NULL,
    tariff_version VARCHAR(20) NOT NULL,
    position_code VARCHAR(20) NOT NULL,
    description_de TEXT,
    description_fr TEXT,
    description_it TEXT,
    tax_points_medical DECIMAL(10,4),
    tax_points_technical DECIMAL(10,4),
    duration_minutes INTEGER,
    cost_weight DECIMAL(10,4),
    mean_stay_days DECIMAL(10,2),
    base_price DECIMAL(10,2),
    valid_from DATE NOT NULL,
    valid_to DATE,
    requires_qualification VARCHAR(100),
    cumulation_rules TEXT,
    synced_at TIMESTAMP,
    UNIQUE(tariff_type, tariff_version, position_code)
);

-- ICD-10 Codes
CREATE TABLE icd10_codes (
    id UUID PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    code_without_dot VARCHAR(10),
    description_de TEXT,
    description_fr TEXT,
    chapter VARCHAR(10),
    block VARCHAR(20),
    category VARCHAR(10),
    valid_from DATE,
    valid_to DATE,
    catalog_version VARCHAR(20)
);

-- Sync Log
CREATE TABLE reference_data_sync_log (
    id UUID PRIMARY KEY,
    data_type VARCHAR(50) NOT NULL,
    sync_started_at TIMESTAMP NOT NULL,
    sync_completed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    records_processed INTEGER,
    records_added INTEGER,
    records_updated INTEGER,
    records_deleted INTEGER,
    error_message TEXT,
    source_version VARCHAR(50)
);

CREATE INDEX idx_gln_type ON gln_registry(participant_type);
CREATE INDEX idx_gln_canton ON gln_registry(canton);
CREATE INDEX idx_ra_atc ON refdata_articles(atc_code);
CREATE INDEX idx_tp_type ON tariff_positions(tariff_type);
CREATE INDEX idx_icd_chapter ON icd10_codes(chapter);
```

---

## References

- [Refdata AG](https://www.refdata.ch/)
- [SASIS AG](https://www.sasis.ch/)
- [MedReg](https://www.medreg.admin.ch/)
- [BAG Spezialitätenliste](https://www.spezialitaetenliste.ch/)
- [SwissDRG](https://www.swissdrg.org/)
- [TARMED](https://www.fmh.ch/themen/tarife/tarmed.cfm)

---

*Status: Draft*
*Priority: MEDIUM*

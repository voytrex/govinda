# GAP-16: Legal Documents, AVB & Tariff Versioning

## Problem Statement

Missing support for:
- **AGB** (Allgemeine Geschäftsbedingungen) - general business terms
- **AVB** (Allgemeine Versicherungsbedingungen) - insurance conditions
- **Tariff versioning** - TARMED → TARDOC transition
- Document versioning and validity periods
- Customer acceptance tracking

---

## 1. Legal Document Management

### Document Types

| Type | German | Purpose |
|------|--------|---------|
| AGB | Allgemeine Geschäftsbedingungen | General business terms |
| AVB | Allgemeine Versicherungsbedingungen | Insurance conditions |
| BB | Besondere Bedingungen | Special conditions |
| ZB | Zusatzbedingungen | Additional conditions |
| PB | Produktbedingungen | Product-specific terms |
| TB | Tarifbestimmungen | Tariff rules |
| DSE | Datenschutzerklärung | Privacy policy |
| WB | Widerrufsbelehrung | Cancellation policy |

### Model

```java
@Entity
@Table(name = "legal_documents")
public class LegalDocument {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private LegalDocumentType documentType;

    @Column(name = "document_code", nullable = false)
    private String documentCode;  // e.g., "AVB-KVG-2026"

    @Column(name = "version", nullable = false)
    private String version;  // e.g., "2026.1"

    // Content
    @Column(name = "title_de", nullable = false)
    private String titleDe;

    @Column(name = "title_fr")
    private String titleFr;

    @Column(name = "title_it")
    private String titleIt;

    @Column(name = "content_de", columnDefinition = "TEXT")
    private String contentDe;

    @Column(name = "content_fr", columnDefinition = "TEXT")
    private String contentFr;

    @Column(name = "content_it", columnDefinition = "TEXT")
    private String contentIt;

    // File references
    @Column(name = "pdf_de_id")
    private UUID pdfDeId;

    @Column(name = "pdf_fr_id")
    private UUID pdfFrId;

    @Column(name = "pdf_it_id")
    private UUID pdfItId;

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Approval
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status = DocumentStatus.DRAFT;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    // Regulatory
    @Column(name = "bag_approved")
    private Boolean bagApproved;  // For KVG products

    @Column(name = "bag_approval_date")
    private LocalDate bagApprovalDate;

    @Column(name = "bag_reference")
    private String bagReference;

    // Applicability
    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to_domain")
    private ServiceDomain appliesToDomain;

    @ElementCollection
    @CollectionTable(name = "legal_document_products")
    @Column(name = "product_id")
    private Set<UUID> appliesToProducts = new HashSet<>();

    // Versioning
    @Column(name = "replaces_document_id")
    private UUID replacesDocumentId;

    @Column(name = "replaced_by_document_id")
    private UUID replacedByDocumentId;

    // Change tracking
    @Column(name = "change_summary_de")
    private String changeSummaryDe;

    @Column(name = "change_summary_fr")
    private String changeSummaryFr;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private long version_;
}

public enum LegalDocumentType {
    AGB("Allgemeine Geschäftsbedingungen"),
    AVB_KVG("AVB Grundversicherung"),
    AVB_VVG("AVB Zusatzversicherung"),
    BB("Besondere Bedingungen"),
    ZB("Zusatzbedingungen"),
    PB("Produktbedingungen"),
    TB("Tarifbestimmungen"),
    DSE("Datenschutzerklärung"),
    WB("Widerrufsbelehrung"),
    MERKBLATT("Merkblatt"),
    LEISTUNGSUEBERSICHT("Leistungsübersicht");

    private final String nameDe;

    LegalDocumentType(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum DocumentStatus {
    DRAFT("Entwurf"),
    IN_REVIEW("In Prüfung"),
    APPROVED("Genehmigt"),
    ACTIVE("Aktiv"),
    SUPERSEDED("Ersetzt"),
    ARCHIVED("Archiviert");

    private final String nameDe;

    DocumentStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### Customer Document Acceptance

```java
@Entity
@Table(name = "document_acceptances")
public class DocumentAcceptance {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    // Who accepted
    @Column(name = "person_id")
    private UUID personId;

    @Column(name = "organization_id")
    private UUID organizationId;

    // What was accepted
    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "document_version", nullable = false)
    private String documentVersion;

    // Context
    @Column(name = "coverage_id")
    private UUID coverageId;

    @Column(name = "policy_id")
    private UUID policyId;

    @Column(name = "application_id")
    private UUID applicationId;

    // Acceptance details
    @Column(name = "accepted_at", nullable = false)
    private Instant acceptedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "acceptance_method", nullable = false)
    private AcceptanceMethod acceptanceMethod;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    // Digital signature (if applicable)
    @Column(name = "signature_hash")
    private String signatureHash;

    @Column(name = "certificate_id")
    private String certificateId;

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "superseded_by_id")
    private UUID supersededById;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;
}

public enum AcceptanceMethod {
    ONLINE_CHECKBOX("Online Checkbox"),
    ONLINE_SIGNATURE("Online Signatur"),
    PAPER_SIGNATURE("Papiersignatur"),
    VERBAL("Mündlich"),
    IMPLIED("Stillschweigend");  // By using service

    private final String nameDe;

    AcceptanceMethod(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 2. Tariff Versioning

### Major Tariff Transitions

| Old | New | Date | Impact |
|-----|-----|------|--------|
| TARMED | TARDOC | 2026-01-01 (planned) | Complete restructure |
| SwissDRG 12.0 | SwissDRG 13.0 | Annual | Weight updates |
| ICD-10 2024 | ICD-10 2025 | Annual | Code changes |

### Tariff Version Model

```java
@Entity
@Table(name = "tariff_versions")
public class TariffVersion {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type", nullable = false)
    private TariffType tariffType;

    @Column(name = "version_code", nullable = false)
    private String versionCode;  // e.g., "TARDOC-1.0"

    @Column(name = "version_name", nullable = false)
    private String versionName;  // e.g., "TARDOC Version 1.0"

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TariffVersionStatus status = TariffVersionStatus.DRAFT;

    // Approval
    @Column(name = "bundesrat_approved")
    private Boolean bundesratApproved;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "approval_reference")
    private String approvalReference;

    // Predecessor/Successor
    @Column(name = "replaces_version_id")
    private UUID replacesVersionId;

    @Column(name = "replaced_by_version_id")
    private UUID replacedByVersionId;

    // Migration
    @Column(name = "migration_mapping_available")
    private boolean migrationMappingAvailable;

    @Column(name = "position_count")
    private Integer positionCount;

    // Documentation
    @Column(name = "release_notes", columnDefinition = "TEXT")
    private String releaseNotes;

    @Column(name = "documentation_url")
    private String documentationUrl;

    // Sync
    @Column(name = "last_sync_at")
    private Instant lastSyncAt;

    @Version
    private long version;
}

public enum TariffVersionStatus {
    DRAFT("Entwurf"),
    PUBLISHED("Veröffentlicht"),
    ACTIVE("Aktiv"),
    DEPRECATED("Veraltet"),
    ARCHIVED("Archiviert");

    private final String nameDe;

    TariffVersionStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### Tariff Position Mapping (Migration)

```java
@Entity
@Table(name = "tariff_position_mappings")
public class TariffPositionMapping {

    @Id
    private UUID id;

    // Source (old tariff)
    @Column(name = "source_tariff_type", nullable = false)
    private String sourceTariffType;  // e.g., "TARMED"

    @Column(name = "source_version", nullable = false)
    private String sourceVersion;

    @Column(name = "source_position_code", nullable = false)
    private String sourcePositionCode;

    // Target (new tariff)
    @Column(name = "target_tariff_type", nullable = false)
    private String targetTariffType;  // e.g., "TARDOC"

    @Column(name = "target_version", nullable = false)
    private String targetVersion;

    @Column(name = "target_position_code")
    private String targetPositionCode;  // null if deleted

    // Mapping type
    @Enumerated(EnumType.STRING)
    @Column(name = "mapping_type", nullable = false)
    private MappingType mappingType;

    @Column(name = "mapping_notes")
    private String mappingNotes;

    // For splits/merges
    @Column(name = "split_factor")
    private BigDecimal splitFactor;  // e.g., 0.5 if split into 2

    @Version
    private long version;
}

public enum MappingType {
    ONE_TO_ONE("1:1 Übernahme"),
    RENAMED("Umbenennung"),
    SPLIT("Aufteilung"),
    MERGED("Zusammenführung"),
    DELETED("Gelöscht"),
    NEW("Neu");

    private final String nameDe;

    MappingType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### TARMED to TARDOC Transition Service

```java
public interface TariffTransitionService {

    /**
     * Map old tariff position to new tariff
     */
    List<TariffPositionMapping> mapPosition(
        String oldTariff, String oldVersion, String positionCode,
        String newTariff, String newVersion);

    /**
     * Calculate equivalent in new tariff
     */
    Money calculateEquivalent(
        TariffPosition oldPosition,
        TariffVersion newVersion,
        Canton canton);

    /**
     * Validate invoice can be processed with given tariff
     */
    ValidationResult validateTariffVersion(
        Invoice invoice,
        LocalDate treatmentDate);

    /**
     * Get active tariff version for date
     */
    TariffVersion getActiveVersion(TariffType type, LocalDate date);
}
```

---

## 3. Tariff Contracts (Taxpunkt Agreements)

### Cantonal Tariff Agreements

```java
@Entity
@Table(name = "tariff_contracts")
public class TariffContract {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type", nullable = false)
    private TariffType tariffType;

    @Column(name = "tariff_version_id")
    private UUID tariffVersionId;

    // Parties
    @Column(name = "canton")
    private Canton canton;  // null = national

    @Column(name = "provider_association")
    private String providerAssociation;  // e.g., "FMH", "Ärztekasse"

    @Column(name = "insurer_association")
    private String insurerAssociation;  // e.g., "santésuisse"

    // Values
    @Column(name = "taxpunkt_value_medical")
    private BigDecimal taxpunktValueMedical;  // AL

    @Column(name = "taxpunkt_value_technical")
    private BigDecimal taxpunktValueTechnical;  // TL

    @Column(name = "base_rate")
    private BigDecimal baseRate;  // For DRG

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status = ContractStatus.ACTIVE;

    // Reference
    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name = "contract_date")
    private LocalDate contractDate;

    @Version
    private long version;
}

public enum ContractStatus {
    NEGOTIATING("In Verhandlung"),
    PENDING_APPROVAL("Genehmigung ausstehend"),
    ACTIVE("Aktiv"),
    TERMINATED("Gekündigt"),
    EXPIRED("Abgelaufen");

    private final String nameDe;

    ContractStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 4. Document Workflow

```java
public interface LegalDocumentService {

    /**
     * Create new document version
     */
    LegalDocument createVersion(LegalDocument document);

    /**
     * Submit for approval
     */
    LegalDocument submitForApproval(UUID documentId);

    /**
     * Approve document
     */
    LegalDocument approve(UUID documentId, UUID approverId);

    /**
     * Activate document (make it current)
     */
    LegalDocument activate(UUID documentId);

    /**
     * Get current version of document type for product
     */
    LegalDocument getCurrentVersion(
        LegalDocumentType type,
        UUID productId,
        LocalDate asOfDate);

    /**
     * Get all documents requiring acceptance
     */
    List<LegalDocument> getDocumentsRequiringAcceptance(
        UUID personId,
        UUID coverageId);

    /**
     * Record acceptance
     */
    DocumentAcceptance recordAcceptance(
        UUID personId,
        UUID documentId,
        AcceptanceMethod method);
}
```

---

## Database Schema

```sql
-- Legal Documents
CREATE TABLE legal_documents (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    document_type VARCHAR(30) NOT NULL,
    document_code VARCHAR(50) NOT NULL,
    version VARCHAR(20) NOT NULL,
    title_de VARCHAR(255) NOT NULL,
    title_fr VARCHAR(255),
    title_it VARCHAR(255),
    content_de TEXT,
    content_fr TEXT,
    content_it TEXT,
    pdf_de_id UUID,
    pdf_fr_id UUID,
    pdf_it_id UUID,
    valid_from DATE NOT NULL,
    valid_to DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    approved_by UUID,
    approved_at TIMESTAMP,
    bag_approved BOOLEAN,
    bag_approval_date DATE,
    bag_reference VARCHAR(50),
    applies_to_domain VARCHAR(20),
    replaces_document_id UUID,
    replaced_by_document_id UUID,
    change_summary_de TEXT,
    change_summary_fr TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP,
    version_ BIGINT DEFAULT 0,
    UNIQUE(document_code, version)
);

-- Document Acceptances
CREATE TABLE document_acceptances (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID,
    organization_id UUID,
    document_id UUID NOT NULL,
    document_version VARCHAR(20) NOT NULL,
    coverage_id UUID,
    policy_id UUID,
    application_id UUID,
    accepted_at TIMESTAMP NOT NULL,
    acceptance_method VARCHAR(20) NOT NULL,
    ip_address VARCHAR(50),
    user_agent TEXT,
    signature_hash VARCHAR(255),
    certificate_id VARCHAR(100),
    valid_from DATE NOT NULL,
    valid_to DATE,
    superseded_by_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tariff Versions
CREATE TABLE tariff_versions (
    id UUID PRIMARY KEY,
    tariff_type VARCHAR(20) NOT NULL,
    version_code VARCHAR(30) NOT NULL,
    version_name VARCHAR(100) NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    bundesrat_approved BOOLEAN,
    approval_date DATE,
    approval_reference VARCHAR(50),
    replaces_version_id UUID,
    replaced_by_version_id UUID,
    migration_mapping_available BOOLEAN DEFAULT FALSE,
    position_count INTEGER,
    release_notes TEXT,
    documentation_url VARCHAR(255),
    last_sync_at TIMESTAMP,
    version BIGINT DEFAULT 0,
    UNIQUE(tariff_type, version_code)
);

-- Tariff Position Mappings
CREATE TABLE tariff_position_mappings (
    id UUID PRIMARY KEY,
    source_tariff_type VARCHAR(20) NOT NULL,
    source_version VARCHAR(30) NOT NULL,
    source_position_code VARCHAR(20) NOT NULL,
    target_tariff_type VARCHAR(20) NOT NULL,
    target_version VARCHAR(30) NOT NULL,
    target_position_code VARCHAR(20),
    mapping_type VARCHAR(20) NOT NULL,
    mapping_notes TEXT,
    split_factor DECIMAL(5,4),
    version BIGINT DEFAULT 0
);

-- Tariff Contracts
CREATE TABLE tariff_contracts (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    tariff_type VARCHAR(20) NOT NULL,
    tariff_version_id UUID,
    canton VARCHAR(2),
    provider_association VARCHAR(100),
    insurer_association VARCHAR(100),
    taxpunkt_value_medical DECIMAL(6,4),
    taxpunkt_value_technical DECIMAL(6,4),
    base_rate DECIMAL(10,2),
    valid_from DATE NOT NULL,
    valid_to DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    contract_number VARCHAR(50),
    contract_date DATE,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_ld_type ON legal_documents(document_type);
CREATE INDEX idx_ld_status ON legal_documents(status);
CREATE INDEX idx_da_person ON document_acceptances(person_id);
CREATE INDEX idx_da_document ON document_acceptances(document_id);
CREATE INDEX idx_tv_type ON tariff_versions(tariff_type);
CREATE INDEX idx_tpm_source ON tariff_position_mappings(source_tariff_type, source_position_code);
```

---

*Status: Draft*
*Priority: MEDIUM*

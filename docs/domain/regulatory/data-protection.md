# Data Protection Requirements

## Overview

This document outlines the data protection requirements for health insurance systems under Swiss law. Health data is classified as **sensitive personal data** requiring enhanced protection measures.

📋 **Law Reference**: [DSG - SR 235.1](https://www.fedlex.admin.ch/eli/cc/2022/491/de) (revised DSG, effective Sept 1, 2023)

📋 **Ordinance Reference**: [DSV - SR 235.11](https://www.fedlex.admin.ch/eli/cc/2022/568/de)

📋 **Supervisory Authority**: [EDÖB](https://www.edoeb.admin.ch)

---

## Regulatory Framework

### Swiss Data Protection Act (DSG)

The revised DSG (2023) aligns Swiss data protection with GDPR standards:

| Principle | Description |
|-----------|-------------|
| **Lawfulness** | Processing must have legal basis |
| **Purpose limitation** | Use only for specified purposes |
| **Proportionality** | Collect only necessary data |
| **Transparency** | Inform data subjects |
| **Accuracy** | Keep data correct and current |
| **Storage limitation** | Delete when no longer needed |
| **Security** | Protect against unauthorized access |

### Health Data Classification

```
┌─────────────────────────────────────────────────────────────────┐
│              DATA SENSITIVITY CLASSIFICATION                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   STANDARD PERSONAL DATA                                        │
│   ├── Name, address                                            │
│   ├── Date of birth                                            │
│   ├── Insurance policy number                                  │
│   └── Premium amounts                                          │
│                                                                 │
│   SENSITIVE PERSONAL DATA (Art. 5 DSG)                         │
│   ├── Health information                    🔴 HIGHEST RISK    │
│   │   ├── Medical diagnoses                                    │
│   │   ├── Treatment history                                    │
│   │   ├── Health declarations                                  │
│   │   └── Claims data                                          │
│   │                                                            │
│   ├── AHV Number                            🟡 SPECIAL RULES   │
│   │   └── Regulated use (AHVG)                                │
│   │                                                            │
│   └── Genetic data                          🔴 HIGHEST RISK    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Processing Health Data

### Legal Bases for Processing

| Basis | KVG Context | VVG Context |
|-------|-------------|-------------|
| **Legal obligation** | ✅ Mandatory insurance administration | ❌ Not applicable |
| **Contract performance** | ✅ Coverage management | ✅ Policy administration |
| **Explicit consent** | ⚠️ For extra purposes | ✅ Required for health data |
| **Vital interests** | ✅ Emergency situations | ✅ Emergency situations |

### Consent Requirements

```java
public class DataProcessingConsent {

    // Types of consent needed
    public enum ConsentType {
        // KVG - implied by legal obligation, but good practice
        KVG_CLAIMS_PROCESSING,

        // VVG - explicit consent required
        VVG_HEALTH_DECLARATION,
        VVG_MEDICAL_RECORDS_REQUEST,
        VVG_REINSURER_SHARING,

        // General
        MARKETING_COMMUNICATIONS,
        DATA_ANALYTICS,
        THIRD_PARTY_SHARING
    }

    private UUID personId;
    private ConsentType type;
    private boolean granted;
    private LocalDateTime consentTimestamp;
    private String consentVersion;           // Version of consent text
    private ConsentChannel channel;          // HOW consent was given
    private String ipAddress;                // For online consent
    private UUID documentId;                 // Signed document reference
}
```

### Health Data Access Controls

```java
@Service
public class HealthDataAccessControl {

    // Role-based access to health data
    private static final Map<Role, Set<HealthDataCategory>> ACCESS_MATRIX = Map.of(
        Role.UNDERWRITER, Set.of(
            HealthDataCategory.HEALTH_DECLARATION,
            HealthDataCategory.MEDICAL_HISTORY
        ),
        Role.CLAIMS_PROCESSOR, Set.of(
            HealthDataCategory.CLAIMS,
            HealthDataCategory.DIAGNOSES,
            HealthDataCategory.TREATMENTS
        ),
        Role.CUSTOMER_SERVICE, Set.of(
            HealthDataCategory.COVERAGE_STATUS
            // NO access to medical details
        ),
        Role.BILLING, Set.of(
            // NO access to health data
        )
    );

    public void checkAccess(User user, HealthDataCategory category) {
        Set<HealthDataCategory> allowed = ACCESS_MATRIX
            .getOrDefault(user.getRole(), Collections.emptySet());

        if (!allowed.contains(category)) {
            auditLog.logUnauthorizedAccess(user, category);
            throw new AccessDeniedException(
                "User not authorized for " + category);
        }

        auditLog.logDataAccess(user, category);
    }
}
```

---

## Data Subject Rights

### Right to Information (Art. 25 DSG)

Data subjects can request information about their personal data.

```java
public class DataSubjectAccessRequest {

    public DataAccessResponse processRequest(UUID personId) {
        return DataAccessResponse.builder()
            .personalData(getPersonalData(personId))
            .processingPurposes(getProcessingPurposes())
            .dataCategories(getDataCategories(personId))
            .recipients(getDataRecipients())
            .retentionPeriod(getRetentionPolicy())
            .dataSource(getDataSource(personId))
            .automatedDecisions(getAutomatedDecisions(personId))
            .build();
    }

    // Response within 30 days (Art. 25 DSG)
    private static final int RESPONSE_DEADLINE_DAYS = 30;
}
```

### Right to Rectification (Art. 32 DSG)

Data subjects can request correction of inaccurate data.

```java
public class DataRectificationRequest {
    private UUID personId;
    private String fieldName;
    private String currentValue;
    private String requestedValue;
    private String evidenceDocumentId;
    private LocalDateTime requestDate;

    // Must process without delay
    public void process() {
        validateRequest();
        updateData();
        notifyDataSubject();
        logRectification();
    }
}
```

### Right to Deletion (Art. 32 DSG)

Data subjects can request deletion, subject to legal retention requirements.

```java
public class DataDeletionRequest {

    public DeletionResponse processRequest(UUID personId) {
        // Check retention obligations
        List<RetentionObligation> obligations = checkRetentionObligations(personId);

        if (obligations.isEmpty()) {
            deleteAllData(personId);
            return DeletionResponse.deleted();
        } else {
            // Cannot delete due to legal requirements
            return DeletionResponse.retained(obligations);
        }
    }

    private List<RetentionObligation> checkRetentionObligations(UUID personId) {
        List<RetentionObligation> obligations = new ArrayList<>();

        // Insurance records: 10 years after contract end
        if (hasActiveOrRecentContract(personId)) {
            obligations.add(new RetentionObligation(
                "Insurance records",
                "Art. 958f OR",
                calculateRetentionEnd(personId)
            ));
        }

        // Tax-relevant documents: 10 years
        // ...

        return obligations;
    }
}
```

### Right to Data Portability (Art. 28 DSG)

Data subjects can request their data in a portable format.

```java
public class DataPortabilityRequest {

    public byte[] exportData(UUID personId, ExportFormat format) {
        PersonalDataExport export = PersonalDataExport.builder()
            .person(getPersonData(personId))
            .addresses(getAddresses(personId))
            .policies(getPolicies(personId))
            .coverages(getCoverages(personId))
            // Health data only with explicit consent
            .healthData(getHealthDataIfConsented(personId))
            .build();

        return switch (format) {
            case JSON -> jsonExporter.export(export);
            case XML -> xmlExporter.export(export);
            case CSV -> csvExporter.export(export);
        };
    }
}
```

---

## Data Retention

### Retention Periods

| Data Category | Retention Period | Legal Basis |
|---------------|------------------|-------------|
| **Insurance contracts** | 10 years after end | Art. 958f OR |
| **Claims/invoices** | 10 years | Art. 958f OR |
| **Health declarations** | Duration + 10 years | VVG, OR |
| **Premium records** | 10 years | OR, tax law |
| **Correspondence** | 10 years | OR |
| **Consent records** | Duration + 3 years | DSG |
| **Access logs** | 1-5 years | Best practice |
| **Marketing data** | Until withdrawal | DSG |

### Retention Policy Implementation

```java
@Entity
public class DataRetentionPolicy {

    @Id
    private UUID id;

    private String dataCategory;
    private int retentionYears;
    private String legalBasis;
    private RetentionTrigger trigger;  // CONTRACT_END, CREATION, LAST_UPDATE

    public LocalDate calculateDeletionDate(LocalDate triggerDate) {
        return triggerDate.plusYears(retentionYears);
    }
}

@Service
public class DataRetentionService {

    @Scheduled(cron = "0 0 2 * * *")  // Daily at 2 AM
    public void processRetention() {
        LocalDate today = LocalDate.now();

        // Find data eligible for deletion
        List<DataForDeletion> eligible = findEligibleForDeletion(today);

        for (DataForDeletion data : eligible) {
            if (noLegalHold(data) && noActiveDispute(data)) {
                secureDelete(data);
                logDeletion(data);
            }
        }
    }

    private void secureDelete(DataForDeletion data) {
        // Actual deletion, not just flagging
        // Consider: anonymization as alternative
    }
}
```

---

## Security Measures

### Technical Measures (Art. 8 DSG)

| Measure | Implementation |
|---------|----------------|
| **Encryption at rest** | AES-256 for databases |
| **Encryption in transit** | TLS 1.3 |
| **Access control** | Role-based (RBAC) |
| **Authentication** | MFA for health data access |
| **Audit logging** | All data access logged |
| **Pseudonymization** | Where possible |

### Organizational Measures

| Measure | Implementation |
|---------|----------------|
| **Data protection training** | Annual mandatory training |
| **Access reviews** | Quarterly review of permissions |
| **Incident response** | Documented procedures |
| **Vendor management** | DPA with all processors |
| **Privacy by design** | Built into development |

### Audit Logging

```java
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    private UUID id;

    private UUID userId;
    private String action;                  // VIEW, UPDATE, DELETE, EXPORT
    private String entityType;              // Person, Coverage, HealthDeclaration
    private UUID entityId;
    private String sensitivityLevel;        // STANDARD, SENSITIVE, HIGHLY_SENSITIVE
    private String ipAddress;
    private String userAgent;
    private Instant timestamp;
    private boolean success;
    private String failureReason;

    // For data changes
    private String previousValue;           // Encrypted or hashed
    private String newValue;                // Encrypted or hashed
}

@Aspect
@Component
public class DataAccessAuditAspect {

    @Around("@annotation(AuditHealthDataAccess)")
    public Object auditHealthDataAccess(ProceedingJoinPoint joinPoint) {
        AuditLog log = createAuditLog(joinPoint);

        try {
            Object result = joinPoint.proceed();
            log.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.setSuccess(false);
            log.setFailureReason(e.getMessage());
            throw e;
        } finally {
            auditLogRepository.save(log);
        }
    }
}
```

---

## Data Breach Notification

### Notification Requirements (Art. 24 DSG)

```
┌─────────────────────────────────────────────────────────────────┐
│              DATA BREACH RESPONSE                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   DETECTION                                                     │
│        │                                                        │
│        ▼                                                        │
│   ASSESSMENT (within hours)                                     │
│   ├── What data was affected?                                  │
│   ├── How many people affected?                                │
│   ├── Risk to individuals?                                     │
│   └── Is it ongoing?                                           │
│        │                                                        │
│        ▼                                                        │
│   HIGH RISK TO INDIVIDUALS?                                    │
│        │                                                        │
│   ┌────┴────┐                                                  │
│   │   YES   │   ──▶  NOTIFY EDÖB "as soon as possible"        │
│   └─────────┘        NOTIFY affected individuals               │
│        │                                                        │
│   ┌────┴────┐                                                  │
│   │   NO    │   ──▶  Document internally                       │
│   └─────────┘        Consider voluntary notification           │
│        │                                                        │
│        ▼                                                        │
│   REMEDIATION                                                   │
│   └── Fix vulnerability                                        │
│   └── Prevent recurrence                                       │
│   └── Document lessons learned                                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Breach Notification Content

```java
public class DataBreachNotification {

    // To EDÖB
    private LocalDateTime breachDetected;
    private LocalDateTime breachOccurred;
    private String natureOfBreach;
    private int approximateNumberAffected;
    private List<String> dataTypesAffected;
    private String likelyConsequences;
    private String measuresTaken;
    private String contactPerson;

    // To affected individuals (if required)
    private String plainLanguageDescription;
    private String recommendedActions;
    private String supportContactInfo;
}
```

---

## Cross-Border Data Transfers

### Transfer Requirements (Art. 16-17 DSG)

| Destination | Requirement |
|-------------|-------------|
| **EU/EEA** | Generally adequate |
| **Other adequate countries** | See EDÖB list |
| **Non-adequate countries** | Additional safeguards required |

### Additional Safeguards

- Standard Contractual Clauses (SCCs)
- Binding Corporate Rules
- Explicit consent
- Necessary for contract

---

## Implementation Checklist

### Data Collection
- [ ] Privacy notice at collection
- [ ] Purpose specification
- [ ] Consent collection for VVG health data
- [ ] Data minimization review

### Data Processing
- [ ] Role-based access control
- [ ] Health data access logging
- [ ] Encryption implementation
- [ ] Processing records maintained

### Data Subject Rights
- [ ] Access request process
- [ ] Rectification process
- [ ] Deletion process (with retention check)
- [ ] Portability export function

### Security
- [ ] Encryption at rest
- [ ] Encryption in transit
- [ ] MFA for sensitive data
- [ ] Audit logging
- [ ] Breach response plan

### Governance
- [ ] Data protection training
- [ ] DPO appointed (if required)
- [ ] Processing records
- [ ] Vendor DPAs

---

## Official Resources

| Resource | URL |
|----------|-----|
| DSG Full Text | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/2022/491/de) |
| EDÖB | [edoeb.admin.ch](https://www.edoeb.admin.ch) |
| EDÖB Guidelines | [edoeb.admin.ch/leitfaeden](https://www.edoeb.admin.ch/edoeb/de/home/dokumentation/taetigkeitsberichte.html) |

---

## Related Documentation

- [KVG Requirements](./kvg-law-requirements.md)
- [VVG Requirements](./vvg-law-requirements.md)
- [Person Entity](../entities/masterdata/person.md)

---

*Last Updated: 2026-01-26*

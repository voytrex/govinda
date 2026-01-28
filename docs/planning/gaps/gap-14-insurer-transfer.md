# GAP-14: Insurer Transfer & Industry Integration

## Problem Statement

Missing support for:
- Person entry/exit tracking with reasons
- Vorversicherer / Nachversicherer chain
- SASIS data exchange
- VeKa (Versichertenkarte) management
- Industry service integrations

---

## 1. Entry & Exit Reasons

### Entry Reasons (Eintritt)

| Reason | Code | Description |
|--------|------|-------------|
| Birth | BIRTH | Newborn registration |
| Immigration | IMMIGRATION | Moving to Switzerland |
| Insurer change | TRANSFER_IN | From another KVG insurer |
| Return from abroad | RETURN | Swiss returning |
| First insurance | FIRST | Previously uninsured (rare) |
| Liechtenstein entry | LI_ENTRY | From FL to CH |
| Grenzgänger choice | GRENZ_CHOICE | Cross-border worker choosing KVG |

### Exit Reasons (Austritt)

| Reason | Code | Description |
|--------|------|-------------|
| Death | DEATH | Deceased |
| Emigration | EMIGRATION | Leaving Switzerland |
| Insurer change | TRANSFER_OUT | To another KVG insurer |
| Liechtenstein exit | LI_EXIT | From CH to FL |
| Grenzgänger exit | GRENZ_EXIT | Choosing home country insurance |
| KVG exemption | EXEMPTION | Granted exemption (rare) |

### Model

```java
public enum EntryReason {
    BIRTH("Geburt"),
    IMMIGRATION("Einwanderung"),
    TRANSFER_IN("Kassenwechsel"),
    RETURN_FROM_ABROAD("Rückkehr aus Ausland"),
    FIRST_INSURANCE("Erstversicherung"),
    LIECHTENSTEIN_ENTRY("Zuzug aus FL"),
    GRENZGAENGER_CHOICE("Grenzgänger Optionsrecht"),
    REACTIVATION("Reaktivierung"),
    OTHER("Andere");

    private final String nameDe;

    EntryReason(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum ExitReason {
    DEATH("Tod"),
    EMIGRATION("Auswanderung"),
    TRANSFER_OUT("Kassenwechsel"),
    LIECHTENSTEIN_EXIT("Wegzug nach FL"),
    GRENZGAENGER_EXIT("Grenzgänger Wechsel"),
    KVG_EXEMPTION("KVG-Befreiung"),
    CANCELLATION_NON_PAYMENT("Kündigung Nichtzahlung"),  // VVG only
    TERMINATION_BY_INSURED("Kündigung Versicherter"),    // VVG only
    TERMINATION_BY_INSURER("Kündigung Versicherer"),     // VVG only
    OTHER("Andere");

    private final String nameDe;

    ExitReason(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 2. Vorversicherer / Nachversicherer

### Insurer Transfer Record

```java
@Entity
@Table(name = "insurer_transfers")
public class InsurerTransfer {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    // Transfer direction
    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type", nullable = false)
    private TransferType transferType;

    // Dates
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;  // When coverage changes

    @Column(name = "notification_date")
    private LocalDate notificationDate;  // When notified

    @Column(name = "confirmation_date")
    private LocalDate confirmationDate;  // When confirmed

    // Previous insurer (for TRANSFER_IN)
    @Column(name = "previous_insurer_code")
    private String previousInsurerCode;  // BAG-Nr

    @Column(name = "previous_insurer_name")
    private String previousInsurerName;

    @Column(name = "previous_insurer_gln")
    private String previousInsurerGln;  // GLN number

    @Column(name = "previous_policy_number")
    private String previousPolicyNumber;

    @Column(name = "previous_coverage_end")
    private LocalDate previousCoverageEnd;

    // Next insurer (for TRANSFER_OUT)
    @Column(name = "next_insurer_code")
    private String nextInsurerCode;

    @Column(name = "next_insurer_name")
    private String nextInsurerName;

    @Column(name = "next_insurer_gln")
    private String nextInsurerGln;

    // Transferred data
    @Column(name = "franchise_used_transferred")
    private Money franchiseUsedTransferred;

    @Column(name = "selbstbehalt_used_transferred")
    private Money selbstbehaltUsedTransferred;

    @Column(name = "enrollment_surcharge_transferred")
    private boolean enrollmentSurchargeTransferred;

    @Column(name = "surcharge_remaining_months")
    private Integer surchargeRemainingMonths;

    // Reason
    @Enumerated(EnumType.STRING)
    @Column(name = "entry_reason")
    private EntryReason entryReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "exit_reason")
    private ExitReason exitReason;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status = TransferStatus.PENDING;

    // SASIS exchange
    @Column(name = "sasis_message_id")
    private String sasisMessageId;

    @Column(name = "sasis_sent_at")
    private Instant sasisSentAt;

    @Column(name = "sasis_response_at")
    private Instant sasisResponseAt;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

public enum TransferType {
    ENTRY,   // Person joining us
    EXIT     // Person leaving us
}

public enum TransferStatus {
    PENDING,           // Transfer initiated
    AWAITING_RESPONSE, // Waiting for other insurer
    CONFIRMED,         // Transfer confirmed
    REJECTED,          // Transfer rejected
    COMPLETED,         // Transfer processed
    CANCELLED          // Transfer cancelled
}
```

### Insurer Registry

```java
@Entity
@Table(name = "insurer_registry")
public class InsurerRegistry {

    @Id
    private UUID id;

    @Column(name = "bag_number", unique = true, nullable = false)
    private String bagNumber;  // BAG insurer number

    @Column(name = "gln", unique = true)
    private String gln;  // Global Location Number

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_name")
    private String shortName;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurer_type")
    private InsurerType insurerType;

    // Contact for transfers
    @Column(name = "transfer_email")
    private String transferEmail;

    @Column(name = "sasis_participant")
    private boolean sasisParticipant = true;

    // Status
    @Column(name = "active")
    private boolean active = true;

    @Column(name = "kvg_licensed")
    private boolean kvgLicensed;

    @Column(name = "vvg_licensed")
    private boolean vvgLicensed;
}

public enum InsurerType {
    KVG_INSURER("KVG-Versicherer"),
    VVG_INSURER("VVG-Versicherer"),
    COMBINED("Kombiniert"),
    REINSURER("Rückversicherer");

    private final String nameDe;

    InsurerType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 3. SASIS Integration

### What is SASIS?

**SASIS AG** - Swiss insurance industry service organization providing:
- Data exchange between insurers (XML/EDI)
- Insurer registry
- VeKa number management
- Clearing services

### SASIS Message Types

| Message | Purpose |
|---------|---------|
| MUT | Mutation (insurer change notification) |
| ANF | Anfrage (inquiry about insured) |
| ANT | Antwort (response to inquiry) |
| VER | Versicherungsnachweis (coverage confirmation) |
| KOS | Kostengutsprache (cost guarantee) |

### Integration Model

```java
@Entity
@Table(name = "sasis_messages")
public class SasisMessage {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private MessageDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private SasisMessageType messageType;

    @Column(name = "message_id", unique = true)
    private String messageId;  // SASIS message ID

    @Column(name = "correlation_id")
    private String correlationId;  // For request/response linking

    // Parties
    @Column(name = "sender_gln")
    private String senderGln;

    @Column(name = "receiver_gln")
    private String receiverGln;

    // Related entities
    @Column(name = "person_id")
    private UUID personId;

    @Column(name = "transfer_id")
    private UUID transferId;

    // Content
    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;  // XML content

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SasisMessageStatus status = SasisMessageStatus.CREATED;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "received_at")
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "error_message")
    private String errorMessage;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

public enum MessageDirection {
    INBOUND,
    OUTBOUND
}

public enum SasisMessageType {
    MUT_NOTIFICATION("Mutationsmeldung"),
    MUT_CONFIRMATION("Mutationsbestätigung"),
    ANF_INQUIRY("Anfrage"),
    ANT_RESPONSE("Antwort"),
    VER_COVERAGE_PROOF("Versicherungsnachweis"),
    KOS_COST_GUARANTEE("Kostengutsprache"),
    STA_STATUS("Statusmeldung");

    private final String nameDe;

    SasisMessageType(String nameDe) {
        this.nameDe = nameDe;
    }
}

public enum SasisMessageStatus {
    CREATED,
    QUEUED,
    SENT,
    DELIVERED,
    PROCESSED,
    FAILED,
    REJECTED
}
```

### SASIS Service Interface

```java
public interface SasisService {

    /**
     * Send insurer change notification (Mutationsmeldung)
     */
    SasisMessage sendTransferNotification(InsurerTransfer transfer);

    /**
     * Query insured person at another insurer
     */
    SasisMessage sendInquiry(UUID personId, String targetInsurerGln);

    /**
     * Respond to inquiry from another insurer
     */
    SasisMessage sendInquiryResponse(String correlationId, Person person);

    /**
     * Send coverage confirmation
     */
    SasisMessage sendCoverageProof(UUID personId, LocalDate asOfDate);

    /**
     * Process incoming SASIS message
     */
    void processInboundMessage(String messageXml);
}
```

---

## 4. VeKa (Versichertenkarte)

### What is VeKa?

The **Versichertenkarte** (VeKa) is the Swiss health insurance card:
- Issued to every KVG-insured person
- Contains chip with insurance data
- Required for healthcare access
- Standardized format (credit card size)

### VeKa Data Elements

| Field | Description |
|-------|-------------|
| VeKa-Nr | Unique card number (20 digits) |
| AHV-Nr | Social security number |
| Name | Insured name |
| Birth date | Date of birth |
| Gender | M/F |
| Insurer | BAG number + name |
| Valid from/to | Validity period |

### Model

```java
@Entity
@Table(name = "insurance_cards")
public class InsuranceCard {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "coverage_id", nullable = false)
    private UUID coverageId;

    // Card identifiers
    @Column(name = "veka_number", unique = true, nullable = false)
    private String vekaNumber;  // 20-digit number

    @Column(name = "card_number")
    private String cardNumber;  // Physical card number

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo;

    // Card status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status = CardStatus.ACTIVE;

    // Physical card
    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "sent_date")
    private LocalDate sentDate;

    @Column(name = "delivery_address_id")
    private UUID deliveryAddressId;

    // Replacement tracking
    @Column(name = "replaces_card_id")
    private UUID replacesCardId;

    @Enumerated(EnumType.STRING)
    @Column(name = "replacement_reason")
    private CardReplacementReason replacementReason;

    // Card ordering
    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "production_status")
    private String productionStatus;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

public enum CardStatus {
    ORDERED,        // Card ordered
    PRODUCED,       // Card produced
    SENT,           // Card sent to insured
    ACTIVE,         // Card in use
    BLOCKED,        // Card blocked (lost/stolen)
    EXPIRED,        // Card expired
    REPLACED,       // Replaced by new card
    CANCELLED       // Card cancelled
}

public enum CardReplacementReason {
    EXPIRY("Ablauf"),
    LOST("Verlust"),
    STOLEN("Diebstahl"),
    DAMAGED("Beschädigung"),
    DATA_CHANGE("Datenänderung"),
    INSURER_CHANGE("Kassenwechsel");

    private final String nameDe;

    CardReplacementReason(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### VeKa Service

```java
public interface VekaService {

    /**
     * Generate new VeKa number for person
     */
    String generateVekaNumber(Person person);

    /**
     * Order new card for person
     */
    InsuranceCard orderCard(UUID personId, UUID coverageId);

    /**
     * Order replacement card
     */
    InsuranceCard orderReplacement(UUID cardId, CardReplacementReason reason);

    /**
     * Block lost/stolen card
     */
    void blockCard(UUID cardId, CardReplacementReason reason);

    /**
     * Verify card is valid
     */
    CardValidationResult validateCard(String vekaNumber);

    /**
     * Get card data for healthcare provider inquiry
     */
    CardData getCardData(String vekaNumber);
}
```

---

## 5. Other Industry Services

### BAG (Bundesamt für Gesundheit)

| Service | Purpose |
|---------|---------|
| Prämienregister | Premium approval registry |
| Versichererverzeichnis | Licensed insurers list |
| Statistik | Reporting requirements |

### SVK (Schweizerischer Versicherungsverband)

| Service | Purpose |
|---------|---------|
| Tarifpool | Tariff data exchange |
| Claims data | Anonymous claims statistics |

### Cantonal Services

| Service | Purpose |
|---------|---------|
| IPV-Schnittstelle | Premium subsidy data |
| Säumigenliste | Defaulter list (non-payers) |
| Einwohnerkontrolle | Resident registry verification |

### Integration Points

```java
public interface ExternalServiceRegistry {

    // BAG
    BagPremiumApproval submitPremiumForApproval(Product product, Tariff tariff);
    List<Insurer> getLicensedInsurers();

    // Cantonal
    IpvDecision queryIpvStatus(UUID personId, Canton canton);
    void reportToDefaulterList(UUID personId, Canton canton);
    void removeFromDefaulterList(UUID personId, Canton canton);

    // Einwohnerkontrolle
    ResidentVerification verifyResident(String ahvNumber, Canton canton);
}
```

---

## 6. Data Exchange Standards

### XML Schemas

| Schema | Use |
|--------|-----|
| SASIS XML | Insurer-to-insurer |
| BAG XML | Regulatory reporting |
| eCH standards | Government data exchange |

### eCH Standards Used

| Standard | Purpose |
|----------|---------|
| eCH-0010 | Address format |
| eCH-0011 | Person identification |
| eCH-0021 | Resident data |
| eCH-0044 | ID number exchange |

---

## Database Schema

```sql
CREATE TABLE insurer_transfers (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID NOT NULL,
    transfer_type VARCHAR(10) NOT NULL,
    effective_date DATE NOT NULL,
    notification_date DATE,
    confirmation_date DATE,
    previous_insurer_code VARCHAR(10),
    previous_insurer_name VARCHAR(100),
    previous_insurer_gln VARCHAR(20),
    previous_policy_number VARCHAR(50),
    previous_coverage_end DATE,
    next_insurer_code VARCHAR(10),
    next_insurer_name VARCHAR(100),
    next_insurer_gln VARCHAR(20),
    franchise_used_transferred DECIMAL(10,2),
    selbstbehalt_used_transferred DECIMAL(10,2),
    enrollment_surcharge_transferred BOOLEAN DEFAULT FALSE,
    surcharge_remaining_months INTEGER,
    entry_reason VARCHAR(30),
    exit_reason VARCHAR(30),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    sasis_message_id VARCHAR(50),
    sasis_sent_at TIMESTAMP,
    sasis_response_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE insurer_registry (
    id UUID PRIMARY KEY,
    bag_number VARCHAR(10) UNIQUE NOT NULL,
    gln VARCHAR(20) UNIQUE,
    name VARCHAR(100) NOT NULL,
    short_name VARCHAR(50),
    insurer_type VARCHAR(20),
    transfer_email VARCHAR(100),
    sasis_participant BOOLEAN DEFAULT TRUE,
    active BOOLEAN DEFAULT TRUE,
    kvg_licensed BOOLEAN,
    vvg_licensed BOOLEAN
);

CREATE TABLE sasis_messages (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    direction VARCHAR(10) NOT NULL,
    message_type VARCHAR(20) NOT NULL,
    message_id VARCHAR(50) UNIQUE,
    correlation_id VARCHAR(50),
    sender_gln VARCHAR(20),
    receiver_gln VARCHAR(20),
    person_id UUID,
    transfer_id UUID,
    payload TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    sent_at TIMESTAMP,
    received_at TIMESTAMP,
    processed_at TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE insurance_cards (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID NOT NULL,
    coverage_id UUID NOT NULL,
    veka_number VARCHAR(20) UNIQUE NOT NULL,
    card_number VARCHAR(30),
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    issued_date DATE,
    sent_date DATE,
    delivery_address_id UUID,
    replaces_card_id UUID,
    replacement_reason VARCHAR(20),
    order_date DATE,
    production_status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_it_person ON insurer_transfers(person_id);
CREATE INDEX idx_it_status ON insurer_transfers(status);
CREATE INDEX idx_sm_correlation ON sasis_messages(correlation_id);
CREATE INDEX idx_ic_person ON insurance_cards(person_id);
CREATE INDEX idx_ic_veka ON insurance_cards(veka_number);
```

---

## References

- [SASIS AG](https://www.sasis.ch/)
- [VeKa Spezifikation](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung/krankenversicherung-leistungserbringer/versichertenkarte.html)
- [BAG Versichererverzeichnis](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung/krankenversicherung-versicherer-aufsicht/verzeichnisse-krankenundrueckversicherer.html)

---

*Status: Draft*
*Priority: HIGH*

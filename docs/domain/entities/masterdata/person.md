# Person Entity

## Overview

The **Person** entity represents an insured person or potential policyholder in the Swiss health insurance system. It is the central entity in the Masterdata bounded context and serves as an aggregate root.

> **German**: Person, Versicherte Person
> **Module**: `govinda-masterdata`
> **Status**: ✅ Implemented

---

## Entity Definition

```java
@Entity
@Table(name = "persons")
public class Person implements Historized<PersonHistoryEntry> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Embedded
    private AhvNumber ahvNr;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaritalStatus maritalStatus;

    @Column(length = 3)
    private String nationality = "CHE";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language preferredLanguage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonStatus status = PersonStatus.ACTIVE;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Address> addresses = new ArrayList<>();

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
| `ahvNr` | AhvNumber | ❌ | Swiss social security number (756.XXXX.XXXX.XX) |
| `lastName` | String | ✅ | Family name |
| `firstName` | String | ✅ | Given name(s) |
| `dateOfBirth` | LocalDate | ✅ | Date of birth |
| `gender` | Gender | ✅ | MALE, FEMALE, OTHER |
| `maritalStatus` | MaritalStatus | ✅ | Legal marital status |
| `nationality` | String(3) | ❌ | ISO 3166-1 alpha-3 (default: CHE) |
| `preferredLanguage` | Language | ✅ | DE, FR, IT, EN |
| `status` | PersonStatus | ✅ | ACTIVE, DECEASED, EMIGRATED |
| `addresses` | List\<Address\> | ❌ | Associated addresses |
| `createdAt` | Instant | Auto | Creation timestamp |
| `updatedAt` | Instant | Auto | Last modification |
| `version` | long | Auto | Optimistic locking |

---

## Key Behaviors

### Full Name

```java
public String fullName() {
    return firstName + " " + lastName;
}
```

### Age Calculation

```java
public int ageAt(LocalDate date) {
    return Period.between(dateOfBirth, date).getYears();
}
```

### Age Group Determination

```java
public AgeGroup ageGroupAt(LocalDate date) {
    int age = ageAt(date);
    return AgeGroup.forAge(age);
}

// Returns:
// - CHILD (0-18)
// - YOUNG_ADULT (19-25)
// - ADULT (26+)
```

### Current Address

```java
public Optional<Address> currentAddress() {
    return addresses.stream()
        .filter(Address::isCurrent)
        .filter(a -> a.getAddressType() == AddressType.MAIN)
        .findFirst();
}
```

### Address at Specific Date

```java
public Optional<Address> addressAt(LocalDate date) {
    return addresses.stream()
        .filter(a -> a.getAddressType() == AddressType.MAIN)
        .filter(a -> a.isValidOn(date))
        .findFirst();
}
```

### Name Change with History

```java
public PersonHistoryEntry changeName(String newLastName, String newFirstName,
                                     LocalDate effectiveDate, String reason, UUID changedBy) {
    // Create history entry for previous state
    PersonHistoryEntry history = PersonHistoryEntry.builder()
        .personId(this.id)
        .lastName(this.lastName)
        .firstName(this.firstName)
        .validFrom(/* previous valid from */)
        .validTo(effectiveDate.minusDays(1))
        .mutationType(MutationType.UPDATE)
        .mutationReason(reason)
        .changedBy(changedBy)
        .build();

    // Update current state
    this.lastName = newLastName;
    this.firstName = newFirstName;
    this.updatedAt = Instant.now();

    return history;
}
```

### Marital Status Change

```java
public PersonHistoryEntry changeMaritalStatus(MaritalStatus newStatus,
                                              LocalDate effectiveDate, String reason, UUID changedBy) {
    // Similar to name change with history tracking
}
```

---

## Validation Rules

### Required Fields

| Field | Rule |
|-------|------|
| `lastName` | Not blank, max 100 chars |
| `firstName` | Not blank, max 100 chars |
| `dateOfBirth` | Not null, not in future |
| `gender` | Not null |
| `maritalStatus` | Not null |
| `preferredLanguage` | Not null |

### AHV Number

- Format: `756.XXXX.XXXX.XX`
- Must pass checksum validation
- Unique within tenant (if provided)

### Business Invariants

```java
// Date of birth cannot be in the future
if (dateOfBirth.isAfter(LocalDate.now())) {
    throw new IllegalArgumentException("Date of birth cannot be in future");
}

// Age must be reasonable (0-150)
int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
if (age < 0 || age > 150) {
    throw new IllegalArgumentException("Invalid age");
}
```

---

## Relationships

### Person → Addresses (1:N)

```
Person (1) ────────────< Address (N)
                  ↑
           personId: UUID (FK)
```

- A person can have multiple addresses
- Each address has a type (MAIN, CORRESPONDENCE, BILLING)
- Addresses have temporal validity (validFrom, validTo)

### Person → HouseholdMember (1:N)

```
Person (1) ────────────< HouseholdMember (N)
                  ↑
           personId: UUID (FK)
```

- A person can belong to multiple households over time
- Membership has temporal validity
- Role: PRIMARY, PARTNER, CHILD

---

## History Tracking

### PersonHistoryEntry

Tracks changes to person data for audit and compliance:

```java
public class PersonHistoryEntry extends HistoryEntry {
    private UUID personId;
    private String lastName;
    private String firstName;
    private MaritalStatus maritalStatus;
    private LocalDate validFrom;      // Business time start
    private LocalDate validTo;        // Business time end
    private Instant recordedAt;       // Transaction time start
    private Instant supersededAt;     // Transaction time end (if corrected)
    private MutationType mutationType;
    private String mutationReason;
    private UUID changedBy;
}
```

### What's Tracked

| Change Type | History Created |
|-------------|-----------------|
| Name change | ✅ Yes |
| Marital status change | ✅ Yes |
| Address change | Via Address history |
| Gender change | ✅ Yes |
| Status change | ✅ Yes |

---

## Multi-Tenancy

All persons are isolated by `tenantId`:

```java
// Repository query
@Query("SELECT p FROM Person p WHERE p.tenantId = :tenantId AND p.id = :id")
Optional<Person> findByIdAndTenantId(UUID id, UUID tenantId);
```

---

## API Examples

### Create Person

```http
POST /api/v1/persons
Content-Type: application/json

{
  "ahvNr": "756.1234.5678.97",
  "lastName": "Müller",
  "firstName": "Hans",
  "dateOfBirth": "1985-03-15",
  "gender": "MALE",
  "maritalStatus": "MARRIED",
  "nationality": "CHE",
  "preferredLanguage": "DE"
}
```

### Response

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "ahvNr": "756.1234.5678.97",
  "lastName": "Müller",
  "firstName": "Hans",
  "fullName": "Hans Müller",
  "dateOfBirth": "1985-03-15",
  "age": 40,
  "ageGroup": "ADULT",
  "gender": "MALE",
  "maritalStatus": "MARRIED",
  "nationality": "CHE",
  "preferredLanguage": "DE",
  "status": "ACTIVE",
  "createdAt": "2025-01-15T10:30:00Z"
}
```

---

## Code Location

| File | Path |
|------|------|
| Entity | `backend/govinda-masterdata/src/main/java/net/voytrex/govinda/masterdata/domain/model/Person.java` |
| Repository | `backend/govinda-masterdata/src/main/java/net/voytrex/govinda/masterdata/domain/repository/PersonRepository.java` |
| Service | `backend/govinda-masterdata/src/main/java/net/voytrex/govinda/masterdata/application/service/PersonService.java` |
| Tests | `backend/govinda-masterdata/src/test/java/net/voytrex/govinda/masterdata/domain/model/PersonTest.java` |

---

## Related Enums

### Gender

```java
public enum Gender {
    MALE("M"),
    FEMALE("F"),
    OTHER("O");
}
```

### MaritalStatus

```java
public enum MaritalStatus {
    SINGLE("S"),
    MARRIED("M"),
    DIVORCED("D"),
    WIDOWED("W"),
    REGISTERED_PARTNERSHIP("P"),
    DISSOLVED_PARTNERSHIP("DP");
}
```

### PersonStatus

```java
public enum PersonStatus {
    ACTIVE,     // Active insured person
    DECEASED,   // Person has died
    EMIGRATED;  // Left Switzerland
}
```

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ Unique AHV | AHV number unique per tenant (if provided) |
| ⚠️ Valid birth date | Cannot be in future |
| ⚠️ History tracking | Name and marital status changes tracked |
| ⚠️ Multi-tenant | All queries filtered by tenantId |
| ⚠️ Age group calculation | Based on date of birth |

---

## Related Documentation

- [Address Entity](./address.md)
- [Household Entity](./household.md)
- [AhvNumber Value Object](../value-objects/ahv-number.md)
- [Age Groups Concept](../../concepts/age-groups.md)

---

*Last Updated: 2026-01-26*

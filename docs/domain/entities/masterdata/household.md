# Household Entity

## Overview

The **Household** entity represents a family unit or group of persons living together for insurance purposes. It is an aggregate root in the Masterdata bounded context and manages household membership with temporal validity.

> **German**: Haushalt
> **Module**: `govinda-masterdata`
> **Status**: ✅ Implemented

---

## Entity Definition

```java
@Entity
@Table(name = "households")
public class Household {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HouseholdMember> members = new ArrayList<>();

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
| `name` | String | ✅ | Household name (e.g., "Familie Müller") |
| `members` | List\<HouseholdMember\> | ❌ | Household members |
| `createdAt` | Instant | Auto | Creation timestamp |
| `updatedAt` | Instant | Auto | Last modification |
| `version` | long | Auto | Optimistic locking |

---

## Extension: Household Type and Institutions (Planned)

To support broadcast fees and other subscription domains, households are extended with a type and optional institution linkage.

```java
public enum HouseholdType {
    PRIVATE,            // Standard private household
    SHARED,             // WG/Flatshare (single household for billing)
    COLLECTIVE          // Institution-run household (collective fee)
}
```

```java
public class Household {
    // ... existing fields ...

    private HouseholdType type = HouseholdType.PRIVATE;
    private Integer residentCount;   // Optional: for collective household reporting
    private UUID institutionId;      // Optional: links to Organization
}
```

### Institution Linkage

- `institutionId` points to an `Organization` that operates the collective household (e.g., elderly home).
- This supports centralized billing at the organization level while maintaining member records.

---

## HouseholdMember Entity

```java
@Entity
@Table(name = "household_members")
public class HouseholdMember {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(nullable = false)
    private UUID personId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HouseholdRole role;

    @Column(nullable = false)
    private LocalDate validFrom;

    private LocalDate validTo;
}
```

### HouseholdMember Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | UUID | ✅ | Unique identifier |
| `household` | Household | ✅ | Parent household |
| `personId` | UUID | ✅ | Reference to Person |
| `role` | HouseholdRole | ✅ | PRIMARY, PARTNER, CHILD |
| `validFrom` | LocalDate | ✅ | Membership start date |
| `validTo` | LocalDate | ❌ | Membership end date (null = current) |

---

## Key Behaviors

### Get Current Members

```java
public List<HouseholdMember> currentMembers() {
    return members.stream()
        .filter(HouseholdMember::isCurrent)
        .collect(Collectors.toList());
}
```

### Get Primary Member (Policyholder)

```java
public Optional<HouseholdMember> primaryMember() {
    return currentMembers().stream()
        .filter(m -> m.getRole() == HouseholdRole.PRIMARY)
        .findFirst();
}
```

### Check Has Primary

```java
public boolean hasPrimary() {
    return primaryMember().isPresent();
}
```

### Count Children

```java
public int childCount() {
    return (int) currentMembers().stream()
        .filter(m -> m.getRole() == HouseholdRole.CHILD)
        .count();
}
```

### Add Member

```java
public void addMember(UUID personId, HouseholdRole role, LocalDate validFrom) {
    // Validate: only one PRIMARY allowed
    if (role == HouseholdRole.PRIMARY && hasPrimary()) {
        throw new IllegalStateException("Household already has a primary member");
    }

    // Validate: person not already member
    boolean alreadyMember = currentMembers().stream()
        .anyMatch(m -> m.getPersonId().equals(personId));
    if (alreadyMember) {
        throw new IllegalStateException("Person is already a household member");
    }

    HouseholdMember member = new HouseholdMember();
    member.setHousehold(this);
    member.setPersonId(personId);
    member.setRole(role);
    member.setValidFrom(validFrom);

    members.add(member);
    this.updatedAt = Instant.now();
}
```

### Remove Member

```java
public void removeMember(UUID personId, LocalDate endDate) {
    HouseholdMember member = currentMembers().stream()
        .filter(m -> m.getPersonId().equals(personId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Person not found in household"));

    // Close membership (don't delete - preserve history)
    member.setValidTo(endDate);
    this.updatedAt = Instant.now();
}
```

---

## HouseholdMember Behaviors

### Is Current

```java
public boolean isCurrent() {
    LocalDate today = LocalDate.now();
    return validFrom.isBefore(today) || validFrom.isEqual(today))
        && (validTo == null || validTo.isAfter(today) || validTo.isEqual(today));
}
```

---

## Relationships

### Household → HouseholdMember (1:N)

```
Household (1) ────────────< HouseholdMember (N)
                    ↑
             householdId: UUID (FK)
```

### HouseholdMember → Person (N:1)

```
HouseholdMember (N) ────────────> Person (1)
          ↑
   personId: UUID (FK)
```

---

## Household Roles

```java
public enum HouseholdRole {
    PRIMARY,   // Main policyholder (Versicherungsnehmer)
    PARTNER,   // Spouse or partner
    CHILD;     // Dependent child
}
```

| Role | Description | Max per Household |
|------|-------------|-------------------|
| `PRIMARY` | Main policyholder, receives invoices | 1 |
| `PARTNER` | Spouse, registered partner | Unlimited |
| `CHILD` | Dependent children | Unlimited |

---

## Business Rules

### Only One Primary

Each household can have only **one** PRIMARY member:

```java
// Enforced in addMember()
if (role == HouseholdRole.PRIMARY && hasPrimary()) {
    throw new IllegalStateException("Household already has a primary member");
}
```

### No Duplicate Members

A person cannot be added to the same household twice:

```java
// Enforced in addMember()
boolean alreadyMember = currentMembers().stream()
    .anyMatch(m -> m.getPersonId().equals(personId));
if (alreadyMember) {
    throw new IllegalStateException("Person is already a household member");
}
```

### Temporal Membership

- Members have `validFrom` and `validTo` dates
- Removing a member sets `validTo` (preserves history)
- A person can be in different households at different times

---

## Family Discount Eligibility

The `childCount()` method supports family discount calculations:

```java
int children = household.childCount();

if (children >= 3) {
    // Apply third-child discount
    // Discount amount varies by insurer and canton
}
```

---

## API Examples

### Create Household

```http
POST /api/v1/households
Content-Type: application/json

{
  "name": "Familie Müller"
}
```

### Add Member

```http
POST /api/v1/households/{householdId}/members
Content-Type: application/json

{
  "personId": "550e8400-e29b-41d4-a716-446655440000",
  "role": "PRIMARY",
  "validFrom": "2024-01-01"
}
```

### Get Household with Members

```http
GET /api/v1/households/{householdId}
```

### Response

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Familie Müller",
  "members": [
    {
      "id": "member-uuid-1",
      "personId": "person-uuid-1",
      "personName": "Hans Müller",
      "role": "PRIMARY",
      "validFrom": "2024-01-01",
      "validTo": null,
      "isCurrent": true
    },
    {
      "id": "member-uuid-2",
      "personId": "person-uuid-2",
      "personName": "Anna Müller",
      "role": "PARTNER",
      "validFrom": "2024-01-01",
      "validTo": null,
      "isCurrent": true
    },
    {
      "id": "member-uuid-3",
      "personId": "person-uuid-3",
      "personName": "Max Müller",
      "role": "CHILD",
      "validFrom": "2024-01-01",
      "validTo": null,
      "isCurrent": true
    }
  ],
  "childCount": 1,
  "hasPrimary": true,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

---

## Code Location

| File | Path |
|------|------|
| Household Entity | `backend/govinda-masterdata/src/main/java/net/voytrex/govinda/masterdata/domain/model/Household.java` |
| HouseholdMember | `backend/govinda-masterdata/src/main/java/net/voytrex/govinda/masterdata/domain/model/HouseholdMember.java` |
| Repository | `backend/govinda-masterdata/src/main/java/net/voytrex/govinda/masterdata/domain/repository/HouseholdRepository.java` |
| Tests | `backend/govinda-masterdata/src/test/java/net/voytrex/govinda/masterdata/domain/model/HouseholdTest.java` |

---

## Use Cases

### Typical Household Structures

```
Single Person:
├── PRIMARY: Hans Müller

Couple:
├── PRIMARY: Hans Müller
└── PARTNER: Anna Müller

Family with Children:
├── PRIMARY: Hans Müller
├── PARTNER: Anna Müller
├── CHILD: Max Müller
├── CHILD: Lisa Müller
└── CHILD: Tom Müller (3rd child - discount eligible)
```

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ One PRIMARY | Only one primary member per household |
| ⚠️ No duplicates | Person cannot be member twice |
| ⚠️ Temporal validity | Membership has start/end dates |
| ⚠️ Soft delete | Removal sets validTo (preserves history) |
| ⚠️ Child counting | Used for family discount eligibility |

---

## Related Documentation

- [Person Entity](./person.md)
- [Address Entity](./address.md)
- [Age Groups Concept](../../concepts/age-groups.md)

---

*Last Updated: 2026-01-28*

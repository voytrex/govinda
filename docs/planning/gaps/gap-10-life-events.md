# GAP-10: Life Events Handling

## Problem Statement

Missing automated handling for life events that affect insurance:
- Newborn registration (3-month window, retroactive coverage)
- Death (coverage termination, notifications)
- Age group transitions (18→19, 25→26)
- Marriage/Divorce (household changes)
- Moving (address, premium region)

---

## 1. Newborn Registration

### Legal Basis
- **KVG Art. 3** - Insurance obligation from birth
- **KVV Art. 6** - 3-month registration deadline

### Rules

| Rule | Value |
|------|-------|
| Registration deadline | 3 months from birth |
| Coverage start | Date of birth (retroactive) |
| Late registration | Surcharge may apply (Art. 5 KVG) |
| Franchise | 0 for children by default |

### Model

```java
@Entity
@Table(name = "life_events")
public class LifeEvent {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private LifeEventType eventType;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;  // When changes take effect

    @Column(name = "deadline_date")
    private LocalDate deadlineDate;  // Action required by

    // Related entities
    @Column(name = "related_person_id")
    private UUID relatedPersonId;  // e.g., mother for newborn

    @Column(name = "related_household_id")
    private UUID relatedHouseholdId;

    // Processing
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.PENDING;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "processed_by")
    private UUID processedBy;

    // Actions taken
    @Column(name = "coverage_created_id")
    private UUID coverageCreatedId;

    @Column(name = "coverage_terminated_id")
    private UUID coverageTerminatedId;

    @Column
    private String notes;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}

public enum LifeEventType {
    BIRTH("Geburt", 90),              // 3-month deadline
    DEATH("Tod", 30),                 // 30-day notification
    MARRIAGE("Heirat", 30),
    DIVORCE("Scheidung", 30),
    REGISTERED_PARTNERSHIP("Eingetragene Partnerschaft", 30),
    PARTNERSHIP_DISSOLUTION("Auflösung Partnerschaft", 30),
    AGE_TRANSITION_19("Übergang 19", 0),   // Auto on birthday
    AGE_TRANSITION_26("Übergang 26", 0),   // Auto on birthday
    IMMIGRATION("Einwanderung", 90),  // 3-month deadline
    EMIGRATION("Auswanderung", 30),
    ADDRESS_CHANGE("Adressänderung", 0),
    ADOPTION("Adoption", 90);

    private final String nameDe;
    private final int deadlineDays;

    LifeEventType(String nameDe, int deadlineDays) {
        this.nameDe = nameDe;
        this.deadlineDays = deadlineDays;
    }
}

public enum EventStatus {
    PENDING,        // Awaiting processing
    IN_PROGRESS,    // Being processed
    COMPLETED,      // Fully processed
    OVERDUE,        // Past deadline
    CANCELLED       // Event cancelled
}
```

### Newborn Registration Service

```java
public class NewbornRegistrationService {

    public LifeEvent registerNewborn(
            Person mother,
            Person newborn,
            LocalDate birthDate) {

        // Validate deadline
        LocalDate deadline = birthDate.plusMonths(3);
        boolean isLate = LocalDate.now().isAfter(deadline);

        // Create life event
        LifeEvent event = new LifeEvent();
        event.setEventType(LifeEventType.BIRTH);
        event.setPersonId(newborn.getId());
        event.setRelatedPersonId(mother.getId());
        event.setEventDate(birthDate);
        event.setEffectiveDate(birthDate);  // Coverage from birth
        event.setDeadlineDate(deadline);

        // Find mother's KVG coverage
        Coverage motherCoverage = findKvgCoverage(mother);
        if (motherCoverage != null) {
            // Create coverage for newborn with same insurer
            Coverage newbornCoverage = createNewbornCoverage(
                newborn,
                motherCoverage.getInsurerId(),
                birthDate,
                Franchise.CHF_0  // Children default
            );
            event.setCoverageCreatedId(newbornCoverage.getId());
        }

        // Apply late registration surcharge if applicable
        if (isLate && !isExcusable(mother, birthDate)) {
            applyLateSurcharge(newborn, birthDate, LocalDate.now());
        }

        event.setStatus(EventStatus.COMPLETED);
        event.setProcessedAt(Instant.now());

        return event;
    }
}
```

---

## 2. Death Handling

### Rules

| Action | Timing |
|--------|--------|
| Coverage termination | End of death month |
| Premium refund | Pro-rata for unused period |
| Household update | Remove from household |
| Notify dependents | If policyholder dies |

### Death Processing

```java
public class DeathProcessingService {

    public LifeEvent processDeath(Person deceased, LocalDate deathDate) {
        LifeEvent event = new LifeEvent();
        event.setEventType(LifeEventType.DEATH);
        event.setPersonId(deceased.getId());
        event.setEventDate(deathDate);
        event.setEffectiveDate(deathDate.withDayOfMonth(
            deathDate.lengthOfMonth()));  // End of month

        // 1. Update person status
        deceased.setStatus(PersonStatus.DECEASED);

        // 2. Terminate all coverages
        List<Coverage> coverages = findActiveCoverages(deceased);
        for (Coverage coverage : coverages) {
            coverage.terminate(event.getEffectiveDate(),
                TerminationReason.DEATH);
        }

        // 3. Remove from household
        HouseholdMember membership = findCurrentMembership(deceased);
        if (membership != null) {
            membership.setValidTo(deathDate);

            // If was PRIMARY, may need to reassign
            if (membership.getRole() == HouseholdRole.PRIMARY) {
                reassignPrimaryMember(membership.getHouseholdId());
            }
        }

        // 4. Calculate premium refund
        Money refund = calculatePremiumRefund(coverages, deathDate);

        // 5. Notify relevant parties
        notifyDeathEvent(deceased, event);

        event.setStatus(EventStatus.COMPLETED);
        return event;
    }
}
```

---

## 3. Age Group Transitions

### KVG Age Groups

| Age Group | Ages | Premium Category |
|-----------|------|------------------|
| CHILD | 0-18 | Lowest |
| YOUNG_ADULT | 19-25 | Middle |
| ADULT | 26+ | Highest |

### Automatic Transition

```java
@Scheduled(cron = "0 0 0 * * *")  // Daily at midnight
public class AgeTransitionJob {

    public void processAgeTransitions() {
        LocalDate today = LocalDate.now();

        // Find persons turning 19 today
        List<Person> turning19 = personRepository
            .findByDateOfBirth(today.minusYears(19));

        for (Person person : turning19) {
            processTransition(person, AgeGroup.YOUNG_ADULT, today);
        }

        // Find persons turning 26 today
        List<Person> turning26 = personRepository
            .findByDateOfBirth(today.minusYears(26));

        for (Person person : turning26) {
            processTransition(person, AgeGroup.ADULT, today);
        }
    }

    private void processTransition(Person person, AgeGroup newGroup,
            LocalDate effectiveDate) {

        LifeEvent event = new LifeEvent();
        event.setEventType(newGroup == AgeGroup.YOUNG_ADULT ?
            LifeEventType.AGE_TRANSITION_19 :
            LifeEventType.AGE_TRANSITION_26);
        event.setPersonId(person.getId());
        event.setEventDate(effectiveDate);

        // Update premium calculation for next period
        Coverage kvgCoverage = findKvgCoverage(person);
        if (kvgCoverage != null) {
            // Premium change effective 1st of next month
            LocalDate premiumChangeDate = effectiveDate
                .plusMonths(1).withDayOfMonth(1);

            recalculatePremium(kvgCoverage, newGroup, premiumChangeDate);

            // Send notification about premium change
            notifyPremiumChange(person, kvgCoverage, premiumChangeDate);
        }

        lifeEventRepository.save(event);
    }
}
```

---

## 4. Marriage / Divorce

### Effects

| Event | Effects |
|-------|---------|
| Marriage | Household merge, optional model change |
| Divorce | Household split, address changes |

### Marriage Processing

```java
public class MarriageService {

    public LifeEvent processMarriage(
            Person spouse1,
            Person spouse2,
            LocalDate marriageDate) {

        LifeEvent event = new LifeEvent();
        event.setEventType(LifeEventType.MARRIAGE);
        event.setPersonId(spouse1.getId());
        event.setRelatedPersonId(spouse2.getId());
        event.setEventDate(marriageDate);

        // 1. Update marital status
        spouse1.setMaritalStatus(MaritalStatus.MARRIED);
        spouse2.setMaritalStatus(MaritalStatus.MARRIED);

        // 2. Optionally merge households
        // (or create new joint household)
        Household jointHousehold = createOrMergeHousehold(
            spouse1, spouse2, marriageDate);
        event.setRelatedHouseholdId(jointHousehold.getId());

        // 3. Notify about potential premium changes
        // (household composition may affect broadcast fee, etc.)

        event.setStatus(EventStatus.COMPLETED);
        return event;
    }
}
```

---

## 5. Address Change / Moving

### Effects by Destination

| Destination | Effect |
|-------------|--------|
| Same premium region | No premium change |
| Different region | Premium recalculation |
| Different canton | IPV may change |
| Abroad | Emigration process |

### Address Change Processing

```java
public class AddressChangeService {

    public LifeEvent processAddressChange(
            Person person,
            Address newAddress,
            LocalDate moveDate) {

        Address oldAddress = person.currentAddress();

        LifeEvent event = new LifeEvent();
        event.setEventType(LifeEventType.ADDRESS_CHANGE);
        event.setPersonId(person.getId());
        event.setEventDate(moveDate);

        // 1. Close old address
        if (oldAddress != null) {
            oldAddress.setValidTo(moveDate.minusDays(1));
        }

        // 2. Set new address
        newAddress.setValidFrom(moveDate);
        person.addAddress(newAddress);

        // 3. Check premium region change
        PremiumRegion oldRegion = getPremiumRegion(oldAddress);
        PremiumRegion newRegion = getPremiumRegion(newAddress);

        if (!oldRegion.equals(newRegion)) {
            // Recalculate premium effective next month
            LocalDate effectiveDate = moveDate
                .plusMonths(1).withDayOfMonth(1);

            Coverage kvgCoverage = findKvgCoverage(person);
            if (kvgCoverage != null) {
                recalculatePremiumForRegion(
                    kvgCoverage, newRegion, effectiveDate);
            }
        }

        // 4. Check canton change (IPV)
        if (oldAddress.getCanton() != newAddress.getCanton()) {
            // IPV must be re-applied in new canton
            notifyIpvChange(person, oldAddress.getCanton(),
                newAddress.getCanton());
        }

        // 5. Update household address if primary member
        updateHouseholdAddressIfNeeded(person, newAddress);

        event.setStatus(EventStatus.COMPLETED);
        return event;
    }
}
```

---

## Database Schema

```sql
CREATE TABLE life_events (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID NOT NULL REFERENCES persons(id),
    event_type VARCHAR(30) NOT NULL,
    event_date DATE NOT NULL,
    effective_date DATE,
    deadline_date DATE,
    related_person_id UUID REFERENCES persons(id),
    related_household_id UUID,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    processed_at TIMESTAMP,
    processed_by UUID,
    coverage_created_id UUID,
    coverage_terminated_id UUID,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_le_person ON life_events(person_id);
CREATE INDEX idx_le_type_date ON life_events(event_type, event_date);
CREATE INDEX idx_le_status ON life_events(status);
```

---

*Status: Draft*
*Priority: HIGH*

# Age Groups (Altersgruppen)

## Overview

Swiss health insurance premiums are calculated based on **age groups** (Altersgruppen). The system recognizes three main categories with different premium levels, reflecting varying healthcare utilization patterns across age ranges.

> **German**: Altersgruppe, Alterskategorie
> **French**: Catégorie d'âge
> **Italian**: Categoria d'età

---

## The Three Age Groups

```
┌─────────────────────────────────────────────────────────────────────┐
│                      SWISS AGE GROUPS                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  0 ─────────────── 18 ─────────── 25 ───────────────────▶ Age      │
│  │                  │              │                                │
│  │    CHILDREN      │ YOUNG ADULTS │        ADULTS                 │
│  │    (Kinder)      │  (Junge      │      (Erwachsene)             │
│  │                  │  Erwachsene) │                                │
│  │                  │              │                                │
│  │  ✓ Lowest        │ ✓ Reduced    │ ✓ Full premiums               │
│  │    premiums      │   premiums   │                                │
│  │  ✓ CHF 0-600     │ ✓ CHF 300-   │ ✓ CHF 300-2500               │
│  │    franchise     │   2500       │   franchise                   │
│  │  ✓ Family        │              │                                │
│  │    discounts     │              │                                │
│  │                  │              │                                │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Age Group Details

### Children (Kinder): Ages 0-18

| Aspect | Details |
|--------|---------|
| **Age Range** | Birth to end of year when turning 18 |
| **Premium Level** | Lowest (~10-20% of adult premium) |
| **Franchise Options** | CHF 0, 100, 200, 300, 400, 600 |
| **Default Franchise** | CHF 0 (no deductible) |
| **Max Selbstbehalt** | CHF 350/year |
| **Family Discounts** | Third+ child often discounted |

**Typical Monthly Premium**: CHF 80-150 (varies by region)

### Young Adults (Junge Erwachsene): Ages 19-25

| Aspect | Details |
|--------|---------|
| **Age Range** | Year of 19th birthday to end of year when turning 25 |
| **Premium Level** | Reduced (~50-70% of adult premium) |
| **Franchise Options** | CHF 300, 500, 1000, 1500, 2000, 2500 |
| **Default Franchise** | CHF 300 |
| **Max Selbstbehalt** | CHF 700/year |

**Typical Monthly Premium**: CHF 200-350 (varies by region)

### Adults (Erwachsene): Ages 26+

| Aspect | Details |
|--------|---------|
| **Age Range** | From year of 26th birthday onwards |
| **Premium Level** | Full premium |
| **Franchise Options** | CHF 300, 500, 1000, 1500, 2000, 2500 |
| **Default Franchise** | CHF 300 |
| **Max Selbstbehalt** | CHF 700/year |

**Typical Monthly Premium**: CHF 350-550 (varies by region)

---

## Age Calculation Rules

### Reference Date

Age group is determined by the insured person's age at the **start of the coverage period**:

```
Birth Date: March 15, 2000

Coverage in 2025:
- Age on Jan 1, 2025: 24 years
- Age Group: YOUNG_ADULT

Coverage in 2026:
- Age on Jan 1, 2026: 25 years
- Age Group: YOUNG_ADULT (still!)

Coverage in 2027:
- Age on Jan 1, 2027: 26 years
- Age Group: ADULT (transition!)
```

### Transition Points

| Transition | Timing | Premium Impact |
|------------|--------|----------------|
| Child → Young Adult | Year of 19th birthday | Premium increases significantly |
| Young Adult → Adult | Year of 26th birthday | Premium increases to full rate |

⚠️ **Important**: The transition happens at the **start of the year** when the age is reached, not on the birthday itself.

---

## Premium Comparison by Age Group

### Example: Zürich (ZH-1), CHF 300 Franchise, Standard Model

| Age Group | Approx. Monthly Premium | % of Adult |
|-----------|------------------------|------------|
| **Child** | CHF 120 | 25% |
| **Young Adult** | CHF 320 | 65% |
| **Adult** | CHF 490 | 100% |

### Why the Differences?

| Age Group | Healthcare Utilization | Risk Profile |
|-----------|----------------------|--------------|
| **Children** | Moderate (check-ups, illness) | Lower risk, faster recovery |
| **Young Adults** | Low (generally healthiest) | Lowest utilization phase |
| **Adults** | Increasing with age | Higher chronic disease risk |

---

## Family Discounts

### Third Child and More

Many insurers offer discounts for families with multiple children:

```
Family with 3 children in same household:

Child 1: Full child premium        CHF 120
Child 2: Full child premium        CHF 120
Child 3: DISCOUNTED (e.g., -50%)   CHF  60
                                   ─────────
Total children's premiums:         CHF 300

(Instead of CHF 360 without discount)
```

### Eligibility Requirements

- Children must be in the **same household**
- Must be insured with the **same insurer**
- Discount applies from the third child onward
- Canton-specific rules may apply

---

## Young Adult Considerations

### When Young Adult Category Ends

The young adult discount ends at the start of the year when the person turns **26**:

```
Born: August 10, 1999

2024: Age 24-25 → Young Adult premium
2025: Age 25-26 → Young Adult premium (birthday in Aug)

2026: Age 26-27 → ADULT premium (full rate!)
      Transition happens January 1, 2026
```

### Financial Planning

Young adults should:
- Expect significant premium increase at age 26
- Consider savings strategies during lower-premium years
- Evaluate franchise choice as adult premiums approach

---

## Age Group Determination in System

### Algorithm

```
function determineAgeGroup(birthDate, referenceDate):
    age = calculateAge(birthDate, referenceDate)

    if age <= 18:
        return AgeGroup.CHILD
    else if age <= 25:
        return AgeGroup.YOUNG_ADULT
    else:
        return AgeGroup.ADULT
```

### Special Cases

| Scenario | Handling |
|----------|----------|
| Born Feb 29 (leap year) | Age calculated to Feb 28 in non-leap years |
| Mid-year coverage start | Use coverage start date as reference |
| Unknown birth date | Cannot determine age group (data required) |

---

## Code Reference

### AgeGroup Enum

```java
public enum AgeGroup {
    CHILD(0, 18, "CHILD"),
    YOUNG_ADULT(19, 25, "YA"),
    ADULT(26, Integer.MAX_VALUE, "ADULT");

    private final int minAge;
    private final int maxAge;
    private final String code;

    public static AgeGroup forAge(int age) {
        if (age <= 18) return CHILD;
        if (age <= 25) return YOUNG_ADULT;
        return ADULT;
    }
}
```

### Person Entity Method

```java
public class Person {
    private LocalDate dateOfBirth;

    public int ageAt(LocalDate date) {
        return Period.between(dateOfBirth, date).getYears();
    }

    public AgeGroup ageGroupAt(LocalDate date) {
        return AgeGroup.forAge(ageAt(date));
    }
}
```

### Usage in Premium Lookup

```java
// Determine premium for a person
Person person = findPerson(personId);
LocalDate effectiveDate = LocalDate.of(2025, 1, 1);

AgeGroup ageGroup = person.ageGroupAt(effectiveDate);
// Use ageGroup for premium table lookup
```

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ Three age groups | Child (0-18), Young Adult (19-25), Adult (26+) |
| ⚠️ Year-based transition | Changes at start of year, not birthday |
| ⚠️ Franchise depends on age | Children have different franchise options |
| ⚠️ Family discounts | Third+ child may receive discount |
| ⚠️ Reference date matters | Age group determined at coverage start |

---

## Official Resources

| Resource | URL |
|----------|-----|
| BAG Premium Info | [bag.admin.ch/praemien](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html) |
| Premium Calculator | [priminfo.admin.ch](https://www.priminfo.admin.ch) |

---

## Related Documentation

- [KVG - Mandatory Insurance](./kvg-mandatory-insurance.md)
- [Franchise System](./franchise-system.md)
- [Premium Regions](./premium-regions.md)
- [Person Entity](../entities/masterdata/person.md)

---

*Last Updated: 2026-01-26*

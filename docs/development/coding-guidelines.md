# Coding Guidelines

Comprehensive coding standards for the Govinda ERP project.

---

## Java Style

### General Formatting

| Rule | Standard |
|------|----------|
| Line length | Maximum 120 characters |
| Indentation | 4 spaces (no tabs) |
| Braces | Same line (K&R style) |
| Blank lines | 1 between methods, 2 between class sections |
| Imports | No wildcards, sorted alphabetically |

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase, nouns | `Person`, `ContractService` |
| Interfaces | PascalCase, nouns/adjectives | `PersonRepository`, `Auditable` |
| Methods | camelCase, verbs | `calculatePremium()`, `findById()` |
| Variables | camelCase, meaningful | `contractStartDate`, `premiumAmount` |
| Constants | SCREAMING_SNAKE_CASE | `MAX_FRANCHISE_AMOUNT`, `DEFAULT_LOCALE` |
| Packages | lowercase, singular | `domain.model`, `infrastructure.persistence` |
| Test classes | `{ClassName}Test` | `PersonTest`, `ContractServiceTest` |
| Test methods | `should_{behavior}_when_{condition}` | `should_calculateAge_when_birthDateProvided` |

### Boolean Naming

```java
// Prefix with: is, has, can, should, was, will
boolean isActive;
boolean hasAddress;
boolean canDelete;
boolean shouldNotify;

// Methods
boolean isValid();
boolean hasPermission(String permission);
boolean canBeDeleted();
```

### Abbreviations

**Allowed:**
- `ID`, `URL`, `DTO`, `API`, `UUID`, `JWT`, `AHV`, `KVG`, `VVG`

**Not Allowed:**
- `addr` → `address`
- `calc` → `calculate`
- `msg` → `message`
- `btn` → `button`
- `num` → `number`

---

## Java 21+ Features

Use modern Java features where appropriate:

### Records (for DTOs and Value Objects)

```java
// Request/Response DTOs
public record CreatePersonRequest(
    String firstName,
    String lastName,
    LocalDate birthDate,
    String ahvNumber
) {}

// Simple value objects
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(currency, "Currency must not be null");
    }
}
```

### Sealed Classes (for Domain Hierarchies)

```java
public sealed class DomainException extends RuntimeException
    permits EntityNotFoundException, ValidationException, AuthenticationException {

    private final String errorCode;

    protected DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
```

### Pattern Matching

```java
// instanceof pattern matching
if (entity instanceof Person person) {
    return person.getFullName();
}

// Switch expressions
String label = switch (status) {
    case ACTIVE -> "Active";
    case INACTIVE -> "Inactive";
    case PENDING -> "Pending";
};
```

### Optional Handling

```java
// Preferred: ifPresentOrElse, map, orElseThrow
return repository.findById(id)
    .map(this::toResponse)
    .orElseThrow(() -> new EntityNotFoundException("error.person.not.found", id));

// Avoid: isPresent() + get()
// BAD:
if (optional.isPresent()) {
    return optional.get();
}
```

---

## Architecture Rules

### Layer Dependencies

```
┌─────────────────────────────────────────────────────────┐
│                         API                             │
│              (Controllers, DTOs, Mappers)               │
└─────────────────────────┬───────────────────────────────┘
                          │ depends on
                          ▼
┌─────────────────────────────────────────────────────────┐
│                     Application                         │
│           (Use Cases, Commands, Queries)                │
└─────────────────────────┬───────────────────────────────┘
                          │ depends on
                          ▼
┌─────────────────────────────────────────────────────────┐
│                       Domain                            │
│      (Entities, Value Objects, Repository Ports)        │
│              *** NO EXTERNAL DEPENDENCIES ***           │
└─────────────────────────────────────────────────────────┘
                          ▲
                          │ implements
┌─────────────────────────┴───────────────────────────────┐
│                    Infrastructure                       │
│           (JPA Adapters, External Services)             │
└─────────────────────────────────────────────────────────┘
```

### Domain Layer Rules

```java
// ALLOWED in domain:
import java.time.*;
import java.util.*;
import java.math.BigDecimal;

// FORBIDDEN in domain:
import org.springframework.*;      // No Spring
import jakarta.persistence.*;      // No JPA annotations
import com.fasterxml.jackson.*;    // No JSON annotations
```

### Package Structure

```
net.voytrex.govinda.{module}/
├── domain/
│   ├── model/              # Entities, Value Objects, Aggregates
│   │   ├── Person.java
│   │   ├── AhvNumber.java  (value object)
│   │   └── PersonStatus.java (enum)
│   ├── repository/         # Repository interfaces (ports)
│   │   └── PersonRepository.java
│   ├── service/            # Domain services (stateless)
│   │   └── PremiumCalculator.java
│   └── exception/          # Domain-specific exceptions
│       └── InvalidAhvNumberException.java
├── application/            # Use cases, orchestration
│   ├── CreatePersonCommand.java
│   ├── PersonSearchQuery.java
│   └── PersonService.java
├── infrastructure/
│   └── persistence/        # JPA implementations
│       ├── JpaPersonRepositoryAdapter.java
│       └── SpringDataPersonRepository.java
└── api/                    # REST layer
    ├── PersonController.java
    ├── CreatePersonRequest.java
    ├── PersonResponse.java
    └── PersonMapper.java
```

---

## Entity Design

### Base Entity

```java
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // equals/hashCode based on ID only
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
```

### Value Object Pattern

```java
// Immutable, validated at construction
public record AhvNumber(String value) {

    private static final Pattern PATTERN =
        Pattern.compile("^756\\.\\d{4}\\.\\d{4}\\.\\d{2}$");

    public AhvNumber {
        Objects.requireNonNull(value, "AHV number must not be null");
        if (!PATTERN.matcher(value).matches()) {
            throw new InvalidAhvNumberException(value);
        }
    }

    public static AhvNumber of(String value) {
        return new AhvNumber(value);
    }

    public static boolean isValid(String value) {
        return value != null && PATTERN.matcher(value).matches();
    }

    public String masked() {
        return "756.****.****.%s".formatted(value.substring(value.length() - 2));
    }
}
```

### Entity with Business Logic

```java
public class Person extends BaseEntity {

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private AhvNumber ahvNumber;
    private PersonStatus status;

    // Business methods (not just getters/setters)
    public String fullName() {
        return "%s %s".formatted(firstName, lastName);
    }

    public int ageAt(LocalDate date) {
        return Period.between(birthDate, date).getYears();
    }

    public AgeGroup ageGroupAt(LocalDate date) {
        return AgeGroup.forAge(ageAt(date));
    }

    public void deactivate() {
        if (this.status == PersonStatus.INACTIVE) {
            throw new IllegalStateException("Person is already inactive");
        }
        this.status = PersonStatus.INACTIVE;
    }

    // Protected setters for JPA (or package-private)
    protected void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
```

---

## Exception Handling

### Domain Exception Hierarchy

```java
public sealed class DomainException extends RuntimeException
    permits EntityNotFoundException, ValidationException, BusinessRuleException {

    private final String errorCode;
    private final Object[] args;

    protected DomainException(String errorCode, Object... args) {
        super(errorCode);
        this.errorCode = errorCode;
        this.args = args;
    }

    public String getErrorCode() { return errorCode; }
    public Object[] getArgs() { return args; }
}

public final class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String errorCode, Object id) {
        super(errorCode, id);
    }
}
```

### Error Codes

```properties
# Format: error.{domain}.{situation}
error.entity.not.found=Entity with ID {0} not found
error.person.not.found=Person with ID {0} not found
error.person.ahv.duplicate=Person with AHV {0} already exists
error.validation.field.required={0} is required
error.validation.field.invalid={0} has invalid format
error.authentication.failed=Invalid username or password
error.authorization.denied=Access denied to resource {0}
```

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            EntityNotFoundException ex,
            Locale locale) {
        String message = messageSource.getMessage(
            ex.getErrorCode(),
            ex.getArgs(),
            locale
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getErrorCode(), message));
    }
}
```

---

## API Design

### Controller Structure

```java
@RestController
@RequestMapping("/api/v1/persons")
@Tag(name = "Persons", description = "Person management")
public class PersonController {

    private final PersonService personService;
    private final PersonMapper mapper;

    @GetMapping("/{id}")
    @Operation(summary = "Get person by ID")
    @ApiResponse(responseCode = "200", description = "Person found")
    @ApiResponse(responseCode = "404", description = "Person not found")
    public ResponseEntity<PersonResponse> getById(
            @PathVariable UUID id,
            @RequestHeader(value = "Accept-Language", defaultValue = "de") String language) {
        return personService.findById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new EntityNotFoundException("error.person.not.found", id));
    }

    @PostMapping
    @Operation(summary = "Create a new person")
    @ApiResponse(responseCode = "201", description = "Person created")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "409", description = "Person already exists")
    public ResponseEntity<PersonResponse> create(
            @Valid @RequestBody CreatePersonRequest request) {
        Person person = personService.create(mapper.toCommand(request));
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapper.toResponse(person));
    }
}
```

### Request/Response DTOs

```java
// Request
public record CreatePersonRequest(
    @NotBlank(message = "{validation.firstName.required}")
    @Size(max = 100)
    String firstName,

    @NotBlank(message = "{validation.lastName.required}")
    @Size(max = 100)
    String lastName,

    @NotNull(message = "{validation.birthDate.required}")
    @Past
    LocalDate birthDate,

    @Pattern(regexp = "^756\\.\\d{4}\\.\\d{4}\\.\\d{2}$")
    String ahvNumber
) {}

// Response
public record PersonResponse(
    UUID id,
    String firstName,
    String lastName,
    LocalDate birthDate,
    String ahvNumber,  // masked in production
    String status,
    Instant createdAt
) {}
```

### HTTP Status Codes

| Code | When to Use |
|------|-------------|
| 200 OK | Successful GET, PUT, PATCH |
| 201 Created | Successful POST (resource created) |
| 204 No Content | Successful DELETE |
| 400 Bad Request | Invalid request format, validation errors |
| 401 Unauthorized | Missing or invalid authentication |
| 403 Forbidden | Authenticated but not authorized |
| 404 Not Found | Resource doesn't exist |
| 409 Conflict | Duplicate resource, state conflict |
| 422 Unprocessable | Valid format but business rule violation |
| 500 Internal Error | Unexpected server error |

---

## Testing Guidelines

### Test Structure

```java
class PersonTest {

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        @DisplayName("should create person with valid data")
        void should_createPerson_when_validDataProvided() {
            // Arrange
            var firstName = "Hans";
            var lastName = "Muster";
            var birthDate = LocalDate.of(1990, 1, 15);

            // Act
            var person = Person.create(firstName, lastName, birthDate);

            // Assert
            assertThat(person.fullName()).isEqualTo("Hans Muster");
            assertThat(person.getStatus()).isEqualTo(PersonStatus.ACTIVE);
        }

        @Test
        @DisplayName("should reject null first name")
        void should_throwException_when_firstNameIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> Person.create(null, "Muster", LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("firstName");
        }
    }

    @Nested
    @DisplayName("Age Calculation")
    class AgeCalculation {

        private Person person;

        @BeforeEach
        void setUp() {
            person = PersonFixture.createDefault();
        }

        @ParameterizedTest
        @CsvSource({
            "2020-01-15, 30",
            "2020-01-14, 29",
            "2025-01-15, 35"
        })
        @DisplayName("should calculate age correctly")
        void should_calculateAge_when_dateProvided(LocalDate date, int expectedAge) {
            // Act
            int age = person.ageAt(date);

            // Assert
            assertThat(age).isEqualTo(expectedAge);
        }
    }
}
```

### Test Fixtures

```java
public class PersonFixture {

    public static Person createDefault() {
        return Person.builder()
            .firstName("Hans")
            .lastName("Muster")
            .birthDate(LocalDate.of(1990, 1, 15))
            .ahvNumber(AhvNumber.of("756.1234.5678.97"))
            .status(PersonStatus.ACTIVE)
            .build();
    }

    public static Person.PersonBuilder aBuilder() {
        return Person.builder()
            .firstName("Hans")
            .lastName("Muster")
            .birthDate(LocalDate.of(1990, 1, 15));
    }
}
```

### Service Test with Mocks

```java
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository repository;

    @InjectMocks
    private PersonService service;

    @Test
    @DisplayName("should return person when found")
    void should_returnPerson_when_existsInRepository() {
        // Arrange
        var id = UUID.randomUUID();
        var person = PersonFixture.createDefault();
        when(repository.findById(id)).thenReturn(Optional.of(person));

        // Act
        var result = service.findById(id);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().fullName()).isEqualTo("Hans Muster");
        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }
}
```

### Integration Test with Testcontainers

```java
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class PersonRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
        .withDatabaseName("govinda_test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private PersonRepository repository;

    @Test
    @DisplayName("should persist and retrieve person")
    void should_persistAndRetrieve_when_validPerson() {
        // Arrange
        var person = PersonFixture.createDefault();

        // Act
        var saved = repository.save(person);
        var found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().fullName()).isEqualTo("Hans Muster");
    }
}
```

---

## Common Patterns

### Repository Pattern

```java
// Domain port (interface)
public interface PersonRepository {
    Optional<Person> findById(UUID id);
    Optional<Person> findByAhvNumber(AhvNumber ahvNumber);
    List<Person> findByLastName(String lastName);
    Person save(Person person);
    void delete(UUID id);
}

// Infrastructure adapter
@Repository
public class JpaPersonRepositoryAdapter implements PersonRepository {

    private final SpringDataPersonRepository jpaRepository;

    @Override
    public Optional<Person> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Person save(Person person) {
        return jpaRepository.save(person);
    }
}
```

### Command Pattern

```java
public record CreatePersonCommand(
    String firstName,
    String lastName,
    LocalDate birthDate,
    AhvNumber ahvNumber,
    UUID tenantId
) {
    public CreatePersonCommand {
        Objects.requireNonNull(firstName, "firstName is required");
        Objects.requireNonNull(lastName, "lastName is required");
        Objects.requireNonNull(birthDate, "birthDate is required");
        Objects.requireNonNull(tenantId, "tenantId is required");
    }
}
```

### Mapper Pattern

```java
@Component
public class PersonMapper {

    public CreatePersonCommand toCommand(CreatePersonRequest request, UUID tenantId) {
        return new CreatePersonCommand(
            request.firstName(),
            request.lastName(),
            request.birthDate(),
            request.ahvNumber() != null ? AhvNumber.of(request.ahvNumber()) : null,
            tenantId
        );
    }

    public PersonResponse toResponse(Person person) {
        return new PersonResponse(
            person.getId(),
            person.getFirstName(),
            person.getLastName(),
            person.getBirthDate(),
            person.getAhvNumber() != null ? person.getAhvNumber().masked() : null,
            person.getStatus().name(),
            person.getCreatedAt()
        );
    }
}
```

---

## Anti-Patterns to Avoid

### Domain

```java
// BAD: Anemic domain model
public class Person {
    private String firstName;
    // Only getters and setters, no behavior
}

// GOOD: Rich domain model
public class Person {
    private String firstName;

    public void changeName(String newFirstName, String reason) {
        addHistoryEntry(new NameChange(this.firstName, newFirstName, reason));
        this.firstName = newFirstName;
    }
}
```

### Services

```java
// BAD: Service doing entity's job
public class PersonService {
    public boolean isAdult(Person person) {
        return person.getAge() >= 18;
    }
}

// GOOD: Behavior on entity
public class Person {
    public boolean isAdult() {
        return this.ageAt(LocalDate.now()) >= 18;
    }
}
```

### Exceptions

```java
// BAD: Generic exceptions
throw new RuntimeException("Person not found");

// GOOD: Specific exceptions with error codes
throw new EntityNotFoundException("error.person.not.found", personId);
```

### Testing

```java
// BAD: Testing implementation details
verify(repository, times(1)).save(any());

// GOOD: Testing behavior
assertThat(result.getStatus()).isEqualTo(PersonStatus.ACTIVE);
```

# Rewine Backend Architecture Documentation

## Table of Contents

1. [Overview](#overview)
2. [Architecture Principles](#architecture-principles)
3. [Project Structure](#project-structure)
4. [Layer Responsibilities](#layer-responsibilities)
5. [Data Flow](#data-flow)
6. [Domain Model](#domain-model)
7. [API Design](#api-design)
8. [Security Architecture](#security-architecture)
9. [Database Design](#database-design)
10. [Exception Handling](#exception-handling)
11. [Testing Strategy](#testing-strategy)
12. [Configuration Management](#configuration-management)
13. [Best Practices & Conventions](#best-practices--conventions)

---

## Overview

Rewine Backend is a RESTful API built with **Spring Boot 3.x** and **Java 21**. The architecture follows a **layered approach** with clear separation of concerns, implementing patterns from Domain-Driven Design (DDD) and Clean Architecture principles.

### Technology Stack

| Category | Technology | Version |
|----------|------------|---------|
| Framework | Spring Boot | 3.2.x |
| Language | Java | 21 (LTS) |
| Build Tool | Maven | 3.9+ |
| Database | PostgreSQL | 15+ |
| Migrations | Flyway | 9.x |
| ORM | Hibernate/JPA | 6.x |
| Security | Spring Security | 6.x |
| JWT | jjwt | 0.12.x |
| Documentation | SpringDoc OpenAPI | 2.3.x |
| Mapping | MapStruct | 1.5.x |
| Validation | Jakarta Validation | 3.x |
| Testing | JUnit 5 + Mockito | 5.x |

---

## Architecture Principles

### 1. Interface-Driven Development

All components are defined by interfaces prefixed with `I` (e.g., `IWineService`). Implementations reside in `impl` subpackages, enabling:
- Easy mocking for tests
- Flexibility to swap implementations
- Clear contract definitions

### 2. Layered Architecture

The application follows a strict layered architecture where each layer has a single responsibility and only communicates with adjacent layers.

```
┌─────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                         │
│                        (Controllers)                            │
├─────────────────────────────────────────────────────────────────┤
│                      APPLICATION LAYER                          │
│                   (Handlers / Use Cases)                        │
├─────────────────────────────────────────────────────────────────┤
│                       SERVICE LAYER                             │
│              (Business Logic / Domain Services)                 │
├─────────────────────────────────────────────────────────────────┤
│                      REPOSITORY LAYER                           │
│                      (Data Access)                              │
├─────────────────────────────────────────────────────────────────┤
│                       DATABASE LAYER                            │
│                      (PostgreSQL)                               │
└─────────────────────────────────────────────────────────────────┘
```

### 3. Dependency Inversion

High-level modules depend on abstractions (interfaces), not on low-level modules. This is enforced through constructor injection.

### 4. Single Responsibility

Each class has one reason to change:
- Controllers handle HTTP concerns
- Handlers orchestrate use cases
- Services contain business logic
- Repositories manage data access

### 5. CQRS-Inspired Separation

Query and Command operations are separated at the service level:
- `IWineQueryService` - Read operations
- `IWineCommandService` - Write operations

---

## Project Structure

```
backend/
├── pom.xml                                # Maven configuration
│
├── src/main/java/com/rewine/backend/
│   │
│   ├── RewineBackendApp.java              # Application entry point
│   │
│   ├── configuration/                      # Spring configurations
│   │   ├── security/
│   │   │   ├── ISecurityConfig.java       # Security interface
│   │   │   └── impl/
│   │   │       └── SecurityConfigImpl.java
│   │   ├── openapi/
│   │   │   ├── IOpenApiConfig.java        # OpenAPI interface
│   │   │   └── impl/
│   │   │       └── OpenApiConfigImpl.java
│   │   └── web/
│   │       ├── IWebConfig.java            # Web/CORS interface
│   │       └── impl/
│   │           └── WebConfigImpl.java
│   │
│   ├── controller/                         # REST controllers
│   │   ├── IHealthController.java
│   │   ├── IAuthController.java
│   │   ├── IWineController.java
│   │   ├── IReviewController.java
│   │   ├── IEventController.java
│   │   ├── IWineRouteController.java
│   │   ├── IUserController.java
│   │   └── impl/
│   │       ├── HealthControllerImpl.java
│   │       ├── AuthControllerImpl.java
│   │       └── ...
│   │
│   ├── handler/                            # Use case handlers
│   │   ├── IAuthHandler.java
│   │   ├── IWineHandler.java
│   │   ├── IEventHandler.java
│   │   └── impl/
│   │       ├── AuthHandlerImpl.java
│   │       └── ...
│   │
│   ├── service/                            # Business services
│   │   ├── IAuthService.java
│   │   ├── IWineQueryService.java
│   │   ├── IWineCommandService.java
│   │   ├── IReviewService.java
│   │   ├── IEventService.java
│   │   ├── IWineRouteService.java
│   │   ├── IUserService.java
│   │   └── impl/
│   │       ├── AuthServiceImpl.java
│   │       └── ...
│   │
│   ├── repository/                         # Data access layer
│   │   ├── IUserRepository.java
│   │   ├── IWineRepository.java
│   │   ├── IReviewRepository.java
│   │   ├── IEventRepository.java
│   │   └── IWineRouteRepository.java
│   │
│   ├── model/                              # Domain models
│   │   ├── entity/                        # JPA entities
│   │   │   ├── UserEntity.java
│   │   │   ├── WineEntity.java
│   │   │   ├── ReviewEntity.java
│   │   │   ├── EventEntity.java
│   │   │   ├── WineRouteEntity.java
│   │   │   └── WineRouteStopEntity.java
│   │   ├── dto/                           # Data Transfer Objects
│   │   │   ├── request/                   # Incoming DTOs
│   │   │   └── response/                  # Outgoing DTOs
│   │   └── mapper/                        # Entity ↔ DTO mappers
│   │
│   ├── exception/                          # Exception handling
│   │   ├── RewineException.java           # Base exception
│   │   ├── ErrorCode.java                 # Error code enum
│   │   ├── ErrorMapping.java              # Error → HTTP status
│   │   └── GlobalExceptionHandler.java    # @ControllerAdvice
│   │
│   └── utils/                              # Utilities
│       ├── converter/
│       │   ├── IConverter.java
│       │   └── impl/
│       ├── logging/
│       │   ├── ILoggingUtils.java
│       │   └── impl/
│       └── validation/
│           ├── IValidationUtils.java
│           └── impl/
│
├── src/main/resources/
│   ├── application.yml                     # Main configuration
│   ├── application-dev.yml                 # Dev profile
│   ├── application-prod.yml                # Prod profile
│   └── db/migration/                       # Flyway migrations
│       └── V1__initial_schema.sql
│
└── src/test/java/                          # Test sources
```

---

## Layer Responsibilities

### Controller Layer

**Purpose**: Handle HTTP requests and responses.

**Responsibilities**:
- Request validation (via annotations)
- Route mapping
- Response serialization
- HTTP status codes
- OpenAPI documentation

**Rules**:
- No business logic
- Delegate to handlers
- Return DTOs, never entities

```java
@RestController
@RequestMapping("/wines")
@Tag(name = "Wines", description = "Wine catalog management")
public class WineControllerImpl implements IWineController {

    private final IWineHandler wineHandler;

    @GetMapping("/{id}")
    @Operation(summary = "Get wine by ID")
    public ResponseEntity<WineResponse> getWine(@PathVariable UUID id) {
        return ResponseEntity.ok(wineHandler.getWineById(id));
    }
}
```

### Handler Layer

**Purpose**: Orchestrate use cases and coordinate services.

**Responsibilities**:
- Combine multiple service calls
- Transaction boundary management
- DTO transformation coordination
- Cross-cutting concerns

**Rules**:
- One handler per domain aggregate
- Coordinate, don't implement business logic
- Handle complex workflows

```java
@Component
public class WineHandlerImpl implements IWineHandler {

    private final IWineQueryService wineQueryService;
    private final IReviewService reviewService;
    private final WineMapper wineMapper;

    @Transactional(readOnly = true)
    public WineDetailResponse getWineById(UUID id) {
        WineEntity wine = wineQueryService.findById(id);
        List<ReviewEntity> reviews = reviewService.findTopReviews(id, 5);
        return wineMapper.toDetailResponse(wine, reviews);
    }
}
```

### Service Layer

**Purpose**: Implement business logic.

**Responsibilities**:
- Domain rules and validations
- Business calculations
- Data transformations
- Repository interactions

**Rules**:
- Single responsibility (Query vs Command)
- No HTTP concerns
- Work with entities

```java
@Service
public class WineQueryServiceImpl implements IWineQueryService {

    private final IWineRepository wineRepository;

    public WineEntity findById(UUID id) {
        return wineRepository.findById(id)
            .orElseThrow(() -> new RewineException(
                ErrorCode.WINE_NOT_FOUND,
                "Wine not found: " + id
            ));
    }

    public Page<WineEntity> search(WineFilter filter, Pageable pageable) {
        // Apply business rules for search
        return wineRepository.findByFilters(filter, pageable);
    }
}
```

### Repository Layer

**Purpose**: Abstract data access.

**Responsibilities**:
- CRUD operations
- Custom queries
- Pagination support

**Rules**:
- Interface extends JpaRepository
- Custom queries via @Query or Specifications
- No business logic

```java
@Repository
public interface IWineRepository extends JpaRepository<WineEntity, UUID> {

    Page<WineEntity> findByType(String type, Pageable pageable);

    @Query("SELECT w FROM WineEntity w WHERE " +
           "LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<WineEntity> search(@Param("search") String search, Pageable pageable);
}
```

---

## Data Flow

### Request Flow

```
┌──────────┐    ┌────────────┐    ┌─────────┐    ┌─────────┐    ┌──────────┐
│  Client  │───▶│ Controller │───▶│ Handler │───▶│ Service │───▶│Repository│
│          │    │            │    │         │    │         │    │          │
│          │    │  - Validate│    │- Orchestr│   │- Business│   │ - Query  │
│          │    │  - Route   │    │- Transact│   │  Logic   │   │ - Persist│
│          │    │  - Document│    │- Map DTOs│   │- Rules   │   │          │
└──────────┘    └────────────┘    └─────────┘    └─────────┘    └──────────┘
     │                │                │              │               │
     │                │                │              │               │
     │                ▼                ▼              ▼               ▼
     │           RequestDTO      Domain Ops     Entity Ops      Database
     │                │                │              │               │
     │                │                │              │               │
     ◀────────────────┴────────────────┴──────────────┴───────────────┘
                              ResponseDTO
```

### Authentication Flow

```
┌──────────┐                                                    ┌──────────┐
│  Client  │                                                    │ Database │
└────┬─────┘                                                    └────┬─────┘
     │                                                               │
     │  1. POST /auth/login {email, password}                        │
     │──────────────────────────────────────────────▶                │
     │                                                               │
     │                      2. Validate credentials                  │
     │                      ◀────────────────────────────────────────│
     │                                                               │
     │  3. Generate JWT tokens                                       │
     │                                                               │
     │  4. Return {accessToken, refreshToken (cookie)}               │
     │◀──────────────────────────────────────────────                │
     │                                                               │
     │  5. GET /wines (Authorization: Bearer <token>)                │
     │──────────────────────────────────────────────▶                │
     │                                                               │
     │                      6. Validate JWT                          │
     │                      7. Extract user context                  │
     │                      8. Process request                       │
     │                      ◀────────────────────────────────────────│
     │                                                               │
     │  9. Return wines list                                         │
     │◀──────────────────────────────────────────────                │
     │                                                               │
```

---

## Domain Model

### Entity Relationship Diagram

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     USER        │       │      WINE       │       │     REVIEW      │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)         │       │ id (PK)         │
│ email           │       │ name            │       │ wine_id (FK)    │
│ password        │       │ winery          │       │ user_id (FK)    │
│ name            │       │ type            │       │ rating          │
│ avatar          │       │ region          │       │ comment         │
│ roles[]         │       │ vintage         │       │ created_at      │
│ is_verified     │       │ price           │       │ updated_at      │
│ created_at      │       │ rating          │       └────────┬────────┘
│ updated_at      │       │ review_count    │                │
└────────┬────────┘       │ created_at      │                │
         │                │ updated_at      │                │
         │                └────────┬────────┘                │
         │                         │                         │
         │    ┌────────────────────┼─────────────────────────┘
         │    │                    │
         ▼    ▼                    │
┌─────────────────┐                │
│     EVENT       │                │
├─────────────────┤                │
│ id (PK)         │                │
│ title           │       ┌────────┴────────┐
│ description     │       │   WINE_ROUTE    │
│ type            │       ├─────────────────┤
│ start_date      │       │ id (PK)         │
│ end_date        │       │ name            │
│ location_*      │       │ description     │
│ price           │       │ region          │
│ max_attendees   │       │ difficulty      │
│ organizer_id(FK)│       │ created_by (FK) │
│ status          │       │ status          │
│ created_at      │       │ created_at      │
│ updated_at      │       │ updated_at      │
└─────────────────┘       └────────┬────────┘
                                   │
                                   ▼
                          ┌─────────────────┐
                          │ WINE_ROUTE_STOP │
                          ├─────────────────┤
                          │ id (PK)         │
                          │ wine_route_id   │
                          │ name            │
                          │ description     │
                          │ stop_order      │
                          │ latitude        │
                          │ longitude       │
                          └─────────────────┘
```

### Entity Conventions

```java
@Entity
@Table(name = "wines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    // Audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
```

---

## API Design

### RESTful Conventions

| Method | Path | Description |
|--------|------|-------------|
| GET | `/wines` | List wines (paginated) |
| GET | `/wines/{id}` | Get wine details |
| POST | `/wines` | Create wine |
| PUT | `/wines/{id}` | Update wine |
| DELETE | `/wines/{id}` | Delete wine |
| POST | `/wines/compare` | Compare wines (AI) |
| POST | `/wines/{id}/ai-profile` | Generate AI profile |

### Response Envelope

All responses follow a consistent structure:

```json
{
  "data": { ... },
  "meta": {
    "page": 1,
    "pageSize": 20,
    "totalItems": 150,
    "totalPages": 8
  }
}
```

### Error Response

```json
{
  "code": "E4000",
  "message": "Wine not found",
  "status": 404,
  "timestamp": "2026-01-14T10:30:00Z",
  "details": null
}
```

### Pagination

Query parameters for pagination:
- `page` - Page number (0-based)
- `size` - Items per page (default: 20, max: 100)
- `sort` - Sort field and direction (e.g., `name,asc`)

---

## Security Architecture

### Authentication Flow

The application uses JWT (JSON Web Token) authentication with refresh token rotation for enhanced security.

```
┌─────────┐                 ┌─────────────┐                 ┌──────────┐
│ Client  │                 │   Backend   │                 │ Database │
└────┬────┘                 └──────┬──────┘                 └────┬─────┘
     │                             │                              │
     │  POST /auth/login           │                              │
     │  {username, password}       │                              │
     │────────────────────────────▶│                              │
     │                             │   Validate credentials       │
     │                             │─────────────────────────────▶│
     │                             │◀─────────────────────────────│
     │                             │                              │
     │                             │   Generate JWT tokens        │
     │                             │   Store refresh token hash   │
     │                             │─────────────────────────────▶│
     │                             │                              │
     │  {accessToken, refreshToken}│                              │
     │◀────────────────────────────│                              │
     │                             │                              │
     │  GET /api/protected         │                              │
     │  Authorization: Bearer xxx  │                              │
     │────────────────────────────▶│                              │
     │                             │   Validate JWT               │
     │                             │   Extract user context       │
     │  Response                   │                              │
     │◀────────────────────────────│                              │
     │                             │                              │
     │  POST /auth/refresh         │                              │
     │  {refreshToken}             │                              │
     │────────────────────────────▶│                              │
     │                             │   Validate refresh token     │
     │                             │   Token rotation (revoke old)│
     │                             │─────────────────────────────▶│
     │  {newAccessToken,           │                              │
     │   newRefreshToken}          │                              │
     │◀────────────────────────────│                              │
```

### Token Configuration

| Token Type | Expiration | Storage |
|------------|------------|---------|
| Access Token | 15 minutes | Client memory/localStorage |
| Refresh Token | 7 days | Client storage, hash in DB |

### JWT Token Structure

**Access Token Claims:**
```json
{
  "sub": "username",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "roles": ["ROLE_USER"],
  "iss": "rewine-backend",
  "iat": 1704067200,
  "exp": 1704068100
}
```

### Security Components

| Component | Location | Responsibility |
|-----------|----------|----------------|
| `JwtProperties` | `configuration/properties/` | JWT configuration (secret, expiration) |
| `JwtAuthenticationFilter` | `configuration/security/` | Extract and validate JWT from requests |
| `JwtAuthenticationEntryPoint` | `configuration/security/` | Handle 401 Unauthorized responses |
| `JwtTokenServiceImpl` | `service/impl/` | Generate and validate tokens |
| `UserDetailsServiceImpl` | `service/impl/` | Load user details for authentication |
| `SecurityConfigImpl` | `configuration/security/impl/` | Spring Security configuration |

### Authorization

Role-based access control (RBAC) with method-level security:

| Role | Code | Permissions |
|------|------|-------------|
| Admin | `ROLE_ADMIN` | Full access to all resources |
| Moderator | `ROLE_MODERATOR` | Review moderation, content management |
| Partner | `ROLE_PARTNER` | Manage own wines, events, routes |
| User | `ROLE_USER` | Browse, review, manage personal cellar |

### Method-Level Security Examples

```java
// Only ADMIN can access
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> deleteUser(UUID userId) { ... }

// ADMIN or MODERATOR can access
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
public ResponseEntity<?> moderateReview(UUID reviewId) { ... }

// User can only access their own data
@PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
public ResponseEntity<?> getUserProfile(UUID userId) { ... }
```

### Refresh Token Rotation

For enhanced security, refresh tokens are rotated on each use:

1. Client sends refresh token
2. Server validates token hash exists in DB and is not expired/revoked
3. Server generates new access token AND new refresh token
4. Old refresh token is marked as revoked with `replaced_by_token_hash`
5. New tokens returned to client

This prevents refresh token reuse attacks.

### Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfigImpl implements ISecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/health", "/version", "/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### Password Security

- **Algorithm**: BCrypt
- **Strength**: 10 rounds (default)
- **Storage**: Only hashed passwords stored, never plaintext

### Request Correlation

All requests include correlation IDs for tracing:

- Header: `X-Request-Id`
- MDC: `requestId`, `userId`, `path`, `method`
- Logged with every request/response

---

## Database Design

### Migration Strategy

Flyway manages database schema evolution:

```
src/main/resources/db/migration/
├── V1__initial_schema.sql        # Initial tables
├── V2__add_indexes.sql           # Performance indexes
├── V3__add_wine_routes.sql       # New feature tables
└── V4__add_audit_columns.sql     # Audit support
```

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Tables | snake_case, plural | `wine_routes` |
| Columns | snake_case | `created_at` |
| Primary Keys | `id` | `id` |
| Foreign Keys | `{table}_id` | `user_id` |
| Indexes | `idx_{table}_{columns}` | `idx_wines_type` |

### Connection Pool

HikariCP configuration:
- Minimum idle: 5
- Maximum pool size: 20
- Connection timeout: 30s
- Idle timeout: 10min

---

## Exception Handling

### Error Code System

```java
public enum ErrorCode {
    // General (1xxx)
    INTERNAL_ERROR("E1000", "Internal server error"),
    VALIDATION_ERROR("E1001", "Validation failed"),
    RESOURCE_NOT_FOUND("E1002", "Resource not found"),

    // Authentication (2xxx)
    AUTHENTICATION_FAILED("E2000", "Authentication failed"),
    TOKEN_EXPIRED("E2002", "Token has expired"),

    // Domain-specific (3xxx-7xxx)
    USER_NOT_FOUND("E3000", "User not found"),
    WINE_NOT_FOUND("E4000", "Wine not found"),
    // ...
}
```

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RewineException.class)
    public ResponseEntity<ErrorResponse> handleRewineException(RewineException ex) {
        HttpStatus status = ErrorMapping.getHttpStatus(ex);
        ErrorResponse response = new ErrorResponse(
            ex.getCode(),
            ex.getMessage(),
            status.value(),
            Instant.now()
        );
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(...) {
        // Handle validation errors
    }
}
```

---

## Testing Strategy

### Test Pyramid

```
        ┌───────────┐
        │   E2E     │  ← Few, slow, high confidence
        ├───────────┤
        │Integration│  ← Some, medium speed
        ├───────────┤
        │   Unit    │  ← Many, fast, isolated
        └───────────┘
```

### Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class WineQueryServiceImplTest {

    @Mock
    private IWineRepository wineRepository;

    @InjectMocks
    private WineQueryServiceImpl wineQueryService;

    @Test
    void findById_whenWineExists_returnsWine() {
        // Given
        UUID id = UUID.randomUUID();
        WineEntity wine = WineEntity.builder().id(id).name("Test").build();
        when(wineRepository.findById(id)).thenReturn(Optional.of(wine));

        // When
        WineEntity result = wineQueryService.findById(id);

        // Then
        assertThat(result).isEqualTo(wine);
    }

    @Test
    void findById_whenWineNotFound_throwsException() {
        // Given
        UUID id = UUID.randomUUID();
        when(wineRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> wineQueryService.findById(id))
            .isInstanceOf(RewineException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WINE_NOT_FOUND);
    }
}
```

### Integration Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class WineControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getWine_returnsWineDetails() throws Exception {
        mockMvc.perform(get("/wines/{id}", wineId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("Test Wine"));
    }
}
```

### Test Coverage Targets

| Layer | Target |
|-------|--------|
| Service | 80%+ |
| Handler | 70%+ |
| Controller | 60%+ |
| Repository | Integration tests |

---

## Configuration Management

### Profile-Based Configuration

```yaml
# application.yml (common)
spring:
  application:
    name: rewine-backend

---
# application-dev.yml
spring:
  datasource:
    url: jdbc:h2:mem:rewinedb
  jpa:
    show-sql: true

---
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
  jpa:
    show-sql: false
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | Database host | localhost |
| `DB_PORT` | Database port | 5432 |
| `DB_NAME` | Database name | rewinedb |
| `DB_USER` | Database user | - |
| `DB_PASSWORD` | Database password | - |
| `JWT_SECRET` | JWT signing secret | - |

---

## Best Practices & Conventions

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Interfaces | `I` prefix | `IWineService` |
| Implementations | `Impl` suffix | `WineServiceImpl` |
| Entities | `Entity` suffix | `WineEntity` |
| DTOs | Request/Response suffix | `WineResponse` |
| Repositories | `I` prefix | `IWineRepository` |

### Code Style

- **Language**: English for all code, comments, and documentation
- **Naming**: camelCase for variables/methods, PascalCase for classes
- **Line Length**: Max 120 characters
- **Imports**: No wildcard imports

### Dependency Injection

```java
// ✅ Constructor injection (preferred)
@Service
public class WineServiceImpl implements IWineService {
    private final IWineRepository wineRepository;

    public WineServiceImpl(IWineRepository wineRepository) {
        this.wineRepository = wineRepository;
    }
}

// ❌ Field injection (avoid)
@Autowired
private IWineRepository wineRepository;
```

### Transaction Management

```java
// Read operations
@Transactional(readOnly = true)
public WineEntity findById(UUID id) { ... }

// Write operations
@Transactional
public WineEntity create(WineEntity wine) { ... }
```

### Logging

```java
private static final Logger log = LoggerFactory.getLogger(WineServiceImpl.class);

public WineEntity findById(UUID id) {
    log.debug("Finding wine by id: {}", id);
    // ...
    log.info("Wine found: {}", wine.getName());
}
```

---

## Related Documentation

- [Project README](../README.md) - Project overview
- [Architecture Overview](../docs/ARCHITECTURE.md) - System-wide architecture
- [Security Guide](../docs/SECURITY.md) - Security details
- [API Documentation](http://localhost:8080/api/v1/swagger-ui.html) - Interactive API docs


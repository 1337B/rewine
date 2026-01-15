# Rewine Backend

Spring Boot 3.x REST API for the Rewine Wine Discovery Platform.

---

## Prerequisites

- **Java 21** (LTS) - Required
- **Maven 3.9+** - Build tool
- **Docker** - For PostgreSQL database (recommended)
- **PostgreSQL 15+** - Database (can run via Docker)

### Verify Java Version

```bash
java -version
# Should show: openjdk version "21.x.x"
```

If you have multiple Java versions, use SDKMAN:
```bash
sdk use java 21.0.5-tem
```

---

## Quick Start

### 1. Start PostgreSQL Database (Required)

The application requires PostgreSQL to run. Start it with Docker:

```bash
# Start PostgreSQL container
docker run -d \
  --name rewine-postgres \
  -e POSTGRES_DB=rewine \
  -e POSTGRES_USER=rewine \
  -e POSTGRES_PASSWORD=rewine_secret \
  -p 5432:5432 \
  postgres:15
```

Or use docker-compose from `/rewine/infra`:
```bash
cd ../infra
docker-compose up -d postgres
```

### 2. Run the Application

```bash
# From /backend directory
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080/api/v1`

---

## Local PostgreSQL Configuration

The default profile (`local`) uses PostgreSQL with Flyway migrations enabled.

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `rewine` | Database name |
| `DB_USER` | `rewine` | Database user |
| `DB_PASSWORD` | `rewine_secret` | Database password |
| `JWT_SECRET` | (dev default) | JWT signing secret |
| `SERVER_PORT` | `8080` | Application port |

### Override Environment Variables

```bash
# Override specific variables
DB_HOST=192.168.1.100 DB_PASSWORD=mypassword mvn spring-boot:run

# Or export them
export DB_HOST=192.168.1.100
export DB_PASSWORD=mypassword
mvn spring-boot:run
```

### Available Profiles

| Profile | Database | Use Case |
|---------|----------|----------|
| `local` (default) | PostgreSQL | Local development |
| `test` | H2 in-memory | Unit tests |
| `integration` | Testcontainers PostgreSQL | Integration tests |
| `production` | PostgreSQL | Production deployment |

### Run with Different Profile

```bash
# Run with test profile (H2 in-memory)
mvn spring-boot:run -Dspring-boot.run.profiles=test

# Run with production profile
mvn spring-boot:run -Dspring-boot.run.profiles=production
```

---

## Standard Commands

### Build Commands

| Command | Description |
|---------|-------------|
| `mvn clean install` | Clean, compile, test, and package |
| `mvn clean install -DskipTests` | Build without running tests |
| `mvn clean package` | Package the application (JAR) |
| `mvn clean compile` | Compile only (no tests, no package) |

### Run Commands

| Command | Description |
|---------|-------------|
| `mvn spring-boot:run` | Run the application (local profile) |
| `mvn spring-boot:run -Dspring-boot.run.profiles=test` | Run with H2 (test profile) |
| `mvn spring-boot:run -Dspring-boot.run.profiles=production` | Run with production profile |
| `java -jar target/rewine-backend-0.0.1-SNAPSHOT.jar` | Run packaged JAR |

### Test Commands

| Command | Description |
|---------|-------------|
| `mvn test` | Run all tests |
| `mvn test -Dtest=ClassName` | Run specific test class |
| `mvn test -Dtest=ClassName#methodName` | Run specific test method |
| `mvn verify` | Run tests + integration tests |
| `mvn test -DfailIfNoTests=false` | Run tests, don't fail if none |

### Code Quality Commands

| Command | Description |
|---------|-------------|
| `mvn checkstyle:check` | Check code style |
| `mvn jacoco:report` | Generate code coverage report |
| `mvn spotbugs:check` | Static analysis (if configured) |
| `mvn dependency:tree` | Show dependency tree |
| `mvn versions:display-dependency-updates` | Check for dependency updates |

### Cleanup Commands

| Command | Description |
|---------|-------------|
| `mvn clean` | Remove target directory |
| `mvn dependency:purge-local-repository` | Clear cached dependencies |

---

## API Endpoints

Once running, these endpoints are available:

### Public Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/health` | GET | Health check |
| `/api/v1/version` | GET | Application version |
| `/api/v1/swagger-ui.html` | GET | Swagger UI (API docs) |
| `/api/v1/api-docs` | GET | OpenAPI JSON spec |
| `/api/v1/actuator/health` | GET | Actuator health endpoint |
| `/api/v1/actuator/info` | GET | Actuator info endpoint |

### Authentication Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/auth/register` | POST | Register a new user |
| `/api/v1/auth/login` | POST | Login and get tokens |
| `/api/v1/auth/refresh` | POST | Refresh access token |
| `/api/v1/auth/logout` | POST | Logout (revoke refresh token) |
| `/api/v1/auth/me` | GET | Get current user profile (requires auth) |

### Admin Endpoints (Requires ROLE_ADMIN)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/admin/test` | GET | Test admin access |
| `/api/v1/admin/moderator-test` | GET | Test moderator access (ADMIN or MODERATOR) |

---

## Authentication

The API uses **JWT (JSON Web Token)** authentication with refresh token rotation.

### How It Works

1. **Register/Login** → Returns `accessToken` (short-lived, 15 min) + `refreshToken` (long-lived, 7 days)
2. **Access Protected Resources** → Include `Authorization: Bearer <accessToken>` header
3. **Refresh Token** → When access token expires, use refresh token to get new tokens
4. **Logout** → Revokes the refresh token

### Authentication Flow Examples

#### Register a New User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securePassword123",
    "name": "John Doe"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "johndoe",
    "email": "john@example.com",
    "name": "John Doe",
    "roles": ["ROLE_USER"],
    "emailVerified": false
  }
}
```

#### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "johndoe",
    "password": "securePassword123"
  }'
```

#### Access Protected Endpoint

```bash
curl http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### Refresh Token

```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4..."
  }'
```

#### Logout

```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4..."
  }'
```

### User Roles

| Role | Description |
|------|-------------|
| `ROLE_USER` | Standard user (default for new registrations) |
| `ROLE_ADMIN` | Administrator with full access |
| `ROLE_MODERATOR` | Content moderator |
| `ROLE_PARTNER` | Business partner |

### Security Features

- **Password Hashing**: BCrypt with strength 10
- **Refresh Token Rotation**: Each refresh generates new tokens, old token is revoked
- **Refresh Token Storage**: Tokens stored hashed (SHA-256) in database
- **Request Correlation**: All requests have `X-Request-Id` header for tracing
- **Method-Level Security**: `@PreAuthorize` annotations for fine-grained access control

---

## Configuration

### Application Profiles

| Profile | Description | Database |
|---------|-------------|----------|
| `default` | Development with H2 | H2 In-Memory |
| `test` | Testing | H2 In-Memory |
| `postgres` | PostgreSQL | PostgreSQL |
| `prod` | Production | PostgreSQL |

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port | 8080 |
| `DB_HOST` | Database host | localhost |
| `DB_PORT` | Database port | 5432 |
| `DB_NAME` | Database name | rewinedb |
| `DB_USER` | Database user | - |
| `DB_PASSWORD` | Database password | - |
| `JWT_SECRET` | JWT signing secret (min 32 chars) | dev-secret |

### JWT Configuration (application.yml)

```yaml
jwt:
  issuer: rewine-backend
  secret: ${JWT_SECRET:your-256-bit-secret-key-here}
  access-token-expiration: 900000    # 15 minutes in milliseconds
  refresh-token-expiration: 604800000 # 7 days in milliseconds
```

---

## Data Initialization

On startup, the application automatically creates default roles if they don't exist:

- `ROLE_USER` - Standard user role
- `ROLE_ADMIN` - Administrator with full access
- `ROLE_MODERATOR` - Content moderator role
- `ROLE_PARTNER` - Business partner role

This is handled by `DataInitializationConfig.java`.

---

## Logging & Observability

### Request Logging

All HTTP requests are logged with correlation IDs:

```
2026-01-14 15:00:00.000 [http-nio-8080-exec-1] [abc123-request-id] [user-id] INFO - Request started: POST /api/v1/auth/login
2026-01-14 15:00:00.150 [http-nio-8080-exec-1] [abc123-request-id] [user-id] INFO - Request completed: 200 (150 ms)
```

### Log Files

| File | Description |
|------|-------------|
| `logs/application.json` | Application logs in JSON format |
| `logs/access.json` | Access logs (request/response) |

### Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/api/v1/actuator/health` | Application health status |
| `/api/v1/actuator/health/liveness` | Liveness probe (Kubernetes) |
| `/api/v1/actuator/health/readiness` | Readiness probe (Kubernetes) |
| `/api/v1/actuator/info` | Application information |
| `/api/v1/actuator/metrics` | Application metrics |

---

## Code Quality

### Checkstyle

The project enforces code style using Checkstyle:

```bash
# Run checkstyle check
mvn checkstyle:check

# Checkstyle runs automatically during build (validate phase)
mvn clean install
```

Key rules enforced:
- No star imports (`import com.example.*`)
- No magic numbers (use constants)
- Proper indentation (4 spaces)
- Line length max 250 characters
- Proper naming conventions

### JaCoCo Code Coverage

Code coverage reports are generated during the test phase:

```bash
# Run tests with coverage
mvn test

# View report at: target/site/jacoco/index.html
```

---

## Development Workflow

### Daily Development

```bash
# 1. Pull latest changes (if team)
# 2. Build and test
mvn clean install

# 3. Run the application
mvn spring-boot:run

# 4. Make changes (hot reload is enabled)

# 5. Run tests before committing
mvn test
```

### Before Committing

```bash
# Full build with tests and checkstyle
mvn clean install

# Verify no issues
mvn verify
```

---

## IDE Setup

### IntelliJ IDEA

1. Open the `backend` folder as a project
2. Import as Maven project
3. Enable annotation processing: `Settings > Build > Compiler > Annotation Processors`
4. Set Project SDK to Java 21

### VS Code

1. Install "Extension Pack for Java"
2. Install "Spring Boot Extension Pack"
3. Open the `backend` folder

---

## Troubleshooting

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or run on different port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Clear Maven Cache

```bash
rm -rf ~/.m2/repository/com/rewine
mvn clean install -U
```

### Java Version Issues

```bash
# Check Java version
java -version

# If wrong version, use SDKMAN
sdk use java 21.0.5-tem
```

### JWT Secret Issues

If you see "JWT secret is not configured" error:
```bash
# Set the JWT secret environment variable
export JWT_SECRET="your-very-long-secret-key-at-least-32-characters"

# Or pass it directly
mvn spring-boot:run -Djwt.secret="your-very-long-secret-key-at-least-32-characters"
```

### Database Connection Issues

For H2 (default):
- Access H2 Console at: `http://localhost:8080/api/v1/h2-console`
- JDBC URL: `jdbc:h2:mem:rewinedb`
- Username: `sa`
- Password: (empty)

For PostgreSQL:
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check connection
psql -h localhost -U rewine -d rewinedb
```

---

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/rewine/backend/
│   │   │   ├── RewineBackendApp.java       # Entry point
│   │   │   ├── configuration/              # Spring configs
│   │   │   │   ├── security/               # Security config + JWT filter
│   │   │   │   ├── properties/             # Configuration properties
│   │   │   │   ├── openapi/                # Swagger/OpenAPI config
│   │   │   │   ├── web/                    # Web/CORS config
│   │   │   │   └── DataInitializationConfig.java
│   │   │   ├── controller/                 # REST controllers
│   │   │   │   ├── impl/                   # Controller implementations
│   │   │   │   └── advice/                 # Exception handlers
│   │   │   ├── dto/                        # Data Transfer Objects
│   │   │   │   ├── request/                # Request DTOs
│   │   │   │   ├── response/               # Response DTOs
│   │   │   │   └── common/                 # Shared DTOs
│   │   │   ├── handler/                    # Use case handlers
│   │   │   ├── service/                    # Business logic
│   │   │   │   └── impl/                   # Service implementations
│   │   │   ├── repository/                 # Data access (JPA)
│   │   │   ├── model/entity/               # JPA entities
│   │   │   ├── exception/                  # Custom exceptions
│   │   │   └── utils/                      # Utilities
│   │   │       ├── logging/                # Request logging filter
│   │   │       ├── converter/              # Type converters
│   │   │       └── validation/             # Validation helpers
│   │   └── resources/
│   │       ├── application.yml             # Main config
│   │       ├── application-postgres.yml    # PostgreSQL config
│   │       ├── logback-spring.xml          # Logging config
│   │       ├── checkstyle.xml              # Checkstyle rules
│   │       └── db/migration/               # Flyway migrations
│   └── test/
│       ├── java/                           # Test sources
│       └── resources/
│           └── application-test.yml        # Test config
├── logs/                                   # Log files (gitignored)
├── pom.xml                                 # Maven config
├── ARCHITECTURE.md                         # Architecture docs
└── README.md                               # This file
```

---

## Related Documentation

- [Architecture Guide](ARCHITECTURE.md) - Detailed architecture documentation
- [API Documentation](http://localhost:8080/api/v1/swagger-ui.html) - Interactive API docs (when running)


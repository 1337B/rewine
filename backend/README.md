# Rewine Backend

Spring Boot 3.x REST API for the Rewine Wine Discovery Platform.

---

## Prerequisites

- **Java 21** (LTS) - Required
- **Maven 3.9+** - Build tool
- **Docker** (optional) - For containerized database

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

### 1. Run with In-Memory Database (H2)

```bash
# From /backend directory
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080/api/v1`

### 2. Run with PostgreSQL (Docker)

```bash
# Start PostgreSQL container
docker run -d \
  --name rewine-postgres \
  -e POSTGRES_DB=rewinedb \
  -e POSTGRES_USER=rewine \
  -e POSTGRES_PASSWORD=rewine123 \
  -p 5432:5432 \
  postgres:15

# Run with PostgreSQL profile
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
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
| `mvn spring-boot:run` | Run the application (dev mode) |
| `mvn spring-boot:run -Dspring-boot.run.profiles=dev` | Run with dev profile |
| `mvn spring-boot:run -Dspring-boot.run.profiles=postgres` | Run with PostgreSQL |
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
| `mvn checkstyle:check` | Check code style (if configured) |
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

| Endpoint | Description |
|----------|-------------|
| `GET /api/v1/health` | Health check |
| `GET /api/v1/version` | Application version |
| `GET /api/v1/swagger-ui.html` | Swagger UI (API docs) |
| `GET /api/v1/api-docs` | OpenAPI JSON spec |
| `/api/v1/h2-console` | H2 Database console (dev only) |

### Test the API

```bash
# Health check
curl http://localhost:8080/api/v1/health

# Version info
curl http://localhost:8080/api/v1/version
```

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
| `JWT_SECRET` | JWT signing secret | dev-secret |

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
# Full build with tests
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

---

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/rewine/backend/
│   │   │   ├── RewineBackendApp.java    # Entry point
│   │   │   ├── configuration/           # Spring configs
│   │   │   ├── controller/              # REST controllers
│   │   │   ├── handler/                 # Use case handlers
│   │   │   ├── service/                 # Business logic
│   │   │   ├── repository/              # Data access
│   │   │   ├── model/entity/            # JPA entities
│   │   │   ├── exception/               # Exception handling
│   │   │   └── utils/                   # Utilities
│   │   └── resources/
│   │       ├── application.yml          # Main config
│   │       └── db/migration/            # Flyway migrations
│   └── test/
│       ├── java/                        # Test sources
│       └── resources/                   # Test configs
├── pom.xml                              # Maven config
├── ARCHITECTURE.md                      # Architecture docs
└── README.md                            # This file
```

---

## Related Documentation

- [Architecture Guide](ARCHITECTURE.md) - Detailed architecture documentation
- [API Documentation](http://localhost:8080/api/v1/swagger-ui.html) - Interactive API docs (when running)


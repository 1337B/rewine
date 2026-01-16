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

## Seed Data (Local Development Only)

When running with the `local` profile, the database is automatically seeded with test data via Flyway repeatable migrations. This makes the API immediately usable for development and testing.

### Seeded Users

| Email | Username | Password | Roles |
|-------|----------|----------|-------|
| `admin@rewine.local` | admin | `Rewine123!` | ROLE_ADMIN, ROLE_USER |
| `partner@rewine.local` | partner | `Rewine123!` | ROLE_PARTNER, ROLE_USER |
| `moderator@rewine.local` | moderator | `Rewine123!` | ROLE_MODERATOR, ROLE_USER |
| `user@rewine.local` | user | `Rewine123!` | ROLE_USER |

> ⚠️ **Security Note**: These credentials are for LOCAL DEVELOPMENT ONLY. They are NOT present in production deployments.

### Seeded Content

| Entity | Count | Description |
|--------|-------|-------------|
| Wineries | 5 | Argentine wineries (Catena Zapata, Norton, Trapiche, Zuccardi, Luigi Bosca) |
| Wines | 20 | Various wines with grapes, styles, ratings |
| Wine Routes | 3 | Routes around Mendoza (Luján de Cuyo, Valle de Uco, Gran Tour) |
| Events | 10 | Wine events around Mendoza (lat: -32.89, lng: -68.83) |

### Quick Login Test

```bash
# Login as admin
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "admin@rewine.local", "password": "Rewine123!"}'

# Login as partner
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "partner@rewine.local", "password": "Rewine123!"}'
```

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

### Wine AI Profile Endpoints (Requires Authentication)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/wines/{id}/ai-profile` | GET | Get AI profile for a wine (generates if not cached) |
| `/api/v1/wines/{id}/ai-profile` | POST | Generate AI profile with options |
| `/api/v1/wines/{id}/ai-profile/status` | GET | Check AI profile cache status |

### Wine Comparison Endpoint (Requires Authentication)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/wines/compare` | POST | Compare two wines using AI (generates if not cached) |

---

## Wine AI Profiles

The platform includes AI-generated wine profiles that provide rich descriptions, tasting notes, food pairings, and serving recommendations.

### How It Works

1. **First Request** → AI generates a profile and caches it in the database
2. **Subsequent Requests** → Returns cached profile instantly (no AI call)
3. **Force Regenerate** → Can optionally regenerate profile via POST request

### AI Profile Content

Each AI profile includes:
- **Summary**: AI-generated description of the wine
- **Tasting Notes**: Appearance, aroma, palate, and finish
- **Food Pairings**: Recommended food combinations
- **Occasions**: Suggested occasions for enjoying the wine
- **Fun Facts**: Interesting facts about the wine or region
- **Serving Recommendations**: Temperature, decanting, glass type, storage tips

### Example: Get AI Profile

```bash
curl -X GET "http://localhost:8080/api/v1/wines/{wineId}/ai-profile?language=es-AR" \
  -H "Authorization: Bearer <accessToken>"
```

**Response:**
```json
{
  "wineId": "550e8400-e29b-41d4-a716-446655440000",
  "wineName": "Malbec Reserve 2020",
  "language": "es-AR",
  "generatedAt": "2026-01-15T12:00:00Z",
  "summary": "El Malbec Reserve 2020 es un vino tinto excepcional...",
  "tastingNotes": {
    "appearance": "Color brillante con reflejos característicos...",
    "aroma": "Aromas complejos con notas frutales...",
    "palate": "En boca presenta un equilibrio excelente...",
    "finish": "Final largo y persistente..."
  },
  "foodPairings": [
    "Carnes rojas a la parrilla",
    "Pastas con salsas robustas",
    "Quesos maduros"
  ],
  "occasions": [
    "Cenas especiales con amigos",
    "Celebraciones familiares"
  ],
  "funFacts": [
    "La región tiene más de 150 años de tradición vitivinícola."
  ],
  "servingRecommendations": {
    "temperature": "Servir entre 16-18°C",
    "decanting": "Se recomienda decantar 30 minutos antes de servir",
    "glassType": "Copa de vino tinto amplia tipo Bordeaux",
    "storageTips": "Conservar en lugar fresco y oscuro"
  }
}
```

### Example: Generate AI Profile (with options)

```bash
curl -X POST "http://localhost:8080/api/v1/wines/{wineId}/ai-profile" \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{
    "language": "en-US",
    "forceRegenerate": false
  }'
```

### Example: Check Profile Status

```bash
curl -X GET "http://localhost:8080/api/v1/wines/{wineId}/ai-profile/status?language=es-AR" \
  -H "Authorization: Bearer <accessToken>"
```

**Response:**
```json
{
  "wineId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "GENERATED",
  "generatedAt": "2026-01-15T12:00:00Z",
  "availableLanguages": ["es-AR"],
  "hasRequestedLanguage": true,
  "requestedLanguage": "es-AR"
}
```

### Supported Languages

| Language Code | Description |
|---------------|-------------|
| `es-AR` | Spanish (Argentina) - Default |
| `en-US` | English (United States) |

### AI Provider Configuration

The AI adapter layer supports multiple providers with clean interfaces, configurable timeouts, and automatic retry logic.

#### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `AI_PROVIDER` | `mock` | AI provider: `mock` (development) or `openai` |
| `AI_ENABLED` | `true` | Enable/disable AI features |
| `OPENAI_API_KEY` | `not-configured` | OpenAI API key (required for `openai` provider) |
| `OPENAI_BASE_URL` | `https://api.openai.com/v1` | OpenAI API base URL |
| `OPENAI_MODEL` | `gpt-4o-mini` | OpenAI model to use |
| `OPENAI_MAX_TOKENS` | `2000` | Maximum tokens for completion |
| `OPENAI_TEMPERATURE` | `0.7` | Temperature for generation (0.0-2.0) |
| `AI_CONNECT_TIMEOUT` | `10` | Connection timeout in seconds |
| `AI_READ_TIMEOUT` | `60` | Read timeout in seconds |
| `AI_MAX_RETRIES` | `2` | Maximum retries for transient errors |
| `AI_HTTP_LOGGING` | `true` | Enable HTTP request/response logging |

#### Configuration Example

```yaml
# application.yml
rewine:
  ai:
    provider: ${AI_PROVIDER:mock}
    enabled: ${AI_ENABLED:true}
    openai:
      base-url: ${OPENAI_BASE_URL:https://api.openai.com/v1}
      api-key: ${OPENAI_API_KEY:not-configured}
      model: ${OPENAI_MODEL:gpt-4o-mini}
      max-tokens: ${OPENAI_MAX_TOKENS:2000}
      temperature: ${OPENAI_TEMPERATURE:0.7}
    http:
      connect-timeout-seconds: ${AI_CONNECT_TIMEOUT:10}
      read-timeout-seconds: ${AI_READ_TIMEOUT:60}
      max-retries: ${AI_MAX_RETRIES:2}
      logging-enabled: ${AI_HTTP_LOGGING:true}
```

#### Provider Behavior

| Provider | API Key Required | Behavior |
|----------|-----------------|----------|
| `mock` | No | Returns deterministic mock responses (development) |
| `openai` | Yes | Calls OpenAI API; falls back to mock if API key missing |

#### Running with OpenAI

```bash
# Export the API key
export OPENAI_API_KEY="sk-your-api-key-here"
export AI_PROVIDER="openai"

# Run the application
mvn spring-boot:run
```

#### Architecture

The AI adapter layer follows clean architecture principles:

- **IAiClient**: Client layer interface for AI operations (pure HTTP adapter)
- **IWinePromptService**: Service layer interface for building AI prompts (business logic)
- **IHttpClientFactory**: Factory for configured HTTP clients with timeouts/retry
- **OpenAiClientImpl**: OpenAI implementation with fallback to mock
- **MockAiClient**: Standalone mock implementation for development

**Design Decision**: Prompt building is placed in the service layer (`IWinePromptService`) rather than the client layer because:
- It encodes domain knowledge about wines (what attributes matter, how to describe them)
- It makes the client layer a pure HTTP adapter
- It's easier to test business logic independently
- It provides flexibility if switching AI providers

All orchestrators use `IAiClient` interface only, never direct HTTP calls.

**Note**: The `mock` provider generates realistic sample profiles for development without requiring external API keys.

---

## Wine Comparisons

The platform supports AI-generated comparisons between two wines, helping users understand the differences and similarities.

### How It Works

1. **First Request** → AI generates a comparison and caches it in the database
2. **Subsequent Requests** → Returns cached comparison instantly (no AI call)
3. **Normalized Pairs** → Wine pairs are normalized (A < B) to prevent duplicate comparisons for (A,B) vs (B,A)
4. **Force Regenerate** → Can optionally regenerate comparison via request parameter

### Comparison Content

Each AI comparison includes:
- **Summary**: Overall comparison summary
- **Attribute Comparison**: Side-by-side comparison of appearance, aroma, palate, finish
- **Similarities**: Key similarities between the wines
- **Differences**: Key differences between the wines
- **Food Pairings**: Food pairing recommendations for each wine and shared pairings
- **Occasions**: Best occasions for each wine
- **Value Assessment**: Price/quality assessment
- **Recommendations**: When to choose wine A vs wine B

### Example: Compare Two Wines

```bash
curl -X POST "http://localhost:8080/api/v1/wines/compare" \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{
    "wineAId": "550e8400-e29b-41d4-a716-446655440000",
    "wineBId": "550e8400-e29b-41d4-a716-446655440001",
    "language": "es-AR",
    "forceRegenerate": false
  }'
```

**Response:**
```json
{
  "wineAId": "550e8400-e29b-41d4-a716-446655440000",
  "wineAName": "Malbec Reserve 2020",
  "wineBId": "550e8400-e29b-41d4-a716-446655440001",
  "wineBName": "Cabernet Sauvignon 2019",
  "language": "es-AR",
  "generatedAt": "2026-01-15T12:00:00Z",
  "cached": false,
  "summary": "Comparando Malbec Reserve 2020 con Cabernet Sauvignon 2019...",
  "attributeComparison": {
    "appearance": {
      "wineA": "Color profundo con reflejos brillantes",
      "wineB": "Color intenso con tonos característicos",
      "comparison": "Ambos presentan excelente claridad"
    }
  },
  "similarities": [
    "Ambos vinos provienen de viñedos de alta calidad",
    "Comparten un perfil de envejecimiento similar"
  ],
  "differences": [
    "Malbec Reserve 2020 tiene mayor intensidad tánica",
    "Cabernet Sauvignon 2019 presenta más notas frutales"
  ],
  "foodPairings": {
    "wineA": ["Asado argentino", "Cordero al horno"],
    "wineB": ["Pasta con ragú", "Ternera a la parrilla"],
    "shared": ["Carnes rojas", "Empanadas"]
  },
  "recommendation": {
    "chooseWineAIf": "Elige Malbec Reserve 2020 si prefieres vinos con mayor estructura",
    "chooseWineBIf": "Elige Cabernet Sauvignon 2019 si buscas un vino más accesible",
    "overallNote": "Ambos son excelentes opciones que satisfarán a cualquier amante del vino"
  }
}
```

---

## Google Maps Integration

The platform integrates with Google Maps APIs for wine route features including static map previews, route calculations, and geocoding.

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `GOOGLE_MAPS_ENABLED` | `true` | Enable/disable Google Maps features |
| `GOOGLE_MAPS_API_KEY` | (empty) | Google Maps API key |

### How It Works

- **With API Key**: Uses Google Maps Static API for route previews, Directions API for route calculations
- **Without API Key**: Falls back to OpenStreetMap for static maps and Haversine formula for distance calculations

### Setting Up Google Maps

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a project or select an existing one
3. Enable the following APIs:
   - **Maps Static API** - For route preview images
   - **Directions API** - For route calculations
   - **Geocoding API** - For address lookup
4. Go to "APIs & Services" → "Credentials" → "Create Credentials" → "API Key"
5. (Recommended) Restrict the API key:
   - Application restrictions: IP addresses or HTTP referrers
   - API restrictions: Only the APIs listed above

### Running with Google Maps

```bash
# Export the API key
export GOOGLE_MAPS_API_KEY="AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
export GOOGLE_MAPS_ENABLED="true"

# Run the application
mvn spring-boot:run
```

Or add to `.env` file:
```dotenv
GOOGLE_MAPS_ENABLED=true
GOOGLE_MAPS_API_KEY=your-google-maps-api-key-here
```

### Fallback Behavior

When Google Maps API is not configured:
- **Static Maps**: Uses OpenStreetMap static map service
- **Distance/Duration**: Uses Haversine formula with average speed estimates
- **Geocoding**: Returns placeholder Mendoza, Argentina coordinates

This ensures the application runs without Google Maps credentials for development.

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
- **Rate Limiting**: Bucket4j-based rate limiting per IP (see below)
- **Security Headers**: Comprehensive HTTP security headers
- **CORS**: Environment-specific CORS configuration

---

## Rate Limiting

The API implements rate limiting to protect against abuse and ensure fair usage.

### Rate Limits by Endpoint

| Endpoint Category | Local Limit | Production Limit | Window |
|------------------|-------------|------------------|--------|
| Login (`/auth/login`) | 50 req | 10 req | per minute |
| Register (`/auth/register`) | 20 req | 5 req | per minute |
| Public GET (`/wines`, `/events`, etc.) | 500 req | 120 req | per minute |
| Authenticated endpoints | 500 req | 200 req | per minute |

### Rate Limit Response Headers

Every response includes rate limit information:

| Header | Description |
|--------|-------------|
| `X-RateLimit-Limit` | Maximum requests allowed in window |
| `X-RateLimit-Remaining` | Requests remaining in current window |
| `X-RateLimit-Reset` | Unix timestamp when the window resets |

### Rate Limit Exceeded Response (HTTP 429)

```json
{
  "timestamp": "2026-01-15T12:00:00Z",
  "path": "/api/v1/auth/login",
  "requestId": "abc123-xyz789",
  "status": 429,
  "code": "RATE_LIMIT_EXCEEDED",
  "message": "Too many requests. Please retry after 45 seconds."
}
```

### Configuration

```yaml
rewine:
  rate-limit:
    enabled: true
    login:
      requests: 10
      window-seconds: 60
    register:
      requests: 5
      window-seconds: 60
    public-get:
      requests: 120
      window-seconds: 60
```

---

## Security Headers

The API includes comprehensive security headers to protect against common web vulnerabilities.

### Headers Applied

| Header | Value (Production) | Description |
|--------|-------------------|-------------|
| `X-Content-Type-Options` | `nosniff` | Prevents MIME type sniffing |
| `X-Frame-Options` | `DENY` | Prevents clickjacking |
| `Referrer-Policy` | `strict-origin-when-cross-origin` | Controls referrer information |
| `Content-Security-Policy` | `default-src 'none'; frame-ancestors 'none'` | Restricts resource loading |
| `Permissions-Policy` | `geolocation=(), camera=(), microphone=()` | Disables sensitive features |
| `X-XSS-Protection` | `1; mode=block` | XSS protection for older browsers |
| `Strict-Transport-Security` | `max-age=31536000; includeSubDomains; preload` | HSTS (production only) |

### Configuration by Profile

| Setting | Local | Production |
|---------|-------|------------|
| HSTS Enabled | No | Yes |
| Frame Options | SAMEORIGIN | DENY |
| CSP | Strict | Strict |

---

## CORS Configuration

Cross-Origin Resource Sharing is configured per environment.

### Local Development

```yaml
rewine:
  cors:
    enabled: true
    allowed-origins:
      - http://localhost:3000
      - http://localhost:4173
      - http://localhost:5173
    allow-credentials: true
```

### Production

```yaml
rewine:
  cors:
    enabled: true
    allowed-origins: ${CORS_ALLOWED_ORIGINS:https://rewine.app,https://www.rewine.app}
    allow-credentials: true
```

**Important**: Production CORS configuration does NOT allow wildcard origins (`*`).

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed origins | `https://rewine.app,https://admin.rewine.app` |

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

## External Provider Configuration

The application uses external services for AI-powered features and maps. These services are designed to gracefully degrade when not configured.

### Provider Availability Handling

| Provider | Required | Behavior When Disabled |
|----------|----------|------------------------|
| **OpenAI** | No | Returns deterministic mock responses for wine profiles and comparisons |
| **Google Maps** | No | Uses OpenStreetMap static maps and Haversine distance calculations |

### Configuration Priority

1. **Environment variables** (highest priority)
2. **application-{profile}.yml** values
3. **Default values** (fallback/mock mode)

### Enabling External Providers

**For OpenAI AI features:**
```bash
export AI_PROVIDER=openai
export OPENAI_API_KEY=sk-your-api-key-here
```

**For Google Maps features:**
```bash
export GOOGLE_MAPS_ENABLED=true
export GOOGLE_MAPS_API_KEY=AIza-your-api-key-here
```

### Provider Behavior Details

#### AI Provider States

| Configuration | Behavior |
|---------------|----------|
| `AI_ENABLED=false` | AI endpoints return 503 Service Unavailable |
| `AI_PROVIDER=mock` | Returns deterministic mock wine profiles |
| `AI_PROVIDER=openai` + no key | Falls back to mock with warning log |
| `AI_PROVIDER=openai` + valid key | Makes real OpenAI API calls |

#### Maps Provider States

| Configuration | Behavior |
|---------------|----------|
| `GOOGLE_MAPS_ENABLED=false` | Uses OpenStreetMap and Haversine calculations |
| No API key configured | Uses placeholder images, Haversine distances |
| Valid API key | Uses Google Maps Static API, Directions API |

### Security Notes

- **API keys are never logged**, even with DEBUG logging enabled
- Properties classes override `toString()` to mask sensitive values
- Use environment variables or secrets managers for production

See [CREDENTIALS_AND_ACCOUNTS.md](../docs/CREDENTIALS_AND_ACCOUNTS.md) for complete credential setup guide.

---

## Related Documentation

- [Architecture Guide](ARCHITECTURE.md) - Detailed architecture documentation
- [API Documentation](http://localhost:8080/api/v1/swagger-ui.html) - Interactive API docs (when running)
- [Credentials & Accounts](../docs/CREDENTIALS_AND_ACCOUNTS.md) - Environment variables and API keys setup
- [Environments](../docs/ENVIRONMENTS.md) - Environment configuration details


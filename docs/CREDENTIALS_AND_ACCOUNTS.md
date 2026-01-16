# Credentials and External Accounts

This document lists all external services used by Rewine and the required credentials/environment variables.

---

## Table of Contents

1. [Environment Variables Overview](#environment-variables-overview)
2. [Database](#database)
3. [JWT Authentication](#jwt-authentication)
4. [AI Services (OpenAI)](#ai-services-openai)
5. [Maps Services (Google Maps)](#maps-services-google-maps)
6. [Test Users (Local Only)](#test-users-local-only)
7. [Security Guidelines](#security-guidelines)

---

## Environment Variables Overview

| Variable | Required | Default | Profile | Description |
|----------|----------|---------|---------|-------------|
| `DB_HOST` | No | `localhost` | All | PostgreSQL host |
| `DB_PORT` | No | `5432` | All | PostgreSQL port |
| `DB_NAME` | No | `rewine` | All | Database name |
| `DB_USER` | No | `rewine` | All | Database user |
| `DB_PASSWORD` | Yes (prod) | `rewine_secret` | All | Database password |
| `JWT_SECRET` | Yes (prod) | Dev default | All | JWT signing secret (min 32 chars) |
| `JWT_ISSUER` | No | `rewine-backend` | All | JWT issuer claim |
| `AI_PROVIDER` | No | `mock` | All | AI provider: `mock` or `openai` |
| `AI_ENABLED` | No | `true` | All | Enable/disable AI features |
| `OPENAI_API_KEY` | For AI | None | All | OpenAI API key |
| `GOOGLE_MAPS_ENABLED` | No | `true` | All | Enable/disable maps features |
| `GOOGLE_MAPS_API_KEY` | For maps | None | All | Google Maps API key |

---

## Database

### PostgreSQL Connection

The application requires PostgreSQL 15+ for all profiles except `test` (which uses H2).

#### Environment Variables

```bash
DB_HOST=localhost        # PostgreSQL hostname
DB_PORT=5432            # PostgreSQL port
DB_NAME=rewine          # Database name
DB_USER=rewine          # Database username
DB_PASSWORD=rewine_secret # Database password (CHANGE IN PRODUCTION)
```

#### Local Development Setup

```bash
# Option 1: Docker
docker run -d \
  --name rewine-postgres \
  -e POSTGRES_DB=rewine \
  -e POSTGRES_USER=rewine \
  -e POSTGRES_PASSWORD=rewine_secret \
  -p 5432:5432 \
  postgres:15

# Option 2: Docker Compose (from /rewine/infra)
cd /rewine/infra
docker-compose up -d postgres
```

---

## JWT Authentication

### Configuration

The JWT secret must be at least 32 characters long for HS512 algorithm.

#### Environment Variables

```bash
JWT_SECRET=your-super-secret-jwt-key-minimum-32-characters-long
JWT_ISSUER=rewine-backend
JWT_ACCESS_TOKEN_EXPIRATION=900000      # 15 minutes in milliseconds
JWT_REFRESH_TOKEN_EXPIRATION=604800000  # 7 days in milliseconds
```

#### Security Notes

- **NEVER** commit real JWT secrets to version control
- Use a cryptographically secure random value in production
- Rotate secrets periodically
- Different secrets per environment (dev/uat/prod)

---

## AI Services (OpenAI)

### Overview

The AI service generates wine profiles and comparisons using OpenAI's GPT models. When the API key is not configured, the system falls back to deterministic mock responses.

### Configuration

#### Environment Variables

```bash
AI_PROVIDER=openai                           # 'mock' or 'openai'
AI_ENABLED=true                              # Enable/disable AI features
OPENAI_API_KEY=sk-your-api-key-here         # OpenAI API key
OPENAI_BASE_URL=https://api.openai.com/v1   # Optional: Override base URL
OPENAI_MODEL=gpt-4o-mini                    # Model to use
OPENAI_MAX_TOKENS=2000                      # Max tokens per request
OPENAI_TEMPERATURE=0.7                      # Generation temperature (0.0-2.0)
AI_CONNECT_TIMEOUT=10                       # Connection timeout (seconds)
AI_READ_TIMEOUT=60                          # Read timeout (seconds)
AI_MAX_RETRIES=2                            # Retries for transient errors
```

### Getting an API Key

1. Go to [OpenAI Platform](https://platform.openai.com/api-keys)
2. Create an account or sign in
3. Navigate to API Keys section
4. Create a new secret key
5. Copy and store securely

### Behavior by Configuration

| `AI_PROVIDER` | `OPENAI_API_KEY` | Behavior |
|---------------|------------------|----------|
| `mock` | Any | Returns deterministic mock responses |
| `openai` | Not set / blank | Returns mock responses (with log warning) |
| `openai` | Valid key | Makes real OpenAI API calls |

### Cost Considerations

- **gpt-4o-mini**: ~$0.15 per 1M input tokens, ~$0.60 per 1M output tokens
- Wine profiles are cached per wine+language, so subsequent requests are free
- Comparisons are cached per wine pair+language

---

## Maps Services (Google Maps)

### Overview

Google Maps is used for route previews, distance calculations, and geocoding. When not configured, the system falls back to placeholder images and Haversine distance calculations.

### Configuration

#### Environment Variables

```bash
GOOGLE_MAPS_ENABLED=true    # Enable/disable maps features
GOOGLE_MAPS_API_KEY=AIza... # Google Maps API key
```

### Getting an API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/apis/credentials)
2. Create a project or select existing
3. Enable required APIs:
   - Maps Static API
   - Directions API
   - Geocoding API
4. Create credentials → API key
5. Restrict the key to your domain/IP for security

### Behavior by Configuration

| `GOOGLE_MAPS_ENABLED` | `GOOGLE_MAPS_API_KEY` | Behavior |
|-----------------------|----------------------|----------|
| `false` | Any | Uses placeholder images, Haversine calculations |
| `true` | Not set / blank | Uses placeholder images, Haversine calculations |
| `true` | Valid key | Makes real Google Maps API calls |

### Cost Considerations

- **Static Maps**: $2 per 1,000 requests
- **Directions**: $5 per 1,000 requests
- **Geocoding**: $5 per 1,000 requests
- Consider caching frequently accessed routes

---

## Test Users (Local Only)

> ⚠️ **WARNING**: These credentials are for LOCAL DEVELOPMENT ONLY and are NOT present in production deployments.

The following test users are seeded automatically when running with the `local` profile:

| Email | Username | Password | Roles |
|-------|----------|----------|-------|
| `admin@rewine.local` | `admin` | `Rewine123!` | ROLE_ADMIN, ROLE_USER |
| `partner@rewine.local` | `partner` | `Rewine123!` | ROLE_PARTNER, ROLE_USER |
| `moderator@rewine.local` | `moderator` | `Rewine123!` | ROLE_MODERATOR, ROLE_USER |
| `user@rewine.local` | `user` | `Rewine123!` | ROLE_USER |

### Quick Login Test

```bash
# Login as admin
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "admin@rewine.local", "password": "Rewine123!"}'
```

---

## Security Guidelines

### DO ✅

- Store secrets in environment variables
- Use different secrets per environment
- Rotate API keys periodically
- Restrict API keys to specific domains/IPs
- Use secrets management tools (AWS Secrets Manager, HashiCorp Vault, etc.) in production
- Audit access to credentials regularly

### DON'T ❌

- Commit secrets to version control
- Log API keys or passwords (even partially masked)
- Share production credentials in plain text
- Use the same credentials across environments
- Store credentials in code or configuration files

### Production Checklist

- [ ] All environment variables are set via secrets manager
- [ ] JWT_SECRET is unique and cryptographically secure
- [ ] Database password is unique and strong
- [ ] API keys are restricted to production domains
- [ ] Credentials are rotated on a schedule
- [ ] Access to production credentials is audited

---

## Related Documentation

- [ENVIRONMENTS.md](./ENVIRONMENTS.md) - Environment configuration details
- [SECURITY.md](./SECURITY.md) - Security architecture and practices
- [Backend README](../backend/README.md) - Backend configuration and setup


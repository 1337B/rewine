# Environments

This document describes the different environments used in the Rewine platform and how configuration is managed across them.

---

## Table of Contents

1. [Environment Overview](#environment-overview)
2. [Environment Differences](#environment-differences)
3. [Configuration Management](#configuration-management)
4. [Environment Variables](#environment-variables)
5. [Local Development Setup](#local-development-setup)
6. [Container Configuration](#container-configuration)
7. [Kubernetes Configuration](#kubernetes-configuration)

---

## Environment Overview

| Environment | Purpose | URL (Planned) | Deployment |
|-------------|---------|---------------|------------|
| **Development** | Local development | `localhost:3000` | Manual |
| **UAT** | Testing & QA | `uat.rewine.com` | Automatic on PR merge |
| **Production** | Live users | `rewine.com` | Manual approval |

### Environment Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│ Development │────▶│     UAT     │────▶│ Production  │
│   (local)   │     │  (staging)  │     │   (live)    │
└─────────────┘     └─────────────┘     └─────────────┘
      │                    │                   │
  Mock API or         Real API            Real API
  Local Backend       Test DB             Prod DB
```

---

## Environment Differences

### Development (Local)

| Aspect | Configuration |
|--------|---------------|
| API | Mock (MSW) or local backend |
| Database | Local PostgreSQL or mocked |
| Auth | Mocked or local |
| Logging | Verbose, console output |
| Features | All features enabled |
| SSL | Not required |

### UAT (Staging)

| Aspect | Configuration |
|--------|---------------|
| API | UAT backend server |
| Database | Separate UAT database |
| Auth | Real auth, test users |
| Logging | Standard, aggregated |
| Features | Feature flags respected |
| SSL | Required (test cert) |

### Production

| Aspect | Configuration |
|--------|---------------|
| API | Production backend cluster |
| Database | Production database (replicated) |
| Auth | Full authentication |
| Logging | Structured, monitored |
| Features | Feature flags respected |
| SSL | Required (valid cert) |

---

## Configuration Management

### Configuration Sources

| Source | Priority | Use Case |
|--------|----------|----------|
| Environment variables | 1 (highest) | Runtime config |
| `.env.local` | 2 | Local overrides |
| `.env.[mode]` | 3 | Mode-specific defaults |
| `.env` | 4 | Shared defaults |
| Code defaults | 5 (lowest) | Fallbacks |

### Vite Environment Modes

```bash
# Development mode (default for `npm run dev`)
npm run dev          # Uses .env.development

# Production mode (default for `npm run build`)
npm run build        # Uses .env.production

# Custom mode
npm run dev -- --mode staging  # Uses .env.staging
```

---

## Environment Variables

### Frontend Variables

All frontend variables must be prefixed with `VITE_` to be exposed to the client.

```bash
# .env.example - Copy to .env.local and customize

# =============================================================================
# API Configuration
# =============================================================================
# Base URL for API requests
VITE_API_BASE_URL=/api/v1

# API request timeout (milliseconds)
VITE_API_TIMEOUT=30000

# =============================================================================
# Mock API (Development Only)
# =============================================================================
# Enable MSW mock service worker
VITE_MOCK_API=false

# =============================================================================
# App Configuration
# =============================================================================
# Application name
VITE_APP_NAME=Rewine

# Environment identifier
VITE_APP_ENV=development

# Base path for the app
VITE_BASE_PATH=/

# =============================================================================
# Feature Flags
# =============================================================================
# Enable wine scanning
VITE_FEATURE_WINE_SCAN=true

# Enable social login
VITE_FEATURE_SOCIAL_LOGIN=false

# =============================================================================
# External Services (Optional)
# =============================================================================
# Google Maps API key
VITE_GOOGLE_MAPS_API_KEY=

# Sentry DSN for error tracking
VITE_SENTRY_DSN=
```

### Variable Reference

| Variable | Type | Default | Description |
|----------|------|---------|-------------|
| `VITE_API_BASE_URL` | string | `/api/v1` | Backend API base URL |
| `VITE_API_TIMEOUT` | number | `30000` | Request timeout in ms |
| `VITE_MOCK_API` | boolean | `false` | Enable MSW mocking |
| `VITE_APP_NAME` | string | `Rewine` | Application name |
| `VITE_APP_ENV` | string | `development` | Environment identifier |
| `VITE_BASE_PATH` | string | `/` | Base path for routing |
| `VITE_FEATURE_WINE_SCAN` | boolean | `false` | Wine scan feature flag |
| `VITE_FEATURE_SOCIAL_LOGIN` | boolean | `false` | Social login feature flag |
| `VITE_GOOGLE_MAPS_API_KEY` | string | - | Maps API key |
| `VITE_SENTRY_DSN` | string | - | Error tracking DSN |

### Environment-Specific Files

```
frontend/
├── .env                    # Shared defaults (committed)
├── .env.example            # Example/template (committed)
├── .env.local              # Local overrides (gitignored)
├── .env.development        # Dev defaults (committed)
├── .env.production         # Prod defaults (committed)
└── .env.production.local   # Prod local overrides (gitignored)
```

### Accessing Variables in Code

```typescript
// src/app/env.ts - Typed access
export const env = {
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  mockApi: import.meta.env.VITE_MOCK_API === 'true',
  appName: import.meta.env.VITE_APP_NAME || 'Rewine',
  isProd: import.meta.env.PROD,
  isDev: import.meta.env.DEV,
}

// Usage in components
import { env } from '@app/env'
console.log(env.apiBaseUrl)
```

---

## Local Development Setup

### Step 1: Create Local Environment File

```bash
cd frontend
cp .env.example .env.local
```

### Step 2: Configure for Your Setup

#### Option A: With Mock API (No Backend Required)

```bash
# .env.local
VITE_API_BASE_URL=/api/v1
VITE_MOCK_API=true
VITE_APP_ENV=development
```

```bash
npm run dev:mock
```

#### Option B: With Local Backend

```bash
# .env.local
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_MOCK_API=false
VITE_APP_ENV=development
```

```bash
npm run dev
```

#### Option C: With Remote Backend (UAT)

```bash
# .env.local
VITE_API_BASE_URL=https://uat-api.rewine.com/api/v1
VITE_MOCK_API=false
VITE_APP_ENV=uat
```

```bash
npm run dev
```

### Step 3: Verify Configuration

```bash
npm run dev
# Check console for: [ENV] Environment loaded: { ... }
```

---

## Container Configuration

### Docker Build-Time Variables

Variables are injected at build time via Docker build args:

```dockerfile
# Dockerfile excerpt
ARG VITE_API_BASE_URL=/api/v1
ARG VITE_MOCK_API=false
ARG VITE_APP_NAME=Rewine
ARG VITE_APP_ENV=production
```

### Building with Custom Config

```bash
# Default production build
docker build -t rewine-frontend .

# Custom API URL
docker build \
  --build-arg VITE_API_BASE_URL=https://api.rewine.com/v1 \
  --build-arg VITE_APP_ENV=production \
  -t rewine-frontend .

# UAT build
docker build \
  --build-arg VITE_API_BASE_URL=https://uat-api.rewine.com/v1 \
  --build-arg VITE_APP_ENV=uat \
  -t rewine-frontend:uat .
```

### Running Container

```bash
# Run on port 8080
docker run -p 8080:80 rewine-frontend

# With environment override (if supported by runtime config)
docker run -p 8080:80 \
  -e VITE_API_BASE_URL=https://api.example.com \
  rewine-frontend
```

---

## Kubernetes Configuration

### ConfigMap (Planned)

```yaml
# k8s/base/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: rewine-frontend-config
data:
  VITE_API_BASE_URL: "/api/v1"
  VITE_APP_NAME: "Rewine"
  VITE_FEATURE_WINE_SCAN: "true"
  VITE_FEATURE_SOCIAL_LOGIN: "false"
```

### Environment Overlays (Planned)

```yaml
# k8s/overlays/uat/configmap-patch.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: rewine-frontend-config
data:
  VITE_API_BASE_URL: "https://uat-api.rewine.com/api/v1"
  VITE_APP_ENV: "uat"
```

```yaml
# k8s/overlays/prod/configmap-patch.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: rewine-frontend-config
data:
  VITE_API_BASE_URL: "https://api.rewine.com/api/v1"
  VITE_APP_ENV: "production"
  VITE_SENTRY_DSN: "https://xxx@sentry.io/xxx"
```

### Deployment Reference (Planned)

```yaml
# k8s/base/deployment.yaml
spec:
  containers:
    - name: frontend
      image: rewine-frontend:latest
      envFrom:
        - configMapRef:
            name: rewine-frontend-config
```

---

## Environment Checklist

### Before Deploying to UAT

- [ ] API URL points to UAT backend
- [ ] Mock API is disabled
- [ ] Feature flags match UAT config
- [ ] Error tracking configured (optional)

### Before Deploying to Production

- [ ] API URL points to production backend
- [ ] Mock API is disabled
- [ ] Feature flags reviewed
- [ ] Error tracking configured
- [ ] Analytics configured (if applicable)
- [ ] SSL certificates valid

---

## Related Documentation

- [Frontend Guide](FRONTEND.md) - Development guide
- [Security](SECURITY.md) - Security configuration
- [Troubleshooting](TROUBLESHOOTING.md) - Common issues


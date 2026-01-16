# Rewine - Wine Discovery Platform

Rewine is a comprehensive wine discovery and management platform designed for wine enthusiasts, sommeliers, and wineries. It enables users to explore wines, manage personal cellars, participate in community reviews, discover wine routes and events, and leverage AI-powered features for wine comparisons and personalized recommendations. The platform supports role-based access for administrators, moderators, partners, and regular users.

---

## Key Features

| Feature | Description |
|---------|-------------|
| **Wine Catalog** | Browse, search, and filter wines by type, region, vintage, price, and more |
| **Personal Cellar** | Track your wine collection with inventory management |
| **Community Reviews** | Read and write wine reviews, rate wines, and engage with other enthusiasts |
| **Wine Scanning** | Scan wine labels to quickly identify and add wines to your collection |
| **AI Compare** | Compare multiple wines side-by-side with AI-generated insights |
| **AI Sommelier Profile** | Get personalized AI-generated profiles for any wine |
| **Wine Routes** | Explore curated wine routes with stops at wineries and tasting rooms |
| **Events** | Discover wine tastings, festivals, tours, and classes near you |
| **Role-Based Access** | Admin, Moderator, Partner, and User roles with appropriate permissions |

---

## Repository Structure

```
rewine/
├── frontend/          # Vue 3 SPA (TypeScript, Vite, Tailwind)
├── backend/           # Spring Boot REST API (Java 21)
├── infra/             # [Planned] Docker, Kubernetes, CI/CD configs
├── docs/              # Project documentation
└── README.md          # This file
```

### Current Status

| Component | Status |
|-----------|--------|
| Frontend | ✅ Implemented |
| Backend | ✅ Implemented |
| Infrastructure | 🔜 Planned |

---

## Quick Start

### Prerequisites

- **Node.js**: v20.x or higher (for frontend)
- **npm**: v10.x or higher (comes with Node.js)
- **Java**: 21 (LTS) (for backend)
- **Maven**: 3.9+ (for backend)
n- **Docker**: Required for PostgreSQL database

### Local Development (Full Stack)

**Option 1: Using Docker Compose (Recommended)**

```bash
# Terminal 1: Start PostgreSQL + Backend
cd infra
docker-compose up -d

# Terminal 2: Start Frontend
cd frontend
cp .env.example .env.local  # First time only
npm install                  # First time only
npm run dev
```

**Option 2: Manual Setup**

```bash
# Terminal 1: Start PostgreSQL
docker run -d --name rewine-postgres \
  -e POSTGRES_DB=rewine \
  -e POSTGRES_USER=rewine \
  -e POSTGRES_PASSWORD=rewine_secret \
  -p 5432:5432 \
  postgres:15

# Terminal 2: Start Backend
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 3: Start Frontend
cd frontend
cp .env.example .env.local  # First time only
npm install                  # First time only
npm run dev
```

**Access Points:**
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api/v1
- API Health: http://localhost:8080/api/v1/actuator/health

**Test Users (local only):**
| Email | Password | Role |
|-------|----------|------|
| admin@rewine.local | Rewine123! | ADMIN |
| partner@rewine.local | Rewine123! | PARTNER |
| moderator@rewine.local | Rewine123! | MODERATOR |
| user@rewine.local | Rewine123! | USER |

### Frontend Development (Mock API - No Backend Required)

```bash
cd frontend
npm install
npm run dev:mock
```

The frontend will be available at `http://localhost:3000` with mocked API responses.

### Backend Development (Standalone)

```bash
cd backend

# With PostgreSQL (requires Docker)
docker run -d --name rewine-postgres \
  -e POSTGRES_DB=rewine \
  -e POSTGRES_USER=rewine \
  -e POSTGRES_PASSWORD=rewine_secret \
  -p 5432:5432 \
  postgres:15

mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The API will be available at `http://localhost:8080/api/v1`.

### Full Stack Development

```bash
# Terminal 1: Start backend (with PostgreSQL running)
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 2: Start frontend (connected to backend)
cd frontend
npm run dev
```

### Environment Variables Reference

For a complete list of environment variables and external service credentials, see [Credentials & Accounts](docs/CREDENTIALS_AND_ACCOUNTS.md).

**Common Backend Variables:**
| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PASSWORD` | `rewine_secret` | Database password |
| `JWT_SECRET` | (dev default) | JWT signing secret |
| `AI_PROVIDER` | `mock` | AI provider (`mock` or `openai`) |
| `OPENAI_API_KEY` | - | OpenAI API key (optional) |
| `GOOGLE_MAPS_API_KEY` | - | Google Maps key (optional) |

**Common Frontend Variables:**
| Variable | Default | Description |
|----------|---------|-------------|
| `VITE_API_BASE_URL` | `http://localhost:8080/api/v1` | Backend API URL |
| `VITE_MOCK_API` | `false` | Enable MSW mocking |

### Common Issues

Having trouble? Check the [Troubleshooting Guide](docs/TROUBLESHOOTING.md) for solutions to common issues:

- **Database connection errors** → Verify PostgreSQL is running
- **Port already in use** → Kill existing process or use different port
- **CORS errors** → Check backend CORS configuration
- **Auth not working** → Verify JWT_SECRET matches and tokens are valid

---

## API Endpoints

### Authentication (Backend)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/auth/register` | POST | Register new user |
| `/api/v1/auth/login` | POST | Login and get JWT tokens |
| `/api/v1/auth/refresh` | POST | Refresh access token |
| `/api/v1/auth/logout` | POST | Revoke refresh token |
| `/api/v1/auth/me` | GET | Get current user profile |

### Other Endpoints

| Endpoint | Description |
|----------|-------------|
| `/api/v1/health` | Health check |
| `/api/v1/version` | Application version |
| `/api/v1/swagger-ui.html` | Swagger UI (API docs) |
| `/api/v1/actuator/health` | Actuator health |

For full API documentation, see [Backend README](backend/README.md) or access Swagger UI when running.

---

## Running Tests

### Frontend Tests

```bash
cd frontend

# Unit tests
npm run test:unit

# Unit tests with coverage
npm run test:coverage

# E2E tests (requires Playwright browsers)
npm run test:e2e:setup   # First time only
npm run test:e2e
```

### Backend Tests

```bash
cd backend

# Run all tests
mvn test

# Run with coverage report
mvn test jacoco:report
# View report at: target/site/jacoco/index.html

# Run checkstyle
mvn checkstyle:check
```

---

## Building for Production

### Frontend Build

```bash
cd frontend

# Type-check and build
npm run build

# Preview production build locally
npm run preview

# Build Docker image
npm run docker:build
```

### Backend Build

```bash
cd backend

# Build JAR
mvn clean package

# Run JAR
java -jar target/rewine-backend-0.0.1-SNAPSHOT.jar
```

---

## Documentation

| Document | Description |
|----------|-------------|
| [Architecture](docs/ARCHITECTURE.md) | System overview, layers, data flow, diagrams |
| [Frontend Guide](docs/FRONTEND.md) | Tech stack, conventions, state management, testing |
| [Backend Guide](backend/README.md) | API endpoints, commands, configuration |
| [Backend Architecture](backend/ARCHITECTURE.md) | Backend layers, security, database design |
| [Security](docs/SECURITY.md) | Authentication, JWT tokens, CORS, headers |
| [Credentials & Accounts](docs/CREDENTIALS_AND_ACCOUNTS.md) | Environment variables, API keys, test users |
| [Development Workflow](docs/DEVELOPMENT_WORKFLOW.md) | Branching, commits, PRs, releases |
| [Environments](docs/ENVIRONMENTS.md) | Dev/UAT/Prod configuration |
| [Troubleshooting](docs/TROUBLESHOOTING.md) | Common issues and solutions |
| [Infrastructure](infra/README.md) | Docker, Kubernetes, CI/CD configuration |

---

## Tech Stack Overview

### Frontend

- **Vue 3** with Composition API
- **TypeScript** for type safety
- **Vite** for fast builds
- **Tailwind CSS** for styling
- **Pinia** for state management
- **Vue Router** for navigation
- **Axios** for HTTP requests
- **Zod** for validation
- **Vue I18n** for internationalization
- **MSW** for API mocking
- **Vitest** + **Playwright** for testing

### Backend

- **Spring Boot 3.2** with Java 21
- **PostgreSQL** / H2 database
- **Spring Security** with JWT authentication
- **Flyway** for migrations
- **OpenAPI/Swagger** documentation
- **Checkstyle** + **JaCoCo** for code quality
- **Logback** with JSON structured logging

### Infrastructure (Planned)

- **Docker** for containerization
- **Kubernetes/OpenShift** for orchestration
- **Jenkins** for CI/CD
- **Prometheus + Grafana** for monitoring

---

## Contributing

1. Read the [Development Workflow](docs/DEVELOPMENT_WORKFLOW.md)
2. Follow the commit conventions
3. Ensure all tests pass before submitting PRs
4. Request review from at least one team member

---

## License

Private - All rights reserved.


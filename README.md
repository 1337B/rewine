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
├── backend/           # [Planned] Spring Boot REST API
├── infra/             # [Planned] Docker, Kubernetes, CI/CD configs
├── docs/              # Project documentation
└── README.md          # This file
```

### Current Status

| Component | Status |
|-----------|--------|
| Frontend | ✅ Implemented |
| Backend | 🔜 Planned |
| Infrastructure | 🔜 Planned |

---

## Quick Start

### Prerequisites

- **Node.js**: v20.x or higher
- **npm**: v10.x or higher (comes with Node.js)
- **Docker**: (optional) for containerized builds

### Local Development

```bash
# Clone the repository
cd rewine

# Navigate to frontend
cd frontend

# Install dependencies
npm install

# Start development server (with mock API)
npm run dev:mock

# Or start without mocks (requires backend)
npm run dev
```

The application will be available at `http://localhost:3000`.

### Running Tests

```bash
# Unit tests
npm run test:unit

# Unit tests with coverage
npm run test:coverage

# E2E tests (requires Playwright browsers)
npm run test:e2e:setup   # First time only
npm run test:e2e
```

### Building for Production

```bash
# Type-check and build
npm run build

# Preview production build locally
npm run preview

# Build Docker image
npm run docker:build

# Run Docker container
npm run docker:run
```

---

## Documentation

| Document | Description |
|----------|-------------|
| [Architecture](docs/ARCHITECTURE.md) | System overview, layers, data flow, diagrams |
| [Frontend Guide](docs/FRONTEND.md) | Tech stack, conventions, state management, testing |
| [Security](docs/SECURITY.md) | Authentication, token handling, CORS, headers |
| [Development Workflow](docs/DEVELOPMENT_WORKFLOW.md) | Branching, commits, PRs, releases |
| [Environments](docs/ENVIRONMENTS.md) | Dev/UAT/Prod configuration |
| [Troubleshooting](docs/TROUBLESHOOTING.md) | Common issues and solutions |

---

## Tech Stack Overview

### Frontend (Current)

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

### Backend (Planned)

- **Spring Boot 3** with Java 21
- **PostgreSQL** database
- **Flyway** for migrations
- **Spring Security** with JWT
- **OpenAPI/Swagger** documentation

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


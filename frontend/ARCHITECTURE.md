# Rewine Frontend Architecture Documentation

## Table of Contents

1. [Overview](#overview)
2. [Architecture Principles](#architecture-principles)
3. [Project Structure](#project-structure)
4. [Layer Responsibilities](#layer-responsibilities)
5. [Data Flow](#data-flow)
6. [State Management](#state-management)
7. [API Integration](#api-integration)
8. [Routing & Navigation](#routing--navigation)
9. [Component Architecture](#component-architecture)
10. [Security Considerations](#security-considerations)
11. [Testing Strategy](#testing-strategy)
12. [Performance Optimization](#performance-optimization)
13. [Internationalization](#internationalization)
14. [Best Practices & Conventions](#best-practices--conventions)

---

## Overview

Rewine Frontend is a modern Single Page Application (SPA) built with **Vue 3** using the **Composition API**. The architecture follows a **layered approach** inspired by Clean Architecture and Domain-Driven Design (DDD) principles, ensuring separation of concerns, maintainability, and scalability.

### Technology Stack

| Category | Technology |
|----------|------------|
| Framework | Vue 3.5+ (Composition API) |
| Build Tool | Vite 6 |
| Language | TypeScript 5.7+ |
| State Management | Pinia |
| Routing | Vue Router 4 |
| HTTP Client | Axios |
| Styling | Tailwind CSS 3.4 |
| Validation | Zod |
| Internationalization | Vue I18n 11 |
| Testing | Vitest (Unit) + Playwright (E2E) |

---

## Architecture Principles

### 1. Separation of Concerns (SoC)

Each layer has a single, well-defined responsibility. The UI components don't know about API implementation details, and the domain logic is independent of the presentation layer.

### 2. Dependency Inversion

High-level modules (pages, components) depend on abstractions (services, stores), not on low-level modules (API clients). This allows for easier testing and flexibility.

### 3. Single Source of Truth

Application state is centralized in Pinia stores. Components read from stores and dispatch actions to modify state, ensuring predictable state management.

### 4. Unidirectional Data Flow

```
User Action → Store Action → API Call → State Update → UI Re-render
```

### 5. Domain-Driven Design (DDD)

Business logic and domain models are isolated in the `/domain` layer, making the codebase reflect the business domain vocabulary.

---

## Project Structure

```
frontend/
├── public/                    # Static assets served as-is
│   ├── images/
│   └── favicon.ico
│
├── src/
│   ├── app/                   # Application bootstrap & core configuration
│   │   ├── App.vue           # Root component
│   │   ├── main.ts           # Application entry point
│   │   ├── router.ts         # Router instance & global guards
│   │   ├── pinia.ts          # Pinia store instance
│   │   ├── env.ts            # Typed environment variables
│   │   ├── http.ts           # Axios instance & interceptors
│   │   └── auth.ts           # Authentication bootstrap helpers
│   │
│   ├── assets/               # Compiled assets (processed by Vite)
│   │   └── styles/
│   │       ├── tailwind.css  # Tailwind base imports
│   │       └── globals.css   # Global custom styles
│   │
│   ├── components/           # Reusable Vue components
│   │   ├── common/           # Generic UI components (no business logic)
│   │   ├── layout/           # Application shell components
│   │   └── feedback/         # User feedback components
│   │
│   ├── config/               # Application configuration
│   │   ├── routes.ts         # Route definitions
│   │   ├── navigation.ts     # Navigation menu configuration
│   │   ├── constants.ts      # Application constants
│   │   └── featureFlags.ts   # Feature toggles
│   │
│   ├── domain/               # Domain layer (business logic)
│   │   ├── wine/
│   │   ├── event/
│   │   ├── route/
│   │   └── user/
│   │
│   ├── pages/                # Route-level page components
│   │   ├── public/
│   │   ├── wines/
│   │   ├── cellar/
│   │   ├── events/
│   │   ├── wine-routes/
│   │   └── admin/
│   │
│   ├── services/             # Application services (use cases)
│   │
│   ├── api/                  # API layer
│   │   ├── clients/          # HTTP client modules
│   │   ├── dto/              # Data Transfer Objects
│   │   └── api.types.ts      # Shared API types
│   │
│   ├── stores/               # Pinia state stores
│   │
│   ├── composables/          # Reusable composition functions
│   │
│   ├── directives/           # Custom Vue directives
│   │
│   ├── utils/                # Utility functions
│   │
│   ├── i18n/                 # Internationalization
│   │   ├── index.ts
│   │   └── locales/
│   │
│   └── types/                # Global TypeScript declarations
│
├── tests/
│   ├── unit/
│   └── e2e/
│
└── [Configuration Files]
```

---

## Layer Responsibilities

### 1. App Layer (`/src/app`)

**Purpose:** Application initialization, configuration, and cross-cutting concerns.

| File | Responsibility |
|------|----------------|
| `main.ts` | Bootstraps Vue app, registers plugins (Pinia, Router, i18n) |
| `App.vue` | Root component, global layout wrapper |
| `router.ts` | Creates router instance, registers global navigation guards |
| `pinia.ts` | Creates and exports Pinia store instance |
| `env.ts` | Provides typed access to environment variables with validation |
| `http.ts` | Configures Axios instance with interceptors for auth & error handling |
| `auth.ts` | Authentication helpers (login, logout, session management) |

**Example - env.ts:**
```typescript
// Typed environment access with defaults and runtime validation
export const env = {
  apiBaseUrl: getEnvString('VITE_API_BASE_URL', 'http://localhost:8080/api'),
  apiTimeout: getEnvNumber('VITE_API_TIMEOUT', 30000),
  isDev: import.meta.env.DEV,
  // ...
} as const
```

---

### 2. Domain Layer (`/src/domain`)

**Purpose:** Contains the core business logic, domain models, and validation rules. This layer is **framework-agnostic** and represents the heart of the application.

Each domain module contains:

| File | Responsibility |
|------|----------------|
| `*.types.ts` | TypeScript interfaces and types representing domain entities |
| `*.mappers.ts` | Functions to transform DTOs ↔ Domain models |
| `*.validators.ts` | Zod schemas for runtime validation |

**Design Rationale:**

- **Types** define the shape of domain entities as the application understands them
- **Mappers** handle the transformation between API responses (snake_case) and domain models (camelCase)
- **Validators** ensure data integrity using Zod schemas, providing both compile-time and runtime type safety

**Example - Wine Domain:**

```typescript
// wine.types.ts - Domain entity
export interface Wine {
  id: string
  name: string
  winery: string
  type: WineType
  grapeVarieties: string[]  // camelCase in domain
  // ...
}

// wine.mappers.ts - DTO to Domain transformation
export function mapWineFromDto(dto: WineDto): Wine {
  return {
    id: dto.id,
    name: dto.name,
    grapeVarieties: dto.grape_varieties ?? [],  // snake_case from API
    // ...
  }
}

// wine.validators.ts - Runtime validation
export const wineSchema = z.object({
  name: z.string().min(1, 'Wine name is required').max(200),
  type: z.enum(['red', 'white', 'rose', 'sparkling', 'dessert', 'fortified']),
  // ...
})
```

---

### 3. API Layer (`/src/api`)

**Purpose:** Handles all HTTP communication with the backend. This layer abstracts the network layer from the rest of the application.

#### Structure:

```
api/
├── clients/          # HTTP client modules per domain
│   ├── auth.client.ts
│   ├── wines.client.ts
│   ├── events.client.ts
│   ├── wineRoutes.client.ts
│   └── users.client.ts
├── dto/              # Data Transfer Objects (API contracts)
│   ├── auth.dto.ts
│   ├── wines.dto.ts
│   ├── events.dto.ts
│   ├── wineRoutes.dto.ts
│   └── users.dto.ts
└── api.types.ts      # Shared types (pagination, errors, etc.)
```

#### Responsibilities:

| Component | Responsibility |
|-----------|----------------|
| **Clients** | Execute HTTP requests, handle endpoints, return raw DTOs |
| **DTOs** | Define the exact shape of API request/response payloads |
| **api.types.ts** | Shared types like `ApiResponse<T>`, `PaginatedResponse<T>`, `ApiError` |

**Key Principle:** API clients return **DTOs**, not domain models. The transformation happens in the service layer.

**Example - wines.client.ts:**
```typescript
export const winesClient = {
  async getWines(params?: WineFilterParamsDto): Promise<PaginatedResponse<WineDto>> {
    const response = await http.get<PaginatedResponse<WineDto>>(API_ENDPOINTS.WINES, { params })
    return response.data
  },

  async getWine(id: string): Promise<WineDto> {
    const response = await http.get<ApiResponse<WineDto>>(`${API_ENDPOINTS.WINES}/${id}`)
    return response.data.data
  },
  // ...
}
```

---

### 4. Services Layer (`/src/services`)

**Purpose:** Application services that orchestrate business operations. They act as a **facade** between the presentation layer and the data layer.

#### Responsibilities:

1. Call API clients to fetch/send data
2. Transform DTOs to domain models using mappers
3. Handle business logic that doesn't belong in stores
4. Provide a clean, domain-oriented API for stores and components

**Data Flow:**
```
Component/Store → Service → API Client → Backend
                    ↓
              Domain Model ← Mapper ← DTO
```

**Example - wines.service.ts:**
```typescript
export const winesService = {
  async getWines(filter?: WineFilter, page = 1, pageSize = 20): Promise<WinesResult> {
    // 1. Transform domain filter to API params
    const params: WineFilterParamsDto = {
      page,
      page_size: pageSize,
      search: filter?.search,
      type: filter?.type,
      // ...
    }

    // 2. Call API client
    const response = await winesClient.getWines(params)

    // 3. Transform DTOs to domain models
    return {
      wines: response.data.map(mapWineFromDto),
      pagination: response.pagination,
    }
  },
  // ...
}
```

---

### 5. Stores Layer (`/src/stores`)

**Purpose:** Centralized state management using Pinia. Stores hold the application state and expose actions to modify it.

#### Store Categories:

| Store | Purpose |
|-------|---------|
| `auth.store.ts` | Authentication state (user, tokens, session) |
| `ui.store.ts` | UI state (theme, toasts, modals, loading) |
| `wines.store.ts` | Wines data, filters, pagination |
| `events.store.ts` | Events data and nearby events |
| `wineRoutes.store.ts` | Wine routes data |
| `cellar.store.ts` | User's personal wine cellar |

#### Store Structure (Composition API):

```typescript
export const useWinesStore = defineStore('wines', () => {
  // State - Reactive references
  const wines = ref<Wine[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters - Computed properties
  const hasWines = computed(() => wines.value.length > 0)

  // Actions - Functions that modify state
  async function fetchWines(page = 1, pageSize = 20) {
    loading.value = true
    try {
      const result = await winesService.getWines(filter.value, page, pageSize)
      wines.value = result.wines
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch wines'
    } finally {
      loading.value = false
    }
  }

  return { wines, loading, error, hasWines, fetchWines }
})
```

#### Design Principles:

1. **Single Responsibility:** Each store manages one domain area
2. **Async Actions:** All API calls happen in store actions
3. **Error Handling:** Stores catch and expose errors for UI consumption
4. **Loading States:** Each async operation tracks its loading state

---

### 6. Components Layer (`/src/components`)

**Purpose:** Reusable Vue components organized by their responsibility.

#### Organization:

```
components/
├── common/      # Generic, reusable UI components (no domain knowledge)
│   ├── BaseButton.vue
│   ├── BaseCard.vue
│   ├── BaseModal.vue
│   ├── BaseInput.vue
│   ├── BaseSelect.vue
│   ├── BaseSpinner.vue
│   └── BaseEmptyState.vue
│
├── layout/      # Application shell components
│   ├── AppHeader.vue
│   ├── AppSidebar.vue
│   ├── AppFooter.vue
│   └── AppShell.vue
│
└── feedback/    # User feedback components
    ├── ToastContainer.vue
    ├── AlertBanner.vue
    └── ConfirmDialog.vue
```

#### Component Categories:

| Category | Characteristics |
|----------|-----------------|
| **Common** | No business logic, purely presentational, highly reusable, accept props for customization |
| **Layout** | Define application structure, may access auth/ui stores |
| **Feedback** | Handle user notifications and confirmations |

#### Base Component Pattern:

Base components (prefixed with `Base`) are atomic UI elements that:
- Accept all customization through props
- Emit events for user interactions
- Don't access stores directly
- Can be composed into more complex components

**Example - BaseButton.vue:**
```vue
<script setup lang="ts">
interface Props {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()
</script>

<template>
  <button
    :class="[variantClasses[variant], sizeClasses[size]]"
    :disabled="disabled || loading"
    @click="emit('click', $event)"
  >
    <slot />
  </button>
</template>
```

---

### 7. Pages Layer (`/src/pages`)

**Purpose:** Route-level components that represent full pages/views. They orchestrate data fetching and compose UI from smaller components.

#### Organization:

```
pages/
├── public/           # Publicly accessible pages
│   ├── HomePage.vue
│   ├── LoginPage.vue
│   ├── RegisterPage.vue
│   └── NotFoundPage.vue
│
├── wines/            # Wine-related pages
│   ├── WineSearchPage.vue
│   ├── WineDetailsPage.vue
│   ├── WineComparePage.vue
│   └── WineScanPage.vue
│
├── cellar/           # User cellar pages
│   └── MyCellarPage.vue
│
├── events/           # Event pages
│   ├── EventsNearbyPage.vue
│   └── EventDetailsPage.vue
│
├── wine-routes/      # Wine route pages
│   ├── WineRoutesExplorerPage.vue
│   └── WineRouteDetailsPage.vue
│
└── admin/            # Admin-only pages
    ├── AdminDashboardPage.vue
    ├── AdminWinesPage.vue
    ├── AdminEventsPage.vue
    ├── AdminRoutesPage.vue
    └── AdminUsersPage.vue
```

#### Page Responsibilities:

1. **Data Fetching:** Call store actions in `onMounted`
2. **Layout Composition:** Arrange components to form the page
3. **Route Integration:** Handle route params and query strings
4. **Error Boundaries:** Display loading/error states

**Example - WineSearchPage.vue:**
```vue
<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useWinesStore } from '@stores/wines.store'
import { useDebounce } from '@composables/useDebounce'

const winesStore = useWinesStore()
const search = ref('')
const debouncedSearch = useDebounce(search, 300)

onMounted(() => {
  winesStore.fetchWines()
})

watch(debouncedSearch, () => {
  winesStore.setFilter({ search: debouncedSearch.value })
  winesStore.fetchWines()
})
</script>

<template>
  <div>
    <BaseInput v-model="search" placeholder="Search wines..." />
    <BaseSpinner v-if="winesStore.loading" />
    <WineGrid v-else :wines="winesStore.wines" />
  </div>
</template>
```

---

### 8. Composables Layer (`/src/composables`)

**Purpose:** Reusable composition functions that encapsulate stateful logic using Vue's Composition API.

#### Available Composables:

| Composable | Purpose |
|------------|---------|
| `useAuth` | Authentication operations and state |
| `useToast` | Toast notification management |
| `usePagination` | Pagination state and navigation |
| `useDebounce` | Debounced values and functions |
| `useModal` | Modal open/close state management |
| `useQueryState` | Sync state with URL query parameters |

#### Design Pattern:

Composables follow the pattern of returning reactive state and functions:

```typescript
export function usePagination(options = {}) {
  // Reactive state
  const page = ref(1)
  const pageSize = ref(20)
  const totalItems = ref(0)

  // Computed values
  const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))
  const hasNextPage = computed(() => page.value < totalPages.value)

  // Functions
  function nextPage() {
    if (hasNextPage.value) page.value++
  }

  // Return public API
  return {
    page,
    pageSize,
    totalPages,
    hasNextPage,
    nextPage,
    // ...
  }
}
```

---

### 9. Utils Layer (`/src/utils`)

**Purpose:** Pure utility functions with no side effects. These are stateless helpers that can be used anywhere.

| File | Purpose |
|------|---------|
| `guard.ts` | Route guards and role-based access control |
| `date.ts` | Date formatting and manipulation |
| `format.ts` | Number, currency, and general formatting |
| `string.ts` | String manipulation (capitalize, slugify, truncate) |
| `object.ts` | Object helpers (isNullish, pick, omit, deepClone) |
| `validation.ts` | Common validation functions |

**Example - format.ts:**
```typescript
export function formatCurrency(value: number, currency = 'ARS', locale = 'es-AR'): string {
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency,
  }).format(value)
}

export function formatDuration(minutes: number): string {
  if (minutes < 60) return `${minutes} min`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return mins === 0 ? `${hours}h` : `${hours}h ${mins}min`
}
```

---

### 10. Config Layer (`/src/config`)

**Purpose:** Centralized application configuration that doesn't change at runtime.

| File | Purpose |
|------|---------|
| `routes.ts` | Route definitions with meta information |
| `navigation.ts` | Navigation menu structure |
| `constants.ts` | Application constants (wine types, regions, etc.) |
| `featureFlags.ts` | Feature toggles for gradual rollouts |

**Example - routes.ts:**
```typescript
export const routes: RouteRecordRaw[] = [
  {
    path: '/wines/:id',
    name: 'wine-details',
    component: WineDetailsPage,
    meta: {
      title: 'Wine Details',
      requiresAuth: false,
    },
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminDashboardPage,
    meta: {
      title: 'Admin Dashboard',
      requiresAuth: true,
      roles: ['admin'],
    },
  },
]
```

---

## Data Flow

### Complete Request Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              USER INTERFACE                              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                  │
│  │    Pages    │    │ Components  │    │ Composables │                  │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘                  │
│         │                  │                  │                          │
│         └──────────────────┼──────────────────┘                          │
│                            │                                             │
│                            ▼                                             │
│                    ┌───────────────┐                                     │
│                    │    STORES     │  ◄── Pinia State Management         │
│                    │  (State Hub)  │                                     │
│                    └───────┬───────┘                                     │
│                            │                                             │
└────────────────────────────┼─────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         APPLICATION LAYER                                │
│                    ┌───────────────┐                                     │
│                    │   SERVICES    │  ◄── Business Logic Orchestration   │
│                    │  (Use Cases)  │                                     │
│                    └───────┬───────┘                                     │
│                            │                                             │
│         ┌──────────────────┼──────────────────┐                          │
│         │                  │                  │                          │
│         ▼                  ▼                  ▼                          │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                  │
│  │   MAPPERS   │    │ VALIDATORS  │    │    UTILS    │                  │
│  │ (Transform) │    │   (Zod)     │    │  (Helpers)  │                  │
│  └─────────────┘    └─────────────┘    └─────────────┘                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         INFRASTRUCTURE LAYER                             │
│                    ┌───────────────┐                                     │
│                    │  API CLIENTS  │  ◄── HTTP Communication             │
│                    │   (Axios)     │                                     │
│                    └───────┬───────┘                                     │
│                            │                                             │
│                    ┌───────┴───────┐                                     │
│                    │     DTOs      │  ◄── API Contracts                  │
│                    └───────────────┘                                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
                             │
                             ▼
                    ┌───────────────┐
                    │   BACKEND     │
                    │     API       │
                    └───────────────┘
```

### Data Transformation Flow

```
Backend Response (JSON)
        │
        ▼
    DTO (snake_case)     ← api/dto/wines.dto.ts
        │
        ▼
    Mapper Function       ← domain/wine/wine.mappers.ts
        │
        ▼
    Domain Model          ← domain/wine/wine.types.ts
    (camelCase)
        │
        ▼
    Pinia Store           ← stores/wines.store.ts
        │
        ▼
    Vue Component         ← pages/wines/WineSearchPage.vue
```

---

## State Management

### Pinia Store Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                        PINIA STORE                            │
├──────────────────────────────────────────────────────────────┤
│  STATE (Refs)           │  GETTERS (Computed)                │
│  ─────────────────────  │  ───────────────────────           │
│  • wines: Wine[]        │  • hasWines: boolean               │
│  • loading: boolean     │  • totalWines: number              │
│  • error: string | null │  • filteredWines: Wine[]           │
│  • filter: WineFilter   │                                    │
├──────────────────────────────────────────────────────────────┤
│  ACTIONS (Functions)                                          │
│  ─────────────────────────────────────────────────────────── │
│  • fetchWines(page, pageSize) → async                        │
│  • setFilter(filter) → sync                                  │
│  • clearFilter() → sync                                      │
│  • reset() → sync                                            │
└──────────────────────────────────────────────────────────────┘
```

### Store Interaction Pattern

```typescript
// In a component
const winesStore = useWinesStore()

// Read state (reactive)
const wines = computed(() => winesStore.wines)
const isLoading = computed(() => winesStore.loading)

// Call actions
await winesStore.fetchWines()
winesStore.setFilter({ type: 'red' })
```

---

## API Integration

### HTTP Client Configuration

The Axios instance in `app/http.ts` provides:

1. **Base URL Configuration:** From environment variables
2. **Request Interceptor:** Automatically attaches JWT token
3. **Response Interceptor:** Handles token refresh on 401 errors
4. **Error Handling:** Standardized error responses

```typescript
// Request Interceptor
http.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.accessToken) {
    config.headers.Authorization = `Bearer ${authStore.accessToken}`
  }
  return config
})

// Response Interceptor (Token Refresh)
http.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      await authStore.refreshToken()
      return http(originalRequest)
    }
    return Promise.reject(error)
  }
)
```

---

## Routing & Navigation

### Route Protection

Routes are protected using Vue Router navigation guards:

```typescript
// Global guard in router.ts
router.beforeEach(authGuard)

// Guard implementation in utils/guard.ts
export async function authGuard(to, from, next) {
  const authStore = useAuthStore()
  
  // Guest-only routes
  if (to.meta.guestOnly && authStore.isAuthenticated) {
    return next('/')
  }
  
  // Protected routes
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }
  
  // Role-based access
  if (to.meta.roles?.length) {
    const hasRole = to.meta.roles.some(role => authStore.user?.roles?.includes(role))
    if (!hasRole) return next('/')
  }
  
  next()
}
```

### Route Meta Configuration

```typescript
interface RouteMeta {
  title?: string           // Page title
  requiresAuth?: boolean   // Requires authentication
  roles?: string[]         // Required roles
  guestOnly?: boolean      // Only for non-authenticated users
}
```

---

## Security Considerations

### 1. Token Management

- Access tokens stored in memory (Pinia store)
- Refresh tokens stored in localStorage (encrypted in production)
- Automatic token refresh before expiration

### 2. XSS Prevention

- Vue's template syntax auto-escapes HTML
- Use `v-html` only with sanitized content
- Content Security Policy headers (configured on server)

### 3. CSRF Protection

- API uses token-based authentication
- No cookies for session management

### 4. Input Validation

- Zod schemas validate all user input
- Server-side validation is the final authority

### 5. Sensitive Data

- No secrets in frontend code
- Environment variables for configuration
- API keys never exposed in client bundle

---

## Testing Strategy

### Unit Tests (Vitest)

```typescript
// tests/unit/example.spec.ts
import { describe, it, expect } from 'vitest'
import { useWinesStore } from '@stores/wines.store'

describe('WinesStore', () => {
  it('should initialize with empty wines array', () => {
    const store = useWinesStore()
    expect(store.wines).toEqual([])
  })
})
```

### E2E Tests (Playwright)

```typescript
// tests/e2e/example.spec.ts
import { test, expect } from '@playwright/test'

test('should search for wines', async ({ page }) => {
  await page.goto('/wines')
  await page.fill('[placeholder="Search wines..."]', 'Malbec')
  await expect(page.locator('.wine-card')).toHaveCount.greaterThan(0)
})
```

---

## Performance Optimization

### 1. Code Splitting

Routes are lazy-loaded:
```typescript
const WineDetailsPage = () => import('@pages/wines/WineDetailsPage.vue')
```

### 2. Component Lazy Loading

Heavy components loaded on demand:
```typescript
const HeavyChart = defineAsyncComponent(() => import('./HeavyChart.vue'))
```

### 3. Debouncing

Search inputs debounced to reduce API calls:
```typescript
const debouncedSearch = useDebounce(search, 300)
```

### 4. Virtual Scrolling

For long lists (future implementation):
```vue
<VirtualList :items="wines" :item-height="80" />
```

---

## Internationalization

### Setup

```typescript
// i18n/index.ts
export const i18n = createI18n({
  legacy: false,
  locale: 'es-AR',
  fallbackLocale: 'es-AR',
  messages: {
    'es-AR': esAR,
    'en-US': enUS,
  },
})
```

### Usage

```vue
<template>
  <h1>{{ $t('wines.title') }}</h1>
  <p>{{ $t('wines.types.red') }}</p>
</template>
```

---

## Best Practices & Conventions

### Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Components | PascalCase | `BaseButton.vue` |
| Composables | camelCase with `use` prefix | `useAuth.ts` |
| Stores | camelCase with `use` prefix and `.store` suffix | `auth.store.ts` |
| Utils | camelCase | `formatDate()` |
| Constants | SCREAMING_SNAKE_CASE | `API_ENDPOINTS` |
| Types/Interfaces | PascalCase | `Wine`, `WineFilter` |
| DTOs | PascalCase with `Dto` suffix | `WineDto` |

### File Organization

1. **One component per file**
2. **Co-locate related files** (types, mappers, validators in domain folders)
3. **Index files for re-exports** when beneficial
4. **Consistent import order:**
   - Vue/external packages
   - Internal absolute imports (@/...)
   - Relative imports

### TypeScript Guidelines

1. **Prefer interfaces over types** for object shapes
2. **Use strict mode** (`strict: true` in tsconfig)
3. **Avoid `any`**, use `unknown` when type is truly unknown
4. **Export types** alongside implementations

### Vue Component Guidelines

1. **Use `<script setup>`** for all components
2. **Define props with `defineProps<T>()`** for type safety
3. **Define emits with `defineEmits<T>()`** for type safety
4. **Use composables** for reusable logic
5. **Keep templates readable** - extract complex logic to computed properties

---

## Conclusion

This architecture provides a solid foundation for building scalable, maintainable Vue applications. The layered approach ensures:

- **Testability:** Each layer can be tested in isolation
- **Maintainability:** Clear boundaries make code easier to understand and modify
- **Scalability:** New features can be added without affecting existing code
- **Type Safety:** TypeScript throughout provides compile-time guarantees
- **Consistency:** Conventions ensure uniform code across the team

For questions or contributions, please follow the established patterns and conventions outlined in this document.


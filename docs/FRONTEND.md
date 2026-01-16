# Frontend Development Guide

This document provides a comprehensive guide for developing the Rewine frontend application.

---

## Table of Contents

1. [Tech Stack](#tech-stack)
2. [Project Setup](#project-setup)
3. [Development Modes](#development-modes)
4. [Folder Structure & Aliases](#folder-structure--aliases)
5. [State Management](#state-management)
6. [API Contract Approach](#api-contract-approach)
7. [Component Guidelines](#component-guidelines)
8. [Styling with Tailwind](#styling-with-tailwind)
9. [Internationalization](#internationalization)
10. [Testing Strategy](#testing-strategy)

---

## Tech Stack

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| Framework | Vue | 3.5+ | UI framework with Composition API |
| Language | TypeScript | 5.7+ | Static typing |
| Build Tool | Vite | 6.x | Fast development and builds |
| Styling | Tailwind CSS | 3.4+ | Utility-first CSS |
| State | Pinia | 2.x | Global state management |
| Routing | Vue Router | 4.x | SPA navigation |
| HTTP | Axios | 1.7+ | API requests |
| Validation | Zod | 3.x | Schema validation |
| i18n | Vue I18n | 11.x | Internationalization |
| Mocking | MSW | 2.x | API mocking for development |
| Unit Testing | Vitest | 3.x | Fast unit tests |
| E2E Testing | Playwright | 1.49+ | End-to-end tests |

---

## Project Setup

### Prerequisites

- Node.js v20.x or higher
- npm v10.x or higher

### Installation

```bash
cd frontend
npm install
```

### Available Scripts

| Command | Description |
|---------|-------------|
| `npm run dev` | Start dev server (requires backend) |
| `npm run dev:mock` | Start dev server with MSW mocks |
| `npm run build` | Type-check and build for production |
| `npm run preview` | Preview production build locally |
| `npm run lint` | Run ESLint |
| `npm run format` | Format code with Prettier |
| `npm run test` | Run tests in watch mode |
| `npm run test:unit` | Run unit tests once |
| `npm run test:coverage` | Run tests with coverage |
| `npm run test:e2e` | Run Playwright E2E tests |
| `npm run docker:build` | Build Docker image |
| `npm run docker:run` | Run Docker container |

---

## Development Modes

### Standard Development (with Backend)

```bash
npm run dev
```

- Connects to real backend API
- Uses `VITE_API_BASE_URL` from `.env`
- Good for integration testing

### Mock Development (without Backend)

```bash
npm run dev:mock
```

- Uses MSW (Mock Service Worker) to intercept API calls
- Returns mock data from `src/mocks/handlers.ts`
- Ideal for frontend-only development

### Mock Configuration

Mock handlers are defined in `src/mocks/handlers.ts`:

```typescript
// Example handler
http.get('/api/v1/wines', () => {
  return HttpResponse.json({
    data: mockWines,
    pagination: { ... }
  })
})
```

Toggle mock behavior in `src/mocks/config.ts`:

```typescript
export const mockConfig = {
  latency: { min: 200, max: 500 },  // Simulated network delay
  failureRate: 0.05,                 // 5% random failures
  enableLogging: true
}
```

---

## Folder Structure & Aliases

### Path Aliases

Configured in `tsconfig.json` and `vite.config.ts`:

| Alias | Path | Usage |
|-------|------|-------|
| `@/*` | `src/*` | General imports |
| `@app/*` | `src/app/*` | App bootstrap |
| `@components/*` | `src/components/*` | Components |
| `@pages/*` | `src/pages/*` | Page components |
| `@domain/*` | `src/domain/*` | Domain types/mappers |
| `@services/*` | `src/services/*` | Service layer |
| `@api/*` | `src/api/*` | API clients/DTOs |
| `@stores/*` | `src/stores/*` | Pinia stores |
| `@composables/*` | `src/composables/*` | Composition functions |
| `@utils/*` | `src/utils/*` | Utilities |
| `@config/*` | `src/config/*` | Configuration |
| `@i18n/*` | `src/i18n/*` | Internationalization |

### Usage Example

```typescript
// Instead of relative paths
import { useWinesStore } from '../../../stores/wines.store'

// Use aliases
import { useWinesStore } from '@stores/wines.store'
```

---

## State Management

### Pinia Store Structure

Each store follows a consistent pattern:

```typescript
// src/stores/wines.store.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useWinesStore = defineStore('wines', () => {
  // State
  const wines = ref<Wine[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const hasWines = computed(() => wines.value.length > 0)

  // Actions
  async function fetchWines(filter?: WineFilter) {
    loading.value = true
    error.value = null
    try {
      const result = await winesService.getWines(filter)
      wines.value = result.wines
    } catch (e) {
      error.value = normalizeError(e)
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    wines,
    loading,
    error,
    // Getters
    hasWines,
    // Actions
    fetchWines
  }
})
```

### Loading & Error Patterns

```vue
<template>
  <!-- Loading state -->
  <BaseSpinner v-if="store.loading" />
  
  <!-- Error state -->
  <AlertBanner v-else-if="store.error" type="error">
    {{ store.error }}
  </AlertBanner>
  
  <!-- Empty state -->
  <BaseEmptyState v-else-if="!store.hasWines" />
  
  <!-- Content -->
  <div v-else>
    <!-- Render content -->
  </div>
</template>
```

### When to Use Stores vs Local State

| Use Store | Use Local State |
|-----------|-----------------|
| Data shared across components | Form input values |
| Cached API data | UI toggle states |
| User session/auth | Component-specific loading |
| Global filters/preferences | Temporary selections |

---

## API Contract Approach

### Layer Flow

```
Page/Component
    ↓ (calls)
Service (wines.service.ts)
    ↓ (calls)
API Client (wines.client.ts)
    ↓ (HTTP)
Backend API
    ↓ (returns)
DTO (WineDto)
    ↓ (mapped by)
Mapper (mapWineFromDto)
    ↓ (returns)
Domain Model (Wine)
```

### DTO Definition

```typescript
// src/api/dto/wines.dto.ts
export interface WineDto {
  id: string
  name: string
  winery: string
  type: string
  region: string
  vintage: number | null
  price: number | null
  rating: number | null
  review_count: number | null  // snake_case from API
  created_at: string
  updated_at: string
}
```

### Domain Model

```typescript
// src/domain/wine/wine.types.ts
export interface Wine {
  id: string
  name: string
  winery: string
  type: WineType
  region: string
  vintage: number | null
  price: number | null
  rating: number | null
  reviewCount: number  // camelCase in domain
  createdAt: Date      // Dates parsed
  updatedAt: Date
}
```

### Mapper

```typescript
// src/domain/wine/wine.mappers.ts
export function mapWineFromDto(dto: WineDto): Wine {
  return {
    id: dto.id,
    name: dto.name,
    winery: dto.winery,
    type: dto.type as WineType,
    region: dto.region,
    vintage: dto.vintage,
    price: dto.price,
    rating: dto.rating,
    reviewCount: dto.review_count ?? 0,
    createdAt: new Date(dto.created_at),
    updatedAt: new Date(dto.updated_at),
  }
}
```

### Service

```typescript
// src/services/wines.service.ts
export const winesService = {
  async getWines(filter?: WineFilter): Promise<WinesResult> {
    const params = mapFilterToParams(filter)
    const response = await winesClient.getWines(params)
    return {
      wines: response.data.map(mapWineFromDto),
      pagination: mapPaginationFromDto(response.pagination)
    }
  }
}
```

---

## Component Guidelines

### Component Types

| Type | Location | Purpose |
|------|----------|---------|
| Common | `components/common/` | Reusable UI (BaseButton, BaseInput, etc.) |
| Layout | `components/layout/` | App shell (Header, Sidebar, Footer) |
| Feedback | `components/feedback/` | Toasts, alerts, confirmations |
| Pages | `pages/` | Route-level components |

### Naming Conventions

```
BaseButton.vue      # Generic component (Base prefix)
AppHeader.vue       # App-specific component (App prefix)
WineCard.vue        # Domain-specific component (Domain prefix)
LoginPage.vue       # Page component (Page suffix)
```

### Component Template

```vue
<script setup lang="ts">
/**
 * ComponentName - Brief description
 *
 * Detailed description of component purpose and usage.
 */
import { computed } from 'vue'

interface Props {
  /** Prop description */
  modelValue: string
  /** Another prop */
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

// Implementation...
</script>

<template>
  <!-- Template -->
</template>
```

---

## Styling with Tailwind

### Configuration

Tailwind is configured in `tailwind.config.js`:

```javascript
module.exports = {
  content: ['./index.html', './src/**/*.{vue,ts}'],
  theme: {
    extend: {
      colors: {
        wine: {
          50: '#fdf2f4',
          // ... shades
          900: '#4a1219',
        }
      }
    }
  }
}
```

### Best Practices

```vue
<!-- Good: Use Tailwind utilities -->
<button class="px-4 py-2 bg-wine-600 text-white rounded-lg hover:bg-wine-700">
  Click me
</button>

<!-- Avoid: Custom CSS for common patterns -->
<style>
.my-button {
  padding: 0.5rem 1rem;
  /* ... */
}
</style>
```

### Responsive Design

```vue
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
  <!-- Responsive grid -->
</div>
```

---

## Internationalization

### Setup

Locales are defined in `src/i18n/locales/`:

```
i18n/
├── index.ts         # i18n setup
└── locales/
    ├── en-US.json   # English
    └── es-AR.json   # Spanish (Argentina)
```

### Usage

```vue
<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
</script>

<template>
  <h1>{{ t('wines.title') }}</h1>
  <p>{{ t('wines.found', { count: 42 }) }}</p>
</template>
```

### Message Files

```json
// en-US.json
{
  "wines": {
    "title": "Wine Catalog",
    "found": "{count} wines found"
  }
}
```

---

## Testing Strategy

### Unit Tests (Vitest)

Location: `tests/unit/`

```typescript
// tests/unit/domain/wine.mappers.spec.ts
import { describe, it, expect } from 'vitest'
import { mapWineFromDto } from '@domain/wine/wine.mappers'

describe('mapWineFromDto', () => {
  it('should map DTO to domain model', () => {
    const dto = { id: '1', name: 'Test Wine', /* ... */ }
    const result = mapWineFromDto(dto)
    expect(result.id).toBe('1')
    expect(result.reviewCount).toBe(0)
  })
})
```

### Component Tests

```typescript
// tests/unit/components/LoginPage.spec.ts
import { mount } from '@vue/test-utils'
import LoginPage from '@pages/public/LoginPage.vue'

describe('LoginPage', () => {
  it('should render login form', () => {
    const wrapper = mount(LoginPage, { /* options */ })
    expect(wrapper.find('form').exists()).toBe(true)
  })
})
```

### E2E Tests (Playwright)

Location: `tests/e2e/`

```typescript
// tests/e2e/smoke.spec.ts
import { test, expect } from '@playwright/test'

test('should load homepage', async ({ page }) => {
  await page.goto('/')
  await expect(page.locator('text=rewine')).toBeVisible()
})
```

### Running Tests

```bash
# Unit tests
npm run test:unit

# With coverage
npm run test:coverage

# E2E tests
npm run test:e2e

# E2E with UI
npm run test:e2e:ui
```

### Coverage Thresholds

Target coverage: 70%+

Focus areas:
- Domain mappers and validators
- Service layer logic
- Store actions
- Critical UI flows

---

## Related Documentation

- [Architecture](ARCHITECTURE.md) - System architecture
- [Backend Guide](../backend/README.md) - Backend configuration
- [Credentials & Accounts](./CREDENTIALS_AND_ACCOUNTS.md) - Environment variables, API keys, test users
- [Environments](ENVIRONMENTS.md) - Environment configuration
- [Security](SECURITY.md) - Security implementation
- [Troubleshooting](TROUBLESHOOTING.md) - Common issues


# Frontend Test Suite

This directory contains the test suite for the Rewine frontend application.

## Structure

```
tests/
├── setup.ts           # Global test setup (MSW, Pinia, mocks)
├── tsconfig.json      # TypeScript config for tests
├── unit/              # Unit tests (Vitest)
│   ├── composables/   # Composable tests
│   ├── components/    # Component tests
│   ├── domain/        # Domain validators and mappers tests
│   └── utils/         # Utility function tests
└── e2e/               # End-to-end tests (Playwright)
    └── smoke.spec.ts  # Smoke tests for critical flows
```

## Running Tests

### Unit Tests

```bash
# Run all unit tests once
npm run test:unit

# Run tests in watch mode
npm run test:unit:watch

# Run with coverage report
npm run test:coverage
```

### E2E Tests

```bash
# Install Playwright browsers (first time only)
npm run test:e2e:setup

# Run all e2e tests
npm run test:e2e

# Run with UI mode (interactive)
npm run test:e2e:ui
```

## Test Configuration

### Vitest
Configuration is in `vitest.config.ts`. Tests run with:
- JSDOM environment for browser APIs
- Path aliases matching the main app
- MSW for API mocking
- Vue Test Utils for component testing

### Playwright
Configuration is in `playwright.config.ts`. Tests run against:
- Dev server with mock API enabled
- Chromium browser
- Screenshots on failure

## Writing Tests

### Unit Tests

```typescript
import { describe, it, expect } from 'vitest'

describe('MyFunction', () => {
  it('should do something', () => {
    expect(myFunction()).toBe(expected)
  })
})
```

### Component Tests

```typescript
import { mount } from '@vue/test-utils'
import MyComponent from '@components/MyComponent.vue'

describe('MyComponent', () => {
  it('should render', () => {
    const wrapper = mount(MyComponent)
    expect(wrapper.text()).toContain('expected text')
  })
})
```

### E2E Tests

```typescript
import { test, expect } from '@playwright/test'

test('should navigate to page', async ({ page }) => {
  await page.goto('/')
  await expect(page.locator('h1')).toBeVisible()
})
```

## Mock API (MSW)

Tests use MSW (Mock Service Worker) for API mocking. Handlers are defined in `src/mocks/handlers.ts`.

The server is automatically started in `setup.ts` and resets between tests.

To add custom handlers for a specific test:

```typescript
import { server } from '../../setup'
import { http, HttpResponse } from 'msw'

beforeEach(() => {
  server.use(
    http.get('/api/v1/custom', () => {
      return HttpResponse.json({ data: 'test' })
    })
  )
})
```

## Coverage

Coverage reports are generated in the `coverage/` directory when running `npm run test:coverage`.

The coverage configuration excludes:
- Mock files (`src/mocks/`)
- Type declaration files (`*.d.ts`)
- Entry point (`src/app/main.ts`)


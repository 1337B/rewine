/**
 * Vitest Test Setup
 *
 * Global test configuration and utilities.
 * Sets up MSW for API mocking in test environment.
 */

import { afterAll, afterEach, beforeAll, beforeEach, vi } from 'vitest'
import { config } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { setupServer } from 'msw/node'
import { handlers } from '../src/mocks/handlers'

// ============================================================================
// MSW Server Setup
// ============================================================================

export const server = setupServer(...handlers)

beforeAll(() => {
  server.listen({ onUnhandledRequest: 'bypass' })
})

afterEach(() => {
  server.resetHandlers()
})

afterAll(() => {
  server.close()
})

// ============================================================================
// Pinia Setup
// ============================================================================

beforeEach(() => {
  setActivePinia(createPinia())
})

// ============================================================================
// Vue Test Utils Configuration
// ============================================================================

// Mock i18n
config.global.mocks = {
  $t: (key: string) => key,
  t: (key: string) => key,
}

// ============================================================================
// Browser API Mocks
// ============================================================================

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
vi.stubGlobal('localStorage', localStorageMock)

// Mock sessionStorage
const sessionStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
vi.stubGlobal('sessionStorage', sessionStorageMock)

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation((query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})

// ============================================================================
// Note: server is exported above for use in individual test files
// ============================================================================


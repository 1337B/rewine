/**
 * MSW Browser Worker
 *
 * Sets up the MSW service worker for browser environments.
 * Only used in development mode with VITE_MOCK_API=true.
 */

import { setupWorker } from 'msw/browser'
import { handlers } from './handlers'

/**
 * MSW browser service worker
 */
export const worker = setupWorker(...handlers)

/**
 * Start the MSW worker
 *
 * @param options - MSW start options
 */
export async function startMockServiceWorker() {
  // Only start in development mode
  if (import.meta.env.PROD) {
    console.warn('[MSW] Mock service worker should not be started in production')
    return
  }

  await worker.start({
    // Don't warn about unhandled requests in development
    onUnhandledRequest: 'bypass',
    // Service worker configuration
    serviceWorker: {
      url: '/mockServiceWorker.js',
    },
  })

  console.log('[MSW] Mock service worker started')
  console.log('[MSW] Available test credentials:')
  console.log('  - demo@rewine.com / demo123 (regular user)')
  console.log('  - admin@rewine.com / admin123 (admin user)')
  console.log('  - partner@winery.com / partner123 (partner user)')
}


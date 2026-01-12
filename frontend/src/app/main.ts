import { createApp } from 'vue'
import App from './App.vue'
import { router } from './router'
import { pinia } from './pinia'
import { i18n } from '@i18n/index'
import { initAuth } from './auth'

import '@/assets/styles/tailwind.css'
import '@/assets/styles/globals.css'

/**
 * Initialize and mount the Vue application
 */
async function bootstrap() {
  // Start MSW mock service worker in development if enabled
  if (import.meta.env.DEV && import.meta.env.VITE_MOCK_API === 'true') {
    const { startMockServiceWorker } = await import('@/mocks')
    await startMockServiceWorker()
  }

  const app = createApp(App)

  app.use(pinia)
  app.use(router)
  app.use(i18n)

  // Mount app first, then initialize auth in the background
  app.mount('#app')

  // Initialize auth session (non-blocking)
  initAuth().catch((error) => {
    console.warn('[Main] Auth initialization failed:', error)
  })
}

// Bootstrap the application
bootstrap()

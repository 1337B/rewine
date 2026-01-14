/**
 * MSW Mock API Module
 *
 * Main entry point for the mock API.
 * Re-exports configuration and browser setup.
 */

export { worker, startMockServiceWorker } from './browser'
export {
  getMockConfig,
  setMockConfig,
  resetMockConfig,
  defaultMockConfig,
  type MockConfig,
} from './config'


/**
 * Application constants (non-secret values)
 */

// Pagination
export const DEFAULT_PAGE_SIZE = 20
export const PAGE_SIZE_OPTIONS = [10, 20, 50, 100]

// Wine types
export const WINE_TYPES = [
  { value: 'red', label: 'Red Wine' },
  { value: 'white', label: 'White Wine' },
  { value: 'rose', label: 'Rosé' },
  { value: 'sparkling', label: 'Sparkling' },
  { value: 'dessert', label: 'Dessert Wine' },
  { value: 'fortified', label: 'Fortified Wine' },
] as const

// Wine regions (Argentina focus)
export const WINE_REGIONS = [
  { value: 'mendoza', label: 'Mendoza' },
  { value: 'san-juan', label: 'San Juan' },
  { value: 'la-rioja', label: 'La Rioja' },
  { value: 'salta', label: 'Salta' },
  { value: 'neuquen', label: 'Neuquén' },
  { value: 'rio-negro', label: 'Río Negro' },
  { value: 'catamarca', label: 'Catamarca' },
] as const

// Grape varieties
export const GRAPE_VARIETIES = [
  { value: 'malbec', label: 'Malbec' },
  { value: 'cabernet-sauvignon', label: 'Cabernet Sauvignon' },
  { value: 'merlot', label: 'Merlot' },
  { value: 'syrah', label: 'Syrah' },
  { value: 'bonarda', label: 'Bonarda' },
  { value: 'torrontes', label: 'Torrontés' },
  { value: 'chardonnay', label: 'Chardonnay' },
  { value: 'sauvignon-blanc', label: 'Sauvignon Blanc' },
  { value: 'pinot-noir', label: 'Pinot Noir' },
  { value: 'tempranillo', label: 'Tempranillo' },
] as const

// Rating scale
export const MIN_RATING = 1
export const MAX_RATING = 5

// Price ranges
export const PRICE_RANGES = [
  { value: 'budget', label: 'Budget ($0 - $15)', min: 0, max: 15 },
  { value: 'moderate', label: 'Moderate ($15 - $30)', min: 15, max: 30 },
  { value: 'premium', label: 'Premium ($30 - $60)', min: 30, max: 60 },
  { value: 'luxury', label: 'Luxury ($60+)', min: 60, max: null },
] as const

// Date formats
export const DATE_FORMAT = 'dd/MM/yyyy'
export const DATE_TIME_FORMAT = 'dd/MM/yyyy HH:mm'
export const TIME_FORMAT = 'HH:mm'

// Storage keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'rewine_auth_token',
  REFRESH_TOKEN: 'rewine_refresh_token',
  USER: 'rewine_user',
  THEME: 'rewine_theme',
  LOCALE: 'rewine_locale',
} as const

// API endpoints
export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    REFRESH: '/auth/refresh',
    LOGOUT: '/auth/logout',
    ME: '/auth/me',
  },
  WINES: '/wines',
  EVENTS: '/events',
  WINE_ROUTES: '/wine-routes',
  USERS: '/users',
  CELLAR: '/cellar',
} as const


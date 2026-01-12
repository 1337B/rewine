/**
 * MSW Request Handlers
 *
 * Mock API handlers for development without backend.
 * Simulates realistic API behavior including latency and errors.
 */

import { http, HttpResponse, delay } from 'msw'
import { getRandomLatency, shouldFail, shouldRefreshFail, getMockConfig } from './config'

// Import fixtures
import winesData from './fixtures/wines.json'
import reviewsData from './fixtures/reviews.json'
import eventsData from './fixtures/events.json'
import routesData from './fixtures/routes.json'
import usersData from './fixtures/users.json'
import comparisonsData from './fixtures/comparisons.json'
import aiProfilesData from './fixtures/aiProfiles.json'

// Type assertions
const wines = winesData.wines as any[]
const reviews = reviewsData.reviews as Record<string, any[]>
const events = eventsData.events as any[]
const routes = routesData.routes as any[]
const users = usersData.users as any[]
const credentials = usersData.credentials as Record<string, string>
const comparisons = comparisonsData.comparisons as Record<string, any>
const aiProfiles = aiProfilesData.profiles as Record<string, any>

// Token storage (in-memory for mocking)
let tokenStore: Record<string, { userId: string; expiresAt: number }> = {}

/**
 * Generate a mock JWT token
 */
function generateToken(userId: string): string {
  const token = `mock-token-${userId}-${Date.now()}`
  const config = getMockConfig()
  tokenStore[token] = {
    userId,
    expiresAt: Date.now() + config.auth.tokenExpiryMs,
  }
  return token
}

/**
 * Validate a token and return user ID
 */
function validateToken(token: string): string | null {
  const stored = tokenStore[token]
  if (!stored) return null
  if (Date.now() > stored.expiresAt) {
    delete tokenStore[token]
    return null
  }
  return stored.userId
}

/**
 * Create paginated response
 */
function paginate<T>(items: T[], page: number, pageSize: number) {
  const start = (page - 1) * pageSize
  const end = start + pageSize
  const paginatedItems = items.slice(start, end)

  return {
    data: paginatedItems,
    pagination: {
      page,
      page_size: pageSize,
      total_items: items.length,
      total_pages: Math.ceil(items.length / pageSize),
      has_next: end < items.length,
      has_previous: page > 1,
    },
  }
}

/**
 * API base URL
 */
const API_BASE = '/api/v1'

/**
 * MSW Handlers
 */
export const handlers = [
  // ============================================================================
  // Auth Handlers
  // ============================================================================

  /**
   * POST /api/v1/auth/login
   */
  http.post(`${API_BASE}/auth/login`, async ({ request }) => {
    await delay(getRandomLatency())

    if (shouldFail('auth/login')) {
      return HttpResponse.json(
        { error: { code: 'SERVER_ERROR', message: 'Internal server error' } },
        { status: 500 }
      )
    }

    const body = await request.json() as { email: string; password: string }
    const { email, password } = body

    // Check credentials
    if (credentials[email] !== password) {
      return HttpResponse.json(
        { error: { code: 'INVALID_CREDENTIALS', message: 'Invalid email or password' } },
        { status: 401 }
      )
    }

    // Find user
    const user = users.find(u => u.email === email)
    if (!user) {
      return HttpResponse.json(
        { error: { code: 'USER_NOT_FOUND', message: 'User not found' } },
        { status: 404 }
      )
    }

    const accessToken = generateToken(user.id)
    const refreshToken = `refresh-${generateToken(user.id)}`

    return HttpResponse.json({
      data: {
        access_token: accessToken,
        refresh_token: refreshToken,
        expires_in: getMockConfig().auth.tokenExpiryMs / 1000,
        token_type: 'Bearer',
        user: {
          id: user.id,
          email: user.email,
          name: user.name,
          avatar: user.avatar,
          roles: user.roles,
          is_verified: user.is_verified,
        },
      },
    })
  }),

  /**
   * POST /api/v1/auth/register
   */
  http.post(`${API_BASE}/auth/register`, async ({ request }) => {
    await delay(getRandomLatency())

    const body = await request.json() as { email: string; password: string; name: string }
    const { email, name } = body

    // Check if email exists
    if (users.find(u => u.email === email)) {
      return HttpResponse.json(
        { error: { code: 'EMAIL_EXISTS', message: 'Email already registered' } },
        { status: 409 }
      )
    }

    // Create new user (in memory only)
    const newUser = {
      id: `user-${Date.now()}`,
      email,
      name,
      avatar: null,
      roles: ['ROLE_USER'],
      is_verified: false,
      preferences: {
        favorite_wine_types: [],
        favorite_regions: [],
        price_range: null,
        notifications: { email: true, push: false, events: true, recommendations: true, newsletter: false },
        locale: 'es-AR',
        theme: 'system',
      },
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString(),
    }

    users.push(newUser)

    const accessToken = generateToken(newUser.id)
    const refreshToken = `refresh-${generateToken(newUser.id)}`

    return HttpResponse.json({
      data: {
        access_token: accessToken,
        refresh_token: refreshToken,
        expires_in: getMockConfig().auth.tokenExpiryMs / 1000,
        token_type: 'Bearer',
        user: {
          id: newUser.id,
          email: newUser.email,
          name: newUser.name,
          avatar: newUser.avatar,
          roles: newUser.roles,
          is_verified: newUser.is_verified,
        },
      },
    })
  }),

  /**
   * POST /api/v1/auth/refresh
   */
  http.post(`${API_BASE}/auth/refresh`, async ({ request }) => {
    await delay(getRandomLatency())

    // Simulate occasional refresh failures
    if (shouldRefreshFail()) {
      return HttpResponse.json(
        { error: { code: 'TOKEN_EXPIRED', message: 'Refresh token expired' } },
        { status: 401 }
      )
    }

    const body = await request.json() as { refresh_token: string }
    const refreshToken = body.refresh_token

    // Extract user ID from refresh token
    const tokenPart = refreshToken.replace('refresh-', '')
    const userId = validateToken(tokenPart)

    if (!userId) {
      return HttpResponse.json(
        { error: { code: 'INVALID_TOKEN', message: 'Invalid refresh token' } },
        { status: 401 }
      )
    }

    const newAccessToken = generateToken(userId)

    return HttpResponse.json({
      data: {
        access_token: newAccessToken,
        expires_in: getMockConfig().auth.tokenExpiryMs / 1000,
      },
    })
  }),

  /**
   * POST /api/v1/auth/logout
   */
  http.post(`${API_BASE}/auth/logout`, async ({ request }) => {
    await delay(getRandomLatency())

    const authHeader = request.headers.get('Authorization')
    if (authHeader) {
      const token = authHeader.replace('Bearer ', '')
      delete tokenStore[token]
    }

    return new HttpResponse(null, { status: 204 })
  }),

  /**
   * GET /api/v1/auth/me
   */
  http.get(`${API_BASE}/auth/me`, async ({ request }) => {
    await delay(getRandomLatency())

    const authHeader = request.headers.get('Authorization')
    if (!authHeader) {
      return HttpResponse.json(
        { error: { code: 'UNAUTHORIZED', message: 'No token provided' } },
        { status: 401 }
      )
    }

    const token = authHeader.replace('Bearer ', '')
    const userId = validateToken(token)

    if (!userId) {
      return HttpResponse.json(
        { error: { code: 'TOKEN_EXPIRED', message: 'Token expired' } },
        { status: 401 }
      )
    }

    const user = users.find(u => u.id === userId)
    if (!user) {
      return HttpResponse.json(
        { error: { code: 'USER_NOT_FOUND', message: 'User not found' } },
        { status: 404 }
      )
    }

    return HttpResponse.json({
      data: {
        id: user.id,
        email: user.email,
        name: user.name,
        avatar: user.avatar,
        roles: user.roles,
        is_verified: user.is_verified,
        created_at: user.created_at,
        updated_at: user.updated_at,
      },
    })
  }),

  // ============================================================================
  // Wine Handlers
  // ============================================================================

  /**
   * GET /api/v1/wines
   */
  http.get(`${API_BASE}/wines`, async ({ request }) => {
    await delay(getRandomLatency())

    if (shouldFail('wines')) {
      return HttpResponse.json(
        { error: { code: 'SERVER_ERROR', message: 'Failed to fetch wines' } },
        { status: 500 }
      )
    }

    const url = new URL(request.url)
    const page = parseInt(url.searchParams.get('page') || '1')
    const pageSize = parseInt(url.searchParams.get('page_size') || '20')
    const search = url.searchParams.get('search')?.toLowerCase()
    const type = url.searchParams.get('type')
    const region = url.searchParams.get('region')
    const minPrice = url.searchParams.get('min_price')
    const maxPrice = url.searchParams.get('max_price')
    const minRating = url.searchParams.get('min_rating')

    let filteredWines = [...wines]

    // Apply filters
    if (search) {
      filteredWines = filteredWines.filter(w =>
        w.name.toLowerCase().includes(search) ||
        w.winery.toLowerCase().includes(search) ||
        w.region.toLowerCase().includes(search)
      )
    }
    if (type) {
      const types = type.split(',')
      filteredWines = filteredWines.filter(w => types.includes(w.type))
    }
    if (region) {
      filteredWines = filteredWines.filter(w => w.region.toLowerCase() === region.toLowerCase())
    }
    if (minPrice) {
      filteredWines = filteredWines.filter(w => w.price >= parseFloat(minPrice))
    }
    if (maxPrice) {
      filteredWines = filteredWines.filter(w => w.price <= parseFloat(maxPrice))
    }
    if (minRating) {
      filteredWines = filteredWines.filter(w => w.rating >= parseFloat(minRating))
    }

    return HttpResponse.json(paginate(filteredWines, page, pageSize))
  }),

  /**
   * GET /api/v1/wines/:id
   */
  http.get(`${API_BASE}/wines/:id`, async ({ params }) => {
    await delay(getRandomLatency())

    const { id } = params
    const wine = wines.find(w => w.id === id)

    if (!wine) {
      return HttpResponse.json(
        { error: { code: 'NOT_FOUND', message: 'Wine not found' } },
        { status: 404 }
      )
    }

    return HttpResponse.json({ data: wine })
  }),

  /**
   * GET /api/v1/wines/:id/reviews
   */
  http.get(`${API_BASE}/wines/:id/reviews`, async ({ params, request }) => {
    await delay(getRandomLatency())

    const { id } = params
    const url = new URL(request.url)
    const page = parseInt(url.searchParams.get('page') || '1')
    const pageSize = parseInt(url.searchParams.get('page_size') || '10')

    const wineReviews = reviews[id as string] || []

    return HttpResponse.json(paginate(wineReviews, page, pageSize))
  }),

  /**
   * GET /api/v1/wines/:id/similar
   */
  http.get(`${API_BASE}/wines/:id/similar`, async ({ params, request }) => {
    await delay(getRandomLatency())

    const { id } = params
    const url = new URL(request.url)
    const limit = parseInt(url.searchParams.get('limit') || '5')

    const wine = wines.find(w => w.id === id)
    if (!wine) {
      return HttpResponse.json(
        { error: { code: 'NOT_FOUND', message: 'Wine not found' } },
        { status: 404 }
      )
    }

    // Find similar wines by type
    const similar = wines
      .filter(w => w.id !== id && w.type === wine.type)
      .slice(0, limit)

    return HttpResponse.json({ data: similar })
  }),

  /**
   * GET /api/v1/wines/recommended
   */
  http.get(`${API_BASE}/wines/recommended`, async ({ request }) => {
    await delay(getRandomLatency())

    const url = new URL(request.url)
    const limit = parseInt(url.searchParams.get('limit') || '10')

    // Return top-rated wines as "recommended"
    const recommended = [...wines]
      .sort((a, b) => b.rating - a.rating)
      .slice(0, limit)

    return HttpResponse.json({ data: recommended })
  }),

  /**
   * GET /api/v1/wines/popular
   */
  http.get(`${API_BASE}/wines/popular`, async ({ request }) => {
    await delay(getRandomLatency())

    const url = new URL(request.url)
    const limit = parseInt(url.searchParams.get('limit') || '10')

    // Return wines with most reviews as "popular"
    const popular = [...wines]
      .sort((a, b) => b.review_count - a.review_count)
      .slice(0, limit)

    return HttpResponse.json({ data: popular })
  }),

  /**
   * POST /api/v1/wines/compare
   */
  http.post(`${API_BASE}/wines/compare`, async ({ request }) => {
    await delay(getRandomLatency() + 500) // Extra delay for AI simulation

    const body = await request.json() as { wine_ids: string[] }
    const { wine_ids } = body

    // Generate cache key
    const cacheKey = wine_ids.sort().join('_')

    // Check for cached comparison
    if (comparisons[cacheKey]) {
      const cached = comparisons[cacheKey]
      const comparedWines = wine_ids.map(id => wines.find(w => w.id === id)).filter(Boolean)

      return HttpResponse.json({
        data: {
          wines: comparedWines,
          comparison: cached.comparison,
          ai_summary: cached.ai_summary,
        },
      })
    }

    // Generate dynamic comparison
    const comparedWines = wine_ids.map(id => wines.find(w => w.id === id)).filter(Boolean)

    if (comparedWines.length < 2) {
      return HttpResponse.json(
        { error: { code: 'INVALID_REQUEST', message: 'Need at least 2 wines to compare' } },
        { status: 400 }
      )
    }

    const comparison = [
      { attribute: 'Price', values: comparedWines.map(w => w.price), winner_index: comparedWines.reduce((min, w, i, arr) => w.price < arr[min].price ? i : min, 0) },
      { attribute: 'Rating', values: comparedWines.map(w => w.rating), winner_index: comparedWines.reduce((max, w, i, arr) => w.rating > arr[max].rating ? i : max, 0) },
      { attribute: 'Reviews', values: comparedWines.map(w => w.review_count), winner_index: comparedWines.reduce((max, w, i, arr) => w.review_count > arr[max].review_count ? i : max, 0) },
      { attribute: 'Alcohol', values: comparedWines.map(w => w.alcohol_content), winner_index: null },
      { attribute: 'Type', values: comparedWines.map(w => w.type), winner_index: null },
    ]

    return HttpResponse.json({
      data: {
        wines: comparedWines,
        comparison,
        ai_summary: `Comparing ${comparedWines.map(w => w.name).join(' vs ')}. Each wine has its unique characteristics suited for different occasions.`,
      },
    })
  }),

  /**
   * GET /api/v1/wines/:id/ai-profile
   */
  http.get(`${API_BASE}/wines/:id/ai-profile`, async ({ params }) => {
    await delay(getRandomLatency() + 1000) // Extra delay for AI simulation

    const { id } = params

    // Check for cached profile
    if (aiProfiles[id as string]) {
      return HttpResponse.json({ data: aiProfiles[id as string] })
    }

    // Generate dynamic profile
    const wine = wines.find(w => w.id === id)
    if (!wine) {
      return HttpResponse.json(
        { error: { code: 'NOT_FOUND', message: 'Wine not found' } },
        { status: 404 }
      )
    }

    const profile = {
      wine_id: id,
      summary: `${wine.name} is a ${wine.type} wine from ${wine.region}, ${wine.country}. ${wine.description}`,
      ideal_occasions: ['Dinner parties', 'Special celebrations', 'Quiet evenings'],
      food_pairings_detailed: (wine.food_pairings || []).map((dish: string, i: number) => ({
        dish,
        category: 'Food',
        match_score: 90 - i * 5,
        reason: `Complements the wine's ${wine.type} characteristics.`,
      })),
      similar_wines: wines.filter(w => w.id !== id && w.type === wine.type).slice(0, 3).map(w => w.name),
      personality_traits: ['Elegant', 'Complex', 'Balanced'],
      generated_at: new Date().toISOString(),
    }

    return HttpResponse.json({ data: profile })
  }),

  // ============================================================================
  // Event Handlers
  // ============================================================================

  /**
   * GET /api/v1/events
   */
  http.get(`${API_BASE}/events`, async ({ request }) => {
    await delay(getRandomLatency())

    const url = new URL(request.url)
    const page = parseInt(url.searchParams.get('page') || '1')
    const pageSize = parseInt(url.searchParams.get('page_size') || '20')
    const search = url.searchParams.get('search')?.toLowerCase()
    const type = url.searchParams.get('type')
    const city = url.searchParams.get('city')
    const latitude = url.searchParams.get('latitude')
    const longitude = url.searchParams.get('longitude')
    const radiusKm = parseFloat(url.searchParams.get('radius_km') || '50')

    let filteredEvents = [...events]

    // Apply filters
    if (search) {
      filteredEvents = filteredEvents.filter(e =>
        e.title.toLowerCase().includes(search) ||
        e.description.toLowerCase().includes(search)
      )
    }
    if (type) {
      const types = type.split(',')
      filteredEvents = filteredEvents.filter(e => types.includes(e.type))
    }
    if (city) {
      filteredEvents = filteredEvents.filter(e => e.location.city.toLowerCase() === city.toLowerCase())
    }

    // Radius filter (simplified distance calculation)
    if (latitude && longitude) {
      const lat = parseFloat(latitude)
      const lon = parseFloat(longitude)
      filteredEvents = filteredEvents.filter(e => {
        if (!e.location.latitude || !e.location.longitude) return true
        const distance = Math.sqrt(
          Math.pow(e.location.latitude - lat, 2) + Math.pow(e.location.longitude - lon, 2)
        ) * 111 // Rough km conversion
        return distance <= radiusKm
      })
    }

    return HttpResponse.json(paginate(filteredEvents, page, pageSize))
  }),

  /**
   * GET /api/v1/events/:id
   */
  http.get(`${API_BASE}/events/:id`, async ({ params }) => {
    await delay(getRandomLatency())

    const { id } = params
    const event = events.find(e => e.id === id)

    if (!event) {
      return HttpResponse.json(
        { error: { code: 'NOT_FOUND', message: 'Event not found' } },
        { status: 404 }
      )
    }

    return HttpResponse.json({ data: event })
  }),

  // ============================================================================
  // Wine Route Handlers
  // ============================================================================

  /**
   * GET /api/v1/wine-routes
   */
  http.get(`${API_BASE}/wine-routes`, async ({ request }) => {
    await delay(getRandomLatency())

    const url = new URL(request.url)
    const page = parseInt(url.searchParams.get('page') || '1')
    const pageSize = parseInt(url.searchParams.get('page_size') || '20')
    const search = url.searchParams.get('search')?.toLowerCase()
    const region = url.searchParams.get('region')
    const difficulty = url.searchParams.get('difficulty')

    let filteredRoutes = [...routes]

    // Apply filters
    if (search) {
      filteredRoutes = filteredRoutes.filter(r =>
        r.name.toLowerCase().includes(search) ||
        r.description.toLowerCase().includes(search)
      )
    }
    if (region) {
      filteredRoutes = filteredRoutes.filter(r => r.region.toLowerCase() === region.toLowerCase())
    }
    if (difficulty) {
      const difficulties = difficulty.split(',')
      filteredRoutes = filteredRoutes.filter(r => difficulties.includes(r.difficulty))
    }

    return HttpResponse.json(paginate(filteredRoutes, page, pageSize))
  }),

  /**
   * GET /api/v1/wine-routes/:id
   */
  http.get(`${API_BASE}/wine-routes/:id`, async ({ params }) => {
    await delay(getRandomLatency())

    const { id } = params
    const route = routes.find(r => r.id === id)

    if (!route) {
      return HttpResponse.json(
        { error: { code: 'NOT_FOUND', message: 'Route not found' } },
        { status: 404 }
      )
    }

    return HttpResponse.json({ data: route })
  }),

  // ============================================================================
  // User Handlers
  // ============================================================================

  /**
   * GET /api/v1/users
   */
  http.get(`${API_BASE}/users`, async ({ request }) => {
    await delay(getRandomLatency())

    const url = new URL(request.url)
    const page = parseInt(url.searchParams.get('page') || '1')
    const pageSize = parseInt(url.searchParams.get('page_size') || '20')

    return HttpResponse.json(paginate(users, page, pageSize))
  }),

  /**
   * GET /api/v1/users/:id
   */
  http.get(`${API_BASE}/users/:id`, async ({ params }) => {
    await delay(getRandomLatency())

    const { id } = params
    const user = users.find(u => u.id === id)

    if (!user) {
      return HttpResponse.json(
        { error: { code: 'NOT_FOUND', message: 'User not found' } },
        { status: 404 }
      )
    }

    return HttpResponse.json({ data: user })
  }),
]


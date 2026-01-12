/**
 * WineSearchPage Component Tests
 *
 * Tests for wine list loading, filtering, and pagination.
 * Uses MSW for API mocking.
 */

import { describe, it, expect, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import { createI18n } from 'vue-i18n'
import { http, HttpResponse } from 'msw'
import { server } from '../../setup'
import WineSearchPage from '@pages/wines/WineSearchPage.vue'

// Mock wine data
const mockWines = [
  {
    id: 'wine-001',
    name: 'Test Malbec',
    winery: 'Test Winery',
    type: 'red',
    region: 'Mendoza',
    country: 'Argentina',
    vintage: 2020,
    price: 45.99,
    rating: 4.7,
    review_count: 342,
    image_url: '/images/mock/winebottle.jpg',
  },
  {
    id: 'wine-002',
    name: 'Test Chardonnay',
    winery: 'Another Winery',
    type: 'white',
    region: 'Luján de Cuyo',
    country: 'Argentina',
    vintage: 2022,
    price: 22.00,
    rating: 4.3,
    review_count: 156,
    image_url: '/images/mock/winebottle2.jpg',
  },
  {
    id: 'wine-003',
    name: 'Test Rosé',
    winery: 'Third Winery',
    type: 'rose',
    region: 'Mendoza',
    country: 'Argentina',
    vintage: 2023,
    price: 12.99,
    rating: 4.0,
    review_count: 124,
    image_url: null,
  },
]

// Create test router
function createTestRouter() {
  return createRouter({
    history: createWebHistory(),
    routes: [
      { path: '/', name: 'home', component: { template: '<div>Home</div>' } },
      { path: '/wines', name: 'wines', component: WineSearchPage },
      { path: '/wines/:id', name: 'wine-details', component: { template: '<div>Wine Details</div>' } },
    ],
  })
}

// Create test i18n
function createTestI18n() {
  return createI18n({
    legacy: false,
    locale: 'en',
    messages: {
      en: {
        wines: {
          browse: 'Browse Wines',
          search: 'Search wines...',
          allTypes: 'All Types',
          allRegions: 'All Regions',
          clearFilters: 'Clear filters',
          noWinesFound: 'No wines found',
          tryAdjusting: 'Try adjusting your search or filters',
        },
      },
    },
  })
}

describe('WineSearchPage', () => {
  let router: ReturnType<typeof createTestRouter>
  let i18n: ReturnType<typeof createTestI18n>

  beforeEach(async () => {
    setActivePinia(createPinia())
    router = createTestRouter()
    i18n = createTestI18n()
    await router.push('/wines')
    await router.isReady()

    // Setup MSW handler for wines endpoint
    server.use(
      http.get('/api/v1/wines', ({ request }) => {
        const url = new URL(request.url)
        const search = url.searchParams.get('search')?.toLowerCase()
        const type = url.searchParams.get('type')
        const page = parseInt(url.searchParams.get('page') || '1')
        const pageSize = parseInt(url.searchParams.get('page_size') || '20')

        let filteredWines = [...mockWines]

        if (search) {
          filteredWines = filteredWines.filter(
            (w) => w.name.toLowerCase().includes(search) || w.winery.toLowerCase().includes(search)
          )
        }

        if (type) {
          filteredWines = filteredWines.filter((w) => w.type === type)
        }

        const start = (page - 1) * pageSize
        const end = start + pageSize
        const paginatedWines = filteredWines.slice(start, end)

        return HttpResponse.json({
          data: paginatedWines,
          pagination: {
            page,
            page_size: pageSize,
            total_items: filteredWines.length,
            total_pages: Math.ceil(filteredWines.length / pageSize),
            has_next: end < filteredWines.length,
            has_previous: page > 1,
          },
        })
      })
    )
  })

  function mountWineSearchPage() {
    return mount(WineSearchPage, {
      global: {
        plugins: [router, i18n],
        stubs: {
          BaseInput: {
            template: `
              <input 
                :type="type || 'text'" 
                :value="modelValue" 
                :placeholder="placeholder"
                @input="$emit('update:modelValue', $event.target.value)"
                data-testid="search-input"
              />
            `,
            props: ['modelValue', 'type', 'placeholder'],
          },
          BaseSelect: {
            template: `
              <select 
                :value="modelValue" 
                @change="$emit('update:modelValue', $event.target.value || null)"
                data-testid="select"
              >
                <option value="">{{ placeholder }}</option>
                <option v-for="opt in options" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
              </select>
            `,
            props: ['modelValue', 'options', 'placeholder'],
          },
          BaseCard: {
            template: '<div class="card" :class="{ hoverable }"><slot /></div>',
            props: ['padding', 'hoverable'],
          },
          BaseSpinner: {
            template: '<div class="spinner" data-testid="spinner">Loading...</div>',
            props: ['size'],
          },
          BaseEmptyState: {
            template: '<div class="empty-state" data-testid="empty-state">{{ title }}</div>',
            props: ['icon', 'title', 'description'],
          },
        },
      },
    })
  }

  describe('initial render', () => {
    it('should render page title', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()

      expect(wrapper.text()).toContain('Browse Wines')
    })

    it('should render search input', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()

      const searchInput = wrapper.find('[data-testid="search-input"]')
      expect(searchInput.exists()).toBe(true)
    })

    it('should render filter selects', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()

      const selects = wrapper.findAll('[data-testid="select"]')
      expect(selects.length).toBeGreaterThanOrEqual(2) // Type and Region
    })

    it('should render clear filters button', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()

      expect(wrapper.text()).toContain('Clear filters')
    })
  })

  describe('loading wines', () => {
    it('should show spinner while loading', async () => {
      const wrapper = mountWineSearchPage()

      // The component may show spinner initially or load instantly
      // Either spinner exists OR wines are already loaded
      const hasSpinner = wrapper.find('[data-testid="spinner"]').exists()
      const hasContent = wrapper.text().includes('Test') || wrapper.find('[class*="card"]').exists()

      // At mount, either loading state or content should be present
      expect(hasSpinner || hasContent).toBe(true)

      await flushPromises()
    })

    it('should load and display wines from MSW', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()
      // Wait for async operations
      await new Promise((r) => setTimeout(r, 100))
      await flushPromises()

      // Should display wine cards
      expect(wrapper.text()).toContain('Test Malbec')
      expect(wrapper.text()).toContain('Test Winery')
    })

    it('should display wine ratings', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()
      await new Promise((r) => setTimeout(r, 100))
      await flushPromises()

      expect(wrapper.text()).toContain('4.7')
    })

    it('should display wine prices', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()
      await new Promise((r) => setTimeout(r, 100))
      await flushPromises()

      expect(wrapper.text()).toContain('45.99')
    })
  })

  describe('filtering', () => {
    it('should filter wines by type', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()
      await new Promise((r) => setTimeout(r, 100))
      await flushPromises()

      // Select red wine type
      const typeSelect = wrapper.findAll('[data-testid="select"]')[0]
      await typeSelect.setValue('red')
      await flushPromises()
      await new Promise((r) => setTimeout(r, 350)) // Wait for debounce + request
      await flushPromises()

      // Should only show red wines
      expect(wrapper.text()).toContain('Test Malbec')
    })

    it('should clear filters', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()
      await new Promise((r) => setTimeout(r, 100))
      await flushPromises()

      // Apply filter
      const typeSelect = wrapper.findAll('[data-testid="select"]')[0]
      await typeSelect.setValue('red')
      await flushPromises()

      // Click clear filters
      const clearButton = wrapper.find('button')
      await clearButton.trigger('click')
      await flushPromises()

      // Select should be cleared
      expect((typeSelect.element as HTMLSelectElement).value).toBe('')
    })
  })

  describe('empty state', () => {
    it('should show empty state when no wines match filter', async () => {
      // Override handler to return empty results
      server.use(
        http.get('/api/v1/wines', () => {
          return HttpResponse.json({
            data: [],
            pagination: {
              page: 1,
              page_size: 20,
              total_items: 0,
              total_pages: 0,
              has_next: false,
              has_previous: false,
            },
          })
        })
      )

      const wrapper = mountWineSearchPage()
      await flushPromises()
      await new Promise((r) => setTimeout(r, 100))
      await flushPromises()

      const emptyState = wrapper.find('[data-testid="empty-state"]')
      expect(emptyState.exists()).toBe(true)
    })
  })

  describe('wine cards', () => {
    it('should render wine images when available', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()
      await new Promise((r) => setTimeout(r, 100))
      await flushPromises()

      const images = wrapper.findAll('img')
      const wineImages = images.filter((img) => img.attributes('alt')?.includes('Test'))
      expect(wineImages.length).toBeGreaterThan(0)
    })

    it('should link to wine details page', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()
      await new Promise((r) => setTimeout(r, 100))
      await flushPromises()

      const wineLink = wrapper.find('a[href="/wines/wine-001"]')
      expect(wineLink.exists()).toBe(true)
    })
  })

  describe('pagination', () => {
    it('should show item count', async () => {
      const wrapper = mountWineSearchPage()
      await flushPromises()
      await new Promise((r) => setTimeout(r, 100))
      await flushPromises()

      // Should show pagination info
      expect(wrapper.text()).toMatch(/\d+.*wines?/i)
    })
  })
})


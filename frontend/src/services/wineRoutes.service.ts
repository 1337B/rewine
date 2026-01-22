import { wineRoutesClient } from '@api/clients/wineRoutes.client'
import { mapWineRouteFromDto, mapWineRouteSummaryFromDto } from '@domain/route/route.mappers'
import type { WineRoute, WineRouteFilter, WineRouteHierarchy } from '@domain/route/route.types'
import type { WineRouteFilterParamsDto } from '@api/dto/wineRoutes.dto'

export interface PaginationMeta {
  pageNumber: number
  pageSize: number
  totalItems: number
  totalPages: number
  hasNext: boolean
  hasPrevious: boolean
}

export interface WineRoutesResult {
  routes: WineRoute[]
  pagination: PaginationMeta
}

/**
 * Wine Routes service
 */
export const wineRoutesService = {
  /**
   * Get hierarchy of countries/regions/subregions
   */
  async getHierarchy(): Promise<WineRouteHierarchy> {
    const response = await wineRoutesClient.getHierarchy()
    return {
      countries: response.countries.map(c => ({
        name: c.name,
        regions: c.regions.map(r => ({
          name: r.name,
          subregions: r.subregions,
        })),
      })),
    }
  },

  /**
   * Get list of countries
   */
  async getCountries(): Promise<string[]> {
    return await wineRoutesClient.getCountries()
  },

  /**
   * Get regions for a country
   */
  async getRegions(country: string): Promise<string[]> {
    return await wineRoutesClient.getRegions(country)
  },

  /**
   * Get subregions for a country/region
   */
  async getSubregions(country: string, region: string): Promise<string[]> {
    return await wineRoutesClient.getSubregions(country, region)
  },

  /**
   * Get paginated list of wine routes
   */
  async getWineRoutes(filter?: WineRouteFilter, page = 0, pageSize = 20): Promise<WineRoutesResult> {
    const params: WineRouteFilterParamsDto = {
      page,
      size: pageSize,
      search: filter?.search,
      country: filter?.country,
      region: filter?.region,
      subregion: filter?.subregion,
    }

    const response = await wineRoutesClient.getWineRoutes(params)

    // Backend returns PageResponse format with items/content arrays
    const items = response.items ?? response.content ?? []

    return {
      routes: items.map(mapWineRouteSummaryFromDto),
      pagination: {
        pageNumber: response.pageNumber ?? page,
        pageSize: response.pageSize ?? pageSize,
        totalItems: response.totalItems ?? 0,
        totalPages: response.totalPages ?? 0,
        hasNext: response.hasNext ?? !response.last,
        hasPrevious: response.hasPrevious ?? !response.first,
      },
    }
  },

  /**
   * Get a single wine route by ID
   */
  async getWineRoute(id: string): Promise<WineRoute> {
    const response = await wineRoutesClient.getWineRoute(id)
    return mapWineRouteFromDto(response)
  },

  // ==========================================================================
  // CRUD Operations (Admin/Partner only - to be implemented in backend)
  // ==========================================================================

  /**
   * Create a new wine route
   * Note: Backend endpoint not yet implemented
   */
  async createWineRoute(route: Partial<WineRoute>): Promise<WineRoute> {
    // TODO: Implement when backend supports POST /wine-routes
    throw new Error('Wine route creation is not yet supported')
  },

  /**
   * Update an existing wine route
   * Note: Backend endpoint not yet implemented
   */
  async updateWineRoute(id: string, route: Partial<WineRoute>): Promise<WineRoute> {
    // TODO: Implement when backend supports PUT /wine-routes/{id}
    throw new Error('Wine route update is not yet supported')
  },

  /**
   * Delete a wine route
   * Note: Backend endpoint not yet implemented
   */
  async deleteWineRoute(id: string): Promise<void> {
    // TODO: Implement when backend supports DELETE /wine-routes/{id}
    throw new Error('Wine route deletion is not yet supported')
  },
}

export default wineRoutesService

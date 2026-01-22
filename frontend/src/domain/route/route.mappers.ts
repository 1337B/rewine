import type { WineRoute, RouteStop, RouteLocation, RouteWinery, WineRouteReview } from './route.types'
import type {
  WineRouteDetailsDto,
  WineRouteSummaryDto,
  RouteStopDto,
  RouteWineryDto,
} from '@api/dto/wineRoutes.dto'

/**
 * Map Wine Route Summary DTO to domain model (for list views)
 */
export function mapWineRouteSummaryFromDto(dto: WineRouteSummaryDto): WineRoute {
  return {
    id: dto.id,
    name: dto.name,
    description: dto.description ?? '',
    region: dto.region ?? '',
    country: dto.country ?? '',
    difficulty: (dto.difficulty as WineRoute['difficulty']) ?? 'moderate',
    duration: dto.estimatedDuration ?? dto.estimatedDays ?? 0,
    distance: dto.totalDistance ?? 0,
    stops: [],
    imageUrl: dto.imageUrl ?? null,
    rating: null,
    reviewCount: 0,
    tags: [],
    isPublished: dto.status === 'ACTIVE',
    createdBy: '',
    createdAt: dto.createdAt ? new Date(dto.createdAt) : new Date(),
    updatedAt: new Date(),
  }
}

/**
 * Map Wine Route Details DTO to domain model (for detail views)
 */
export function mapWineRouteFromDto(dto: WineRouteDetailsDto): WineRoute {
  return {
    id: dto.id,
    name: dto.name,
    description: dto.description ?? '',
    region: dto.region ?? '',
    country: dto.country ?? '',
    difficulty: (dto.difficulty as WineRoute['difficulty']) ?? 'moderate',
    duration: dto.estimatedDuration ?? dto.estimatedDays ?? 0,
    distance: dto.totalDistance ?? 0,
    stops: dto.stops?.map(mapRouteStopFromDto) ?? [],
    imageUrl: dto.imageUrl ?? null,
    rating: null,
    reviewCount: 0,
    tags: dto.recommendedWineTypes ?? [],
    isPublished: dto.status === 'ACTIVE',
    createdBy: dto.createdById ?? '',
    createdAt: dto.createdAt ? new Date(dto.createdAt) : new Date(),
    updatedAt: dto.updatedAt ? new Date(dto.updatedAt) : new Date(),
  }
}

/**
 * Map Route Stop DTO to domain model
 */
export function mapRouteStopFromDto(dto: RouteStopDto): RouteStop {
  return {
    id: dto.id,
    order: dto.stopOrder ?? 0,
    name: dto.name,
    type: (dto.type as RouteStop['type']) ?? 'winery',
    description: dto.description ?? '',
    location: {
      address: dto.address ?? '',
      city: '',
      latitude: dto.latitude ?? 0,
      longitude: dto.longitude ?? 0,
    },
    duration: dto.estimatedDuration ?? 0,
    winery: null,
  }
}

/**
 * Map Route Winery DTO to domain model
 */
export function mapRouteWineryFromDto(dto: RouteWineryDto): RouteWinery {
  return {
    id: dto.id,
    name: dto.name,
    wines: [],
  }
}

/**
 * Map Wine Route domain model to DTO for API requests
 */
export function mapWineRouteToDto(route: Partial<WineRoute>): Record<string, unknown> {
  return {
    name: route.name,
    description: route.description,
    region: route.region,
    country: route.country,
    difficulty: route.difficulty,
    estimatedDuration: route.duration,
    totalDistance: route.distance,
    imageUrl: route.imageUrl ?? undefined,
    recommendedWineTypes: route.tags,
  }
}


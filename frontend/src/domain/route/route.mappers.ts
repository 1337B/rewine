import type { WineRoute, RouteStop, RouteLocation, RouteWinery, WineRouteReview } from './route.types'
import type { WineRouteDto, RouteStopDto, RouteLocationDto, RouteWineryDto, WineRouteReviewDto } from '@api/dto/wineRoutes.dto'

/**
 * Map Wine Route DTO to domain model
 */
export function mapWineRouteFromDto(dto: WineRouteDto): WineRoute {
  return {
    id: dto.id,
    name: dto.name,
    description: dto.description,
    region: dto.region,
    country: dto.country,
    difficulty: dto.difficulty,
    duration: dto.duration,
    distance: dto.distance,
    stops: dto.stops?.map(mapRouteStopFromDto) ?? [],
    imageUrl: dto.image_url ?? null,
    rating: dto.rating ?? null,
    reviewCount: dto.review_count ?? 0,
    tags: dto.tags ?? [],
    isPublished: dto.is_published ?? false,
    createdBy: dto.created_by,
    createdAt: new Date(dto.created_at),
    updatedAt: new Date(dto.updated_at),
  }
}

/**
 * Map Route Stop DTO to domain model
 */
export function mapRouteStopFromDto(dto: RouteStopDto): RouteStop {
  return {
    id: dto.id,
    order: dto.order,
    name: dto.name,
    type: dto.type,
    description: dto.description,
    location: mapRouteLocationFromDto(dto.location),
    duration: dto.duration,
    winery: dto.winery ? mapRouteWineryFromDto(dto.winery) : null,
  }
}

/**
 * Map Route Location DTO to domain model
 */
export function mapRouteLocationFromDto(dto: RouteLocationDto): RouteLocation {
  return {
    address: dto.address,
    city: dto.city,
    latitude: dto.latitude,
    longitude: dto.longitude,
  }
}

/**
 * Map Route Winery DTO to domain model
 */
export function mapRouteWineryFromDto(dto: RouteWineryDto): RouteWinery {
  return {
    id: dto.id,
    name: dto.name,
    wines: dto.wines ?? [],
  }
}

/**
 * Map Wine Route Review DTO to domain model
 */
export function mapWineRouteReviewFromDto(dto: WineRouteReviewDto): WineRouteReview {
  return {
    id: dto.id,
    routeId: dto.route_id,
    userId: dto.user_id,
    userName: dto.user_name,
    rating: dto.rating,
    comment: dto.comment,
    visitedAt: new Date(dto.visited_at),
    createdAt: new Date(dto.created_at),
  }
}

/**
 * Map Wine Route domain model to DTO for API requests
 */
export function mapWineRouteToDto(route: Partial<WineRoute>): Partial<WineRouteDto> {
  return {
    name: route.name,
    description: route.description,
    region: route.region,
    country: route.country,
    difficulty: route.difficulty,
    duration: route.duration,
    distance: route.distance,
    image_url: route.imageUrl ?? undefined,
    tags: route.tags,
    is_published: route.isPublished,
  }
}


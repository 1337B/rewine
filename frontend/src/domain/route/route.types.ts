/**
 * Wine Route domain types
 */

export interface WineRoute {
  id: string
  name: string
  description: string
  region: string
  country: string
  difficulty: RouteDifficulty
  duration: number // in hours
  distance: number // in kilometers
  stops: RouteStop[]
  imageUrl: string | null
  rating: number | null
  reviewCount: number
  tags: string[]
  isPublished: boolean
  createdBy: string
  createdAt: Date
  updatedAt: Date
}

export type RouteDifficulty = 'easy' | 'moderate' | 'challenging'

export interface RouteStop {
  id: string
  order: number
  name: string
  type: StopType
  description: string
  location: RouteLocation
  duration: number // suggested time in minutes
  winery: RouteWinery | null
}

export type StopType = 'winery' | 'restaurant' | 'viewpoint' | 'attraction' | 'accommodation'

export interface RouteLocation {
  address: string
  city: string
  latitude: number
  longitude: number
}

export interface RouteWinery {
  id: string
  name: string
  wines: string[]
}

export interface WineRouteFilter {
  search?: string
  country?: string
  region?: string
  subregion?: string
  difficulty?: RouteDifficulty | RouteDifficulty[]
  minDuration?: number
  maxDuration?: number
  minDistance?: number
  maxDistance?: number
  minRating?: number
  sortBy?: 'name' | 'rating' | 'duration' | 'distance'
  sortOrder?: 'asc' | 'desc'
}

export interface WineRouteHierarchy {
  countries: WineRouteHierarchyCountry[]
}

export interface WineRouteHierarchyCountry {
  name: string
  regions: WineRouteHierarchyRegion[]
}

export interface WineRouteHierarchyRegion {
  name: string
  subregions: string[]
}

export interface WineRouteReview {
  id: string
  routeId: string
  userId: string
  userName: string
  rating: number
  comment: string
  visitedAt: Date
  createdAt: Date
}


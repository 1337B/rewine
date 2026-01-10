import type { RouteDifficulty, StopType } from '@domain/route/route.types'

export interface WineRouteDto {
  id: string
  name: string
  description: string
  region: string
  country: string
  difficulty: RouteDifficulty
  duration: number
  distance: number
  stops?: RouteStopDto[]
  image_url?: string
  rating?: number
  review_count?: number
  tags?: string[]
  is_published?: boolean
  created_by: string
  created_at: string
  updated_at: string
}

export interface RouteStopDto {
  id: string
  order: number
  name: string
  type: StopType
  description: string
  location: RouteLocationDto
  duration: number
  winery?: RouteWineryDto
}

export interface RouteLocationDto {
  address: string
  city: string
  latitude: number
  longitude: number
}

export interface RouteWineryDto {
  id: string
  name: string
  wines?: string[]
}

export interface WineRouteReviewDto {
  id: string
  route_id: string
  user_id: string
  user_name: string
  rating: number
  comment: string
  visited_at: string
  created_at: string
}

export interface CreateWineRouteRequestDto {
  name: string
  description: string
  region: string
  country: string
  difficulty: RouteDifficulty
  duration: number
  distance: number
  image_url?: string
  tags?: string[]
}

export interface AddRouteStopRequestDto {
  name: string
  type: StopType
  description: string
  location: RouteLocationDto
  duration: number
  winery_id?: string
}

export interface CreateWineRouteReviewRequestDto {
  rating: number
  comment?: string
  visited_at: string
}

export interface WineRouteFilterParamsDto {
  search?: string
  region?: string | string[]
  difficulty?: string | string[]
  min_duration?: number
  max_duration?: number
  min_distance?: number
  max_distance?: number
  min_rating?: number
  sort_by?: string
  sort_order?: 'asc' | 'desc'
  page?: number
  page_size?: number
}


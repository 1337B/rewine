import type { WineType } from '@domain/wine/wine.types'

export interface WineDto {
  id: string
  name: string
  winery: string
  type: WineType
  region: string
  country: string
  grape_varieties?: string[]
  vintage?: number
  alcohol_content?: number
  price?: number
  rating?: number
  review_count?: number
  description?: string
  image_url?: string
  tasting_notes?: TastingNotesDto
  food_pairings?: string[]
  created_at: string
  updated_at: string
}

export interface TastingNotesDto {
  appearance: string
  aroma: string[]
  palate: string[]
  finish: string
  body: 'light' | 'medium' | 'full'
  sweetness: 'dry' | 'off-dry' | 'medium-sweet' | 'sweet'
  tannins: 'low' | 'medium' | 'high'
  acidity: 'low' | 'medium' | 'high'
}

export interface WineReviewDto {
  id: string
  wine_id: string
  user_id: string
  user_name: string
  rating: number
  comment: string
  created_at: string
}

export interface CreateWineRequestDto {
  name: string
  winery: string
  type: WineType
  region: string
  country: string
  grape_varieties?: string[]
  vintage?: number
  alcohol_content?: number
  price?: number
  description?: string
  image_url?: string
  food_pairings?: string[]
}

export interface CreateWineReviewRequestDto {
  rating: number
  comment?: string
}

export interface WineFilterParamsDto {
  search?: string
  type?: string | string[]
  region?: string | string[]
  grape_variety?: string | string[]
  min_price?: number
  max_price?: number
  min_rating?: number
  vintage?: number | number[]
  sort_by?: string
  sort_order?: 'asc' | 'desc'
  page?: number
  page_size?: number
}


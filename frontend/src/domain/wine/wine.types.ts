/**
 * Wine domain types
 */

export interface Wine {
  id: string
  name: string
  winery: string
  type: WineType
  region: string
  country: string
  grapeVarieties: string[]
  vintage: number | null
  alcoholContent: number | null
  price: number | null
  rating: number | null
  reviewCount: number
  description: string
  imageUrl: string | null
  tastingNotes: TastingNotes | null
  foodPairings: string[]
  createdAt: Date
  updatedAt: Date
}

export type WineType = 'red' | 'white' | 'rose' | 'sparkling' | 'dessert' | 'fortified'

export interface TastingNotes {
  appearance: string
  aroma: string[]
  palate: string[]
  finish: string
  body: 'light' | 'medium' | 'full'
  sweetness: 'dry' | 'off-dry' | 'medium-sweet' | 'sweet'
  tannins: 'low' | 'medium' | 'high'
  acidity: 'low' | 'medium' | 'high'
}

export interface WineReview {
  id: string
  wineId: string
  userId: string
  userName: string
  rating: number
  comment: string
  createdAt: Date
}

export interface WineFilter {
  search?: string
  type?: WineType | WineType[]
  region?: string | string[]
  grapeVariety?: string | string[]
  minPrice?: number
  maxPrice?: number
  minRating?: number
  vintage?: number | number[]
  sortBy?: 'name' | 'price' | 'rating' | 'vintage'
  sortOrder?: 'asc' | 'desc'
}

export interface WineComparison {
  wines: Wine[]
  attributes: ComparisonAttribute[]
}

export interface ComparisonAttribute {
  name: string
  values: (string | number | null)[]
}


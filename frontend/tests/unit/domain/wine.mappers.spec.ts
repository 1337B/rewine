/**
 * Wine Mappers Unit Tests
 *
 * Tests for DTO to domain model mapping functions.
 */

import { describe, it, expect } from 'vitest'
import {
  mapWineFromDto,
  mapWineSummaryFromDto,
  mapTastingNotesFromDto,
  mapWineReviewFromDto,
} from '@domain/wine/wine.mappers'
import type { WineDto, WineSummaryDto, TastingNotesDto, WineReviewDto } from '@api/dto/wines.dto'

describe('Wine Mappers', () => {
  describe('mapWineFromDto', () => {
    it('should map complete wine DTO to domain model', () => {
      const dto: WineDto = {
        id: 'wine-001',
        name: 'Test Malbec',
        winery: 'Test Winery',
        type: 'red',
        region: 'Mendoza',
        country: 'Argentina',
        grape_varieties: ['Malbec'],
        vintage: 2020,
        alcohol_content: 14.5,
        price: 45.99,
        rating: 4.7,
        review_count: 342,
        description: 'A bold red wine',
        image_url: '/images/wine.jpg',
        food_pairings: ['Steak', 'Lamb'],
        created_at: '2024-01-15T10:00:00Z',
        updated_at: '2024-06-20T15:30:00Z',
      }

      const result = mapWineFromDto(dto)

      expect(result.id).toBe('wine-001')
      expect(result.name).toBe('Test Malbec')
      expect(result.winery).toBe('Test Winery')
      expect(result.type).toBe('red')
      expect(result.region).toBe('Mendoza')
      expect(result.country).toBe('Argentina')
      expect(result.grapeVarieties).toEqual(['Malbec'])
      expect(result.vintage).toBe(2020)
      expect(result.alcoholContent).toBe(14.5)
      expect(result.price).toBe(45.99)
      expect(result.rating).toBe(4.7)
      expect(result.reviewCount).toBe(342)
      expect(result.description).toBe('A bold red wine')
      expect(result.imageUrl).toBe('/images/wine.jpg')
      expect(result.foodPairings).toEqual(['Steak', 'Lamb'])
      expect(result.createdAt).toBeInstanceOf(Date)
      expect(result.updatedAt).toBeInstanceOf(Date)
    })

    it('should handle null optional fields with defaults', () => {
      // Create a minimal DTO to test null handling
      const dto = {
        id: 'wine-002',
        name: 'Basic Wine',
        winery: 'Basic Winery',
        type: 'white',
        region: 'Unknown',
        country: 'Argentina',
        grape_varieties: undefined,
        vintage: null,
        alcohol_content: null,
        price: null,
        rating: null,
        review_count: null,
        description: undefined,
        image_url: null,
        food_pairings: null,
        created_at: '2024-01-15T10:00:00Z',
        updated_at: '2024-06-20T15:30:00Z',
      } as unknown as WineDto

      const result = mapWineFromDto(dto)

      expect(result.id).toBe('wine-002')
      expect(result.grapeVarieties).toEqual([])
      expect(result.vintage).toBeNull()
      expect(result.alcoholContent).toBeNull()
      expect(result.price).toBeNull()
      expect(result.rating).toBeNull()
      expect(result.reviewCount).toBe(0)
      expect(result.description).toBe('')
      expect(result.imageUrl).toBeNull()
      expect(result.foodPairings).toEqual([])
    })
  })

  describe('mapWineSummaryFromDto', () => {
    it('should map wine summary DTO to domain model', () => {
      const dto: WineSummaryDto = {
        id: 'wine-001',
        name: 'Test Wine',
        winery: 'Test Winery',
        type: 'red',
        region: 'Mendoza',
        country: 'Argentina',
        vintage: 2020,
        price: 29.99,
        rating: 4.5,
        review_count: 100,
        image_url: '/images/wine.jpg',
      }

      const result = mapWineSummaryFromDto(dto)

      expect(result.id).toBe('wine-001')
      expect(result.name).toBe('Test Wine')
      expect(result.winery).toBe('Test Winery')
      expect(result.type).toBe('red')
      expect(result.region).toBe('Mendoza')
      expect(result.country).toBe('Argentina')
      expect(result.vintage).toBe(2020)
      expect(result.price).toBe(29.99)
      expect(result.rating).toBe(4.5)
      expect(result.reviewCount).toBe(100)
      expect(result.imageUrl).toBe('/images/wine.jpg')
    })

    it('should handle missing optional fields', () => {
      const dto = {
        id: 'wine-002',
        name: 'Basic Wine',
        winery: 'Basic Winery',
        type: 'white',
        region: 'Unknown',
        country: 'Argentina',
        vintage: null,
        price: null,
        rating: null,
        review_count: null,
        image_url: null,
      } as unknown as WineSummaryDto

      const result = mapWineSummaryFromDto(dto)

      expect(result.vintage).toBeNull()
      expect(result.price).toBeNull()
      expect(result.rating).toBeNull()
      expect(result.reviewCount).toBe(0)
      expect(result.imageUrl).toBeNull()
    })
  })

  describe('mapTastingNotesFromDto', () => {
    it('should map tasting notes DTO correctly', () => {
      const dto: TastingNotesDto = {
        appearance: 'Deep ruby',
        aroma: ['blackberry', 'vanilla', 'oak'],
        palate: ['full-bodied', 'smooth'],
        finish: 'Long and elegant',
        body: 'Full',
        sweetness: 'Dry',
        tannins: 'Medium-high',
        acidity: 'Medium',
      }

      const result = mapTastingNotesFromDto(dto)

      expect(result.appearance).toBe('Deep ruby')
      expect(result.aroma).toEqual(['blackberry', 'vanilla', 'oak'])
      expect(result.palate).toEqual(['full-bodied', 'smooth'])
      expect(result.finish).toBe('Long and elegant')
      expect(result.body).toBe('Full')
      expect(result.sweetness).toBe('Dry')
      expect(result.tannins).toBe('Medium-high')
      expect(result.acidity).toBe('Medium')
    })

    it('should handle null arrays with defaults', () => {
      const dto = {
        appearance: 'Light gold',
        aroma: undefined,
        palate: undefined,
        finish: 'Crisp',
        body: 'Light',
        sweetness: 'Off-dry',
        tannins: 'Low',
        acidity: 'High',
      } as unknown as TastingNotesDto

      const result = mapTastingNotesFromDto(dto)

      expect(result.aroma).toEqual([])
      expect(result.palate).toEqual([])
    })
  })

  describe('mapWineReviewFromDto', () => {
    it('should map review DTO correctly', () => {
      const dto: WineReviewDto = {
        id: 'review-001',
        wine_id: 'wine-001',
        user_id: 'user-001',
        user_name: 'John Doe',
        user_avatar: '/images/user.jpg',
        rating: 5,
        comment: 'Excellent wine!',
        helpful_count: 24,
        created_at: '2024-06-15T18:30:00Z',
      }

      const result = mapWineReviewFromDto(dto)

      expect(result.id).toBe('review-001')
      expect(result.wineId).toBe('wine-001')
      expect(result.userId).toBe('user-001')
      expect(result.userName).toBe('John Doe')
      expect(result.userAvatar).toBe('/images/user.jpg')
      expect(result.rating).toBe(5)
      expect(result.comment).toBe('Excellent wine!')
      expect(result.helpfulCount).toBe(24)
      expect(result.createdAt).toBeInstanceOf(Date)
    })

    it('should handle null avatar', () => {
      const dto: WineReviewDto = {
        id: 'review-002',
        wine_id: 'wine-001',
        user_id: 'user-002',
        user_name: 'Jane Doe',
        user_avatar: null,
        rating: 4,
        comment: 'Good wine',
        helpful_count: 0,
        created_at: '2024-06-15T18:30:00Z',
      }

      const result = mapWineReviewFromDto(dto)

      expect(result.userAvatar).toBeNull()
      expect(result.helpfulCount).toBe(0)
    })
  })
})


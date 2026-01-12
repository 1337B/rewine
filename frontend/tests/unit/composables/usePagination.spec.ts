/**
 * usePagination Composable Unit Tests
 *
 * Tests for pagination state management and methods.
 */

import { describe, it, expect } from 'vitest'
import { usePagination } from '@composables/usePagination'

describe('usePagination', () => {
  describe('initial state', () => {
    it('should use default options', () => {
      const pagination = usePagination()

      expect(pagination.page.value).toBe(1)
      expect(pagination.pageSize.value).toBe(20)
      expect(pagination.totalItems.value).toBe(0)
      expect(pagination.totalPages.value).toBe(0)
    })

    it('should use custom initial values', () => {
      const pagination = usePagination({
        initialPage: 2,
        initialPageSize: 50,
      })

      expect(pagination.page.value).toBe(2)
      expect(pagination.pageSize.value).toBe(50)
    })

    it('should provide page size options', () => {
      const pagination = usePagination({
        pageSizeOptions: [5, 10, 25],
      })

      expect(pagination.pageSizeOptions).toEqual([5, 10, 25])
    })
  })

  describe('computed values', () => {
    it('should calculate totalPages correctly', () => {
      const pagination = usePagination()
      pagination.pageSize.value = 10
      pagination.setTotalItems(45)

      expect(pagination.totalPages.value).toBe(5)
    })

    it('should return 0 totalPages when no items', () => {
      const pagination = usePagination()

      expect(pagination.totalPages.value).toBe(0)
    })

    it('should calculate hasNextPage correctly', () => {
      const pagination = usePagination()
      pagination.pageSize.value = 10
      pagination.setTotalItems(25)
      pagination.page.value = 1

      expect(pagination.hasNextPage.value).toBe(true)

      pagination.page.value = 3
      expect(pagination.hasNextPage.value).toBe(false)
    })

    it('should calculate hasPreviousPage correctly', () => {
      const pagination = usePagination()
      pagination.page.value = 1

      expect(pagination.hasPreviousPage.value).toBe(false)

      pagination.page.value = 2
      expect(pagination.hasPreviousPage.value).toBe(true)
    })

    it('should calculate startItem and endItem correctly', () => {
      const pagination = usePagination()
      pagination.pageSize.value = 10
      pagination.setTotalItems(45)
      pagination.page.value = 2

      expect(pagination.startItem.value).toBe(11)
      expect(pagination.endItem.value).toBe(20)
    })

    it('should handle last page with partial items', () => {
      const pagination = usePagination()
      pagination.pageSize.value = 10
      pagination.setTotalItems(45)
      pagination.page.value = 5

      expect(pagination.startItem.value).toBe(41)
      expect(pagination.endItem.value).toBe(45)
    })

    it('should return 0 for startItem when no items', () => {
      const pagination = usePagination()

      expect(pagination.startItem.value).toBe(0)
    })
  })

  describe('navigation methods', () => {
    it('should go to next page', () => {
      const pagination = usePagination()
      pagination.pageSize.value = 10
      pagination.setTotalItems(30)
      pagination.page.value = 1

      pagination.nextPage()
      expect(pagination.page.value).toBe(2)
    })

    it('should not go beyond last page', () => {
      const pagination = usePagination()
      pagination.pageSize.value = 10
      pagination.setTotalItems(20)
      pagination.page.value = 2

      pagination.nextPage()
      expect(pagination.page.value).toBe(2)
    })

    it('should go to previous page', () => {
      const pagination = usePagination()
      pagination.page.value = 3

      pagination.previousPage()
      expect(pagination.page.value).toBe(2)
    })

    it('should not go below first page', () => {
      const pagination = usePagination()
      pagination.page.value = 1

      pagination.previousPage()
      expect(pagination.page.value).toBe(1)
    })

    it('should go to specific page', () => {
      const pagination = usePagination()
      pagination.pageSize.value = 10
      pagination.setTotalItems(50)

      pagination.goToPage(3)
      expect(pagination.page.value).toBe(3)
    })

    it('should not go to invalid page', () => {
      const pagination = usePagination()
      pagination.pageSize.value = 10
      pagination.setTotalItems(30)
      pagination.page.value = 2

      pagination.goToPage(0)
      expect(pagination.page.value).toBe(2)

      pagination.goToPage(5)
      expect(pagination.page.value).toBe(2)
    })
  })

  describe('setPageSize', () => {
    it('should update page size', () => {
      const pagination = usePagination()

      pagination.setPageSize(50)
      expect(pagination.pageSize.value).toBe(50)
    })

    it('should reset to first page when changing page size', () => {
      const pagination = usePagination()
      pagination.page.value = 3

      pagination.setPageSize(50)
      expect(pagination.page.value).toBe(1)
    })
  })

  describe('setTotalItems', () => {
    it('should update total items', () => {
      const pagination = usePagination()

      pagination.setTotalItems(100)
      expect(pagination.totalItems.value).toBe(100)
    })

    it('should adjust page if current page exceeds new total', () => {
      const pagination = usePagination()
      pagination.pageSize.value = 10
      pagination.setTotalItems(50)
      pagination.page.value = 5

      // Now reduce total items
      pagination.setTotalItems(25)
      expect(pagination.page.value).toBe(3) // Adjusted to last valid page
    })
  })

  describe('reset', () => {
    it('should reset to initial state', () => {
      const pagination = usePagination({
        initialPage: 1,
        initialPageSize: 20,
      })

      pagination.page.value = 5
      pagination.pageSize.value = 50
      pagination.setTotalItems(200)

      pagination.reset()

      expect(pagination.page.value).toBe(1)
      expect(pagination.pageSize.value).toBe(20)
      expect(pagination.totalItems.value).toBe(0)
    })
  })
})


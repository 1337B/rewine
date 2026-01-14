import { ref, computed, watch } from 'vue'
import type { Ref } from 'vue'

export interface PaginationOptions {
  initialPage?: number
  initialPageSize?: number
  pageSizeOptions?: number[]
}

export interface PaginationState {
  page: Ref<number>
  pageSize: Ref<number>
  totalItems: Ref<number>
  totalPages: Ref<number>
}

/**
 * Pagination composable
 */
export function usePagination(options: PaginationOptions = {}) {
  const {
    initialPage = 1,
    initialPageSize = 20,
    pageSizeOptions = [10, 20, 50, 100],
  } = options

  const page = ref(initialPage)
  const pageSize = ref(initialPageSize)
  const totalItems = ref(0)

  const totalPages = computed(() =>
    totalItems.value > 0 ? Math.ceil(totalItems.value / pageSize.value) : 0
  )

  const hasNextPage = computed(() => page.value < totalPages.value)
  const hasPreviousPage = computed(() => page.value > 1)

  const startItem = computed(() =>
    totalItems.value > 0 ? (page.value - 1) * pageSize.value + 1 : 0
  )

  const endItem = computed(() =>
    Math.min(page.value * pageSize.value, totalItems.value)
  )

  function nextPage() {
    if (hasNextPage.value) {
      page.value++
    }
  }

  function previousPage() {
    if (hasPreviousPage.value) {
      page.value--
    }
  }

  function goToPage(newPage: number) {
    if (newPage >= 1 && newPage <= totalPages.value) {
      page.value = newPage
    }
  }

  function setPageSize(newPageSize: number) {
    pageSize.value = newPageSize
    page.value = 1 // Reset to first page when changing page size
  }

  function setTotalItems(total: number) {
    totalItems.value = total
    // Ensure current page is valid
    if (page.value > totalPages.value && totalPages.value > 0) {
      page.value = totalPages.value
    }
  }

  function reset() {
    page.value = initialPage
    pageSize.value = initialPageSize
    totalItems.value = 0
  }

  // Watch for page size changes
  watch(pageSize, () => {
    // Recalculate current page to stay on similar items
    const firstItemIndex = (page.value - 1) * pageSize.value
    page.value = Math.floor(firstItemIndex / pageSize.value) + 1
  })

  return {
    // State
    page,
    pageSize,
    totalItems,
    totalPages,
    pageSizeOptions,
    // Computed
    hasNextPage,
    hasPreviousPage,
    startItem,
    endItem,
    // Methods
    nextPage,
    previousPage,
    goToPage,
    setPageSize,
    setTotalItems,
    reset,
  }
}

export default usePagination


/**
 * useDebounce Composable Unit Tests
 *
 * Tests for debounce functionality.
 */

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { ref, nextTick } from 'vue'
import { useDebounce, useDebounceFn } from '@composables/useDebounce'

describe('useDebounce', () => {
  beforeEach(() => {
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('useDebounce (value)', () => {
    it('should return initial value immediately', () => {
      const source = ref('initial')
      const debounced = useDebounce(source, 300)

      expect(debounced.value).toBe('initial')
    })

    it('should debounce value changes', async () => {
      const source = ref('initial')
      const debounced = useDebounce(source, 300)

      source.value = 'changed'
      await nextTick()

      // Value should not change immediately
      expect(debounced.value).toBe('initial')

      // Advance time
      vi.advanceTimersByTime(300)
      await nextTick()

      expect(debounced.value).toBe('changed')
    })

    it('should cancel previous timeout on rapid changes', async () => {
      const source = ref('initial')
      const debounced = useDebounce(source, 300)

      source.value = 'first'
      await nextTick()
      vi.advanceTimersByTime(100)

      source.value = 'second'
      await nextTick()
      vi.advanceTimersByTime(100)

      source.value = 'third'
      await nextTick()

      // Should still be initial
      expect(debounced.value).toBe('initial')

      // Advance full delay
      vi.advanceTimersByTime(300)
      await nextTick()

      // Should be last value
      expect(debounced.value).toBe('third')
    })

    it('should work with different types', async () => {
      const source = ref(0)
      const debounced = useDebounce(source, 100)

      source.value = 42
      await nextTick()
      vi.advanceTimersByTime(100)
      await nextTick()

      expect(debounced.value).toBe(42)
    })

    it('should work with custom delay', async () => {
      const source = ref('test')
      const debounced = useDebounce(source, 500)

      source.value = 'updated'
      await nextTick()

      vi.advanceTimersByTime(400)
      await nextTick()
      expect(debounced.value).toBe('test')

      vi.advanceTimersByTime(100)
      await nextTick()
      expect(debounced.value).toBe('updated')
    })
  })

  describe('useDebounceFn', () => {
    it('should debounce function calls', async () => {
      const callback = vi.fn()
      const { debounced } = useDebounceFn(callback, 300)

      debounced('arg1')
      debounced('arg2')
      debounced('arg3')

      // Should not have been called yet
      expect(callback).not.toHaveBeenCalled()

      vi.advanceTimersByTime(300)

      // Should be called once with last args
      expect(callback).toHaveBeenCalledTimes(1)
      expect(callback).toHaveBeenCalledWith('arg3')
    })

    it('should pass all arguments', async () => {
      const callback = vi.fn()
      const { debounced } = useDebounceFn(callback, 100)

      debounced('a', 'b', 'c')
      vi.advanceTimersByTime(100)

      expect(callback).toHaveBeenCalledWith('a', 'b', 'c')
    })

    it('should cancel pending calls', async () => {
      const callback = vi.fn()
      const { debounced, cancel } = useDebounceFn(callback, 300)

      debounced('test')
      cancel()
      vi.advanceTimersByTime(300)

      expect(callback).not.toHaveBeenCalled()
    })

    it('should allow new calls after cancel', async () => {
      const callback = vi.fn()
      const { debounced, cancel } = useDebounceFn(callback, 100)

      debounced('first')
      cancel()
      vi.advanceTimersByTime(100)

      debounced('second')
      vi.advanceTimersByTime(100)

      expect(callback).toHaveBeenCalledTimes(1)
      expect(callback).toHaveBeenCalledWith('second')
    })

    it('should use default delay of 300ms', async () => {
      const callback = vi.fn()
      const { debounced } = useDebounceFn(callback)

      debounced()
      vi.advanceTimersByTime(299)
      expect(callback).not.toHaveBeenCalled()

      vi.advanceTimersByTime(1)
      expect(callback).toHaveBeenCalled()
    })
  })
})


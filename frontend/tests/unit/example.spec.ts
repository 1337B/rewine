/**
 * Basic sanity tests
 *
 * Ensures test infrastructure works correctly.
 */

import { describe, it, expect } from 'vitest'

describe('Test Infrastructure', () => {
  it('should run vitest correctly', () => {
    expect(1 + 1).toBe(2)
  })

  it('should support async tests', async () => {
    const result = await Promise.resolve('success')
    expect(result).toBe('success')
  })
})

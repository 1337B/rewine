/**
 * Validation Utilities Unit Tests
 *
 * Tests for Zod integration helpers and validation utilities.
 */

import { describe, it, expect } from 'vitest'
import { z } from 'zod'
import {
  validateWithSchema,
  formatZodErrors,
  trimFormData,
  isValidEmail,
  isNonEmpty,
} from '@utils/validation'

describe('Validation Utilities', () => {
  describe('validateWithSchema', () => {
    const testSchema = z.object({
      email: z.string().email('Invalid email'),
      name: z.string().min(1, 'Name required'),
      age: z.number().min(18, 'Must be 18+'),
    })

    it('should return success for valid data', () => {
      const validData = {
        email: 'test@example.com',
        name: 'John Doe',
        age: 25,
      }

      const result = validateWithSchema(testSchema, validData)

      expect(result.success).toBe(true)
      expect(result.data).toEqual(validData)
      expect(result.errors).toEqual({})
    })

    it('should return field errors for invalid data', () => {
      const invalidData = {
        email: 'invalid-email',
        name: '',
        age: 15,
      }

      const result = validateWithSchema(testSchema, invalidData)

      expect(result.success).toBe(false)
      expect(result.data).toBeUndefined()
      expect(result.errors.email).toBe('Invalid email')
      expect(result.errors.name).toBe('Name required')
      expect(result.errors.age).toBe('Must be 18+')
    })

    it('should return first error per field only', () => {
      const schemaWithMultipleErrors = z.object({
        password: z.string()
          .min(8, 'Too short')
          .regex(/[A-Z]/, 'Need uppercase'),
      })

      const result = validateWithSchema(schemaWithMultipleErrors, { password: 'a' })

      expect(result.success).toBe(false)
      // Should only have first error
      expect(result.errors.password).toBe('Too short')
    })
  })

  describe('formatZodErrors', () => {
    it('should format nested path correctly', () => {
      const schema = z.object({
        user: z.object({
          profile: z.object({
            age: z.number().min(18, 'Must be 18+'),
          }),
        }),
      })

      const result = schema.safeParse({ user: { profile: { age: 10 } } })
      expect(result.success).toBe(false)

      if (!result.success) {
        const errors = formatZodErrors(result.error)
        expect(errors['user.profile.age']).toBe('Must be 18+')
      }
    })

    it('should handle array paths', () => {
      const schema = z.object({
        items: z.array(z.string().min(1, 'Item required')),
      })

      const result = schema.safeParse({ items: ['valid', ''] })
      expect(result.success).toBe(false)

      if (!result.success) {
        const errors = formatZodErrors(result.error)
        expect(errors['items.1']).toBe('Item required')
      }
    })
  })

  describe('trimFormData', () => {
    it('should trim string values', () => {
      const data = {
        name: '  John Doe  ',
        email: ' test@example.com ',
      }

      const result = trimFormData(data)

      expect(result.name).toBe('John Doe')
      expect(result.email).toBe('test@example.com')
    })

    it('should preserve non-string values', () => {
      const data = {
        name: '  John  ',
        age: 25,
        active: true,
        metadata: null,
      }

      const result = trimFormData(data)

      expect(result.name).toBe('John')
      expect(result.age).toBe(25)
      expect(result.active).toBe(true)
      expect(result.metadata).toBeNull()
    })

    it('should handle empty object', () => {
      const result = trimFormData({})
      expect(result).toEqual({})
    })
  })

  describe('isValidEmail', () => {
    it('should return true for valid emails', () => {
      const validEmails = [
        'test@example.com',
        'user.name@domain.org',
        'user+tag@example.co.uk',
      ]

      validEmails.forEach((email) => {
        expect(isValidEmail(email)).toBe(true)
      })
    })

    it('should return false for invalid emails', () => {
      const invalidEmails = [
        'invalid',
        '@example.com',
        'user@',
        'user@.com',
        '',
      ]

      invalidEmails.forEach((email) => {
        expect(isValidEmail(email)).toBe(false)
      })
    })
  })

  describe('isNonEmpty', () => {
    it('should return true for non-empty strings', () => {
      expect(isNonEmpty('hello')).toBe(true)
      expect(isNonEmpty('  text  ')).toBe(true)
    })

    it('should return false for empty or whitespace strings', () => {
      expect(isNonEmpty('')).toBe(false)
      expect(isNonEmpty('   ')).toBe(false)
    })

    it('should return false for null/undefined', () => {
      expect(isNonEmpty(null)).toBe(false)
      expect(isNonEmpty(undefined)).toBe(false)
    })
  })
})


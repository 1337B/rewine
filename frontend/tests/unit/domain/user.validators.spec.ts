/**
 * User Validators Unit Tests
 *
 * Tests for Zod schemas and validation logic.
 */

import { describe, it, expect } from 'vitest'
import {
  loginUserSchema,
  registerUserSchema,
  userRoleSchema,
  changePasswordSchema,
} from '@domain/user/user.validators'

describe('User Validators', () => {
  describe('loginUserSchema', () => {
    it('should validate correct login data', () => {
      const validData = {
        email: 'test@example.com',
        password: 'password123',
      }

      const result = loginUserSchema.safeParse(validData)
      expect(result.success).toBe(true)
      if (result.success) {
        expect(result.data.email).toBe(validData.email)
        expect(result.data.password).toBe(validData.password)
      }
    })

    it('should reject invalid email', () => {
      const invalidData = {
        email: 'invalid-email',
        password: 'password123',
      }

      const result = loginUserSchema.safeParse(invalidData)
      expect(result.success).toBe(false)
      if (!result.success) {
        const emailError = result.error.issues.find(i => i.path[0] === 'email')
        expect(emailError).toBeDefined()
        expect(emailError?.message).toBe('Invalid email address')
      }
    })

    it('should reject empty password', () => {
      const invalidData = {
        email: 'test@example.com',
        password: '',
      }

      const result = loginUserSchema.safeParse(invalidData)
      expect(result.success).toBe(false)
      if (!result.success) {
        const passwordError = result.error.issues.find(i => i.path[0] === 'password')
        expect(passwordError).toBeDefined()
        expect(passwordError?.message).toBe('Password is required')
      }
    })

    it('should reject missing fields', () => {
      const result = loginUserSchema.safeParse({})
      expect(result.success).toBe(false)
      if (!result.success) {
        expect(result.error.issues.length).toBeGreaterThanOrEqual(2)
      }
    })
  })

  describe('registerUserSchema', () => {
    it('should validate correct registration data', () => {
      const validData = {
        email: 'newuser@example.com',
        password: 'SecurePass1',
        confirmPassword: 'SecurePass1',
        name: 'Test User',
      }

      const result = registerUserSchema.safeParse(validData)
      expect(result.success).toBe(true)
    })

    it('should reject weak password - no uppercase', () => {
      const invalidData = {
        email: 'newuser@example.com',
        password: 'weakpass1',
        confirmPassword: 'weakpass1',
        name: 'Test User',
      }

      const result = registerUserSchema.safeParse(invalidData)
      expect(result.success).toBe(false)
      if (!result.success) {
        const passwordError = result.error.issues.find(i => i.path[0] === 'password')
        expect(passwordError).toBeDefined()
        expect(passwordError?.message).toContain('uppercase')
      }
    })

    it('should reject weak password - no number', () => {
      const invalidData = {
        email: 'newuser@example.com',
        password: 'WeakPassword',
        confirmPassword: 'WeakPassword',
        name: 'Test User',
      }

      const result = registerUserSchema.safeParse(invalidData)
      expect(result.success).toBe(false)
      if (!result.success) {
        const passwordError = result.error.issues.find(i => i.path[0] === 'password')
        expect(passwordError).toBeDefined()
        expect(passwordError?.message).toContain('number')
      }
    })

    it('should reject short password', () => {
      const invalidData = {
        email: 'newuser@example.com',
        password: 'Pass1',
        confirmPassword: 'Pass1',
        name: 'Test User',
      }

      const result = registerUserSchema.safeParse(invalidData)
      expect(result.success).toBe(false)
      if (!result.success) {
        const passwordError = result.error.issues.find(i => i.path[0] === 'password')
        expect(passwordError).toBeDefined()
        expect(passwordError?.message).toContain('8 characters')
      }
    })

    it('should reject password mismatch', () => {
      const invalidData = {
        email: 'newuser@example.com',
        password: 'SecurePass1',
        confirmPassword: 'DifferentPass1',
        name: 'Test User',
      }

      const result = registerUserSchema.safeParse(invalidData)
      expect(result.success).toBe(false)
      if (!result.success) {
        const confirmError = result.error.issues.find(i => i.path[0] === 'confirmPassword')
        expect(confirmError).toBeDefined()
        expect(confirmError?.message).toBe('Passwords do not match')
      }
    })

    it('should reject empty name', () => {
      const invalidData = {
        email: 'newuser@example.com',
        password: 'SecurePass1',
        confirmPassword: 'SecurePass1',
        name: '',
      }

      const result = registerUserSchema.safeParse(invalidData)
      expect(result.success).toBe(false)
    })

    it('should reject too long name', () => {
      const invalidData = {
        email: 'newuser@example.com',
        password: 'SecurePass1',
        confirmPassword: 'SecurePass1',
        name: 'A'.repeat(101),
      }

      const result = registerUserSchema.safeParse(invalidData)
      expect(result.success).toBe(false)
    })
  })

  describe('userRoleSchema', () => {
    it('should validate valid roles', () => {
      const validRoles = ['ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_PARTNER', 'ROLE_USER']

      validRoles.forEach((role) => {
        const result = userRoleSchema.safeParse(role)
        expect(result.success).toBe(true)
      })
    })

    it('should reject invalid roles', () => {
      const result = userRoleSchema.safeParse('ROLE_SUPER_ADMIN')
      expect(result.success).toBe(false)
    })
  })

  describe('changePasswordSchema', () => {
    it('should validate correct password change data', () => {
      const validData = {
        currentPassword: 'CurrentPass1',
        newPassword: 'NewSecurePass1',
        confirmNewPassword: 'NewSecurePass1',
      }

      const result = changePasswordSchema.safeParse(validData)
      expect(result.success).toBe(true)
    })

    it('should reject when new passwords do not match', () => {
      const invalidData = {
        currentPassword: 'CurrentPass1',
        newPassword: 'NewSecurePass1',
        confirmNewPassword: 'DifferentPass1',
      }

      const result = changePasswordSchema.safeParse(invalidData)
      expect(result.success).toBe(false)
    })
  })
})


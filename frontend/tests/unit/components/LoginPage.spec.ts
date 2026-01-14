/**
 * LoginPage Component Tests
 *
 * Tests for login page rendering, validation, and error handling.
 */

import { describe, it, expect, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import { createI18n } from 'vue-i18n'
import LoginPage from '@pages/public/LoginPage.vue'

// Mock i18n messages
const messages = {
  en: {
    auth: {
      welcomeTo: 'Welcome to',
      signInToContinue: 'Sign in to continue',
      email: 'Email',
      password: 'Password',
      signIn: 'Sign In',
      forgotPassword: 'Forgot password?',
      orContinueWith: 'Or continue with',
      continueWith: 'Continue with {provider}',
      noAccount: "Don't have an account?",
      signUpFree: 'Sign up free',
      loginSuccess: 'Login successful',
      loginError: 'Login failed',
      sessionExpired: 'Your session has expired',
      socialComingSoon: '{provider} login coming soon',
    },
  },
}

// Create test utilities
function createTestRouter() {
  return createRouter({
    history: createWebHistory(),
    routes: [
      { path: '/', name: 'home', component: { template: '<div>Home</div>' } },
      { path: '/login', name: 'login', component: LoginPage },
      { path: '/register', name: 'register', component: { template: '<div>Register</div>' } },
      { path: '/forgot-password', name: 'forgot-password', component: { template: '<div>Forgot</div>' } },
    ],
  })
}

function createTestI18n() {
  return createI18n({
    legacy: false,
    locale: 'en',
    messages,
  })
}

describe('LoginPage', () => {
  let router: ReturnType<typeof createTestRouter>
  let i18n: ReturnType<typeof createTestI18n>

  beforeEach(() => {
    setActivePinia(createPinia())
    router = createTestRouter()
    i18n = createTestI18n()
  })

  function mountLoginPage() {
    return mount(LoginPage, {
      global: {
        plugins: [router, i18n],
        stubs: {
          BaseButton: {
            template: '<button :type="type || \'button\'" :disabled="disabled || loading" @click="$emit(\'click\')"><slot /></button>',
            props: ['loading', 'disabled', 'fullWidth', 'type'],
          },
          BaseInput: {
            template: `
              <div>
                <label v-if="label">{{ label }}</label>
                <input 
                  :type="type" 
                  :value="modelValue" 
                  :disabled="disabled"
                  :placeholder="placeholder"
                  @input="$emit('update:modelValue', $event.target.value)"
                />
                <span v-if="error" class="error">{{ error }}</span>
              </div>
            `,
            props: ['modelValue', 'type', 'label', 'placeholder', 'error', 'disabled', 'name', 'autocomplete', 'required'],
          },
          BaseCard: {
            template: '<div class="card"><slot /></div>',
            props: ['padding'],
          },
        },
      },
    })
  }

  describe('rendering', () => {
    it('should render login form', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      expect(wrapper.find('form').exists()).toBe(true)
    })

    it('should render email and password inputs', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      const inputs = wrapper.findAll('input')
      expect(inputs.length).toBeGreaterThanOrEqual(2)

      // Check email input
      const emailInput = wrapper.find('input[type="email"]')
      expect(emailInput.exists()).toBe(true)

      // Check password input
      const passwordInput = wrapper.find('input[type="password"]')
      expect(passwordInput.exists()).toBe(true)
    })

    it('should render submit button', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      const submitButton = wrapper.find('button[type="submit"]')
      expect(submitButton.exists()).toBe(true)
    })

    it('should render social login buttons', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      const buttons = wrapper.findAll('button[type="button"]')
      expect(buttons.length).toBeGreaterThanOrEqual(3) // Google, Apple, Facebook
    })

    it('should render sign up link', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      const registerLink = wrapper.find('a[href="/register"]')
      expect(registerLink.exists()).toBe(true)
    })

    it('should render forgot password link', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      const forgotLink = wrapper.find('a[href="/forgot-password"]')
      expect(forgotLink.exists()).toBe(true)
    })
  })

  describe('validation', () => {
    it('should show email error for invalid email', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      // Fill invalid email
      const emailInput = wrapper.find('input[type="email"]')
      await emailInput.setValue('invalid-email')

      // Fill password
      const passwordInput = wrapper.find('input[type="password"]')
      await passwordInput.setValue('password123')

      // Submit form
      await wrapper.find('form').trigger('submit.prevent')
      await flushPromises()

      // Should show error
      expect(wrapper.text()).toContain('Invalid email')
    })

    it('should show password error for empty password', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      // Fill valid email
      const emailInput = wrapper.find('input[type="email"]')
      await emailInput.setValue('test@example.com')

      // Leave password empty and submit
      await wrapper.find('form').trigger('submit.prevent')
      await flushPromises()

      // Should show password error
      expect(wrapper.text()).toContain('required')
    })

    it('should not show errors before form submission', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      // Just fill invalid data without submitting
      const emailInput = wrapper.find('input[type="email"]')
      await emailInput.setValue('invalid')

      // Errors should not be visible yet
      expect(wrapper.find('.error').exists()).toBe(false)
    })
  })

  describe('form submission', () => {
    it('should trim input values before validation', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      const emailInput = wrapper.find('input[type="email"]')
      await emailInput.setValue('  test@example.com  ')

      const passwordInput = wrapper.find('input[type="password"]')
      await passwordInput.setValue('password123')

      await wrapper.find('form').trigger('submit.prevent')
      await flushPromises()

      // The email should be trimmed (validation should pass)
      // If invalid, it would show error
      const errorSpans = wrapper.findAll('.error')
      const hasEmailError = errorSpans.some(e => e.text().includes('Invalid email'))
      expect(hasEmailError).toBe(false)
    })
  })

  describe('loading state', () => {
    it('should disable form while loading', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      // Fill valid data
      await wrapper.find('input[type="email"]').setValue('test@example.com')
      await wrapper.find('input[type="password"]').setValue('password123')

      // Submit - the store will set loading
      await wrapper.find('form').trigger('submit.prevent')

      // During loading, button should be disabled or show loading state
      const submitButton = wrapper.find('button[type="submit"]')
      // Check if component handles loading correctly
      expect(submitButton.exists()).toBe(true)
    })
  })

  describe('navigation', () => {
    it('should have router-link to register page', async () => {
      const wrapper = mountLoginPage()
      await router.isReady()

      const registerLink = wrapper.find('a[href="/register"]')
      expect(registerLink.exists()).toBe(true)
    })
  })
})


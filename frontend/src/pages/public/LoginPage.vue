<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAuth } from '@composables/useAuth'
import { useToast } from '@composables/useToast'
import BaseButton from '@components/common/BaseButton.vue'
import BaseInput from '@components/common/BaseInput.vue'
import BaseCard from '@components/common/BaseCard.vue'

const route = useRoute()
const { login, loading, error } = useAuth()
const toast = useToast()

const email = ref('')
const password = ref('')
const formError = ref('')

async function handleSubmit() {
  formError.value = ''

  if (!email.value || !password.value) {
    formError.value = 'Please fill in all fields'
    return
  }

  try {
    const redirect = (route.query.redirect as string) || '/'
    await login(email.value, password.value, redirect)
    toast.success('Welcome!')
  } catch {
    formError.value = error.value || 'Login failed'
    toast.error('Login failed')
  }
}

// Social login handlers (to be implemented with actual OAuth)
function loginWithGoogle() {
  // TODO: Implement Google OAuth
  toast.info('Google login coming soon!')
}

function loginWithApple() {
  // TODO: Implement Apple OAuth
  toast.info('Apple login coming soon!')
}

function loginWithFacebook() {
  // TODO: Implement Facebook OAuth
  toast.info('Facebook login coming soon!')
}
</script>

<template>
  <div class="min-h-[80vh] flex items-center justify-center px-4">
    <BaseCard padding="lg" class="w-full max-w-md">
      <!-- Logo & Header -->
      <div class="text-center mb-8">
        <router-link to="/" class="inline-block mb-4">
          <img src="/images/icons/reshot-icon-grapes-DH3FRP42X5.svg" alt="Rewine" class="w-16 h-16 mx-auto" />
        </router-link>
        <h1 class="text-2xl font-bold text-gray-900">Welcome to <span class="text-wine-700">rewine</span></h1>
        <p class="text-gray-600 mt-2">Sign in to continue your wine journey</p>
      </div>

      <!-- Email Login Form -->
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <BaseInput
          v-model="email"
          type="email"
          label="Email"
          placeholder="your@email.com"
          required
        />

        <BaseInput
          v-model="password"
          type="password"
          label="Password"
          placeholder="••••••••"
          required
        />

        <div class="flex items-center justify-end">
          <router-link to="/forgot-password" class="text-sm text-wine-600 hover:text-wine-700">
            Forgot password?
          </router-link>
        </div>

        <p v-if="formError" class="text-sm text-red-600">
          {{ formError }}
        </p>

        <BaseButton
          type="submit"
          :loading="loading"
          class="w-full"
        >
          Sign In
        </BaseButton>
      </form>

      <!-- Divider -->
      <div class="relative my-6">
        <div class="absolute inset-0 flex items-center">
          <div class="w-full border-t border-gray-200"></div>
        </div>
        <div class="relative flex justify-center text-sm">
          <span class="px-4 bg-white text-gray-500">or continue with</span>
        </div>
      </div>

      <!-- Social Login Buttons -->
      <div class="space-y-3">
        <button
          type="button"
          class="w-full flex items-center justify-center gap-3 px-4 py-3 bg-white border border-gray-300 rounded-xl text-gray-700 font-medium hover:bg-gray-50 transition-colors"
          @click="loginWithGoogle"
        >
          <svg class="w-5 h-5" viewBox="0 0 24 24">
            <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
            <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
            <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
            <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
          </svg>
          Continue with Google
        </button>

        <button
          type="button"
          class="w-full flex items-center justify-center gap-3 px-4 py-3 bg-black text-white rounded-xl font-medium hover:bg-gray-900 transition-colors"
          @click="loginWithApple"
        >
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M17.05 20.28c-.98.95-2.05.8-3.08.35-1.09-.46-2.09-.48-3.24 0-1.44.62-2.2.44-3.06-.35C2.79 15.25 3.51 7.59 9.05 7.31c1.35.07 2.29.74 3.08.8 1.18-.24 2.31-.93 3.57-.84 1.51.12 2.65.72 3.4 1.8-3.12 1.87-2.38 5.98.48 7.13-.57 1.5-1.31 2.99-2.54 4.09l.01-.01zM12.03 7.25c-.15-2.23 1.66-4.07 3.74-4.25.29 2.58-2.34 4.5-3.74 4.25z"/>
          </svg>
          Continue with Apple
        </button>

        <button
          type="button"
          class="w-full flex items-center justify-center gap-3 px-4 py-3 bg-[#1877F2] text-white rounded-xl font-medium hover:bg-[#166FE5] transition-colors"
          @click="loginWithFacebook"
        >
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
          </svg>
          Continue with Facebook
        </button>
      </div>

      <!-- Sign Up Link -->
      <div class="mt-6 text-center text-sm">
        <p class="text-gray-600">
          Don't have an account?
          <router-link to="/register" class="text-wine-600 hover:text-wine-700 font-medium">
            Sign up for free
          </router-link>
        </p>
      </div>
    </BaseCard>
  </div>
</template>


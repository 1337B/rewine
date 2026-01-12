<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@stores/auth.store'
import { useToast } from '@composables/useToast'
import BaseButton from '@components/common/BaseButton.vue'
import BaseInput from '@components/common/BaseInput.vue'
import BaseCard from '@components/common/BaseCard.vue'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

// Form state
const email = ref('')
const password = ref('')
const formSubmitted = ref(false)

// Computed from store
const loading = computed(() => authStore.loading)
const generalError = computed(() => authStore.error)

// Field-level errors
const emailError = computed(() => {
  if (!formSubmitted.value) return undefined
  if (!email.value) return t('auth.emailRequired')
  return authStore.getFieldError('email') ?? undefined
})

const passwordError = computed(() => {
  if (!formSubmitted.value) return undefined
  if (!password.value) return t('auth.passwordRequired')
  return authStore.getFieldError('password') ?? undefined
})

// Check for session expired on mount
onMounted(() => {
  if (authStore.isSessionExpired) {
    toast.warning(t('auth.sessionExpired'))
    authStore.clearErrors()
  }
})

async function handleSubmit() {
  formSubmitted.value = true
  authStore.clearErrors()

  // Basic client-side validation
  if (!email.value || !password.value) {
    return
  }

  try {
    await authStore.login(email.value, password.value)

    // Redirect to intended destination or home
    const returnUrl = (route.query.returnUrl as string) || '/'
    toast.success(t('auth.loginSuccess'))
    router.push(returnUrl)
  } catch {
    // Error is already handled in store
    toast.error(generalError.value || t('auth.loginError'))
  }
}

// Social login handlers
function loginWithGoogle() {
  toast.info(t('auth.socialComingSoon', { provider: 'Google' }))
}

function loginWithApple() {
  toast.info(t('auth.socialComingSoon', { provider: 'Apple' }))
}

function loginWithFacebook() {
  toast.info(t('auth.socialComingSoon', { provider: 'Facebook' }))
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
        <h1 class="text-2xl font-bold text-gray-900">
          {{ t('auth.welcomeTo') }} <span class="text-wine-700">rewine</span>
        </h1>
        <p class="text-gray-600 mt-2">{{ t('auth.signInToContinue') }}</p>
      </div>

      <!-- Email Login Form -->
      <form @submit.prevent="handleSubmit" class="space-y-4" novalidate>
        <div>
          <BaseInput
            v-model="email"
            type="email"
            :label="t('auth.email')"
            placeholder="your@email.com"
            :error="emailError"
            :disabled="loading"
            autocomplete="email"
            required
          />
        </div>

        <div>
          <BaseInput
            v-model="password"
            type="password"
            :label="t('auth.password')"
            placeholder="••••••••"
            :error="passwordError"
            :disabled="loading"
            autocomplete="current-password"
            required
          />
        </div>

        <div class="flex items-center justify-end">
          <router-link
            to="/forgot-password"
            class="text-sm text-wine-600 hover:text-wine-700"
            tabindex="-1"
          >
            {{ t('auth.forgotPassword') }}
          </router-link>
        </div>

        <!-- General error message -->
        <div
          v-if="generalError && !emailError && !passwordError"
          class="p-3 bg-red-50 border border-red-200 rounded-lg"
        >
          <p class="text-sm text-red-700 flex items-center gap-2">
            <svg class="w-4 h-4 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
            </svg>
            {{ generalError }}
          </p>
        </div>

        <BaseButton
          type="submit"
          :loading="loading"
          :disabled="loading"
          class="w-full"
        >
          {{ t('auth.signIn') }}
        </BaseButton>
      </form>

      <!-- Divider -->
      <div class="relative my-6">
        <div class="absolute inset-0 flex items-center">
          <div class="w-full border-t border-gray-200"></div>
        </div>
        <div class="relative flex justify-center text-sm">
          <span class="px-4 bg-white text-gray-500">{{ t('auth.orContinueWith') }}</span>
        </div>
      </div>

      <!-- Social Login Buttons -->
      <div class="space-y-3">
        <button
          type="button"
          :disabled="loading"
          class="w-full flex items-center justify-center gap-3 px-4 py-3 bg-white border border-gray-300 rounded-xl text-gray-700 font-medium hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          @click="loginWithGoogle"
        >
          <svg class="w-5 h-5" viewBox="0 0 24 24">
            <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
            <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
            <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
            <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
          </svg>
          {{ t('auth.continueWith', { provider: 'Google' }) }}
        </button>

        <button
          type="button"
          :disabled="loading"
          class="w-full flex items-center justify-center gap-3 px-4 py-3 bg-black text-white rounded-xl font-medium hover:bg-gray-900 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          @click="loginWithApple"
        >
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M17.05 20.28c-.98.95-2.05.8-3.08.35-1.09-.46-2.09-.48-3.24 0-1.44.62-2.2.44-3.06-.35C2.79 15.25 3.51 7.59 9.05 7.31c1.35.07 2.29.74 3.08.8 1.18-.24 2.31-.93 3.57-.84 1.51.12 2.65.72 3.4 1.8-3.12 1.87-2.38 5.98.48 7.13-.57 1.5-1.31 2.99-2.54 4.09l.01-.01zM12.03 7.25c-.15-2.23 1.66-4.07 3.74-4.25.29 2.58-2.34 4.5-3.74 4.25z"/>
          </svg>
          {{ t('auth.continueWith', { provider: 'Apple' }) }}
        </button>

        <button
          type="button"
          :disabled="loading"
          class="w-full flex items-center justify-center gap-3 px-4 py-3 bg-[#1877F2] text-white rounded-xl font-medium hover:bg-[#166FE5] transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          @click="loginWithFacebook"
        >
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
          </svg>
          {{ t('auth.continueWith', { provider: 'Facebook' }) }}
        </button>
      </div>

      <!-- Sign Up Link -->
      <div class="mt-6 text-center text-sm">
        <p class="text-gray-600">
          {{ t('auth.noAccount') }}
          <router-link to="/register" class="text-wine-600 hover:text-wine-700 font-medium">
            {{ t('auth.signUpFree') }}
          </router-link>
        </p>
      </div>
    </BaseCard>
  </div>
</template>


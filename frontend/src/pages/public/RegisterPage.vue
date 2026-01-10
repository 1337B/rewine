<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@composables/useAuth'
import { useToast } from '@composables/useToast'
import BaseButton from '@components/common/BaseButton.vue'
import BaseInput from '@components/common/BaseInput.vue'
import BaseCard from '@components/common/BaseCard.vue'

const router = useRouter()
const { register, loading, error } = useAuth()
const toast = useToast()

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const formError = ref('')

async function handleSubmit() {
  formError.value = ''

  if (!name.value || !email.value || !password.value || !confirmPassword.value) {
    formError.value = 'Please fill in all fields'
    return
  }

  if (password.value !== confirmPassword.value) {
    formError.value = 'Passwords do not match'
    return
  }

  if (password.value.length < 8) {
    formError.value = 'Password must be at least 8 characters'
    return
  }

  try {
    await register(email.value, password.value, name.value)
    toast.success('Account created successfully!')
    router.push('/')
  } catch {
    formError.value = error.value || 'Registration failed'
    toast.error('Registration failed')
  }
}
</script>

<template>
  <div class="min-h-[80vh] flex items-center justify-center">
    <BaseCard padding="lg" class="w-full max-w-md">
      <div class="text-center mb-8">
        <h1 class="text-2xl font-bold text-gray-900">Create an account</h1>
        <p class="text-gray-600 mt-2">Join the wine community</p>
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <BaseInput
          v-model="name"
          type="text"
          label="Name"
          placeholder="Your name"
          required
        />

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
          hint="At least 8 characters"
          required
        />

        <BaseInput
          v-model="confirmPassword"
          type="password"
          label="Confirm Password"
          placeholder="••••••••"
          required
        />

        <p v-if="formError" class="text-sm text-red-600">
          {{ formError }}
        </p>

        <BaseButton
          type="submit"
          :loading="loading"
          class="w-full"
        >
          Create Account
        </BaseButton>
      </form>

      <div class="mt-6 text-center text-sm">
        <p class="text-gray-600">
          Already have an account?
          <router-link to="/login" class="text-wine-600 hover:text-wine-700 font-medium">
            Sign in
          </router-link>
        </p>
      </div>
    </BaseCard>
  </div>
</template>


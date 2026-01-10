<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useEventsStore } from '@stores/events.store'
import { useAuth } from '@composables/useAuth'
import { useToast } from '@composables/useToast'
import BaseButton from '@components/common/BaseButton.vue'
import BaseCard from '@components/common/BaseCard.vue'
import BaseSpinner from '@components/common/BaseSpinner.vue'
import { formatDate, formatTime } from '@utils/date'

const route = useRoute()
const router = useRouter()
const eventsStore = useEventsStore()
const { isLoggedIn } = useAuth()
const toast = useToast()

const eventId = computed(() => route.params.id as string)
const event = computed(() => eventsStore.currentEvent)

onMounted(() => {
  if (eventId.value) {
    eventsStore.fetchEvent(eventId.value)
  }
})

function goBack() {
  router.back()
}

async function handleRegister() {
  if (!isLoggedIn.value) {
    router.push(`/login?redirect=${route.fullPath}`)
    return
  }

  try {
    await eventsStore.registerForEvent(eventId.value)
    toast.success('Successfully registered for event!')
  } catch {
    toast.error('Failed to register')
  }
}
</script>

<template>
  <div>
    <!-- Back button -->
    <button
      type="button"
      class="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-6"
      @click="goBack"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      Back
    </button>

    <!-- Loading -->
    <div v-if="eventsStore.loading" class="flex justify-center py-12">
      <BaseSpinner size="lg" />
    </div>

    <!-- Event Details -->
    <div v-else-if="event" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Main Content -->
      <div class="lg:col-span-2 space-y-6">
        <div class="aspect-video bg-gray-100 rounded-lg overflow-hidden">
          <img
            v-if="event.imageUrl"
            :src="event.imageUrl"
            :alt="event.title"
            class="w-full h-full object-cover"
          />
          <div v-else class="w-full h-full flex items-center justify-center bg-gray-50">
            <img src="/images/icons/reshot-icon-vine-tasting-P5GDV7FUBS.svg" alt="Event" class="w-24 h-24 opacity-40" />
          </div>
        </div>

        <div>
          <span class="px-3 py-1 bg-wine-100 text-wine-700 rounded-full text-sm capitalize">
            {{ event.type }}
          </span>
          <h1 class="text-3xl font-bold text-gray-900 mt-4">{{ event.title }}</h1>
        </div>

        <BaseCard>
          <h3 class="font-semibold text-gray-900 mb-2">About this event</h3>
          <p class="text-gray-600 whitespace-pre-line">{{ event.description }}</p>
        </BaseCard>

        <BaseCard>
          <h3 class="font-semibold text-gray-900 mb-4">Organizer</h3>
          <p class="text-gray-900">{{ event.organizer.name }}</p>
          <p class="text-sm text-gray-600">{{ event.organizer.email }}</p>
        </BaseCard>
      </div>

      <!-- Sidebar -->
      <div class="space-y-6">
        <BaseCard>
          <div class="space-y-4">
            <div>
              <p class="text-sm text-gray-500">Date & Time</p>
              <p class="font-semibold text-gray-900">{{ formatDate(event.startDate) }}</p>
              <p class="text-gray-600">{{ formatTime(event.startDate) }} - {{ formatTime(event.endDate) }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Location</p>
              <p class="font-semibold text-gray-900">{{ event.location.name }}</p>
              <p class="text-gray-600">{{ event.location.address }}</p>
              <p class="text-gray-600">{{ event.location.city }}, {{ event.location.region }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Price</p>
              <p class="text-2xl font-bold text-wine-600">
                {{ event.price ? `$${event.price}` : 'Free' }}
              </p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Attendees</p>
              <p class="text-gray-900">
                {{ event.currentAttendees }}{{ event.maxAttendees ? ` / ${event.maxAttendees}` : '' }}
              </p>
            </div>
          </div>

          <BaseButton class="w-full mt-6" @click="handleRegister">
            Register for Event
          </BaseButton>
        </BaseCard>

        <BaseCard v-if="event.tags?.length">
          <h3 class="font-semibold text-gray-900 mb-2">Tags</h3>
          <div class="flex flex-wrap gap-2">
            <span
              v-for="tag in event.tags"
              :key="tag"
              class="px-2 py-1 bg-gray-100 text-gray-700 rounded text-sm"
            >
              {{ tag }}
            </span>
          </div>
        </BaseCard>
      </div>
    </div>

    <!-- Not found -->
    <div v-else class="text-center py-12">
      <p class="text-gray-600">Event not found</p>
    </div>
  </div>
</template>


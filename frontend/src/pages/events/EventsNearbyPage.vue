<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useEventsStore } from '@stores/events.store'
import BaseCard from '@components/common/BaseCard.vue'
import BaseSpinner from '@components/common/BaseSpinner.vue'
import BaseEmptyState from '@components/common/BaseEmptyState.vue'
import { formatDate, formatTime } from '@utils/date'

const eventsStore = useEventsStore()
const locationError = ref('')

onMounted(async () => {
  // Try to get user location
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        eventsStore.fetchNearbyEvents(position.coords.latitude, position.coords.longitude)
      },
      () => {
        locationError.value = 'Unable to get location'
        eventsStore.fetchEvents()
      }
    )
  } else {
    eventsStore.fetchEvents()
  }
})
</script>

<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold text-gray-900">Events Nearby</h1>

    <p v-if="locationError" class="text-sm text-yellow-600">
      {{ locationError }}. Showing all events instead.
    </p>

    <!-- Loading -->
    <div v-if="eventsStore.loading" class="flex justify-center py-12">
      <BaseSpinner size="lg" />
    </div>

    <!-- Empty State -->
    <BaseEmptyState
      v-else-if="!eventsStore.hasEvents && !eventsStore.hasNearbyEvents"
      icon="calendar"
      title="No events found"
      description="Check back later for upcoming wine events"
    />

    <!-- Events List -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <router-link
        v-for="event in eventsStore.nearbyEvents.length ? eventsStore.nearbyEvents : eventsStore.events"
        :key="event.id"
        :to="`/events/${event.id}`"
      >
        <BaseCard hoverable padding="none" class="overflow-hidden">
          <div class="aspect-video bg-gray-100 relative">
            <img
              v-if="event.imageUrl"
              :src="event.imageUrl"
              :alt="event.title"
              class="w-full h-full object-cover"
            />
            <div v-else class="w-full h-full flex items-center justify-center bg-gray-50">
              <img src="/images/icons/reshot-icon-vine-tasting-P5GDV7FUBS.svg" alt="Event" class="w-16 h-16 opacity-40" />
            </div>
            <span class="absolute top-2 right-2 px-2 py-1 bg-wine-600 text-white text-xs rounded capitalize">
              {{ event.type }}
            </span>
          </div>
          <div class="p-4">
            <h3 class="font-semibold text-gray-900 truncate">{{ event.title }}</h3>
            <p class="text-sm text-gray-600 mt-1">
              {{ formatDate(event.startDate) }} · {{ formatTime(event.startDate) }}
            </p>
            <p class="text-sm text-gray-500 mt-1 truncate">
              📍 {{ event.location.city }}
            </p>
            <div class="flex items-center justify-between mt-3">
              <span class="text-wine-600 font-semibold">
                {{ event.price ? `$${event.price}` : 'Free' }}
              </span>
              <span class="text-sm text-gray-500">
                {{ event.currentAttendees }}{{ event.maxAttendees ? `/${event.maxAttendees}` : '' }} attending
              </span>
            </div>
          </div>
        </BaseCard>
      </router-link>
    </div>
  </div>
</template>


import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { eventsService } from '@services/events.service'
import type { Event, EventFilter } from '@domain/event/event.types'
import type { PaginationMeta } from '@api/api.types'

export const useEventsStore = defineStore('events', () => {
  // State
  const events = ref<Event[]>([])
  const nearbyEvents = ref<Event[]>([])
  const currentEvent = ref<Event | null>(null)
  const pagination = ref<PaginationMeta | null>(null)
  const filter = ref<EventFilter>({})
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const hasEvents = computed(() => events.value.length > 0)
  const hasNearbyEvents = computed(() => nearbyEvents.value.length > 0)
  const upcomingEvents = computed(() =>
    events.value.filter((e) => e.startDate > new Date()).sort((a, b) => a.startDate.getTime() - b.startDate.getTime())
  )

  // Actions
  async function fetchEvents(page = 1, pageSize = 20) {
    loading.value = true
    error.value = null

    try {
      const result = await eventsService.getEvents(filter.value, page, pageSize)
      events.value = result.events
      pagination.value = result.pagination
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch events'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchEvent(id: string) {
    loading.value = true
    error.value = null

    try {
      currentEvent.value = await eventsService.getEvent(id)
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch event'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchNearbyEvents(latitude: number, longitude: number, radiusKm = 50) {
    loading.value = true
    error.value = null

    try {
      nearbyEvents.value = await eventsService.getNearbyEvents(latitude, longitude, radiusKm)
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch nearby events'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function registerForEvent(eventId: string) {
    try {
      await eventsService.registerForEvent(eventId)
      // Refresh current event to update attendee count
      if (currentEvent.value?.id === eventId) {
        await fetchEvent(eventId)
      }
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to register for event'
      throw err
    }
  }

  async function cancelRegistration(eventId: string) {
    try {
      await eventsService.cancelRegistration(eventId)
      // Refresh current event to update attendee count
      if (currentEvent.value?.id === eventId) {
        await fetchEvent(eventId)
      }
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to cancel registration'
      throw err
    }
  }

  function setFilter(newFilter: EventFilter) {
    filter.value = newFilter
  }

  function clearFilter() {
    filter.value = {}
  }

  function reset() {
    events.value = []
    nearbyEvents.value = []
    currentEvent.value = null
    pagination.value = null
    filter.value = {}
    error.value = null
  }

  return {
    // State
    events,
    nearbyEvents,
    currentEvent,
    pagination,
    filter,
    loading,
    error,
    // Getters
    hasEvents,
    hasNearbyEvents,
    upcomingEvents,
    // Actions
    fetchEvents,
    fetchEvent,
    fetchNearbyEvents,
    registerForEvent,
    cancelRegistration,
    setFilter,
    clearFilter,
    reset,
  }
})


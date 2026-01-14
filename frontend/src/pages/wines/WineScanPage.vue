<script setup lang="ts">
/**
 * WineScanPage - Scan wine labels to identify wines
 *
 * Features:
 * - Camera or file upload
 * - Scanning animation
 * - Result display with edit capability
 * - Navigation to wine details
 */

import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import BaseCard from '@components/common/BaseCard.vue'
import BaseButton from '@components/common/BaseButton.vue'
import BaseInput from '@components/common/BaseInput.vue'
import { winesService } from '@services/wines.service'
import type { WineSummary } from '@domain/wine/wine.types'

const router = useRouter()
const { t } = useI18n()

// State types
type ScanState = 'idle' | 'scanning' | 'detected' | 'notFound' | 'error'

// State
const state = ref<ScanState>('idle')
const errorMessage = ref<string | null>(null)
const detectedWine = ref<WineSummary | null>(null)
const suggestions = ref<WineSummary[]>([])
const confidence = ref<number>(0)

// Editable fields for manual correction
const editMode = ref(false)
const editWinery = ref('')
const editName = ref('')
const editVintage = ref<number | null>(null)

// Camera state
const videoRef = ref<HTMLVideoElement | null>(null)
const canvasRef = ref<HTMLCanvasElement | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
const hasCamera = ref(false)
const cameraStream = ref<MediaStream | null>(null)
const cameraActive = ref(false)

// Check camera availability
onMounted(async () => {
  try {
    const devices = await navigator.mediaDevices.enumerateDevices()
    hasCamera.value = devices.some(device => device.kind === 'videoinput')
  } catch {
    hasCamera.value = false
  }
})

// Cleanup camera on unmount
onUnmounted(() => {
  stopCamera()
})

// Start camera
async function startCamera() {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: 'environment' }
    })
    cameraStream.value = stream
    cameraActive.value = true

    if (videoRef.value) {
      videoRef.value.srcObject = stream
      await videoRef.value.play()
    }
  } catch (error) {
    console.error('Failed to start camera:', error)
    errorMessage.value = t('scan.cameraError')
    state.value = 'error'
  }
}

// Stop camera
function stopCamera() {
  if (cameraStream.value) {
    cameraStream.value.getTracks().forEach(track => track.stop())
    cameraStream.value = null
    cameraActive.value = false
  }
}

// Capture image from camera
function captureFromCamera() {
  if (!videoRef.value || !canvasRef.value) return

  const canvas = canvasRef.value
  const video = videoRef.value

  canvas.width = video.videoWidth
  canvas.height = video.videoHeight

  const ctx = canvas.getContext('2d')
  if (!ctx) return

  ctx.drawImage(video, 0, 0)

  canvas.toBlob(async (blob) => {
    if (blob) {
      stopCamera()
      await scanImage(new File([blob], 'capture.jpg', { type: 'image/jpeg' }))
    }
  }, 'image/jpeg', 0.8)
}

// Handle file upload
function triggerFileUpload() {
  fileInputRef.value?.click()
}

function handleFileSelect(event: Event) {
  const input = event.target as HTMLInputElement
  if (input.files && input.files[0]) {
    scanImage(input.files[0])
  }
}

// Scan image
async function scanImage(file: File) {
  state.value = 'scanning'
  errorMessage.value = null

  try {
    const result = await winesService.scanWineLabel(file)

    if (result.wine) {
      detectedWine.value = result.wine
      confidence.value = result.confidence
      suggestions.value = result.suggestions
      state.value = 'detected'

      // Populate edit fields
      editWinery.value = result.wine.winery
      editName.value = result.wine.name
      editVintage.value = result.wine.vintage
    } else if (result.suggestions.length > 0) {
      suggestions.value = result.suggestions
      state.value = 'notFound'
    } else {
      state.value = 'notFound'
    }
  } catch (error) {
    console.error('Scan failed:', error)
    errorMessage.value = t('scan.scanError')
    state.value = 'error'
  }
}

// Manual search with edited fields
async function searchWithEdits() {
  state.value = 'scanning'

  try {
    // Search using the wines service
    const result = await winesService.getWines({
      search: `${editWinery.value} ${editName.value}`.trim(),
      vintage: editVintage.value ?? undefined,
    }, 1, 5)

    if (result.wines.length > 0) {
      detectedWine.value = result.wines[0]
      suggestions.value = result.wines.slice(1)
      confidence.value = 0.8 // Manual match
      state.value = 'detected'
      editMode.value = false
    } else {
      state.value = 'notFound'
    }
  } catch (error) {
    console.error('Search failed:', error)
    errorMessage.value = t('scan.searchError')
    state.value = 'error'
  }
}

// Go to wine details
function goToWineDetails(wine: WineSummary) {
  router.push(`/wines/${wine.id}`)
}

// Reset to idle state
function reset() {
  state.value = 'idle'
  detectedWine.value = null
  suggestions.value = []
  confidence.value = 0
  errorMessage.value = null
  editMode.value = false
  editWinery.value = ''
  editName.value = ''
  editVintage.value = null

  // Clear file input
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

// Computed
const confidenceLabel = computed(() => {
  if (confidence.value >= 0.9) return t('scan.confidenceHigh')
  if (confidence.value >= 0.7) return t('scan.confidenceMedium')
  return t('scan.confidenceLow')
})

const confidenceColor = computed(() => {
  if (confidence.value >= 0.9) return 'text-green-600 bg-green-100'
  if (confidence.value >= 0.7) return 'text-yellow-600 bg-yellow-100'
  return 'text-orange-600 bg-orange-100'
})
</script>

<template>
  <div class="max-w-2xl mx-auto space-y-6">
    <h1 class="text-2xl font-bold text-gray-900">{{ t('scan.title') }}</h1>

    <!-- Hidden file input -->
    <input
      ref="fileInputRef"
      type="file"
      accept="image/*"
      capture="environment"
      class="hidden"
      @change="handleFileSelect"
    />

    <!-- Hidden canvas for camera capture -->
    <canvas ref="canvasRef" class="hidden" />

    <!-- IDLE State -->
    <template v-if="state === 'idle'">
      <!-- Camera Mode -->
      <BaseCard v-if="cameraActive" class="overflow-hidden">
        <div class="relative aspect-[4/3] bg-black rounded-lg overflow-hidden">
          <video
            ref="videoRef"
            class="w-full h-full object-cover"
            autoplay
            playsinline
            muted
          />

          <!-- Camera overlay guide -->
          <div class="absolute inset-0 pointer-events-none">
            <div class="absolute inset-8 border-2 border-white/50 rounded-lg"></div>
            <div class="absolute bottom-4 left-0 right-0 text-center text-white text-sm">
              {{ t('scan.positionLabel') }}
            </div>
          </div>
        </div>

        <div class="flex gap-3 mt-4">
          <BaseButton @click="captureFromCamera" class="flex-1">
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            {{ t('scan.capture') }}
          </BaseButton>
          <BaseButton variant="outline" @click="stopCamera">
            {{ t('common.cancel') }}
          </BaseButton>
        </div>
      </BaseCard>

      <!-- Upload/Camera Selection -->
      <BaseCard v-else class="text-center py-12">
        <img
          src="/images/icons/reshot-icon-wine-bottle-and-cup-375YBXTJSG.svg"
          alt="Scan Wine"
          class="w-24 h-24 mx-auto mb-6"
        />
        <h2 class="text-xl font-semibold text-gray-900 mb-2">
          {{ t('scan.headline') }}
        </h2>
        <p class="text-gray-600 mb-6 max-w-md mx-auto">
          {{ t('scan.description') }}
        </p>

        <div class="flex flex-col sm:flex-row gap-3 justify-center">
          <BaseButton v-if="hasCamera" size="lg" @click="startCamera">
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            {{ t('scan.openCamera') }}
          </BaseButton>

          <BaseButton
            :variant="hasCamera ? 'outline' : 'primary'"
            size="lg"
            @click="triggerFileUpload"
          >
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            {{ t('scan.uploadImage') }}
          </BaseButton>
        </div>
      </BaseCard>

      <!-- How it works -->
      <BaseCard>
        <h3 class="font-semibold text-gray-900 mb-4">{{ t('scan.howItWorks') }}</h3>
        <ol class="space-y-4">
          <li class="flex gap-4">
            <span class="flex-shrink-0 w-8 h-8 bg-wine-100 text-wine-600 rounded-full flex items-center justify-center font-semibold">1</span>
            <div>
              <p class="font-medium text-gray-900">{{ t('scan.step1Title') }}</p>
              <p class="text-sm text-gray-600">{{ t('scan.step1Description') }}</p>
            </div>
          </li>
          <li class="flex gap-4">
            <span class="flex-shrink-0 w-8 h-8 bg-wine-100 text-wine-600 rounded-full flex items-center justify-center font-semibold">2</span>
            <div>
              <p class="font-medium text-gray-900">{{ t('scan.step2Title') }}</p>
              <p class="text-sm text-gray-600">{{ t('scan.step2Description') }}</p>
            </div>
          </li>
          <li class="flex gap-4">
            <span class="flex-shrink-0 w-8 h-8 bg-wine-100 text-wine-600 rounded-full flex items-center justify-center font-semibold">3</span>
            <div>
              <p class="font-medium text-gray-900">{{ t('scan.step3Title') }}</p>
              <p class="text-sm text-gray-600">{{ t('scan.step3Description') }}</p>
            </div>
          </li>
        </ol>
      </BaseCard>
    </template>

    <!-- SCANNING State -->
    <template v-else-if="state === 'scanning'">
      <BaseCard class="text-center py-16">
        <div class="relative w-24 h-24 mx-auto mb-6">
          <!-- Scanning animation -->
          <div class="absolute inset-0 border-4 border-wine-200 rounded-full"></div>
          <div class="absolute inset-0 border-4 border-wine-600 rounded-full border-t-transparent animate-spin"></div>
          <img
            src="/images/icons/reshot-icon-wine-bottle-and-cup-375YBXTJSG.svg"
            alt=""
            class="absolute inset-2 w-20 h-20 opacity-50"
          />
        </div>
        <h2 class="text-xl font-semibold text-gray-900 mb-2">
          {{ t('scan.analyzing') }}
        </h2>
        <p class="text-gray-600">
          {{ t('scan.analyzingDescription') }}
        </p>
      </BaseCard>
    </template>

    <!-- DETECTED State -->
    <template v-else-if="state === 'detected' && detectedWine">
      <BaseCard>
        <!-- Confidence badge -->
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-900">{{ t('scan.wineFound') }}</h2>
          <span :class="['text-xs font-medium px-2 py-1 rounded-full', confidenceColor]">
            {{ Math.round(confidence * 100) }}% {{ confidenceLabel }}
          </span>
        </div>

        <!-- Edit mode -->
        <template v-if="editMode">
          <div class="space-y-4 mb-6">
            <BaseInput
              v-model="editWinery"
              :label="t('wines.winery')"
              :placeholder="t('scan.wineryPlaceholder')"
            />
            <BaseInput
              v-model="editName"
              :label="t('scan.wineName')"
              :placeholder="t('scan.wineNamePlaceholder')"
            />
            <BaseInput
              v-model.number="editVintage"
              type="number"
              :label="t('wines.vintage')"
              :placeholder="t('scan.vintagePlaceholder')"
              :min="1900"
              :max="2030"
            />
          </div>
          <div class="flex gap-3">
            <BaseButton @click="searchWithEdits" class="flex-1">
              {{ t('scan.searchAgain') }}
            </BaseButton>
            <BaseButton variant="outline" @click="editMode = false">
              {{ t('common.cancel') }}
            </BaseButton>
          </div>
        </template>

        <!-- Wine result -->
        <template v-else>
          <div
            class="flex gap-4 p-4 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100 transition-colors"
            @click="goToWineDetails(detectedWine)"
          >
            <div class="w-16 h-20 bg-gray-200 rounded flex-shrink-0 flex items-center justify-center">
              <img
                v-if="detectedWine.imageUrl"
                :src="detectedWine.imageUrl"
                :alt="detectedWine.name"
                class="w-full h-full object-cover rounded"
              />
              <svg v-else class="w-8 h-8 text-gray-400" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2L8 8h8l-4-6zm0 8c-2.21 0-4 1.79-4 4v6h8v-6c0-2.21-1.79-4-4-4z"/>
              </svg>
            </div>
            <div class="flex-1 min-w-0">
              <h3 class="font-semibold text-gray-900 truncate">{{ detectedWine.name }}</h3>
              <p class="text-sm text-gray-600">{{ detectedWine.winery }}</p>
              <div class="flex items-center gap-3 mt-1 text-sm text-gray-500">
                <span v-if="detectedWine.vintage">{{ detectedWine.vintage }}</span>
                <span>{{ detectedWine.region }}, {{ detectedWine.country }}</span>
              </div>
              <div v-if="detectedWine.rating" class="flex items-center gap-1 mt-2">
                <svg class="w-4 h-4 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/>
                </svg>
                <span class="font-medium">{{ detectedWine.rating.toFixed(1) }}</span>
              </div>
            </div>
            <div class="flex items-center text-gray-400">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </div>
          </div>

          <div class="flex gap-3 mt-4">
            <BaseButton @click="goToWineDetails(detectedWine)" class="flex-1">
              {{ t('scan.viewDetails') }}
            </BaseButton>
            <BaseButton variant="outline" @click="editMode = true">
              {{ t('scan.notMyWine') }}
            </BaseButton>
          </div>
        </template>
      </BaseCard>

      <!-- Suggestions -->
      <BaseCard v-if="suggestions.length > 0 && !editMode">
        <h3 class="font-semibold text-gray-900 mb-4">{{ t('scan.otherMatches') }}</h3>
        <div class="space-y-3">
          <div
            v-for="wine in suggestions"
            :key="wine.id"
            class="flex gap-3 p-3 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100 transition-colors"
            @click="goToWineDetails(wine)"
          >
            <div class="w-10 h-12 bg-gray-200 rounded flex-shrink-0 flex items-center justify-center">
              <img
                v-if="wine.imageUrl"
                :src="wine.imageUrl"
                :alt="wine.name"
                class="w-full h-full object-cover rounded"
              />
              <svg v-else class="w-5 h-5 text-gray-400" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2L8 8h8l-4-6zm0 8c-2.21 0-4 1.79-4 4v6h8v-6c0-2.21-1.79-4-4-4z"/>
              </svg>
            </div>
            <div class="flex-1 min-w-0">
              <p class="font-medium text-gray-900 text-sm truncate">{{ wine.name }}</p>
              <p class="text-xs text-gray-500">{{ wine.winery }} · {{ wine.vintage || 'NV' }}</p>
            </div>
          </div>
        </div>
      </BaseCard>

      <!-- Scan another -->
      <div class="text-center">
        <button
          class="text-wine-600 hover:text-wine-700 text-sm font-medium"
          @click="reset"
        >
          {{ t('scan.scanAnother') }}
        </button>
      </div>
    </template>

    <!-- NOT FOUND State -->
    <template v-else-if="state === 'notFound'">
      <BaseCard class="text-center py-12">
        <div class="w-20 h-20 mx-auto mb-6 bg-gray-100 rounded-full flex items-center justify-center">
          <svg class="w-10 h-10 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <h2 class="text-xl font-semibold text-gray-900 mb-2">
          {{ t('scan.notFoundTitle') }}
        </h2>
        <p class="text-gray-600 mb-6 max-w-md mx-auto">
          {{ t('scan.notFoundDescription') }}
        </p>

        <!-- Manual entry form -->
        <div class="max-w-sm mx-auto space-y-4 mb-6 text-left">
          <BaseInput
            v-model="editWinery"
            :label="t('wines.winery')"
            :placeholder="t('scan.wineryPlaceholder')"
          />
          <BaseInput
            v-model="editName"
            :label="t('scan.wineName')"
            :placeholder="t('scan.wineNamePlaceholder')"
          />
          <BaseInput
            v-model.number="editVintage"
            type="number"
            :label="t('wines.vintage')"
            :placeholder="t('scan.vintagePlaceholder')"
            :min="1900"
            :max="2030"
          />
        </div>

        <div class="flex flex-col sm:flex-row gap-3 justify-center">
          <BaseButton @click="searchWithEdits" :disabled="!editWinery && !editName">
            {{ t('scan.searchManually') }}
          </BaseButton>
          <BaseButton variant="outline" @click="reset">
            {{ t('scan.tryAgain') }}
          </BaseButton>
        </div>
      </BaseCard>

      <!-- Suggestions if available -->
      <BaseCard v-if="suggestions.length > 0">
        <h3 class="font-semibold text-gray-900 mb-4">{{ t('scan.didYouMean') }}</h3>
        <div class="space-y-3">
          <div
            v-for="wine in suggestions"
            :key="wine.id"
            class="flex gap-3 p-3 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100 transition-colors"
            @click="goToWineDetails(wine)"
          >
            <div class="w-10 h-12 bg-gray-200 rounded flex-shrink-0"></div>
            <div class="flex-1 min-w-0">
              <p class="font-medium text-gray-900 text-sm truncate">{{ wine.name }}</p>
              <p class="text-xs text-gray-500">{{ wine.winery }} · {{ wine.vintage || 'NV' }}</p>
            </div>
          </div>
        </div>
      </BaseCard>
    </template>

    <!-- ERROR State -->
    <template v-else-if="state === 'error'">
      <BaseCard class="text-center py-12">
        <div class="w-20 h-20 mx-auto mb-6 bg-red-100 rounded-full flex items-center justify-center">
          <svg class="w-10 h-10 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
          </svg>
        </div>
        <h2 class="text-xl font-semibold text-gray-900 mb-2">
          {{ t('scan.errorTitle') }}
        </h2>
        <p class="text-gray-600 mb-6">
          {{ errorMessage || t('scan.errorDescription') }}
        </p>
        <BaseButton @click="reset">
          {{ t('common.tryAgain') }}
        </BaseButton>
      </BaseCard>
    </template>
  </div>
</template>


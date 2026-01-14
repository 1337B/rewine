<script setup lang="ts">
import BaseCard from '@components/common/BaseCard.vue'

const stats = [
  { label: 'Total Wines', value: '1,234', change: '+12%' },
  { label: 'Total Events', value: '56', change: '+8%' },
  { label: 'Total Users', value: '3,456', change: '+23%' },
  { label: 'Wine Routes', value: '24', change: '+4%' },
]
</script>

<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold text-gray-900">Admin Dashboard</h1>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <BaseCard v-for="stat in stats" :key="stat.label">
        <p class="text-sm text-gray-500">{{ stat.label }}</p>
        <div class="flex items-end justify-between mt-2">
          <span class="text-3xl font-bold text-gray-900">{{ stat.value }}</span>
          <span class="text-sm text-green-600">{{ stat.change }}</span>
        </div>
      </BaseCard>
    </div>

    <!-- Quick Actions -->
    <BaseCard>
      <h3 class="font-semibold text-gray-900 mb-4">Quick Actions</h3>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <router-link to="/admin/wines" class="p-4 bg-gray-50 rounded-lg hover:bg-gray-100 text-center">
          <img src="/images/icons/reshot-icon-red-wine-L2HFAY75WG.svg" alt="Wines" class="w-10 h-10 mx-auto" />
          <p class="text-sm font-medium text-gray-900 mt-2">Manage Wines</p>
        </router-link>
        <router-link to="/admin/events" class="p-4 bg-gray-50 rounded-lg hover:bg-gray-100 text-center">
          <img src="/images/icons/reshot-icon-toast-65A7YV3EXC.svg" alt="Events" class="w-10 h-10 mx-auto" />
          <p class="text-sm font-medium text-gray-900 mt-2">Manage Events</p>
        </router-link>
        <router-link to="/admin/routes" class="p-4 bg-gray-50 rounded-lg hover:bg-gray-100 text-center">
          <img src="/images/icons/reshot-icon-vineyard-H8S2KEC3PT.svg" alt="Routes" class="w-10 h-10 mx-auto" />
          <p class="text-sm font-medium text-gray-900 mt-2">Manage Routes</p>
        </router-link>
        <router-link to="/admin/users" class="p-4 bg-gray-50 rounded-lg hover:bg-gray-100 text-center">
          <img src="/images/icons/reshot-icon-male-sommelier-4J9YXUNDT3.svg" alt="Users" class="w-10 h-10 mx-auto" />
          <p class="text-sm font-medium text-gray-900 mt-2">Manage Users</p>
        </router-link>
      </div>
    </BaseCard>

    <!-- Recent Activity placeholder -->
    <BaseCard>
      <h3 class="font-semibold text-gray-900 mb-4">Recent Activity</h3>
      <p class="text-gray-500 text-sm">No recent activity to display</p>
    </BaseCard>
  </div>
</template>


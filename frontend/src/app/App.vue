<script setup lang="ts">
import { RouterView } from 'vue-router'
import AppShell from '@components/layout/AppShell.vue'
import ToastContainer from '@components/feedback/ToastContainer.vue'
</script>

<template>
  <AppShell>
    <RouterView />
  </AppShell>
  <ToastContainer />
</template>

<style>
#app {
  min-height: 100vh;
}
</style>


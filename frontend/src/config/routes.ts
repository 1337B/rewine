import type { RouteRecordRaw } from 'vue-router'

// Public pages
import HomePage from '@pages/public/HomePage.vue'
import LoginPage from '@pages/public/LoginPage.vue'
import RegisterPage from '@pages/public/RegisterPage.vue'
import NotFoundPage from '@pages/public/NotFoundPage.vue'

// Wine pages
import WineSearchPage from '@pages/wines/WineSearchPage.vue'
import WineDetailsPage from '@pages/wines/WineDetailsPage.vue'
import WineComparePage from '@pages/wines/WineComparePage.vue'
import WineScanPage from '@pages/wines/WineScanPage.vue'

// Cellar pages
import MyCellarPage from '@pages/cellar/MyCellarPage.vue'

// Event pages
import EventsNearbyPage from '@pages/events/EventsNearbyPage.vue'
import EventDetailsPage from '@pages/events/EventDetailsPage.vue'

// Wine route pages
import WineRoutesExplorerPage from '@pages/wine-routes/WineRoutesExplorerPage.vue'
import WineRouteDetailsPage from '@pages/wine-routes/WineRouteDetailsPage.vue'

// Admin pages
import AdminDashboardPage from '@pages/admin/AdminDashboardPage.vue'
import AdminWinesPage from '@pages/admin/AdminWinesPage.vue'
import AdminEventsPage from '@pages/admin/AdminEventsPage.vue'
import AdminRoutesPage from '@pages/admin/AdminRoutesPage.vue'
import AdminUsersPage from '@pages/admin/AdminUsersPage.vue'

export interface RouteMeta {
  requiresAuth?: boolean
  roles?: string[]
  title?: string
  guestOnly?: boolean
}

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean
    roles?: string[]
    title?: string
    guestOnly?: boolean
  }
}

export const routes: RouteRecordRaw[] = [
  // Public routes
  {
    path: '/',
    name: 'home',
    component: HomePage,
    meta: { title: 'Home' },
  },
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: { title: 'Login', guestOnly: true },
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterPage,
    meta: { title: 'Register', guestOnly: true },
  },

  // Wine routes
  {
    path: '/wines',
    name: 'wines',
    component: WineSearchPage,
    meta: { title: 'Search Wines' },
  },
  {
    path: '/wines/:id',
    name: 'wine-details',
    component: WineDetailsPage,
    meta: { title: 'Wine Details' },
  },
  {
    path: '/wines/compare',
    name: 'wine-compare',
    component: WineComparePage,
    meta: { title: 'Compare Wines' },
  },
  {
    path: '/wines/scan',
    name: 'wine-scan',
    component: WineScanPage,
    meta: { title: 'Scan Wine', requiresAuth: true },
  },

  // Cellar routes
  {
    path: '/cellar',
    name: 'cellar',
    component: MyCellarPage,
    meta: { title: 'My Cellar', requiresAuth: true },
  },

  // Event routes
  {
    path: '/events',
    name: 'events',
    component: EventsNearbyPage,
    meta: { title: 'Events Nearby' },
  },
  {
    path: '/events/:id',
    name: 'event-details',
    component: EventDetailsPage,
    meta: { title: 'Event Details' },
  },

  // Wine route routes
  {
    path: '/wine-routes',
    name: 'wine-routes',
    component: WineRoutesExplorerPage,
    meta: { title: 'Wine Routes' },
  },
  {
    path: '/wine-routes/:id',
    name: 'wine-route-details',
    component: WineRouteDetailsPage,
    meta: { title: 'Wine Route Details' },
  },

  // Admin routes
  {
    path: '/admin',
    name: 'admin',
    component: AdminDashboardPage,
    meta: { title: 'Admin Dashboard', requiresAuth: true, roles: ['admin'] },
  },
  {
    path: '/admin/wines',
    name: 'admin-wines',
    component: AdminWinesPage,
    meta: { title: 'Manage Wines', requiresAuth: true, roles: ['admin'] },
  },
  {
    path: '/admin/events',
    name: 'admin-events',
    component: AdminEventsPage,
    meta: { title: 'Manage Events', requiresAuth: true, roles: ['admin'] },
  },
  {
    path: '/admin/routes',
    name: 'admin-routes',
    component: AdminRoutesPage,
    meta: { title: 'Manage Routes', requiresAuth: true, roles: ['admin'] },
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    component: AdminUsersPage,
    meta: { title: 'Manage Users', requiresAuth: true, roles: ['admin'] },
  },

  // 404 catch-all
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundPage,
    meta: { title: 'Page Not Found' },
  },
]

export default routes


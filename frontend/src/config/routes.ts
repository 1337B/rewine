import type { RouteRecordRaw } from 'vue-router'
import { ROLES, type Role } from '@config/roles'

// Re-export ROLES for backward compatibility
export { ROLES, type Role } from '@config/roles'

// Public pages
import HomePage from '@pages/public/HomePage.vue'
import LoginPage from '@pages/public/LoginPage.vue'
import RegisterPage from '@pages/public/RegisterPage.vue'
import NotFoundPage from '@pages/public/NotFoundPage.vue'
import ForbiddenPage from '@pages/public/ForbiddenPage.vue'
import ErrorPage from '@pages/public/ErrorPage.vue'

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


/**
 * Route meta interface for type safety
 */
export interface RouteMeta {
  requiresAuth?: boolean
  roles?: Role[]
  titleKey?: string
  title?: string
  guestOnly?: boolean
}

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean
    roles?: Role[]
    titleKey?: string
    title?: string
    guestOnly?: boolean
  }
}

export const routes: RouteRecordRaw[] = [
  // ===== PUBLIC ROUTES =====
  {
    path: '/',
    name: 'home',
    component: HomePage,
    meta: { titleKey: 'nav.home' },
  },
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: { titleKey: 'auth.login', guestOnly: true },
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterPage,
    meta: { titleKey: 'auth.register', guestOnly: true },
  },

  // ===== ERROR PAGES =====
  {
    path: '/403',
    name: 'forbidden',
    component: ForbiddenPage,
    meta: { titleKey: 'errors.forbidden' },
  },
  {
    path: '/500',
    name: 'error',
    component: ErrorPage,
    meta: { titleKey: 'errors.serverError' },
  },

  // ===== WINE ROUTES (Public browsing, some features require auth) =====
  {
    path: '/wines',
    name: 'wines',
    component: WineSearchPage,
    meta: { titleKey: 'wines.title' },
  },
  {
    path: '/wines/compare',
    name: 'wine-compare',
    component: WineComparePage,
    meta: { titleKey: 'wines.compare' },
  },
  {
    path: '/wines/scan',
    name: 'wine-scan',
    component: WineScanPage,
    meta: {
      titleKey: 'wines.scan',
      requiresAuth: true,
      roles: [ROLES.USER, ROLES.PARTNER, ROLES.MODERATOR, ROLES.ADMIN],
    },
  },
  {
    path: '/wines/:id',
    name: 'wine-details',
    component: WineDetailsPage,
    meta: { titleKey: 'wines.title' },
  },

  // ===== CELLAR ROUTES (Requires authentication) =====
  {
    path: '/cellar',
    name: 'cellar',
    component: MyCellarPage,
    meta: {
      titleKey: 'cellar.title',
      requiresAuth: true,
      roles: [ROLES.USER, ROLES.PARTNER, ROLES.MODERATOR, ROLES.ADMIN],
    },
  },

  // ===== EVENT ROUTES (Public browsing) =====
  {
    path: '/events',
    name: 'events',
    component: EventsNearbyPage,
    meta: { titleKey: 'events.title' },
  },
  {
    path: '/events/:id',
    name: 'event-details',
    component: EventDetailsPage,
    meta: { titleKey: 'events.title' },
  },

  // ===== WINE ROUTE ROUTES (Public browsing) =====
  {
    path: '/wine-routes',
    name: 'wine-routes',
    component: WineRoutesExplorerPage,
    meta: { titleKey: 'routes.title' },
  },
  {
    path: '/wine-routes/:id',
    name: 'wine-route-details',
    component: WineRouteDetailsPage,
    meta: { titleKey: 'routes.title' },
  },

  // ===== ADMIN ROUTES (Requires ROLE_ADMIN or ROLE_MODERATOR) =====
  {
    path: '/admin',
    name: 'admin',
    component: AdminDashboardPage,
    meta: {
      titleKey: 'nav.admin',
      requiresAuth: true,
      roles: [ROLES.ADMIN, ROLES.MODERATOR],
    },
  },
  {
    path: '/admin/wines',
    name: 'admin-wines',
    component: AdminWinesPage,
    meta: {
      titleKey: 'nav.admin',
      requiresAuth: true,
      roles: [ROLES.ADMIN, ROLES.MODERATOR],
    },
  },
  {
    path: '/admin/events',
    name: 'admin-events',
    component: AdminEventsPage,
    meta: {
      titleKey: 'nav.admin',
      requiresAuth: true,
      roles: [ROLES.ADMIN, ROLES.MODERATOR],
    },
  },
  {
    path: '/admin/routes',
    name: 'admin-routes',
    component: AdminRoutesPage,
    meta: {
      titleKey: 'nav.admin',
      requiresAuth: true,
      roles: [ROLES.ADMIN, ROLES.MODERATOR],
    },
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    component: AdminUsersPage,
    meta: {
      titleKey: 'nav.admin',
      requiresAuth: true,
      roles: [ROLES.ADMIN], // Only admins can manage users
    },
  },

  // ===== 404 CATCH-ALL =====
  {
    path: '/404',
    name: 'not-found-page',
    component: NotFoundPage,
    meta: { titleKey: 'errors.notFound' },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundPage,
    meta: { titleKey: 'errors.notFound' },
  },
]

export default routes

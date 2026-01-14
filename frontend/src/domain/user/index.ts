/**
 * User domain barrel export
 */

// Types
export type {
  User,
  UserRole,
  UserProfile,
  UserPreferences,
  UserStats,
  PriceRange,
  NotificationPreferences,
} from './user.types'

// Validators & Schemas
export {
  userRoleSchema,
  themeSchema,
  priceRangeSchema,
  notificationPreferencesSchema,
  userPreferencesSchema,
  userSchema,
  registerUserSchema,
  loginUserSchema,
  updateUserProfileSchema,
  changePasswordSchema,
} from './user.validators'

// Inferred types from validators
export type {
  RegisterUserInput,
  LoginUserInput,
  UpdateUserProfileInput,
  ChangePasswordInput,
  UserPreferencesInput,
} from './user.validators'

// Mappers
export {
  mapUserFromDto,
  mapUserToDto,
  mapUserProfileFromDto,
} from './user.mappers'


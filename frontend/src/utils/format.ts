/**
 * Formatting utility functions
 */

/**
 * Format a number as currency
 */
export function formatCurrency(
  value: number,
  currency = 'ARS',
  locale = 'es-AR'
): string {
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency,
  }).format(value)
}

/**
 * Format a number with thousand separators
 */
export function formatNumber(
  value: number,
  options?: Intl.NumberFormatOptions,
  locale = 'es-AR'
): string {
  return new Intl.NumberFormat(locale, options).format(value)
}

/**
 * Format a number as a percentage
 */
export function formatPercent(
  value: number,
  decimals = 0,
  locale = 'es-AR'
): string {
  return new Intl.NumberFormat(locale, {
    style: 'percent',
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  }).format(value)
}

/**
 * Format a number with decimal places
 */
export function formatDecimal(
  value: number,
  decimals = 2,
  locale = 'es-AR'
): string {
  return new Intl.NumberFormat(locale, {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  }).format(value)
}

/**
 * Format a file size in bytes to human-readable format
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes'

  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`
}

/**
 * Format a duration in minutes to hours and minutes
 */
export function formatDuration(minutes: number): string {
  if (minutes < 60) {
    return `${minutes} min`
  }

  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60

  if (mins === 0) {
    return `${hours}h`
  }

  return `${hours}h ${mins}min`
}

/**
 * Format a distance in kilometers
 */
export function formatDistance(km: number): string {
  if (km < 1) {
    return `${Math.round(km * 1000)} m`
  }

  return `${km.toFixed(1)} km`
}

/**
 * Format a rating (e.g., 4.5 stars)
 */
export function formatRating(rating: number, maxRating = 5): string {
  return `${rating.toFixed(1)}/${maxRating}`
}


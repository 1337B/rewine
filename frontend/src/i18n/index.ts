import { createI18n } from 'vue-i18n'
import esAR from './locales/es-AR.json'
import enUS from './locales/en-US.json'
import { STORAGE_KEYS } from '@config/constants'

export type SupportedLocale = 'es-AR' | 'en-US'

const messages = {
  'es-AR': esAR,
  'en-US': enUS,
}

function getStoredLocale(): SupportedLocale {
  const stored = localStorage.getItem(STORAGE_KEYS.LOCALE)
  if (stored && (stored === 'es-AR' || stored === 'en-US')) {
    return stored
  }
  return 'es-AR'
}

export const i18n = createI18n({
  legacy: false,
  locale: getStoredLocale(),
  fallbackLocale: 'es-AR',
  messages,
})

export function setLocale(locale: SupportedLocale) {
  i18n.global.locale.value = locale
  localStorage.setItem(STORAGE_KEYS.LOCALE, locale)
  document.documentElement.setAttribute('lang', locale)
}

export function getCurrentLocale(): SupportedLocale {
  return i18n.global.locale.value as SupportedLocale
}

export default i18n


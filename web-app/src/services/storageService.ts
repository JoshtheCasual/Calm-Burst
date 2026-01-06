/**
 * StorageService - Unified storage wrapper for web and native platforms
 * Uses Capacitor Preferences API on native platforms (iOS/Android)
 * Falls back to localStorage on web platforms
 * Handles storage access errors (e.g., private browsing mode, SSR)
 */

import { Preferences } from '@capacitor/preferences'
import { Capacitor } from '@capacitor/core'

export const StorageService = {
  /**
   * Get a value from storage or return the default value
   * @param key - The storage key
   * @param defaultValue - The default value to return if key doesn't exist or on error
   * @returns Promise resolving to the stored value or default value
   */
  async get<T>(key: string, defaultValue: T): Promise<T> {
    try {
      // SSR safety check
      if (typeof window === 'undefined') {
        return defaultValue
      }

      // Use Capacitor Preferences on native platforms
      if (Capacitor.isNativePlatform()) {
        const { value } = await Preferences.get({ key })

        if (value === null) {
          return defaultValue
        }

        return JSON.parse(value) as T
      }

      // Fallback to localStorage on web
      const item = window.localStorage.getItem(key)

      if (item === null) {
        return defaultValue
      }

      return JSON.parse(item) as T
    } catch (error) {
      console.error(`Error getting item from storage (key: ${key}):`, error)
      return defaultValue
    }
  },

  /**
   * Save a value to storage
   * @param key - The storage key
   * @param value - The value to store
   */
  async set<T>(key: string, value: T): Promise<void> {
    try {
      // SSR safety check
      if (typeof window === 'undefined') {
        return
      }

      const serializedValue = JSON.stringify(value)

      // Use Capacitor Preferences on native platforms
      if (Capacitor.isNativePlatform()) {
        await Preferences.set({
          key,
          value: serializedValue
        })
        return
      }

      // Fallback to localStorage on web
      window.localStorage.setItem(key, serializedValue)
    } catch (error) {
      console.error(`Error setting item in storage (key: ${key}):`, error)
    }
  },

  /**
   * Remove a value from storage
   * @param key - The storage key to remove
   */
  async remove(key: string): Promise<void> {
    try {
      // SSR safety check
      if (typeof window === 'undefined') {
        return
      }

      // Use Capacitor Preferences on native platforms
      if (Capacitor.isNativePlatform()) {
        await Preferences.remove({ key })
        return
      }

      // Fallback to localStorage on web
      window.localStorage.removeItem(key)
    } catch (error) {
      console.error(`Error removing item from storage (key: ${key}):`, error)
    }
  },

  /**
   * Clear all values from storage
   */
  async clear(): Promise<void> {
    try {
      // SSR safety check
      if (typeof window === 'undefined') {
        return
      }

      // Use Capacitor Preferences on native platforms
      if (Capacitor.isNativePlatform()) {
        await Preferences.clear()
        return
      }

      // Fallback to localStorage on web
      window.localStorage.clear()
    } catch (error) {
      console.error('Error clearing storage:', error)
    }
  },
}

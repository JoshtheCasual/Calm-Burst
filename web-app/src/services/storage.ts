/**
 * Storage service for managing app data persistence
 */

const STORAGE_KEYS = {
  SETTINGS: 'calm-burst-settings',
  USER_DATA: 'calm-burst-user',
  SESSIONS: 'calm-burst-sessions',
} as const

export const StorageService = {
  /**
   * Get an item from localStorage
   */
  getItem<T>(key: string): T | null {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : null
    } catch (error) {
      console.error(`Error getting item from storage: ${key}`, error)
      return null
    }
  },

  /**
   * Set an item in localStorage
   */
  setItem<T>(key: string, value: T): void {
    try {
      localStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error(`Error setting item in storage: ${key}`, error)
    }
  },

  /**
   * Remove an item from localStorage
   */
  removeItem(key: string): void {
    try {
      localStorage.removeItem(key)
    } catch (error) {
      console.error(`Error removing item from storage: ${key}`, error)
    }
  },

  /**
   * Clear all items from localStorage
   */
  clear(): void {
    try {
      localStorage.clear()
    } catch (error) {
      console.error('Error clearing storage', error)
    }
  },

  /**
   * Storage keys
   */
  keys: STORAGE_KEYS,
}

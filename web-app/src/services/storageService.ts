/**
 * StorageService - Wrapper for localStorage with TypeScript generics
 * Handles localStorage access errors (e.g., private browsing mode)
 */

export const StorageService = {
  /**
   * Get a value from localStorage or return the default value
   * @param key - The storage key
   * @param defaultValue - The default value to return if key doesn't exist or on error
   * @returns The stored value or default value
   */
  get<T>(key: string, defaultValue: T): T {
    try {
      if (typeof window === 'undefined') {
        return defaultValue
      }

      const item = window.localStorage.getItem(key)

      if (item === null) {
        return defaultValue
      }

      return JSON.parse(item) as T
    } catch (error) {
      console.error(`Error getting item from localStorage (key: ${key}):`, error)
      return defaultValue
    }
  },

  /**
   * Save a value to localStorage
   * @param key - The storage key
   * @param value - The value to store
   */
  set<T>(key: string, value: T): void {
    try {
      if (typeof window === 'undefined') {
        return
      }

      window.localStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error(`Error setting item in localStorage (key: ${key}):`, error)
    }
  },

  /**
   * Remove a value from localStorage
   * @param key - The storage key to remove
   */
  remove(key: string): void {
    try {
      if (typeof window === 'undefined') {
        return
      }

      window.localStorage.removeItem(key)
    } catch (error) {
      console.error(`Error removing item from localStorage (key: ${key}):`, error)
    }
  },

  /**
   * Clear all values from localStorage
   */
  clear(): void {
    try {
      if (typeof window === 'undefined') {
        return
      }

      window.localStorage.clear()
    } catch (error) {
      console.error('Error clearing localStorage:', error)
    }
  },
}

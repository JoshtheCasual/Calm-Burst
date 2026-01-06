import { useState, useCallback, useEffect } from 'react'
import { StorageService } from '@/services/storageService'

// Storage keys
const STORAGE_KEYS = {
  INTERVAL: 'calmburst_interval',
  QUIET_START: 'calmburst_quiet_start',
  QUIET_END: 'calmburst_quiet_end',
} as const

// Valid interval values in hours
const VALID_INTERVALS = [2, 6, 12, 24] as const
type ValidInterval = (typeof VALID_INTERVALS)[number]

// Time format validation (HH:mm)
const TIME_FORMAT_REGEX = /^([01]\d|2[0-3]):([0-5]\d)$/

/**
 * Validates if a time string matches HH:mm format
 */
function isValidTimeFormat(time: string): boolean {
  return TIME_FORMAT_REGEX.test(time)
}

/**
 * Validates if an interval value is valid
 */
function isValidInterval(interval: number): interval is ValidInterval {
  return (VALID_INTERVALS as readonly number[]).includes(interval)
}

/**
 * Hook for managing application settings with Capacitor Preferences persistence
 * @returns Settings state and update functions
 */
export function useSettings() {
  // Initialize state with defaults (will be loaded from storage in useEffect)
  const [interval, setIntervalState] = useState<number>(2)
  const [quietStart, setQuietStartState] = useState<string>('22:00')
  const [quietEnd, setQuietEndState] = useState<string>('08:00')

  // Load settings from storage on mount
  useEffect(() => {
    const loadSettings = async () => {
      // Load interval
      const storedInterval = await StorageService.get<number>(STORAGE_KEYS.INTERVAL, 2)

      if (!isValidInterval(storedInterval)) {
        console.warn(`Invalid interval value: ${storedInterval}, resetting to 2 hours`)
        setIntervalState(2)
        await StorageService.set(STORAGE_KEYS.INTERVAL, 2)
      } else {
        setIntervalState(storedInterval)
      }

      // Load quiet hours
      const storedQuietStart = await StorageService.get<string>(STORAGE_KEYS.QUIET_START, '22:00')

      if (!isValidTimeFormat(storedQuietStart)) {
        console.warn(`Invalid quietStart format: ${storedQuietStart}, resetting to 22:00`)
        setQuietStartState('22:00')
        await StorageService.set(STORAGE_KEYS.QUIET_START, '22:00')
      } else {
        setQuietStartState(storedQuietStart)
      }

      const storedQuietEnd = await StorageService.get<string>(STORAGE_KEYS.QUIET_END, '08:00')

      if (!isValidTimeFormat(storedQuietEnd)) {
        console.warn(`Invalid quietEnd format: ${storedQuietEnd}, resetting to 08:00`)
        setQuietEndState('08:00')
        await StorageService.set(STORAGE_KEYS.QUIET_END, '08:00')
      } else {
        setQuietEndState(storedQuietEnd)
      }
    }

    loadSettings()
  }, []) // Only run on mount

  /**
   * Update the interval setting
   * @param value - New interval value (must be 2, 6, 12, or 24)
   */
  const updateInterval = useCallback((value: number) => {
    if (!isValidInterval(value)) {
      console.error(
        `Invalid interval value: ${value}. Must be one of: ${VALID_INTERVALS.join(', ')}`
      )
      return
    }

    setIntervalState(value)
    // Async storage operation - fire and forget
    StorageService.set(STORAGE_KEYS.INTERVAL, value).catch((error) => {
      console.error('Failed to save interval to storage:', error)
    })
  }, [])

  /**
   * Update the quiet hours settings
   * @param start - Quiet hours start time (HH:mm format)
   * @param end - Quiet hours end time (HH:mm format)
   */
  const updateQuietHours = useCallback((start: string, end: string) => {
    if (!isValidTimeFormat(start)) {
      console.error(`Invalid start time format: ${start}. Expected format: HH:mm`)
      return
    }

    if (!isValidTimeFormat(end)) {
      console.error(`Invalid end time format: ${end}. Expected format: HH:mm`)
      return
    }

    setQuietStartState(start)
    setQuietEndState(end)
    // Async storage operations - fire and forget
    Promise.all([
      StorageService.set(STORAGE_KEYS.QUIET_START, start),
      StorageService.set(STORAGE_KEYS.QUIET_END, end)
    ]).catch((error) => {
      console.error('Failed to save quiet hours to storage:', error)
    })
  }, [])

  /**
   * Explicit save function (settings are auto-saved on change, but this can be used for manual triggers)
   */
  const saveSettings = useCallback(() => {
    // Async storage operations - fire and forget
    Promise.all([
      StorageService.set(STORAGE_KEYS.INTERVAL, interval),
      StorageService.set(STORAGE_KEYS.QUIET_START, quietStart),
      StorageService.set(STORAGE_KEYS.QUIET_END, quietEnd)
    ]).catch((error) => {
      console.error('Failed to save settings to storage:', error)
    })
  }, [interval, quietStart, quietEnd])

  return {
    interval,
    quietStart,
    quietEnd,
    updateInterval,
    updateQuietHours,
    saveSettings,
  }
}

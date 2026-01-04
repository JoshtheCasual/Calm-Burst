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
 * Hook for managing application settings with localStorage persistence
 * @returns Settings state and update functions
 */
export function useSettings() {
  // Initialize state from localStorage with defaults
  const [interval, setIntervalState] = useState<number>(() =>
    StorageService.get<number>(STORAGE_KEYS.INTERVAL, 2)
  )

  const [quietStart, setQuietStartState] = useState<string>(() =>
    StorageService.get<string>(STORAGE_KEYS.QUIET_START, '22:00')
  )

  const [quietEnd, setQuietEndState] = useState<string>(() =>
    StorageService.get<string>(STORAGE_KEYS.QUIET_END, '08:00')
  )

  // Validate and load settings on mount
  useEffect(() => {
    // Validate interval
    if (!isValidInterval(interval)) {
      console.warn(`Invalid interval value: ${interval}, resetting to 2 hours`)
      setIntervalState(2)
      StorageService.set(STORAGE_KEYS.INTERVAL, 2)
    }

    // Validate quiet hours
    if (!isValidTimeFormat(quietStart)) {
      console.warn(`Invalid quietStart format: ${quietStart}, resetting to 22:00`)
      setQuietStartState('22:00')
      StorageService.set(STORAGE_KEYS.QUIET_START, '22:00')
    }

    if (!isValidTimeFormat(quietEnd)) {
      console.warn(`Invalid quietEnd format: ${quietEnd}, resetting to 08:00`)
      setQuietEndState('08:00')
      StorageService.set(STORAGE_KEYS.QUIET_END, '08:00')
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
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
    StorageService.set(STORAGE_KEYS.INTERVAL, value)
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
    StorageService.set(STORAGE_KEYS.QUIET_START, start)
    StorageService.set(STORAGE_KEYS.QUIET_END, end)
  }, [])

  /**
   * Explicit save function (settings are auto-saved on change, but this can be used for manual triggers)
   */
  const saveSettings = useCallback(() => {
    StorageService.set(STORAGE_KEYS.INTERVAL, interval)
    StorageService.set(STORAGE_KEYS.QUIET_START, quietStart)
    StorageService.set(STORAGE_KEYS.QUIET_END, quietEnd)
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

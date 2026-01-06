import { createContext, useContext, ReactNode, useEffect } from 'react'
import { useQuotes } from '@/hooks/useQuotes'
import { useSettings } from '@/hooks/useSettings'
import { useNotifications } from '@/hooks/useNotifications'
import type { Quote } from '@/types'

/**
 * AppContext - Global state management for the Calm Burst app
 * Combines quote and settings state into a single context
 */

interface AppContextValue {
  // Quote state
  currentQuote: Quote | null
  loading: boolean
  error: string | null
  loadNewQuote: () => Promise<void>

  // Settings state
  interval: number
  quietStart: string
  quietEnd: string
  updateInterval: (value: number) => void
  updateQuietHours: (start: string, end: string) => void
  saveSettings: () => void

  // Notification state
  permissionStatus: string
  notificationsEnabled: boolean
  requestNotificationPermission: () => Promise<void>
  scheduleNotifications: () => Promise<void>
  cancelNotifications: () => Promise<void>
}

const AppContext = createContext<AppContextValue | undefined>(undefined)

interface AppProviderProps {
  children: ReactNode
}

/**
 * AppProvider - Wraps the app with global state
 * Combines useQuotes, useSettings, and useNotifications hooks
 */
export function AppProvider({ children }: AppProviderProps) {
  // Quote state from useQuotes hook
  const { currentQuote, loading, error, loadNewQuote } = useQuotes()

  // Settings state from useSettings hook
  const { interval, quietStart, quietEnd, updateInterval, updateQuietHours, saveSettings } =
    useSettings()

  // Notification state from useNotifications hook
  const {
    permissionStatus,
    notificationsEnabled,
    isInitialized,
    requestNotificationPermission,
    scheduleNotifications: scheduleNotificationsBase,
    cancelNotifications,
  } = useNotifications()

  // Wrapper for scheduleNotifications that uses current settings
  const scheduleNotifications = async (): Promise<void> => {
    await scheduleNotificationsBase(interval, quietStart, quietEnd)
  }

  // Auto-schedule notifications when settings change (if notifications are enabled)
  useEffect(() => {
    if (notificationsEnabled && isInitialized) {
      const autoSchedule = async () => {
        try {
          await scheduleNotifications()
          console.log('[AppContext] Auto-scheduled notifications after settings change')
        } catch (error) {
          console.error('[AppContext] Failed to auto-schedule notifications:', error)
        }
      }

      autoSchedule()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [interval, quietStart, quietEnd, notificationsEnabled, isInitialized])

  const value: AppContextValue = {
    // Quote state
    currentQuote,
    loading,
    error,
    loadNewQuote,

    // Settings state
    interval,
    quietStart,
    quietEnd,
    updateInterval,
    updateQuietHours,
    saveSettings,

    // Notification state
    permissionStatus,
    notificationsEnabled,
    requestNotificationPermission,
    scheduleNotifications,
    cancelNotifications,
  }

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>
}

/**
 * useAppContext - Hook to access the app context
 * @throws Error if used outside of AppProvider
 */
// eslint-disable-next-line react-refresh/only-export-components
export function useAppContext() {
  const context = useContext(AppContext)

  if (context === undefined) {
    throw new Error('useAppContext must be used within an AppProvider')
  }

  return context
}

import { createContext, useContext, ReactNode } from 'react'
import { useQuotes } from '@/hooks/useQuotes'
import { useSettings } from '@/hooks/useSettings'
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
}

const AppContext = createContext<AppContextValue | undefined>(undefined)

interface AppProviderProps {
  children: ReactNode
}

/**
 * AppProvider - Wraps the app with global state
 * Combines useQuotes and useSettings hooks
 */
export function AppProvider({ children }: AppProviderProps) {
  // Quote state from useQuotes hook
  const { currentQuote, loading, error, loadNewQuote } = useQuotes()

  // Settings state from useSettings hook
  const { interval, quietStart, quietEnd, updateInterval, updateQuietHours, saveSettings } =
    useSettings()

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

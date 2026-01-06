import { useState, useEffect, useCallback } from 'react'
import type { Quote } from '@/types'
import { QuoteService } from '@/services'

/**
 * useQuotes Hook - Manages quote state and loading in React components
 *
 * Features:
 * - Automatically loads a random quote on mount
 * - Provides loading and error states
 * - Supports loading specific quotes by ID
 * - Supports loading new random quotes
 *
 * @returns Object containing quote state and loading functions
 */
export function useQuotes() {
  const [currentQuote, setCurrentQuote] = useState<Quote | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  /**
   * Load a new random quote
   * Optionally excludes the current quote to avoid immediate repeats
   */
  const loadNewQuote = useCallback(async () => {
    setLoading(true)
    setError(null)

    try {
      // Exclude current quote ID to avoid immediate repeats
      const excludeIds = currentQuote ? [currentQuote.id] : []
      const quote = await QuoteService.getRandomQuote(excludeIds)
      setCurrentQuote(quote)
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : 'Failed to load quote'
      setError(errorMessage)
      console.error('Error loading quote:', err)
    } finally {
      setLoading(false)
    }
  }, [currentQuote])

  /**
   * Load a specific quote by ID
   * @param id - The quote ID to load
   */
  const loadQuote = useCallback(async (id: number) => {
    setLoading(true)
    setError(null)

    try {
      const quote = await QuoteService.getQuoteById(id)

      if (!quote) {
        throw new Error(`Quote with ID ${id} not found`)
      }

      setCurrentQuote(quote)
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : 'Failed to load quote'
      setError(errorMessage)
      console.error('Error loading quote:', err)
    } finally {
      setLoading(false)
    }
  }, [])

  // Load initial quote on mount
  useEffect(() => {
    loadNewQuote()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []) // Only run on mount, not when loadNewQuote changes

  return {
    currentQuote,
    loading,
    error,
    loadNewQuote,
    loadQuote,
  }
}

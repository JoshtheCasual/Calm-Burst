import type { Quote } from '@/types'

/**
 * QuoteService - Manages loading and caching of inspirational quotes
 *
 * Features:
 * - Lazy loads quotes from JSON file
 * - Caches quotes in memory after first load
 * - Provides random quote selection with exclusion support
 * - Error handling for missing or invalid quote data
 */
class QuoteServiceClass {
  private quotes: Quote[] | null = null
  private loadingPromise: Promise<Quote[]> | null = null

  /**
   * Load quotes from JSON file (with caching)
   */
  private async loadQuotes(): Promise<Quote[]> {
    // Return cached quotes if already loaded
    if (this.quotes) {
      return this.quotes
    }

    // Return existing promise if already loading
    if (this.loadingPromise) {
      return this.loadingPromise
    }

    // Start loading quotes
    this.loadingPromise = (async () => {
      try {
        // Use dynamic import to load the JSON file
        const module = await import('@/assets/quotes.json')
        const rawData = module.default || module

        // Handle both { quotes: [...] } and direct array formats
        const quotesData = Array.isArray(rawData) ? rawData : rawData.quotes

        // Validate that we got an array
        if (!Array.isArray(quotesData)) {
          throw new Error('Invalid quotes data: expected an array')
        }

        // Validate quote structure
        quotesData.forEach((quote, index) => {
          if (
            typeof quote.id !== 'number' ||
            typeof quote.text !== 'string' ||
            typeof quote.author !== 'string'
          ) {
            throw new Error(
              `Invalid quote at index ${index}: missing required fields`
            )
          }
        })

        this.quotes = quotesData as Quote[]
        return this.quotes
      } catch (error) {
        this.loadingPromise = null // Reset so we can retry

        // Provide helpful error messages
        if (error instanceof Error) {
          if (error.message.includes('Cannot find module')) {
            throw new Error(
              'Quotes file not found. Please ensure quotes.json exists at src/assets/quotes.json'
            )
          }
          throw error
        }

        throw new Error('Failed to load quotes: Unknown error')
      }
    })()

    return this.loadingPromise
  }

  /**
   * Get all quotes
   * @returns Promise resolving to array of all quotes
   */
  async getAllQuotes(): Promise<Quote[]> {
    return this.loadQuotes()
  }

  /**
   * Get a random quote, optionally excluding specific IDs
   * @param excludeIds - Array of quote IDs to exclude (for avoiding repeats)
   * @returns Promise resolving to a random quote
   */
  async getRandomQuote(excludeIds: number[] = []): Promise<Quote> {
    const quotes = await this.loadQuotes()

    if (quotes.length === 0) {
      throw new Error('No quotes available')
    }

    // Filter out excluded quotes
    let availableQuotes = quotes
    if (excludeIds.length > 0) {
      availableQuotes = quotes.filter((quote) => !excludeIds.includes(quote.id))
    }

    // If all quotes are excluded, use all quotes
    if (availableQuotes.length === 0) {
      availableQuotes = quotes
    }

    // Select random quote
    const randomIndex = Math.floor(Math.random() * availableQuotes.length)
    return availableQuotes[randomIndex]
  }

  /**
   * Get a specific quote by ID
   * @param id - The quote ID to retrieve
   * @returns Promise resolving to the quote, or undefined if not found
   */
  async getQuoteById(id: number): Promise<Quote | undefined> {
    const quotes = await this.loadQuotes()
    return quotes.find((quote) => quote.id === id)
  }

  /**
   * Clear the quote cache (useful for testing or forcing reload)
   */
  clearCache(): void {
    this.quotes = null
    this.loadingPromise = null
  }
}

// Export singleton instance
export const QuoteService = new QuoteServiceClass()

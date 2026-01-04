import { Button, QuoteCard } from '@/components'
import { useAppContext } from '@/context'

export function HomeView() {
  const { currentQuote, loading, error, loadNewQuote } = useAppContext()

  return (
    <div className="flex flex-col items-center justify-center min-h-[calc(100vh-200px)] px-4">
      <div className="w-full max-w-2xl space-y-6">
        {/* Quote Display */}
        <div className="w-full" role="region" aria-label="Inspirational quote">
          {loading ? (
            <div className="card min-h-[200px] flex items-center justify-center">
              <div className="flex flex-col items-center space-y-3">
                <div
                  className="animate-spin rounded-full h-12 w-12 border-4 border-primary border-t-transparent"
                  role="status"
                  aria-label="Loading quote"
                />
                <p className="text-text-muted">Loading quote...</p>
              </div>
            </div>
          ) : error ? (
            <div className="card min-h-[200px] flex items-center justify-center">
              <div className="flex flex-col items-center space-y-3 text-center">
                <p className="text-red-500">Error: {error}</p>
                <Button variant="secondary" onClick={loadNewQuote}>
                  Try Again
                </Button>
              </div>
            </div>
          ) : currentQuote ? (
            <QuoteCard quote={currentQuote} />
          ) : (
            <div className="card min-h-[200px] flex items-center justify-center">
              <p className="text-text-muted text-center">No quote available</p>
            </div>
          )}
        </div>

        {/* New Quote Button */}
        <div className="flex justify-center">
          <Button
            variant="primary"
            onClick={loadNewQuote}
            disabled={loading}
            className="min-w-[200px] min-h-[48px] text-lg disabled:opacity-50 disabled:cursor-not-allowed"
            aria-label="Load a new inspirational quote"
          >
            {loading ? 'Loading...' : 'New Quote'}
          </Button>
        </div>

        {/* Empty State Instructions */}
        {!currentQuote && !loading && !error && (
          <div className="text-center mt-8">
            <p className="text-text-muted">
              Click &ldquo;New Quote&rdquo; to get started with your daily inspiration
            </p>
          </div>
        )}
      </div>
    </div>
  )
}

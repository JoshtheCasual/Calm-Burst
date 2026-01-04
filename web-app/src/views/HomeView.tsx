import { useState } from 'react'
import { Button } from '@/components'

// Placeholder for QuoteCard component (will be created by another agent)
interface QuoteCardProps {
  quote?: string
  author?: string
}

function QuoteCard({ quote, author }: QuoteCardProps) {
  if (!quote) {
    return (
      <div className="card min-h-[200px] flex items-center justify-center">
        <p className="text-text-muted text-center">No quote available</p>
      </div>
    )
  }

  return (
    <div className="card">
      <blockquote className="text-lg sm:text-xl text-text-light leading-relaxed mb-4">
        &ldquo;{quote}&rdquo;
      </blockquote>
      {author && (
        <footer className="text-right">
          <cite className="text-text-muted not-italic">— {author}</cite>
        </footer>
      )}
    </div>
  )
}

export function HomeView() {
  const [quote, setQuote] = useState<string | undefined>(undefined)
  const [author, setAuthor] = useState<string | undefined>(undefined)
  const [isLoading, setIsLoading] = useState(false)

  const handleNewQuote = async () => {
    setIsLoading(true)

    // Placeholder for quote loading logic (will be implemented later)
    // Simulating API call with setTimeout
    setTimeout(() => {
      // This is just placeholder data for now
      const placeholderQuotes = [
        {
          quote: 'The present moment is filled with joy and happiness. If you are attentive, you will see it.',
          author: 'Thich Nhat Hanh'
        },
        {
          quote: 'Feelings come and go like clouds in a windy sky. Conscious breathing is my anchor.',
          author: 'Thich Nhat Hanh'
        },
        {
          quote: 'Breathing in, I calm my body. Breathing out, I smile.',
          author: 'Thich Nhat Hanh'
        }
      ]

      const randomQuote = placeholderQuotes[Math.floor(Math.random() * placeholderQuotes.length)]
      setQuote(randomQuote.quote)
      setAuthor(randomQuote.author)
      setIsLoading(false)
    }, 500)
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-[calc(100vh-200px)] px-4">
      <div className="w-full max-w-2xl space-y-6">
        {/* Quote Display */}
        <div className="w-full" role="region" aria-label="Inspirational quote">
          {isLoading ? (
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
          ) : (
            <QuoteCard quote={quote} author={author} />
          )}
        </div>

        {/* New Quote Button */}
        <div className="flex justify-center">
          <Button
            variant="primary"
            onClick={handleNewQuote}
            disabled={isLoading}
            className="min-w-[200px] min-h-[48px] text-lg disabled:opacity-50 disabled:cursor-not-allowed"
            aria-label="Load a new inspirational quote"
          >
            {isLoading ? 'Loading...' : 'New Quote'}
          </Button>
        </div>

        {/* Empty State Instructions */}
        {!quote && !isLoading && (
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

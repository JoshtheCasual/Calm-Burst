import { Card } from '@/components/Card'
import { Quote } from '@/types'

interface QuoteCardProps {
  quote: Quote
  className?: string
}

export function QuoteCard({ quote, className = '' }: QuoteCardProps) {
  return (
    <Card className={`flex flex-col gap-4 ${className}`}>
      {/* Quote Text - Large and Readable */}
      <blockquote className="text-xl md:text-2xl leading-relaxed text-text font-serif">
        &ldquo;{quote.text}&rdquo;
      </blockquote>

      {/* Quote Metadata - Smaller and Secondary */}
      <div className="flex flex-col gap-1 text-sm text-text-muted">
        <cite className="not-italic font-medium text-text-light">
          — {quote.author}
        </cite>
        {quote.year && (
          <span className="text-xs">
            {quote.year}
          </span>
        )}
        {quote.context && (
          <span className="text-xs italic">
            {quote.context}
          </span>
        )}
      </div>
    </Card>
  )
}

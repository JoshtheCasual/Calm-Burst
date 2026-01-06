interface IntervalSelectorProps {
  value: number
  onChange: (value: number) => void
}

interface IntervalOption {
  label: string
  value: number
}

const intervals: IntervalOption[] = [
  { label: 'Every 1-2 hours', value: 2 },
  { label: 'Every 1-6 hours', value: 6 },
  { label: 'Every 1-12 hours', value: 12 },
  { label: 'Every 1-24 hours', value: 24 },
]

export function IntervalSelector({ value, onChange }: IntervalSelectorProps) {
  return (
    <div role="radiogroup" aria-labelledby="interval-selector-label" className="space-y-3">
      <label id="interval-selector-label" className="block text-sm font-semibold text-text mb-2">
        Notification Interval
      </label>
      {intervals.map((interval) => {
        const isSelected = value === interval.value
        return (
          <label
            key={interval.value}
            className={`
              flex items-center p-4 rounded-lg border-2 cursor-pointer transition-all duration-200
              min-h-[48px] touch-manipulation
              ${
                isSelected
                  ? 'border-primary bg-primary/10 shadow-md'
                  : 'border-secondary/30 bg-white hover:border-secondary hover:shadow-sm'
              }
            `}
          >
            <input
              type="radio"
              name="interval"
              value={interval.value}
              checked={isSelected}
              onChange={() => onChange(interval.value)}
              className="sr-only"
              aria-label={interval.label}
            />
            <div
              className={`
                flex-shrink-0 w-5 h-5 rounded-full border-2 mr-3 flex items-center justify-center transition-all
                ${
                  isSelected
                    ? 'border-primary bg-primary'
                    : 'border-secondary/50 bg-white'
                }
              `}
            >
              {isSelected && (
                <div className="w-2 h-2 bg-white rounded-full" />
              )}
            </div>
            <span
              className={`
                text-base font-medium select-none
                ${isSelected ? 'text-primary-dark' : 'text-text'}
              `}
            >
              {interval.label}
            </span>
          </label>
        )
      })}
    </div>
  )
}

import { InputHTMLAttributes } from 'react'

interface TimePickerInputProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'onChange' | 'value' | 'type'> {
  label: string
  value: string
  onChange: (value: string) => void
}

export function TimePickerInput({
  label,
  value,
  onChange,
  className = '',
  id,
  ...props
}: TimePickerInputProps) {
  // Generate a unique ID if not provided
  const inputId = id || `time-picker-${label.toLowerCase().replace(/\s+/g, '-')}`

  return (
    <div className={`flex flex-col ${className}`}>
      <label
        htmlFor={inputId}
        className="block text-sm font-semibold text-text mb-2"
      >
        {label}
      </label>
      <input
        id={inputId}
        type="time"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        aria-label={label}
        className="
          w-full px-4 py-3
          border-2 border-secondary/30 rounded-lg
          bg-white text-text
          focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20
          transition-all duration-200
          text-base
          min-h-[48px]
          touch-manipulation
        "
        {...props}
      />
    </div>
  )
}

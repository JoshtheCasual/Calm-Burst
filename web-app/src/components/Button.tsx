import { ButtonHTMLAttributes, ReactNode } from 'react'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary'
  children: ReactNode
  fullWidth?: boolean
}

export function Button({
  variant = 'primary',
  children,
  fullWidth = false,
  className = '',
  ...props
}: ButtonProps) {
  const baseClasses = fullWidth ? 'w-full' : ''
  const variantClasses = variant === 'primary' ? 'btn-primary' : 'btn-secondary'

  return (
    <button className={`${variantClasses} ${baseClasses} ${className}`} {...props}>
      {children}
    </button>
  )
}

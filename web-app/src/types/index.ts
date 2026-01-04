// Common type definitions for the Calm Burst app

export interface User {
  id: string
  name?: string
  email?: string
}

export interface BreathingPattern {
  id: string
  name: string
  inhale: number
  hold: number
  exhale: number
  cycles: number
}

export interface Settings {
  soundEnabled: boolean
  hapticEnabled: boolean
  theme: 'light' | 'dark' | 'auto'
  defaultPattern?: string
}

export interface Session {
  id: string
  patternId: string
  startTime: Date
  duration: number
  completed: boolean
}

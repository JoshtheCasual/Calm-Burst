import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card } from '@/components/Card'
import { Button } from '@/components/Button'
import { IntervalSelector } from '@/components/IntervalSelector'
import { TimePickerInput } from '@/components/TimePickerInput'

export function SettingsView() {
  const navigate = useNavigate()
  const [interval, setInterval] = useState(2)
  const [quietStart, setQuietStart] = useState('22:00')
  const [quietEnd, setQuietEnd] = useState('08:00')

  const handleSave = () => {
    // Placeholder - will be implemented with actual save logic
    console.log('Settings saved:', { interval, quietStart, quietEnd })
  }

  const handleBack = () => {
    navigate('/')
  }

  return (
    <div className="min-h-screen bg-background p-4 md:p-6">
      <div className="max-w-2xl mx-auto space-y-6">
        {/* Page Title */}
        <header className="text-center mb-8">
          <h1 className="text-3xl md:text-4xl font-bold text-text mb-2">
            Settings
          </h1>
          <p className="text-text-muted">
            Customize your notification preferences
          </p>
        </header>

        {/* Notification Interval Section */}
        <section aria-labelledby="interval-heading">
          <Card className="space-y-4">
            <h2 id="interval-heading" className="text-xl font-semibold text-text">
              Notification Schedule
            </h2>
            <p className="text-sm text-text-muted mb-2">
              How often would you like to receive calming notifications?
            </p>
            <IntervalSelector value={interval} onChange={setInterval} />
          </Card>
        </section>

        {/* Quiet Hours Section */}
        <section aria-labelledby="quiet-hours-heading">
          <Card className="space-y-4">
            <h2 id="quiet-hours-heading" className="text-xl font-semibold text-text">
              Quiet Hours
            </h2>
            <p className="text-sm text-text-muted">
              Set times when you don&apos;t want to receive notifications
            </p>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <TimePickerInput
                label="Start Time"
                value={quietStart}
                onChange={setQuietStart}
              />
              <TimePickerInput
                label="End Time"
                value={quietEnd}
                onChange={setQuietEnd}
              />
            </div>
          </Card>
        </section>

        {/* Action Buttons */}
        <div className="flex flex-col-reverse md:flex-row gap-3 pt-4">
          <Button
            variant="secondary"
            onClick={handleBack}
            className="min-h-[48px]"
            aria-label="Back to home page"
          >
            Back to Home
          </Button>
          <Button
            variant="primary"
            onClick={handleSave}
            className="min-h-[48px] md:ml-auto"
            aria-label="Save settings"
          >
            Save Settings
          </Button>
        </div>
      </div>
    </div>
  )
}

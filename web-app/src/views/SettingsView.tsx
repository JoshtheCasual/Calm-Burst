import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card } from '@/components/Card'
import { Button } from '@/components/Button'
import { IntervalSelector } from '@/components/IntervalSelector'
import { TimePickerInput } from '@/components/TimePickerInput'
import { useAppContext } from '@/context'

export function SettingsView() {
  const navigate = useNavigate()
  const {
    interval,
    quietStart,
    quietEnd,
    updateInterval,
    updateQuietHours,
    saveSettings,
    scheduleNotifications,
    requestNotificationPermission,
    notificationsEnabled,
    permissionStatus,
  } = useAppContext()

  const [showSuccessMessage, setShowSuccessMessage] = useState(false)
  const [showErrorMessage, setShowErrorMessage] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')
  const [isSaving, setIsSaving] = useState(false)

  const handleSave = async () => {
    setIsSaving(true)
    setShowSuccessMessage(false)
    setShowErrorMessage(false)

    try {
      // Save settings first
      saveSettings()

      // Request notification permission if not already granted
      if (permissionStatus !== 'granted') {
        try {
          await requestNotificationPermission()
        } catch (permError) {
          // Permission denied - show message but don't fail completely
          setErrorMessage(
            'Notification permission denied. Please enable notifications in your device settings to receive reminders.'
          )
          setShowErrorMessage(true)
          setIsSaving(false)
          return
        }
      }

      // Schedule notifications with the saved settings
      try {
        await scheduleNotifications()
        setShowSuccessMessage(true)
        console.log('Settings saved and notifications scheduled:', {
          interval,
          quietStart,
          quietEnd,
        })

        // Hide success message after 3 seconds
        setTimeout(() => {
          setShowSuccessMessage(false)
        }, 3000)
      } catch (scheduleError) {
        setErrorMessage('Settings saved, but failed to schedule notifications. Please try again.')
        setShowErrorMessage(true)
        console.error('Error scheduling notifications:', scheduleError)
      }
    } catch (error) {
      setErrorMessage('Failed to save settings. Please try again.')
      setShowErrorMessage(true)
      console.error('Error saving settings:', error)
    } finally {
      setIsSaving(false)
    }
  }

  const handleQuietStartChange = (value: string) => {
    updateQuietHours(value, quietEnd)
  }

  const handleQuietEndChange = (value: string) => {
    updateQuietHours(quietStart, value)
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

        {/* Success Message */}
        {showSuccessMessage && (
          <div
            className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative"
            role="alert"
          >
            <span className="block sm:inline">
              Settings saved and notifications scheduled successfully!
            </span>
          </div>
        )}

        {/* Error Message */}
        {showErrorMessage && (
          <div
            className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
            role="alert"
          >
            <span className="block sm:inline">{errorMessage}</span>
            <button
              className="absolute top-0 bottom-0 right-0 px-4 py-3"
              onClick={() => setShowErrorMessage(false)}
              aria-label="Close error message"
            >
              <span className="text-xl">&times;</span>
            </button>
          </div>
        )}

        {/* Notification Status Info */}
        {!notificationsEnabled && (
          <div
            className="bg-yellow-100 border border-yellow-400 text-yellow-700 px-4 py-3 rounded relative"
            role="alert"
          >
            <span className="block sm:inline">
              Notifications are currently disabled. Click &quot;Save Settings&quot; to enable
              them.
            </span>
          </div>
        )}

        {/* Notification Interval Section */}
        <section aria-labelledby="interval-heading">
          <Card className="space-y-4">
            <h2 id="interval-heading" className="text-xl font-semibold text-text">
              Notification Schedule
            </h2>
            <p className="text-sm text-text-muted mb-2">
              How often would you like to receive calming notifications?
            </p>
            <IntervalSelector value={interval} onChange={updateInterval} />
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
                onChange={handleQuietStartChange}
              />
              <TimePickerInput
                label="End Time"
                value={quietEnd}
                onChange={handleQuietEndChange}
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
            disabled={isSaving}
          >
            {isSaving ? 'Saving...' : 'Save Settings'}
          </Button>
        </div>
      </div>
    </div>
  )
}

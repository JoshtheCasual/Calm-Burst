import { useState, useEffect, useCallback } from 'react'
import { NotificationPlugin } from '@/services/notificationPlugin'
import { NotificationScheduler } from '@/services/notificationScheduler'
import { QuoteService } from '@/services/quoteService'

export type NotificationPermissionStatus = 'granted' | 'denied' | 'prompt' | 'unknown'

/**
 * useNotifications Hook - Manages native notification permissions and scheduling
 *
 * Features:
 * - Checks notification permissions on mount
 * - Requests permissions when needed
 * - Schedules quote notifications based on user settings
 * - Respects quiet hours
 * - Handles permission denied gracefully
 *
 * @returns Object containing notification state and control functions
 */
export function useNotifications() {
  const [permissionStatus, setPermissionStatus] =
    useState<NotificationPermissionStatus>('unknown')
  const [notificationsEnabled, setNotificationsEnabled] = useState<boolean>(false)
  const [isInitialized, setIsInitialized] = useState<boolean>(false)

  /**
   * Check initial permission status on mount
   */
  useEffect(() => {
    const checkInitialPermission = async () => {
      try {
        const permission = await NotificationPlugin.checkPermissions()
        setPermissionStatus(permission.display as NotificationPermissionStatus)
        setNotificationsEnabled(permission.display === 'granted')
        setIsInitialized(true)
      } catch (error) {
        console.error('[useNotifications] Error checking permissions:', error)
        setPermissionStatus('denied')
        setNotificationsEnabled(false)
        setIsInitialized(true)
      }
    }

    checkInitialPermission()
  }, [])

  /**
   * Request notification permission from the user
   */
  const requestNotificationPermission = useCallback(async (): Promise<void> => {
    try {
      const permission = await NotificationPlugin.requestPermissions()
      setPermissionStatus(permission.display as NotificationPermissionStatus)
      setNotificationsEnabled(permission.display === 'granted')

      if (permission.display !== 'granted') {
        throw new Error('Notification permission denied')
      }
    } catch (error) {
      console.error('[useNotifications] Error requesting permission:', error)
      throw error
    }
  }, [])

  /**
   * Schedule notifications based on interval and quiet hours
   * @param interval - Notification interval in hours (2, 6, 12, or 24)
   * @param quietStart - Quiet hours start time (HH:mm format)
   * @param quietEnd - Quiet hours end time (HH:mm format)
   */
  const scheduleNotifications = useCallback(
    async (interval: number, quietStart: string, quietEnd: string): Promise<void> => {
      try {
        // Check permission first
        const permission = await NotificationPlugin.checkPermissions()
        if (permission.display !== 'granted') {
          throw new Error('Notification permission not granted')
        }

        // Cancel existing notifications before scheduling new ones
        await NotificationPlugin.cancelAllNotifications()

        // Calculate next notification time using scheduler logic
        const nextNotificationTime = NotificationScheduler.scheduleNotification(
          interval,
          quietStart,
          quietEnd
        )

        // Get a random quote for the notification
        const quotes = await QuoteService.getAllQuotes()
        const randomQuote = quotes[Math.floor(Math.random() * quotes.length)]

        // Truncate quote text to 100 characters if needed
        const quoteBody =
          randomQuote.text.length > 100
            ? randomQuote.text.substring(0, 97) + '...'
            : randomQuote.text

        // Schedule the notification
        const success = await NotificationPlugin.scheduleNotification(
          1, // Notification ID
          'Calm Burst',
          quoteBody,
          nextNotificationTime
        )

        if (!success) {
          throw new Error('Failed to schedule notification')
        }

        console.log(
          `[useNotifications] Notification scheduled for ${nextNotificationTime.toISOString()}`
        )
      } catch (error) {
        console.error('[useNotifications] Error scheduling notifications:', error)
        throw error
      }
    },
    []
  )

  /**
   * Cancel all scheduled notifications
   */
  const cancelNotifications = useCallback(async (): Promise<void> => {
    try {
      await NotificationPlugin.cancelAllNotifications()
      console.log('[useNotifications] All notifications cancelled')
    } catch (error) {
      console.error('[useNotifications] Error cancelling notifications:', error)
      throw error
    }
  }, [])

  return {
    permissionStatus,
    notificationsEnabled,
    isInitialized,
    requestNotificationPermission,
    scheduleNotifications,
    cancelNotifications,
  }
}

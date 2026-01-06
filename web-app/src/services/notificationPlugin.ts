/**
 * Notification Plugin Service
 * Wrapper around Capacitor LocalNotifications for native notifications
 */

import { LocalNotifications } from '@capacitor/local-notifications'
import { Capacitor } from '@capacitor/core'

/**
 * Permission status type
 */
export type PermissionStatus = 'granted' | 'denied' | 'prompt' | 'unknown'

/**
 * Permission result with display property
 */
export interface NotificationPermission {
  display: PermissionStatus
}

/**
 * Notification options interface
 */
export interface NotificationOptions {
  id: number
  title: string
  body: string
  schedule?: {
    at: Date
  }
  sound?: string
  badge?: number
}

/**
 * Check the current notification permission status
 * @returns Permission status with display property
 */
export async function checkPermissions(): Promise<NotificationPermission> {
  // Web platform fallback
  if (!Capacitor.isNativePlatform()) {
    console.log('Web platform - returning denied permission')
    return { display: 'denied' }
  }

  try {
    const result = await LocalNotifications.checkPermissions()

    // Map Capacitor's PermissionState to our PermissionStatus
    if (result.display === 'granted') {
      return { display: 'granted' }
    } else if (result.display === 'denied') {
      return { display: 'denied' }
    } else if (result.display === 'prompt') {
      return { display: 'prompt' }
    }

    return { display: 'unknown' }
  } catch (error) {
    console.error('Error checking notification permissions:', error)
    return { display: 'unknown' }
  }
}

/**
 * Request notification permissions from the user
 * @returns Permission status after request with display property
 */
export async function requestPermissions(): Promise<NotificationPermission> {
  // Web platform fallback
  if (!Capacitor.isNativePlatform()) {
    console.log('Web platform - cannot request permission')
    return { display: 'denied' }
  }

  try {
    const result = await LocalNotifications.requestPermissions()

    if (result.display === 'granted') {
      return { display: 'granted' }
    } else if (result.display === 'denied') {
      return { display: 'denied' }
    } else if (result.display === 'prompt') {
      return { display: 'prompt' }
    }

    return { display: 'unknown' }
  } catch (error) {
    console.error('Error requesting notification permissions:', error)
    return { display: 'denied' }
  }
}

/**
 * Schedule a notification with individual parameters
 * @param id - Unique notification ID
 * @param title - Notification title
 * @param body - Notification body text
 * @param at - Date/time to show the notification
 * @returns Success status
 */
export async function scheduleNotification(
  id: number,
  title: string,
  body: string,
  at: Date
): Promise<boolean> {
  // Web platform fallback
  if (!Capacitor.isNativePlatform()) {
    console.log('Web platform - cannot schedule notifications')
    console.log('Would schedule:', { id, title, body, at })
    return false
  }

  try {
    await LocalNotifications.schedule({
      notifications: [
        {
          id,
          title,
          body,
          schedule: {
            at,
          },
        },
      ],
    })
    console.log(`Notification scheduled: ID ${id} at ${at.toISOString()}`)
    return true
  } catch (error) {
    console.error('Error scheduling notification:', error)
    return false
  }
}

/**
 * Cancel all pending notifications
 */
export async function cancelAllNotifications(): Promise<void> {
  try {
    const pending = await LocalNotifications.getPending()

    if (pending.notifications.length > 0) {
      const ids = pending.notifications.map((n) => ({ id: n.id }))
      await LocalNotifications.cancel({ notifications: ids })
    }
  } catch (error) {
    console.error('Error cancelling notifications:', error)
    throw new Error('Failed to cancel notifications')
  }
}

/**
 * Cancel a specific notification by ID
 * @param id - Notification ID to cancel
 */
export async function cancelNotification(id: number): Promise<void> {
  try {
    await LocalNotifications.cancel({ notifications: [{ id }] })
  } catch (error) {
    console.error('Error cancelling notification:', error)
    throw new Error('Failed to cancel notification')
  }
}

/**
 * Get all pending notifications
 */
export async function getPendingNotifications(): Promise<number[]> {
  try {
    const pending = await LocalNotifications.getPending()
    return pending.notifications.map((n) => n.id)
  } catch (error) {
    console.error('Error getting pending notifications:', error)
    return []
  }
}

/**
 * Notification received callback type
 */
export type NotificationReceivedCallback = (notification: {
  id: number
  title: string
  body: string
}) => void

/**
 * Add listener for when notifications are received/tapped
 * @param callback - Function to call when notification is tapped
 * @returns Promise that resolves to cleanup function
 */
export async function addNotificationReceivedListener(
  callback: NotificationReceivedCallback
): Promise<() => void> {
  // Only add listeners on native platforms
  if (!Capacitor.isNativePlatform()) {
    console.log('Web platform - notification listener not added')
    return () => {}
  }

  try {
    const listener = await LocalNotifications.addListener(
      'localNotificationActionPerformed',
      (notification) => {
        callback({
          id: notification.notification.id,
          title: notification.notification.title || '',
          body: notification.notification.body || '',
        })
      }
    )

    // Return cleanup function
    return () => {
      listener.remove()
    }
  } catch (error) {
    console.error('Error adding notification listener:', error)
    return () => {}
  }
}

/**
 * Remove all notification listeners
 * Call this on component unmount or app cleanup
 */
export async function removeAllListeners(): Promise<void> {
  try {
    await LocalNotifications.removeAllListeners()
  } catch (error) {
    console.error('Error removing listeners:', error)
  }
}

/**
 * NotificationPlugin service object
 */
export const NotificationPlugin = {
  checkPermissions,
  requestPermissions,
  scheduleNotification,
  cancelNotification,
  cancelAllNotifications,
  getPendingNotifications,
  addNotificationReceivedListener,
  removeAllListeners,
}

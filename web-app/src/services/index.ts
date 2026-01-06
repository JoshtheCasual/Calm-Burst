// Export all services from this file for easier imports
export { StorageService } from './storage'
export { StorageService as StorageServiceV2 } from './storageService'
export { QuoteService } from './quoteService'
export {
  NotificationScheduler,
  calculateNextNotificationTime,
  isWithinQuietHours,
  adjustForQuietHours,
  scheduleNotification,
} from './notificationScheduler'
export { NotificationPlugin } from './notificationPlugin'
export type {
  PermissionStatus,
  NotificationPermission,
  NotificationOptions,
  NotificationReceivedCallback,
} from './notificationPlugin'

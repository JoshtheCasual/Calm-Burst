/**
 * Notification Scheduler Service
 * Handles notification timing with quiet hours logic
 */

/**
 * Calculate the next notification time based on interval with random delay
 * Random delay logic:
 * - 2 hours: random between 1-2 hours (60-120 minutes)
 * - 6 hours: random between 1-6 hours (60-360 minutes)
 * - 12 hours: random between 1-12 hours (60-720 minutes)
 * - 24 hours: random between 1-24 hours (60-1440 minutes)
 *
 * @param intervalHours - The notification interval in hours (2, 6, 12, or 24)
 * @returns Next notification time with random delay applied
 */
export function calculateNextNotificationTime(intervalHours: number): Date {
  const now = new Date()

  // Calculate random delay in minutes
  // Minimum 60 minutes (1 hour), maximum is the full interval
  const minMinutes = 60
  const maxMinutes = intervalHours * 60
  const randomMinutes = Math.floor(Math.random() * (maxMinutes - minMinutes + 1)) + minMinutes

  // Add random delay to current time
  const nextTime = new Date(now.getTime() + randomMinutes * 60 * 1000)

  return nextTime
}

/**
 * Check if a given time falls within quiet hours
 * Handles midnight-crossing scenarios (e.g., 22:00 - 08:00)
 *
 * @param time - The time to check
 * @param quietStart - Quiet hours start time (HH:mm format)
 * @param quietEnd - Quiet hours end time (HH:mm format)
 * @returns true if time is within quiet hours, false otherwise
 */
export function isWithinQuietHours(
  time: Date,
  quietStart: string,
  quietEnd: string
): boolean {
  // Parse time components
  const timeHours = time.getHours()
  const timeMinutes = time.getMinutes()
  const timeInMinutes = timeHours * 60 + timeMinutes

  // Parse quiet hours
  const [startHour, startMinute] = quietStart.split(':').map(Number)
  const [endHour, endMinute] = quietEnd.split(':').map(Number)
  const startInMinutes = startHour * 60 + startMinute
  const endInMinutes = endHour * 60 + endMinute

  // Check if quiet hours cross midnight
  if (startInMinutes > endInMinutes) {
    // Crosses midnight (e.g., 22:00 - 08:00)
    // Quiet if time >= start OR time < end
    return timeInMinutes >= startInMinutes || timeInMinutes < endInMinutes
  } else {
    // Same day (e.g., 08:00 - 22:00)
    // Quiet if time >= start AND time < end
    return timeInMinutes >= startInMinutes && timeInMinutes < endInMinutes
  }
}

/**
 * Adjust a time if it falls within quiet hours
 * If the time is within quiet hours, move it to the end of quiet hours
 *
 * @param time - The time to potentially adjust
 * @param quietStart - Quiet hours start time (HH:mm format)
 * @param quietEnd - Quiet hours end time (HH:mm format)
 * @returns Adjusted time (or original if not in quiet hours)
 */
export function adjustForQuietHours(
  time: Date,
  quietStart: string,
  quietEnd: string
): Date {
  if (!isWithinQuietHours(time, quietStart, quietEnd)) {
    return time
  }

  // Parse quiet end time
  const [endHour, endMinute] = quietEnd.split(':').map(Number)

  // Create adjusted time
  const adjusted = new Date(time)
  adjusted.setHours(endHour, endMinute, 0, 0)

  // If quiet hours cross midnight and we're in the early morning
  // the adjusted time should be today
  const [startHour] = quietStart.split(':').map(Number)
  const [endHourParsed] = quietEnd.split(':').map(Number)

  if (startHour > endHourParsed) {
    // Quiet hours cross midnight
    const currentHours = time.getHours()

    // If current time is in the late evening (>= startHour),
    // we need to move to tomorrow's quietEnd
    if (currentHours >= startHour) {
      adjusted.setDate(adjusted.getDate() + 1)
    }
  }

  return adjusted
}

/**
 * Schedule the next notification with interval and quiet hours consideration
 * Combines random delay calculation with quiet hours adjustment
 *
 * @param intervalHours - The notification interval in hours (2, 6, 12, or 24)
 * @param quietStart - Quiet hours start time (HH:mm format, e.g., "22:00")
 * @param quietEnd - Quiet hours end time (HH:mm format, e.g., "08:00")
 * @returns The actual scheduled notification time
 */
export function scheduleNotification(
  intervalHours: number,
  quietStart: string,
  quietEnd: string
): Date {
  // Calculate next time with random delay
  const nextTime = calculateNextNotificationTime(intervalHours)

  // Adjust for quiet hours if necessary
  const scheduledTime = adjustForQuietHours(nextTime, quietStart, quietEnd)

  return scheduledTime
}

/**
 * NotificationScheduler service object for consistency with other services
 */
export const NotificationScheduler = {
  calculateNextNotificationTime,
  isWithinQuietHours,
  adjustForQuietHours,
  scheduleNotification,
}

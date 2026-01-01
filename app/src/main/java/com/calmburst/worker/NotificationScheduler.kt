package com.calmburst.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Schedules periodic notification work using WorkManager.
 *
 * This class manages the scheduling of NotificationWorker instances that deliver
 * motivational quotes at configurable intervals. It supports:
 * - Four interval modes: 1-2h, 1-6h, 1-12h, 1-24h
 * - Random delay within the interval window for natural timing
 * - Battery-aware constraints to avoid draining battery
 * - Persistent scheduling across device reboots
 *
 * @property context Application context for accessing WorkManager
 *
 * Example usage:
 * ```
 * val scheduler = NotificationScheduler(context)
 * scheduler.scheduleNotifications(intervalHours = 6)
 * // Later, to stop notifications:
 * scheduler.cancelNotifications()
 * ```
 */
class NotificationScheduler(private val context: Context) {

    companion object {
        /**
         * Unique work name for the notification worker.
         * Using a unique name ensures only one periodic work instance exists at a time.
         */
        private const val WORK_NAME = "motivational_quote_notifications"
    }

    /**
     * Schedules periodic notifications based on the specified interval.
     *
     * This method creates a PeriodicWorkRequest that:
     * - Runs every [intervalHours] hours (WorkManager's minimum interval is 15 minutes)
     * - Adds a random initial delay between 1 hour and [intervalHours] to avoid predictable timing
     * - Applies battery constraints to preserve device battery
     * - Replaces any existing scheduled work with the same name
     *
     * The random delay creates a more natural notification experience, so notifications
     * don't arrive at exactly the same time every day.
     *
     * @param intervalHours The maximum interval in hours between notifications.
     *                      Supported values: 2, 6, 12, 24 (representing 1-2h, 1-6h, 1-12h, 1-24h)
     *                      Default is 6 hours (1-6h interval mode)
     *
     * Interval mapping:
     * - 2 hours: Notifications every 1-2 hours (very frequent)
     * - 6 hours: Notifications every 1-6 hours (frequent)
     * - 12 hours: Notifications every 1-12 hours (medium)
     * - 24 hours: Notifications every 1-24 hours (infrequent)
     *
     * Example:
     * ```
     * // Schedule notifications every 1-6 hours
     * scheduler.scheduleNotifications(intervalHours = 6)
     * ```
     */
    fun scheduleNotifications(intervalHours: Int = 6) {
        // Define constraints to preserve battery life
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) // Don't run when battery is low
            .build()

        // Calculate a random initial delay between 1 hour and the max interval
        // This makes notifications feel more natural and less predictable
        val minDelayMinutes = 60L // 1 hour minimum
        val maxDelayMinutes = intervalHours * 60L // Convert hours to minutes
        val randomDelayMinutes = if (maxDelayMinutes > minDelayMinutes) {
            Random.nextLong(minDelayMinutes, maxDelayMinutes)
        } else {
            minDelayMinutes
        }

        // Create a periodic work request
        // Note: WorkManager's minimum repeat interval is 15 minutes
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            intervalHours.toLong(),
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(randomDelayMinutes, TimeUnit.MINUTES)
            .build()

        // Schedule the work, replacing any existing work with the same name
        // REPLACE policy ensures we don't have duplicate workers
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        android.util.Log.d(
            "NotificationScheduler",
            "Scheduled notifications every $intervalHours hours with initial delay of $randomDelayMinutes minutes"
        )
    }

    /**
     * Cancels all scheduled notification work.
     *
     * This method:
     * - Stops all pending and future notification work
     * - Cancels any currently running NotificationWorker instances
     * - Clears the work from WorkManager's database
     *
     * Call this when:
     * - User disables notifications in settings
     * - User logs out or deletes their account
     * - App is being uninstalled (cleanup)
     *
     * The cancellation is immediate and persistent. To resume notifications,
     * call scheduleNotifications() again.
     *
     * Example:
     * ```
     * // User toggled notifications off in settings
     * scheduler.cancelNotifications()
     * ```
     */
    fun cancelNotifications() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        android.util.Log.d("NotificationScheduler", "Cancelled all scheduled notifications")
    }

    /**
     * Checks if notifications are currently scheduled.
     *
     * @return true if notification work is scheduled, false otherwise
     *
     * Note: This is useful for UI to show whether notifications are enabled.
     * However, this method uses getWorkInfosForUniqueWork which returns a LiveData/Flow,
     * so callers should use coroutines or observe the LiveData.
     */
    fun isScheduled(): Boolean {
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(WORK_NAME)
        return try {
            val workInfoList = workInfos.get()
            workInfoList.any { !it.state.isFinished }
        } catch (e: Exception) {
            android.util.Log.e("NotificationScheduler", "Error checking work status", e)
            false
        }
    }
}

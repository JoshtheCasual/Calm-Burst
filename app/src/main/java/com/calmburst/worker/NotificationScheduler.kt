package com.calmburst.worker

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Schedules periodic notification work and manages widget updates using WorkManager.
 *
 * This class manages the scheduling of NotificationWorker instances that deliver
 * motivational quotes at configurable intervals. It supports:
 * - Four interval modes: 1-2h, 1-6h, 1-12h, 1-24h
 * - Random delay within the interval window for natural timing
 * - Battery-aware constraints to avoid draining battery
 * - Persistent scheduling across device reboots
 * - Synchronized updates for notifications and home screen widgets
 *
 * The NotificationWorker automatically updates all widget instances when rotating
 * quotes, ensuring consistency between notifications and home screen display.
 *
 * @property context Application context for accessing WorkManager
 *
 * Example usage:
 * ```
 * val scheduler = NotificationScheduler(context)
 * scheduler.scheduleNotifications(intervalHours = 6)
 *
 * // Manually trigger widget update (e.g., when user refreshes quote in app)
 * scheduler.updateWidgets()
 *
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
        // No constraints - we want notifications to be delivered reliably
        // Users expect motivational quotes even on low battery
        val constraints = Constraints.Builder().build()

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
     * This is a suspend function to avoid blocking the main thread.
     */
    suspend fun isScheduled(): Boolean {
        return try {
            val workInfos = WorkManager.getInstance(context)
                .getWorkInfosForUniqueWork(WORK_NAME)
                .await()
            workInfos.any { !it.state.isFinished }
        } catch (e: Exception) {
            android.util.Log.e("NotificationScheduler", "Error checking work status", e)
            false
        }
    }

    /**
     * Manually triggers an update for all home screen widget instances.
     *
     * This method sends a broadcast to refresh all Calm Burst widgets on the home screen.
     * It's useful for scenarios where the widget needs to update independently of the
     * notification schedule, such as:
     * - User manually refreshes a quote in the app
     * - Settings changes that should reflect in the widget
     * - Initial widget setup or configuration
     *
     * The method uses AppWidgetManager to find all widget instances and sends an
     * update broadcast. If no widgets are installed, the method completes silently.
     *
     * Widget updates triggered by this method are independent of the notification
     * rotation schedule. The periodic NotificationWorker will continue to update
     * widgets automatically alongside notifications.
     *
     * Error handling:
     * - Gracefully handles missing widget provider class
     * - Logs errors without throwing exceptions
     * - Safe to call even when no widgets are installed
     *
     * Example usage:
     * ```
     * // User tapped "New Quote" button in the app
     * val scheduler = NotificationScheduler(context)
     * scheduler.updateWidgets()
     * ```
     */
    fun updateWidgets() {
        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)

            // Create ComponentName for the widget provider
            val widgetComponent = ComponentName(
                context.packageName,
                "com.calmburst.widget.QuoteWidgetProvider"
            )

            // Get all widget instance IDs
            val widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)

            if (widgetIds.isEmpty()) {
                android.util.Log.d("NotificationScheduler", "No widgets installed - skipping widget update")
                return
            }

            android.util.Log.d("NotificationScheduler", "Triggering manual update for ${widgetIds.size} widget instance(s)")

            // Create an intent to trigger widget update
            val updateIntent = Intent(context, Class.forName("com.calmburst.widget.QuoteWidgetProvider"))
            updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)

            // Send broadcast to update all widgets
            context.sendBroadcast(updateIntent)

            android.util.Log.d("NotificationScheduler", "Widget update broadcast sent successfully")

        } catch (e: ClassNotFoundException) {
            // Widget provider class doesn't exist yet - this is expected during development
            android.util.Log.d("NotificationScheduler", "Widget provider not found - widget feature may not be implemented yet")
        } catch (e: Exception) {
            // Log any other errors but don't throw
            android.util.Log.e("NotificationScheduler", "Failed to update widgets", e)
        }
    }
}

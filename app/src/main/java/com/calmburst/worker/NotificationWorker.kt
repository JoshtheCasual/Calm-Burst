package com.calmburst.worker

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.calmburst.data.PreferencesManager
import com.calmburst.data.Quote
import com.calmburst.data.QuoteRepository
import com.calmburst.util.NotificationHelper
import kotlinx.coroutines.flow.first
import java.util.Calendar

/**
 * WorkManager worker for sending motivational quote notifications and updating home screen widgets.
 *
 * This worker is scheduled periodically by NotificationScheduler and:
 * - Checks if current time is within quiet hours
 * - Retrieves a random quote from QuoteRepository
 * - Displays the notification using NotificationHelper
 * - Updates all home screen widget instances with the same quote
 * - Saves the quote to PreferencesManager as the last shown quote
 *
 * The worker extends CoroutineWorker to support suspend functions from
 * PreferencesManager and other coroutine-based operations.
 *
 * Widget updates ensure consistency between notifications and home screen display,
 * providing a unified quote rotation experience across all app surfaces.
 *
 * @param context Application context
 * @param params Worker parameters from WorkManager
 *
 * Example WorkManager execution:
 * ```
 * val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(6, TimeUnit.HOURS)
 *     .setConstraints(constraints)
 *     .build()
 * WorkManager.getInstance(context).enqueue(workRequest)
 * ```
 */
class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    /**
     * Executes the notification work on a background thread.
     *
     * This method:
     * 1. Checks if the current time falls within quiet hours
     * 2. If in quiet hours, skips notification and returns success
     * 3. Otherwise, fetches a random quote and displays it
     * 4. Updates all home screen widget instances with the same quote
     * 5. Saves the quote to preferences for display in the app
     *
     * Widget updates happen alongside notifications to ensure the home screen
     * widget displays the same quote as the notification, providing consistency
     * across all app surfaces.
     *
     * @return Result.success() if work completed (even if skipped due to quiet hours),
     *         Result.failure() if an error occurred
     */
    override suspend fun doWork(): Result {
        return try {
            val preferencesManager = PreferencesManager(applicationContext)

            // Check if we're currently in quiet hours
            if (isInQuietHours(preferencesManager)) {
                // Skip notification during quiet hours, but return success
                // WorkManager will retry at the next scheduled interval
                android.util.Log.d("NotificationWorker", "Skipping notification - currently in quiet hours")
                return Result.success()
            }

            // Get a random quote from the repository
            val quoteRepository = QuoteRepository(applicationContext)
            val quote = quoteRepository.getRandomQuote()

            // Save the quote as the last shown quote
            preferencesManager.saveLastQuote(quote)

            // Create and show the notification
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.showNotification(quote)

            // Update home screen widgets with the same quote
            updateWidgets(quote)

            android.util.Log.d("NotificationWorker", "Notification sent: ${quote.text} - ${quote.author}")
            Result.success()
        } catch (e: Exception) {
            // Log the error and return failure
            // WorkManager may retry based on backoff policy
            android.util.Log.e("NotificationWorker", "Failed to send notification", e)
            Result.failure()
        }
    }

    /**
     * Checks if the current time falls within the user's configured quiet hours.
     *
     * Quiet hours are a time range during which notifications should not be sent.
     * This handles edge cases where the range crosses midnight (e.g., 22:00 to 07:00).
     *
     * @param preferencesManager PreferencesManager instance to retrieve quiet hours settings
     * @return true if current time is within quiet hours, false otherwise
     *
     * Example scenarios:
     * - Quiet hours: 22:00-07:00, Current: 23:30 → returns true
     * - Quiet hours: 22:00-07:00, Current: 03:00 → returns true
     * - Quiet hours: 22:00-07:00, Current: 14:00 → returns false
     * - Quiet hours: 09:00-17:00, Current: 12:00 → returns true (no midnight crossing)
     */
    private suspend fun isInQuietHours(preferencesManager: PreferencesManager): Boolean {
        // Get quiet hours from preferences
        val (startHour, startMinute) = preferencesManager.quietStartTime.first()
        val (endHour, endMinute) = preferencesManager.quietEndTime.first()

        // Get current time
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)

        // Convert times to minutes since midnight for easier comparison
        val currentMinutes = currentHour * 60 + currentMinute
        val startMinutes = startHour * 60 + startMinute
        val endMinutes = endHour * 60 + endMinute

        // Check if quiet hours cross midnight (e.g., 22:00 to 07:00)
        return if (startMinutes > endMinutes) {
            // Range crosses midnight: quiet hours are from start to 23:59 OR from 00:00 to end
            currentMinutes >= startMinutes || currentMinutes < endMinutes
        } else {
            // Normal range within the same day: quiet hours are from start to end
            currentMinutes >= startMinutes && currentMinutes < endMinutes
        }
    }

    /**
     * Updates all home screen widget instances with the provided quote.
     *
     * This method:
     * - Finds all instances of the Calm Burst widget on the home screen
     * - Sends an update broadcast to refresh each widget with the new quote
     * - Handles the case where no widgets are installed gracefully
     * - Logs the update status for debugging
     *
     * Widget updates are performed using AppWidgetManager to ensure consistency
     * between notifications and the home screen display. The same quote shown in
     * the notification will appear on all widget instances.
     *
     * If no widgets are installed, this method completes silently without errors,
     * ensuring the notification workflow continues normally.
     *
     * @param quote The Quote object to display on the widgets
     *
     * Error handling:
     * - Catches and logs any exceptions to prevent notification failures
     * - Widget update failures do not affect notification delivery
     * - Gracefully handles missing widget provider class
     */
    private fun updateWidgets(quote: Quote) {
        try {
            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)

            // Create ComponentName for the widget provider
            // The widget provider class will be com.calmburst.widget.QuoteWidgetProvider
            val widgetComponent = ComponentName(
                applicationContext.packageName,
                "com.calmburst.widget.QuoteWidgetProvider"
            )

            // Get all widget instance IDs
            val widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)

            if (widgetIds.isEmpty()) {
                android.util.Log.d("NotificationWorker", "No widgets installed - skipping widget update")
                return
            }

            android.util.Log.d("NotificationWorker", "Updating ${widgetIds.size} widget instance(s) with quote: ${quote.text}")

            // Create an intent to trigger widget update
            // The widget provider will receive this and update all instances
            val updateIntent = Intent(applicationContext, Class.forName("com.calmburst.widget.QuoteWidgetProvider"))
            updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)

            // Send broadcast to update all widgets
            applicationContext.sendBroadcast(updateIntent)

            android.util.Log.d("NotificationWorker", "Widget update broadcast sent successfully")

        } catch (e: ClassNotFoundException) {
            // Widget provider class doesn't exist yet - this is expected during development
            android.util.Log.d("NotificationWorker", "Widget provider not found - widget feature may not be implemented yet")
        } catch (e: Exception) {
            // Log any other errors but don't fail the notification
            android.util.Log.e("NotificationWorker", "Failed to update widgets (non-critical)", e)
        }
    }
}

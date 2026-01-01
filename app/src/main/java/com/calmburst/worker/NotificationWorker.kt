package com.calmburst.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.calmburst.data.PreferencesManager
import com.calmburst.data.QuoteRepository
import com.calmburst.util.NotificationHelper
import kotlinx.coroutines.flow.first
import java.util.Calendar

/**
 * WorkManager worker for sending motivational quote notifications.
 *
 * This worker is scheduled periodically by NotificationScheduler and:
 * - Checks if current time is within quiet hours
 * - Retrieves a random quote from QuoteRepository
 * - Displays the notification using NotificationHelper
 * - Saves the quote to PreferencesManager as the last shown quote
 *
 * The worker extends CoroutineWorker to support suspend functions from
 * PreferencesManager and other coroutine-based operations.
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
     * 4. Saves the quote to preferences for display in the app
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
}

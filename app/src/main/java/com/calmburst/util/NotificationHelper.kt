package com.calmburst.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.calmburst.MainActivity
import com.calmburst.R
import com.calmburst.data.Quote

/**
 * Helper class for creating and managing motivational quote notifications.
 *
 * This class handles:
 * - Creating notification channels (required for Android 8.0+)
 * - Building and displaying notifications with quote content
 * - Handling notification taps to open the app
 * - Using BigTextStyle for long quotes
 *
 * @property context Android context for accessing system services
 *
 * Example usage:
 * ```
 * val helper = NotificationHelper(context)
 * helper.createNotificationChannel()
 * val quote = Quote("Stay positive", "Author", "", "")
 * helper.showNotification(quote)
 * ```
 */
class NotificationHelper(private val context: Context) {

    companion object {
        /**
         * Unique identifier for the notification channel.
         * Used to group notifications and allow users to customize notification settings.
         */
        const val CHANNEL_ID = "motivational_quotes_channel"

        /**
         * Unique notification ID for replacing existing notifications.
         * Using a fixed ID ensures only one quote notification is shown at a time.
         */
        const val NOTIFICATION_ID = 1

        /**
         * Channel name visible to users in notification settings.
         */
        private const val CHANNEL_NAME = "Motivational Quotes"

        /**
         * Channel description explaining the purpose to users.
         */
        private const val CHANNEL_DESCRIPTION = "Periodic motivational quote notifications"
    }

    /**
     * Creates the notification channel for Android 8.0 (API 26) and above.
     *
     * Notification channels are required on Android O+ and allow users to:
     * - Control notification importance
     * - Enable/disable sounds and vibrations
     * - Customize notification behavior per channel
     *
     * This method is safe to call multiple times - if the channel exists, it will be updated.
     * Should be called early in the app lifecycle (e.g., Application.onCreate or MainActivity.onCreate).
     */
    fun createNotificationChannel() {
        // Notification channels are only required on Android 8.0 (API 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                // Enable notification sound for better user awareness
                setSound(null, null) // Use default sound
                // Disable vibration for a calmer notification experience
                enableVibration(false)
                // Show notification badge on app icon
                setShowBadge(true)
            }

            // Register the channel with the system
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Builds and displays a notification with the given quote.
     *
     * The notification:
     * - Shows the quote text as the title
     * - Shows the author as the subtitle
     * - Uses BigTextStyle to display long quotes without truncation
     * - Opens MainActivity when tapped
     * - Replaces any existing quote notification
     *
     * @param quote The Quote object to display in the notification
     *
     * Note: On Android 13+ (API 33), the POST_NOTIFICATIONS permission must be granted
     * for notifications to appear. The calling code should handle permission requests.
     */
    fun showNotification(quote: Quote) {
        // Create an intent to open MainActivity when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            // Clear the back stack and create a new task
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Create a PendingIntent with appropriate flags for API level compatibility
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification using NotificationCompat for backward compatibility
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // App icon for notification
            .setContentTitle(quote.author) // Show author as title
            .setContentText(quote.text) // Show quote text
            .setStyle(
                // Use BigTextStyle to show the full quote without truncation
                NotificationCompat.BigTextStyle()
                    .bigText(quote.text)
                    .setBigContentTitle(quote.author)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Standard priority for motivational content
            .setContentIntent(pendingIntent) // Open app when notification is tapped
            .setAutoCancel(true) // Dismiss notification when tapped
            .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Categorize as a message
            .build()

        // Display the notification using NotificationManagerCompat
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // On Android 13+, this exception is thrown if POST_NOTIFICATIONS permission is not granted
            android.util.Log.w("NotificationHelper", "Failed to show notification - permission not granted", e)
        }
    }

    /**
     * Cancels any currently displayed quote notification.
     *
     * This is useful for clearing notifications when:
     * - User disables notifications in settings
     * - User logs out or resets the app
     * - Testing notification behavior
     */
    fun cancelNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}

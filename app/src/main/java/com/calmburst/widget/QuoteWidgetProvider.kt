package com.calmburst.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.calmburst.data.PreferencesManager
import com.calmburst.data.Quote
import com.calmburst.data.QuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Widget provider for displaying motivational quotes on the home screen.
 *
 * This AppWidgetProvider manages the Calm Burst quote widget, which displays:
 * - The quote text
 * - Author name
 * - Year of the quote
 *
 * Features:
 * - Displays the last shown quote from PreferencesManager on widget load
 * - Tap to refresh: Users can tap the widget to get a new random quote
 * - Persistent quotes: The widget remembers the last quote even after device restart
 * - Error handling: Gracefully handles null quotes and XML parsing errors
 * - Material Design 3: Uses the app's brown color scheme for consistency
 *
 * Widget updates are triggered by:
 * 1. System updates (onUpdate) - e.g., when widget is added or device reboots
 * 2. User taps (onReceive with ACTION_REFRESH) - manual refresh
 *
 * Required resources:
 * - Layout: res/layout/app_widget.xml (defines widget UI structure)
 * - Widget info: res/xml/app_widget_info.xml (defines widget metadata)
 *
 * Example widget info XML:
 * ```xml
 * <appwidget-provider
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:minWidth="250dp"
 *     android:minHeight="110dp"
 *     android:updatePeriodMillis="0"
 *     android:initialLayout="@layout/widget_quote"
 *     android:resizeMode="horizontal|vertical"
 *     android:widgetCategory="home_screen"/>
 * ```
 *
 * Example widget layout structure (widget_quote.xml):
 * ```xml
 * <LinearLayout>
 *     <TextView android:id="@+id/widget_quote_text" />
 *     <TextView android:id="@+id/widget_quote_author" />
 *     <TextView android:id="@+id/widget_quote_year" />
 * </LinearLayout>
 * ```
 */
class QuoteWidgetProvider : AppWidgetProvider() {

    companion object {
        /**
         * Custom action for manual widget refresh when user taps the widget.
         */
        private const val ACTION_REFRESH = "com.calmburst.ACTION_WIDGET_REFRESH"

        /**
         * Log tag for debugging widget operations.
         */
        private const val TAG = "QuoteWidgetProvider"
    }

    /**
     * Called when widget is first added and periodically thereafter.
     *
     * This method loads the last shown quote from PreferencesManager and displays it.
     * If no quote exists, it fetches a new random quote from QuoteRepository.
     *
     * @param context Application context
     * @param appWidgetManager Manager for updating widget views
     * @param appWidgetIds Array of widget IDs to update
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        android.util.Log.d(TAG, "onUpdate called for ${appWidgetIds.size} widget(s)")

        // Update each widget instance
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    /**
     * Called when the widget receives a broadcast.
     *
     * This handles:
     * 1. System widget updates (delegates to onUpdate)
     * 2. User tap events (ACTION_REFRESH) - fetches a new quote and updates widget
     *
     * @param context Application context
     * @param intent Broadcast intent containing the action
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_REFRESH -> {
                android.util.Log.d(TAG, "Manual refresh triggered by user tap")

                // Get all widget IDs for this provider
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, QuoteWidgetProvider::class.java)
                )

                // Fetch a new quote and update all widgets
                for (appWidgetId in appWidgetIds) {
                    refreshWidget(context, appWidgetManager, appWidgetId)
                }
            }
        }
    }

    /**
     * Updates a widget with the last shown quote or a fallback quote.
     *
     * This method:
     * 1. Retrieves the last quote from PreferencesManager
     * 2. If no quote exists, fetches a new random quote
     * 3. Updates the widget UI with RemoteViews
     * 4. Sets up click listener for manual refresh
     *
     * @param context Application context
     * @param appWidgetManager Manager for updating widget views
     * @param appWidgetId ID of the widget to update
     */
    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val preferencesManager = PreferencesManager(context)
                val quoteRepository = QuoteRepository(context)

                // Try to get the last shown quote
                var quote = preferencesManager.lastQuote.first()

                // If no quote is saved, fetch a new random quote
                if (quote == null) {
                    android.util.Log.d(TAG, "No last quote found, fetching new quote")
                    quote = quoteRepository.getRandomQuote()
                    preferencesManager.saveLastQuote(quote)
                }

                // Update the widget with the quote
                updateWidgetViews(context, appWidgetManager, appWidgetId, quote)

                android.util.Log.d(TAG, "Widget updated with quote: ${quote.text.take(30)}...")
            } catch (e: Exception) {
                // Handle any errors gracefully
                android.util.Log.e(TAG, "Error updating widget", e)

                // Display a fallback quote
                val fallbackQuote = Quote(
                    text = "Keep moving forward.",
                    author = "Calm Burst",
                    year = "2026",
                    context = "Default motivational message"
                )
                updateWidgetViews(context, appWidgetManager, appWidgetId, fallbackQuote)
            }
        }
    }

    /**
     * Refreshes a widget with a new random quote.
     *
     * This method is called when the user taps the widget to get a new quote.
     * It:
     * 1. Fetches a random quote from QuoteRepository
     * 2. Saves it to PreferencesManager as the last shown quote
     * 3. Updates the widget UI with the new quote
     *
     * @param context Application context
     * @param appWidgetManager Manager for updating widget views
     * @param appWidgetId ID of the widget to refresh
     */
    private fun refreshWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val preferencesManager = PreferencesManager(context)
                val quoteRepository = QuoteRepository(context)

                // Fetch a new random quote
                val quote = quoteRepository.getRandomQuote()

                // Save it as the last shown quote
                preferencesManager.saveLastQuote(quote)

                // Update the widget with the new quote
                updateWidgetViews(context, appWidgetManager, appWidgetId, quote)

                android.util.Log.d(TAG, "Widget refreshed with new quote: ${quote.text.take(30)}...")
            } catch (e: Exception) {
                // Handle XML parsing errors or other exceptions
                android.util.Log.e(TAG, "Error refreshing widget", e)

                // Display a fallback quote on error
                val fallbackQuote = Quote(
                    text = "Keep moving forward.",
                    author = "Calm Burst",
                    year = "2026",
                    context = "Default motivational message"
                )
                updateWidgetViews(context, appWidgetManager, appWidgetId, fallbackQuote)
            }
        }
    }

    /**
     * Sanitizes quote text to prevent RemoteViews injection attacks.
     *
     * This method:
     * 1. Removes control characters except newlines and tabs
     * 2. Limits length to 500 characters to prevent UI issues
     * 3. Returns a safe string for display in RemoteViews
     *
     * @param text The text to sanitize
     * @return Sanitized text safe for RemoteViews display
     */
    private fun sanitizeQuoteText(text: String): String {
        // Remove control characters except newlines (\n) and tabs (\t)
        val cleaned = text.replace(Regex("[\\p{Cntrl}&&[^\n\t]]"), "")

        // Limit length to 500 characters
        return if (cleaned.length > 500) {
            cleaned.substring(0, 500) + "..."
        } else {
            cleaned
        }
    }

    /**
     * Updates the widget UI using RemoteViews.
     *
     * This method:
     * 1. Creates a RemoteViews object with the widget layout
     * 2. Sets the quote text, author, and year in the TextViews
     * 3. Configures a PendingIntent for tap-to-refresh functionality
     * 4. Applies the updated views to the widget
     *
     * The widget layout (res/layout/widget_quote.xml) should contain:
     * - TextView with id: widget_quote_text (for quote text)
     * - TextView with id: widget_quote_author (for author name)
     * - TextView with id: widget_quote_year (for year)
     *
     * @param context Application context
     * @param appWidgetManager Manager for updating widget views
     * @param appWidgetId ID of the widget to update
     * @param quote Quote object to display
     */
    private fun updateWidgetViews(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        quote: Quote
    ) {
        // Create RemoteViews for the widget layout
        // NOTE: This references res/layout/app_widget.xml
        val views = RemoteViews(context.packageName, R.layout.app_widget)

        // Set the quote text with sanitization to prevent injection
        views.setTextViewText(R.id.widget_quote_text, "\"${sanitizeQuoteText(quote.text)}\"")

        // Set the author and year separately with sanitization
        views.setTextViewText(R.id.widget_author_text, "— ${sanitizeQuoteText(quote.author)}")
        views.setTextViewText(R.id.widget_year_text, sanitizeQuoteText(quote.year))

        // Create a PendingIntent for tap-to-refresh
        // When user taps the widget, send ACTION_REFRESH broadcast
        val refreshIntent = Intent(context, QuoteWidgetProvider::class.java).apply {
            action = ACTION_REFRESH
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set click listener on the entire widget
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

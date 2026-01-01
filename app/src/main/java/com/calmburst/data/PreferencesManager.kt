package com.calmburst.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manages application preferences using Jetpack DataStore.
 *
 * This class provides a type-safe API for storing and retrieving app settings including:
 * - Last shown quote (text, author, year, context)
 * - Notification interval settings
 * - Quiet hours configuration (start and end times)
 * - Ad removal purchase status
 *
 * All data access is done via Flow for reactive updates and coroutines for
 * asynchronous operations.
 *
 * @property context Android context for accessing DataStore
 *
 * @constructor Creates a new PreferencesManager instance
 *
 * Example usage:
 * ```
 * val prefsManager = PreferencesManager(context)
 *
 * // Save a quote
 * lifecycleScope.launch {
 *     prefsManager.saveLastQuote(quote)
 * }
 *
 * // Observe notification interval
 * prefsManager.notificationInterval.collect { interval ->
 *     println("Current interval: $interval")
 * }
 * ```
 */
class PreferencesManager(private val context: Context) {

    // DataStore extension property for accessing preferences
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "calm_burst_prefs")

    /**
     * Preference keys for all stored data.
     *
     * These keys are used to read and write values in DataStore.
     * They are private to ensure type safety and encapsulation.
     */
    private object PreferenceKeys {
        // Last shown quote fields
        val LAST_QUOTE_TEXT = stringPreferencesKey("last_quote_text")
        val LAST_QUOTE_AUTHOR = stringPreferencesKey("last_quote_author")
        val LAST_QUOTE_YEAR = stringPreferencesKey("last_quote_year")
        val LAST_QUOTE_CONTEXT = stringPreferencesKey("last_quote_context")

        // Notification settings
        val NOTIFICATION_INTERVAL = stringPreferencesKey("notification_interval")

        // Quiet hours settings (stored as hour * 60 + minute for easier comparison)
        val QUIET_HOURS_START = intPreferencesKey("quiet_hours_start")
        val QUIET_HOURS_END = intPreferencesKey("quiet_hours_end")

        // Purchase status
        val AD_REMOVAL_PURCHASED = booleanPreferencesKey("ad_removal_purchased")
    }

    /**
     * Notification interval options.
     *
     * Defines how frequently notifications are sent to the user.
     * Each interval represents a range where notifications are sent randomly within that window.
     */
    enum class NotificationInterval(val value: String, val displayName: String, val maxHours: Int) {
        SHORT("short", "Every 1-2 Hours", 2),
        MEDIUM("medium", "Every 1-6 Hours", 6),
        LONG("long", "Every 1-12 Hours", 12),
        DAILY("daily", "Every 1-24 Hours", 24);

        companion object {
            /**
             * Converts a string value to a NotificationInterval enum.
             *
             * @param value String representation of the interval
             * @return Corresponding NotificationInterval, or MEDIUM as default
             */
            fun fromValue(value: String): NotificationInterval {
                return values().find { it.value == value } ?: MEDIUM
            }
        }
    }

    // ========== Last Quote Flows ==========

    /**
     * Flow that emits the last shown quote text.
     *
     * Emits null if no quote has been saved yet.
     */
    val lastQuoteText: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PreferenceKeys.LAST_QUOTE_TEXT] }

    /**
     * Flow that emits the last shown quote author.
     *
     * Emits null if no quote has been saved yet.
     */
    val lastQuoteAuthor: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PreferenceKeys.LAST_QUOTE_AUTHOR] }

    /**
     * Flow that emits the last shown quote year.
     *
     * Emits null if no quote has been saved yet.
     */
    val lastQuoteYear: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PreferenceKeys.LAST_QUOTE_YEAR] }

    /**
     * Flow that emits the last shown quote context.
     *
     * Emits null if no quote has been saved yet.
     */
    val lastQuoteContext: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PreferenceKeys.LAST_QUOTE_CONTEXT] }

    /**
     * Flow that emits the complete last shown quote.
     *
     * Combines all quote fields into a single Quote object.
     * Emits null if no quote has been saved yet.
     */
    val lastQuote: Flow<Quote?> = context.dataStore.data
        .map { preferences ->
            val text = preferences[PreferenceKeys.LAST_QUOTE_TEXT]
            val author = preferences[PreferenceKeys.LAST_QUOTE_AUTHOR]
            val year = preferences[PreferenceKeys.LAST_QUOTE_YEAR]
            val context = preferences[PreferenceKeys.LAST_QUOTE_CONTEXT]

            // Return Quote only if all fields are present
            if (text != null && author != null && year != null && context != null) {
                Quote(text = text, author = author, year = year, context = context)
            } else {
                null
            }
        }

    // ========== Notification Settings Flows ==========

    /**
     * Flow that emits the current notification interval setting.
     *
     * Defaults to MEDIUM (1-6 hours) if not set.
     */
    val notificationInterval: Flow<NotificationInterval> = context.dataStore.data
        .map { preferences ->
            val value = preferences[PreferenceKeys.NOTIFICATION_INTERVAL]
                ?: NotificationInterval.MEDIUM.value
            NotificationInterval.fromValue(value)
        }

    // ========== Quiet Hours Flows ==========

    /**
     * Flow that emits the quiet hours start time in minutes since midnight.
     *
     * For example, 22:30 would be stored as 1350 (22 * 60 + 30).
     * Defaults to null if not set (no quiet hours).
     */
    val quietHoursStart: Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[PreferenceKeys.QUIET_HOURS_START] }

    /**
     * Flow that emits the quiet hours end time in minutes since midnight.
     *
     * For example, 7:00 would be stored as 420 (7 * 60).
     * Defaults to null if not set (no quiet hours).
     */
    val quietHoursEnd: Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[PreferenceKeys.QUIET_HOURS_END] }

    /**
     * Flow that emits the quiet hours start time as a (hour, minute) pair.
     *
     * Defaults to (22, 0) if not set.
     */
    val quietStartTime: Flow<Pair<Int, Int>> = quietHoursStart.map { minutes ->
        if (minutes != null) {
            minutesToHourMinute(minutes)
        } else {
            Pair(22, 0) // Default: 10 PM
        }
    }

    /**
     * Flow that emits the quiet hours end time as a (hour, minute) pair.
     *
     * Defaults to (8, 0) if not set.
     */
    val quietEndTime: Flow<Pair<Int, Int>> = quietHoursEnd.map { minutes ->
        if (minutes != null) {
            minutesToHourMinute(minutes)
        } else {
            Pair(8, 0) // Default: 8 AM
        }
    }

    // ========== Purchase Status Flow ==========

    /**
     * Flow that emits the ad removal purchase status.
     *
     * Returns true if the user has purchased ad removal, false otherwise.
     */
    val adRemovalPurchased: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferenceKeys.AD_REMOVAL_PURCHASED] ?: false }

    /**
     * Alias for [adRemovalPurchased] for backward compatibility.
     */
    val adsRemoved: Flow<Boolean> = adRemovalPurchased

    // ========== Save Functions ==========

    /**
     * Saves the last shown quote to preferences.
     *
     * This should be called whenever a new quote is displayed to the user,
     * allowing the app to avoid showing the same quote repeatedly.
     *
     * @param quote The Quote object to save
     *
     * Example:
     * ```
     * lifecycleScope.launch {
     *     prefsManager.saveLastQuote(currentQuote)
     * }
     * ```
     */
    suspend fun saveLastQuote(quote: Quote) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_QUOTE_TEXT] = quote.text
            preferences[PreferenceKeys.LAST_QUOTE_AUTHOR] = quote.author
            preferences[PreferenceKeys.LAST_QUOTE_YEAR] = quote.year
            preferences[PreferenceKeys.LAST_QUOTE_CONTEXT] = quote.context
        }
    }

    /**
     * Saves the notification interval preference.
     *
     * @param interval The NotificationInterval to set
     *
     * Example:
     * ```
     * lifecycleScope.launch {
     *     prefsManager.saveNotificationInterval(NotificationInterval.EVERY_3_HOURS)
     * }
     * ```
     */
    suspend fun saveNotificationInterval(interval: NotificationInterval) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.NOTIFICATION_INTERVAL] = interval.value
        }
    }

    /**
     * Saves the quiet hours start time.
     *
     * Quiet hours prevent notifications from being sent during specified times
     * (e.g., during sleep hours).
     *
     * **SECURITY/VALIDATION**: This method validates input to prevent invalid time configurations:
     * - Hour and minute must be in valid ranges
     * - Start time cannot equal end time (would create 0-hour or 24-hour quiet period)
     * - Overnight quiet hours are allowed (e.g., 22:00 - 08:00)
     *
     * @param hour Hour in 24-hour format (0-23)
     * @param minute Minute (0-59)
     * @throws IllegalArgumentException if hour/minute out of range or start equals end
     *
     * Example:
     * ```
     * lifecycleScope.launch {
     *     // Set quiet hours to start at 22:30 (10:30 PM)
     *     prefsManager.saveQuietHoursStart(22, 30)
     * }
     * ```
     */
    suspend fun saveQuietHoursStart(hour: Int, minute: Int) {
        require(hour in 0..23) { "Hour must be between 0 and 23" }
        require(minute in 0..59) { "Minute must be between 0 and 59" }

        val startMinutes = hour * 60 + minute

        // Validate that start time doesn't equal end time
        // This prevents setting quiet hours for 0 hours or 24 hours (entire day)
        context.dataStore.data.map { preferences ->
            val endMinutes = preferences[PreferenceKeys.QUIET_HOURS_END]
            if (endMinutes != null && startMinutes == endMinutes) {
                throw IllegalArgumentException(
                    "Quiet hours start time ($hour:${minute.toString().padStart(2, '0')}) " +
                    "cannot be the same as end time. This would create an invalid quiet hours period."
                )
            }
        }.first() // Get first emission to trigger validation once

        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.QUIET_HOURS_START] = startMinutes
        }
    }

    /**
     * Saves the quiet hours end time.
     *
     * **SECURITY/VALIDATION**: This method validates input to prevent invalid time configurations:
     * - Hour and minute must be in valid ranges
     * - End time cannot equal start time (would create 0-hour or 24-hour quiet period)
     * - Overnight quiet hours are allowed (e.g., 22:00 - 08:00)
     *
     * @param hour Hour in 24-hour format (0-23)
     * @param minute Minute (0-59)
     * @throws IllegalArgumentException if hour/minute out of range or end equals start
     *
     * Example:
     * ```
     * lifecycleScope.launch {
     *     // Set quiet hours to end at 7:00 AM
     *     prefsManager.saveQuietHoursEnd(7, 0)
     * }
     * ```
     */
    suspend fun saveQuietHoursEnd(hour: Int, minute: Int) {
        require(hour in 0..23) { "Hour must be between 0 and 23" }
        require(minute in 0..59) { "Minute must be between 0 and 59" }

        val endMinutes = hour * 60 + minute

        // Validate that end time doesn't equal start time
        // This prevents setting quiet hours for 0 hours or 24 hours (entire day)
        context.dataStore.data.map { preferences ->
            val startMinutes = preferences[PreferenceKeys.QUIET_HOURS_START]
            if (startMinutes != null && endMinutes == startMinutes) {
                throw IllegalArgumentException(
                    "Quiet hours end time ($hour:${minute.toString().padStart(2, '0')}) " +
                    "cannot be the same as start time. This would create an invalid quiet hours period."
                )
            }
        }.first() // Get first emission to trigger validation once

        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.QUIET_HOURS_END] = endMinutes
        }
    }

    /**
     * Clears quiet hours settings.
     *
     * After calling this, notifications will be sent at any time.
     *
     * Example:
     * ```
     * lifecycleScope.launch {
     *     prefsManager.clearQuietHours()
     * }
     * ```
     */
    suspend fun clearQuietHours() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.QUIET_HOURS_START)
            preferences.remove(PreferenceKeys.QUIET_HOURS_END)
        }
    }

    /**
     * Saves the ad removal purchase status.
     *
     * This should be called after a successful in-app purchase to hide ads.
     *
     * @param purchased True if the user has purchased ad removal
     *
     * Example:
     * ```
     * lifecycleScope.launch {
     *     prefsManager.saveAdRemovalPurchased(true)
     * }
     * ```
     */
    suspend fun saveAdRemovalPurchased(purchased: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.AD_REMOVAL_PURCHASED] = purchased
        }
    }

    // ========== Utility Functions ==========

    /**
     * Converts minutes since midnight to an hour-minute pair.
     *
     * This is a utility function for converting the stored integer time
     * back to hours and minutes for UI display.
     *
     * @param minutes Minutes since midnight (0-1439)
     * @return Pair of (hour, minute)
     *
     * Example:
     * ```
     * val (hour, min) = minutesToHourMinute(1350) // Returns (22, 30)
     * ```
     */
    fun minutesToHourMinute(minutes: Int): Pair<Int, Int> {
        val hour = minutes / 60
        val minute = minutes % 60
        return Pair(hour, minute)
    }

    /**
     * Clears all preferences.
     *
     * This is useful for testing or when a user wants to reset the app.
     * Use with caution as this operation cannot be undone.
     *
     * Example:
     * ```
     * lifecycleScope.launch {
     *     prefsManager.clearAll()
     * }
     * ```
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

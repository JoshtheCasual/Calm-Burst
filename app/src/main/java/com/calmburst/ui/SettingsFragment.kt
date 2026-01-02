package com.calmburst.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.calmburst.MainActivity
import com.calmburst.R
import com.calmburst.data.PreferencesManager
import com.calmburst.databinding.FragmentSettingsBinding
import com.calmburst.worker.NotificationScheduler
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.util.Calendar

/**
 * Settings fragment for configuring notification preferences and app features.
 *
 * This fragment provides the following settings:
 *
 * 1. **Notification Interval**: Controls how frequently motivational quotes are displayed
 *    - Every 1-2 hours (short)
 *    - Every 1-6 hours (medium) - default
 *    - Every 1-12 hours (long)
 *    - Every 1-24 hours (daily)
 *
 * 2. **Quiet Hours**: Defines a time range when notifications should not be sent
 *    - Start time (default: 22:00 / 10 PM)
 *    - End time (default: 08:00 / 8 AM)
 *    - Uses 24-hour format internally, displays user's preferred format
 *
 * All settings are persisted using [PreferencesManager] and DataStore.
 * Changing the notification interval triggers [NotificationScheduler] to
 * reschedule the WorkManager periodic tasks.
 *
 * Design Features:
 * - Material Design 3 components (cards, buttons, radio buttons)
 * - Earthy color palette for visual consistency
 * - Minimum touch targets of 48dp for accessibility
 * - Large text sizes (18sp minimum) for readability
 * - Time picker dialogs for intuitive time selection
 *
 * @see PreferencesManager
 * @see NotificationScheduler
 * @see HomeFragment
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var notificationScheduler: NotificationScheduler

    private var startHour: Int = 22 // Default: 10 PM
    private var startMinute: Int = 0
    private var endHour: Int = 8 // Default: 8 AM
    private var endMinute: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())
        notificationScheduler = NotificationScheduler(requireContext())

        setupClickListeners()
        loadSettings()
    }

    /**
     * Sets up click listeners for all interactive elements:
     * - Time picker buttons for quiet hours
     * - Back to home navigation button
     * Note: Radio button listener is set up in loadSettings() to avoid triggering during load
     */
    private fun setupClickListeners() {
        // Quiet hours time pickers
        binding.startTimeButton.setOnClickListener {
            showTimePickerDialog(true)
        }

        binding.endTimeButton.setOnClickListener {
            showTimePickerDialog(false)
        }

        // Back button
        binding.backButton.setOnClickListener {
            navigateToHome()
        }
    }

    /**
     * Loads saved settings from PreferencesManager and updates the UI.
     * Uses .first() for one-time reads to avoid infinite collection loops.
     */
    private fun loadSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Temporarily remove listener to avoid triggering save during load
            binding.intervalRadioGroup.setOnCheckedChangeListener(null)

            // Load interval setting (one-time read)
            val interval = preferencesManager.notificationInterval.first()
            val radioButtonId = when (interval) {
                PreferencesManager.NotificationInterval.SHORT -> R.id.interval_short
                PreferencesManager.NotificationInterval.MEDIUM -> R.id.interval_medium
                PreferencesManager.NotificationInterval.LONG -> R.id.interval_long
                PreferencesManager.NotificationInterval.DAILY -> R.id.interval_daily
            }
            binding.intervalRadioGroup.check(radioButtonId)

            // Re-attach listener after loading
            binding.intervalRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                val selectedInterval = when (checkedId) {
                    R.id.interval_short -> PreferencesManager.NotificationInterval.SHORT
                    R.id.interval_medium -> PreferencesManager.NotificationInterval.MEDIUM
                    R.id.interval_long -> PreferencesManager.NotificationInterval.LONG
                    R.id.interval_daily -> PreferencesManager.NotificationInterval.DAILY
                    else -> PreferencesManager.NotificationInterval.MEDIUM
                }
                saveInterval(selectedInterval)
            }

            // Load quiet hours start time (one-time read)
            val startMinutesSinceMidnight = preferencesManager.quietHoursStart.first()
            if (startMinutesSinceMidnight != null) {
                val (hour, minute) = preferencesManager.minutesToHourMinute(startMinutesSinceMidnight)
                startHour = hour
                startMinute = minute
            } else {
                // Default values if not set
                startHour = 22
                startMinute = 0
            }
            updateStartTimeButton()

            // Load quiet hours end time (one-time read)
            val endMinutesSinceMidnight = preferencesManager.quietHoursEnd.first()
            if (endMinutesSinceMidnight != null) {
                val (hour, minute) = preferencesManager.minutesToHourMinute(endMinutesSinceMidnight)
                endHour = hour
                endMinute = minute
            } else {
                // Default values if not set
                endHour = 8
                endMinute = 0
            }
            updateEndTimeButton()
        }
    }

    /**
     * Saves the selected notification interval to preferences and reschedules notifications.
     *
     * @param interval The NotificationInterval enum value
     */
    private fun saveInterval(interval: PreferencesManager.NotificationInterval) {
        viewLifecycleOwner.lifecycleScope.launch {
            preferencesManager.saveNotificationInterval(interval)
            // Reschedule notifications with new interval (hours)
            notificationScheduler.scheduleNotifications(interval.maxHours)
        }
    }

    /**
     * Shows a TimePickerDialog for selecting quiet hours start or end time.
     *
     * @param isStartTime true for start time, false for end time
     */
    private fun showTimePickerDialog(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val currentHour = if (isStartTime) startHour else endHour
        val currentMinute = if (isStartTime) startMinute else endMinute

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                if (isStartTime) {
                    startHour = hourOfDay
                    startMinute = minute
                    updateStartTimeButton()
                    saveQuietHoursStart()
                } else {
                    endHour = hourOfDay
                    endMinute = minute
                    updateEndTimeButton()
                    saveQuietHoursEnd()
                }
            },
            currentHour,
            currentMinute,
            true // Use 24-hour format
        )

        timePickerDialog.show()
    }

    /**
     * Updates the start time button text with the selected time.
     * Formats time as HH:MM in 24-hour format.
     */
    private fun updateStartTimeButton() {
        binding.startTimeButton.text = String.format("%02d:%02d", startHour, startMinute)
    }

    /**
     * Updates the end time button text with the selected time.
     * Formats time as HH:MM in 24-hour format.
     */
    private fun updateEndTimeButton() {
        binding.endTimeButton.text = String.format("%02d:%02d", endHour, endMinute)
    }

    /**
     * Saves the quiet hours start time to preferences.
     * Converts hour and minute to minutes since midnight for storage.
     *
     * **VALIDATION**: Handles validation errors from PreferencesManager:
     * - Shows toast if start time equals end time
     * - Prevents saving invalid time configurations
     */
    private fun saveQuietHoursStart() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                preferencesManager.saveQuietHoursStart(startHour, startMinute)
            } catch (e: IllegalArgumentException) {
                // Validation failed - show user-friendly error message
                Toast.makeText(
                    requireContext(),
                    "Invalid quiet hours: Start time cannot be the same as end time",
                    Toast.LENGTH_LONG
                ).show()

                // Revert to previous value by reloading from preferences
                val minutes = preferencesManager.quietHoursStart.first()
                if (minutes != null) {
                    val (hour, minute) = preferencesManager.minutesToHourMinute(minutes)
                    startHour = hour
                    startMinute = minute
                    updateStartTimeButton()
                }
            }
        }
    }

    /**
     * Saves the quiet hours end time to preferences.
     * Converts hour and minute to minutes since midnight for storage.
     *
     * **VALIDATION**: Handles validation errors from PreferencesManager:
     * - Shows toast if end time equals start time
     * - Prevents saving invalid time configurations
     */
    private fun saveQuietHoursEnd() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                preferencesManager.saveQuietHoursEnd(endHour, endMinute)
            } catch (e: IllegalArgumentException) {
                // Validation failed - show user-friendly error message
                Toast.makeText(
                    requireContext(),
                    "Invalid quiet hours: End time cannot be the same as start time",
                    Toast.LENGTH_LONG
                ).show()

                // Revert to previous value by reloading from preferences
                val minutes = preferencesManager.quietHoursEnd.first()
                if (minutes != null) {
                    val (hour, minute) = preferencesManager.minutesToHourMinute(minutes)
                    endHour = hour
                    endMinute = minute
                    updateEndTimeButton()
                }
            }
        }
    }

    /**
     * Navigates back to the HomeFragment.
     * Uses the MainActivity's navigation method to handle the fragment transaction.
     */
    private fun navigateToHome() {
        (activity as? MainActivity)?.showHomeFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

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
            try {
                // Load all settings first (suspend operations)
                val interval = preferencesManager.notificationInterval.first()
                val startMinutesSinceMidnight = preferencesManager.quietHoursStart.first()
                val endMinutesSinceMidnight = preferencesManager.quietHoursEnd.first()

                // Check if view is still valid before updating UI
                val currentBinding = _binding ?: return@launch

                // Temporarily remove listener to avoid triggering save during load
                currentBinding.intervalRadioGroup.setOnCheckedChangeListener(null)

                // Update interval radio button
                val radioButtonId = when (interval) {
                    PreferencesManager.NotificationInterval.SHORT -> R.id.interval_short
                    PreferencesManager.NotificationInterval.MEDIUM -> R.id.interval_medium
                    PreferencesManager.NotificationInterval.LONG -> R.id.interval_long
                    PreferencesManager.NotificationInterval.DAILY -> R.id.interval_daily
                }
                currentBinding.intervalRadioGroup.check(radioButtonId)

                // Re-attach listener after loading
                currentBinding.intervalRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                    val selectedInterval = when (checkedId) {
                        R.id.interval_short -> PreferencesManager.NotificationInterval.SHORT
                        R.id.interval_medium -> PreferencesManager.NotificationInterval.MEDIUM
                        R.id.interval_long -> PreferencesManager.NotificationInterval.LONG
                        R.id.interval_daily -> PreferencesManager.NotificationInterval.DAILY
                        else -> PreferencesManager.NotificationInterval.MEDIUM
                    }
                    saveInterval(selectedInterval)
                }

                // Update quiet hours start time
                if (startMinutesSinceMidnight != null) {
                    val (hour, minute) = preferencesManager.minutesToHourMinute(startMinutesSinceMidnight)
                    startHour = hour
                    startMinute = minute
                } else {
                    startHour = 22
                    startMinute = 0
                }
                currentBinding.startTimeButton.text = String.format("%02d:%02d", startHour, startMinute)

                // Update quiet hours end time
                if (endMinutesSinceMidnight != null) {
                    val (hour, minute) = preferencesManager.minutesToHourMinute(endMinutesSinceMidnight)
                    endHour = hour
                    endMinute = minute
                } else {
                    endHour = 8
                    endMinute = 0
                }
                currentBinding.endTimeButton.text = String.format("%02d:%02d", endHour, endMinute)

            } catch (e: Exception) {
                android.util.Log.e("SettingsFragment", "Error loading settings", e)
            }
        }
    }

    /**
     * Saves the selected notification interval to preferences and reschedules notifications.
     *
     * @param interval The NotificationInterval enum value
     */
    private fun saveInterval(interval: PreferencesManager.NotificationInterval) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                preferencesManager.saveNotificationInterval(interval)
                // Reschedule notifications with new interval (hours)
                notificationScheduler.scheduleNotifications(interval.maxHours)
            } catch (e: Exception) {
                android.util.Log.e("SettingsFragment", "Error saving interval", e)
            }
        }
    }

    /**
     * Shows a TimePickerDialog for selecting quiet hours start or end time.
     *
     * @param isStartTime true for start time, false for end time
     */
    private fun showTimePickerDialog(isStartTime: Boolean) {
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
        _binding?.startTimeButton?.text = String.format("%02d:%02d", startHour, startMinute)
    }

    /**
     * Updates the end time button text with the selected time.
     * Formats time as HH:MM in 24-hour format.
     */
    private fun updateEndTimeButton() {
        _binding?.endTimeButton?.text = String.format("%02d:%02d", endHour, endMinute)
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
                context?.let {
                    Toast.makeText(
                        it,
                        "Invalid quiet hours: Start time cannot be the same as end time",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Revert to previous value by reloading from preferences
                try {
                    val minutes = preferencesManager.quietHoursStart.first()
                    if (minutes != null) {
                        val (hour, minute) = preferencesManager.minutesToHourMinute(minutes)
                        startHour = hour
                        startMinute = minute
                        _binding?.startTimeButton?.text = String.format("%02d:%02d", startHour, startMinute)
                    }
                } catch (e2: Exception) {
                    android.util.Log.e("SettingsFragment", "Error reverting start time", e2)
                }
            } catch (e: Exception) {
                android.util.Log.e("SettingsFragment", "Error saving start time", e)
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
                context?.let {
                    Toast.makeText(
                        it,
                        "Invalid quiet hours: End time cannot be the same as start time",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Revert to previous value by reloading from preferences
                try {
                    val minutes = preferencesManager.quietHoursEnd.first()
                    if (minutes != null) {
                        val (hour, minute) = preferencesManager.minutesToHourMinute(minutes)
                        endHour = hour
                        endMinute = minute
                        _binding?.endTimeButton?.text = String.format("%02d:%02d", endHour, endMinute)
                    }
                } catch (e2: Exception) {
                    android.util.Log.e("SettingsFragment", "Error reverting end time", e2)
                }
            } catch (e: Exception) {
                android.util.Log.e("SettingsFragment", "Error saving end time", e)
            }
        }
    }

    /**
     * Navigates back to the HomeFragment.
     * Pops the back stack to return to the previous fragment.
     */
    private fun navigateToHome() {
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

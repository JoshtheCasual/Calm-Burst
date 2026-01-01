package com.calmburst

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.calmburst.data.PreferencesManager
import com.calmburst.databinding.ActivityMainBinding
import com.calmburst.ui.HomeFragment

/**
 * Main activity for Calm Burst app.
 *
 * Implements a single-activity architecture with fragment-based navigation.
 * Responsibilities:
 * - Request POST_NOTIFICATIONS permission on Android 13+ (API 33+)
 * - Manage fragment navigation between HomeFragment and SettingsFragment
 * - Set up initial fragment (HomeFragment) on first launch
 *
 * @see HomeFragment
 * @see com.calmburst.ui.SettingsFragment
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager

    /**
     * Permission launcher for requesting POST_NOTIFICATIONS permission.
     * Required on Android 13+ (API 33+) to display notifications.
     */
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted - notifications can be shown
        } else {
            // Permission denied - user won't receive notifications
            // Could show a rationale dialog here if needed
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)

        // Request notification permission on Android 13+
        requestNotificationPermission()

        // Initialize fragments only on first creation
        if (savedInstanceState == null) {
            showHomeFragment()
        }
    }

    /**
     * Requests POST_NOTIFICATIONS permission on Android 13+ (API 33+).
     * On earlier Android versions, this permission is not required.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale and request permission
                    // For now, just request directly
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Request permission directly
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    /**
     * Displays the HomeFragment as the current fragment.
     * This is the default landing screen showing the last motivational quote.
     */
    fun showHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }

    /**
     * Displays the SettingsFragment as the current fragment.
     * Called when user navigates to settings.
     */
    fun showSettingsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, com.calmburst.ui.SettingsFragment())
            .addToBackStack(null)
            .commit()
    }

    /**
     * Gets the PreferencesManager instance.
     * Call this from fragments to access user preferences.
     */
    fun getPreferencesManager(): PreferencesManager {
        return preferencesManager
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

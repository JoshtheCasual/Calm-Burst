package com.calmburst

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.calmburst.data.PreferencesManager
import com.calmburst.databinding.ActivityMainBinding
import com.calmburst.ui.HomeFragment
import com.calmburst.util.BillingHelper
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.launch

/**
 * Main activity for Calm Burst app.
 *
 * Implements a single-activity architecture with fragment-based navigation.
 * Responsibilities:
 * - Request POST_NOTIFICATIONS permission on Android 13+ (API 33+)
 * - Initialize and display AdMob banner ad at the bottom
 * - Hide ads if user has purchased ad removal
 * - Manage fragment navigation between HomeFragment and SettingsFragment
 * - Set up initial fragment (HomeFragment) on first launch
 *
 * @see HomeFragment
 * @see com.calmburst.ui.SettingsFragment
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var billingHelper: BillingHelper
    private var adView: AdView? = null

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

        // Initialize BillingHelper
        initializeBilling()

        // Initialize AdMob
        initializeAdMob()

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
     * Initializes Google Play Billing for in-app purchases.
     * Sets up purchase listener to handle ad removal purchases.
     */
    private fun initializeBilling() {
        billingHelper = BillingHelper(this, preferencesManager)

        // Initialize billing with purchase update listener
        billingHelper.initialize { purchased ->
            // Hide or show ads based on purchase status
            if (purchased) {
                hideAds()
            } else {
                loadBannerAd()
            }
        }
    }

    /**
     * Initializes AdMob SDK and loads the banner ad.
     * Checks if user has purchased ad removal before displaying ads.
     */
    private fun initializeAdMob() {
        lifecycleScope.launch {
            // Check if ads are removed
            preferencesManager.adRemovalPurchased.collect { adsRemoved ->
                if (adsRemoved) {
                    // User purchased ad removal - hide ads
                    hideAds()
                } else {
                    // Show ads
                    loadBannerAd()
                }
            }
        }
    }

    /**
     * Loads and displays the AdMob banner ad at the bottom of the screen.
     * Uses test ad unit ID from BuildConfig.
     */
    private fun loadBannerAd() {
        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {}

        // Create AdView
        adView = AdView(this).apply {
            adUnitId = BuildConfig.ADMOB_BANNER_ID
            setAdSize(AdSize.BANNER)
        }

        // Add AdView to container
        binding.adContainer.removeAllViews()
        binding.adContainer.addView(adView)
        binding.adContainer.visibility = View.VISIBLE

        // Load ad
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
    }

    /**
     * Hides the ad container when user has purchased ad removal.
     */
    private fun hideAds() {
        binding.adContainer.visibility = View.GONE
        adView?.destroy()
        adView = null
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
     * Launches the in-app purchase flow for ad removal.
     * Call this from SettingsFragment when user clicks "Remove Ads" button.
     *
     * @return true if purchase flow launched successfully, false otherwise
     */
    fun launchAdRemovalPurchase(): Boolean {
        return if (billingHelper.isBillingAvailable()) {
            billingHelper.launchPurchaseFlow()
        } else {
            false
        }
    }

    /**
     * Gets the PreferencesManager instance.
     * Call this from fragments to access user preferences.
     */
    fun getPreferencesManager(): PreferencesManager {
        return preferencesManager
    }

    override fun onPause() {
        adView?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
    }

    override fun onDestroy() {
        adView?.destroy()
        billingHelper.endConnection()
        super.onDestroy()
    }
}

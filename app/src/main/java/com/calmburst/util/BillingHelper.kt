package com.calmburst.util

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import com.calmburst.data.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Helper class for managing Google Play Billing
 *
 * Handles:
 * - BillingClient initialization and connection
 * - Product details query for "remove_ads" product
 * - Purchase flow launch and result handling
 * - Purchase acknowledgment and verification
 * - Ad removal status persistence via PreferencesManager
 *
 * Usage:
 * 1. Create instance: val billingHelper = BillingHelper(activity, preferencesManager)
 * 2. Initialize: billingHelper.initialize(onPurchaseUpdate = { ... })
 * 3. Launch purchase: billingHelper.launchPurchaseFlow()
 * 4. Check status: billingHelper.shouldShowAds()
 * 5. Clean up: billingHelper.endConnection()
 *
 * **PRODUCTION LOGGING NOTE**:
 * This class contains debug logging statements (Log.d, Log.w, Log.e) that will appear
 * in production builds. For enhanced security and performance:
 *
 * Option 1: Use ProGuard to strip debug logs (recommended)
 * Add to proguard-rules.pro:
 * ```
 * -assumenosideeffects class android.util.Log {
 *     public static *** d(...);
 *     public static *** v(...);
 * }
 * ```
 *
 * Option 2: Wrap logs in BuildConfig.DEBUG checks:
 * ```
 * if (BuildConfig.DEBUG) {
 *     Log.d(TAG, "Debug message")
 * }
 * ```
 *
 * Option 3: Create a custom Logger wrapper (see security audit report)
 *
 * Currently, Log.e() statements are kept for error tracking in production,
 * but consider using a crash reporting service (Firebase Crashlytics) instead.
 *
 * @param activity The activity context for launching purchase flows
 * @param preferencesManager Manager for persisting ad removal status
 */
class BillingHelper(
    private val activity: Activity,
    private val preferencesManager: PreferencesManager
) {

    companion object {
        private const val TAG = "BillingHelper"

        /**
         * Product ID for ad removal (one-time purchase)
         * Configure this product in Google Play Console with price $1.00 USD
         */
        const val PRODUCT_ID_REMOVE_ADS = "remove_ads"
    }

    private var billingClient: BillingClient? = null
    private var productDetails: ProductDetails? = null
    private var onPurchaseUpdateListener: ((Boolean) -> Unit)? = null

    // Track billing client state
    private var isConnected = false
    private var isInitialized = false

    /**
     * Initialize the BillingClient and set up purchase listeners
     *
     * @param onPurchaseUpdate Callback invoked when purchase status changes (true = purchased, false = not purchased)
     */
    fun initialize(onPurchaseUpdate: (Boolean) -> Unit) {
        this.onPurchaseUpdateListener = onPurchaseUpdate

        // Create BillingClient with purchases updated listener
        billingClient = BillingClient.newBuilder(activity)
            .setListener { billingResult, purchases ->
                handlePurchasesUpdate(billingResult, purchases)
            }
            .enablePendingPurchases() // Required to handle pending purchases
            .build()

        // Start connection to Google Play Billing
        startConnection()
    }

    /**
     * Start connection to Google Play Billing service
     */
    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing client connected successfully")
                    isConnected = true

                    // Query existing purchases to restore purchase state
                    queryExistingPurchases()

                    // Query product details for the remove_ads product
                    queryProductDetails()
                } else {
                    Log.e(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                    isConnected = false
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
                isConnected = false
                // Implement retry logic with exponential backoff if needed
                // For simplicity, we'll let the user retry by reopening the app
            }
        })
    }

    /**
     * Query product details from Google Play
     * Required before launching purchase flow
     */
    private fun queryProductDetails() {
        if (!isConnected) {
            Log.e(TAG, "Cannot query products: billing client not connected")
            return
        }

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_REMOVE_ADS)
                .setProductType(BillingClient.ProductType.INAPP) // One-time in-app purchase
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (productDetailsList.isNotEmpty()) {
                    productDetails = productDetailsList[0]
                    isInitialized = true
                    Log.d(TAG, "Product details loaded: ${productDetails?.name}")
                } else {
                    Log.w(TAG, "No product details found for $PRODUCT_ID_REMOVE_ADS")
                }
            } else {
                Log.e(TAG, "Failed to query product details: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Query existing purchases to restore purchase state
     * Call this on app startup to check if user already purchased ad removal
     */
    private fun queryExistingPurchases() {
        if (!isConnected) {
            Log.e(TAG, "Cannot query purchases: billing client not connected")
            return
        }

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient?.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Queried ${purchases.size} existing purchases")

                // Check if user has purchased ad removal
                val hasAdRemoval = purchases.any { purchase ->
                    purchase.products.contains(PRODUCT_ID_REMOVE_ADS) &&
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }

                // Save purchase status and notify listener
                CoroutineScope(Dispatchers.Main).launch {
                    savePurchaseStatus(hasAdRemoval)
                }
            } else {
                Log.e(TAG, "Failed to query purchases: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Launch purchase flow for ad removal product
     *
     * Prerequisites:
     * - Billing client must be connected
     * - Product details must be loaded
     *
     * @return true if purchase flow launched successfully, false otherwise
     */
    fun launchPurchaseFlow(): Boolean {
        if (!isConnected) {
            Log.e(TAG, "Cannot launch purchase: billing client not connected")
            return false
        }

        if (!isInitialized || productDetails == null) {
            Log.e(TAG, "Cannot launch purchase: product details not loaded")
            return false
        }

        // Build purchase flow params
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails!!)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        // Launch billing flow
        val billingResult = billingClient?.launchBillingFlow(activity, billingFlowParams)

        return if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.d(TAG, "Purchase flow launched successfully")
            true
        } else {
            Log.e(TAG, "Failed to launch purchase flow: ${billingResult?.debugMessage}")
            false
        }
    }

    /**
     * Handle purchase updates from BillingClient
     * Called when user completes or cancels a purchase
     *
     * @param billingResult Result of the purchase operation
     * @param purchases List of purchases (may be null)
     */
    private fun handlePurchasesUpdate(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // Purchase successful, process purchases
                purchases?.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        // Verify and acknowledge purchase
                        handlePurchase(purchase)
                    } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                        // Payment pending (e.g., waiting for bank transfer)
                        Log.d(TAG, "Purchase pending for: ${purchase.products}")
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                // User canceled the purchase - no action needed
                Log.d(TAG, "User canceled purchase")
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // User already owns this item
                Log.d(TAG, "Item already owned")
                CoroutineScope(Dispatchers.Main).launch {
                    savePurchaseStatus(true)
                }
            }
            else -> {
                // Handle other error cases
                Log.e(TAG, "Purchase failed: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Handle a completed purchase
     * Verify signature and acknowledge purchase
     *
     * **SECURITY WARNING - CRITICAL**: This implementation only performs CLIENT-SIDE validation.
     * This is INSECURE for production and vulnerable to purchase fraud.
     *
     * **PRODUCTION REQUIREMENTS**:
     * Before deploying to production, you MUST implement server-side purchase verification:
     *
     * 1. Send the purchase token to your secure backend server
     * 2. Use Google Play Developer API to verify the purchase on your server
     * 3. Validate the purchase signature using Google's public key
     * 4. Check purchase state, order ID, and product ID server-side
     * 5. Only unlock features after receiving server confirmation
     * 6. Store purchase verification status on your server
     *
     * **WHY THIS IS CRITICAL**:
     * - Attackers can modify the APK to bypass client-side checks
     * - Purchase tokens can be intercepted and replayed
     * - Client-side validation can be manipulated
     * - This violates Google Play's security policies
     * - Can result in revenue loss and Play Store suspension
     *
     * **REFERENCE**: https://developer.android.com/google/play/billing/security
     *
     * @param purchase The purchase to handle
     */
    private fun handlePurchase(purchase: Purchase) {
        // Check if purchase contains our product
        if (!purchase.products.contains(PRODUCT_ID_REMOVE_ADS)) {
            Log.w(TAG, "Purchase does not contain $PRODUCT_ID_REMOVE_ADS")
            return
        }

        // ⚠️ SECURITY WARNING: This is client-side only validation - INSECURE for production!
        // TODO: Implement server-side verification before production deployment

        // For production, verify the purchase on your server BEFORE acknowledging
        // See verifyPurchaseOnServer() method below for implementation guidance

        if (!purchase.isAcknowledged) {
            // TODO: Replace this with server verification:
            // verifyPurchaseOnServer(purchase.purchaseToken, purchase.signature) { isValid ->
            //     if (isValid) {
            //         acknowledgePurchase(purchase)
            //     } else {
            //         Log.e(TAG, "Server verification failed - possible fraud attempt")
            //     }
            // }

            acknowledgePurchase(purchase)
        } else {
            // Already acknowledged, just save the status
            CoroutineScope(Dispatchers.Main).launch {
                savePurchaseStatus(true)
            }
        }
    }

    /**
     * **STUB METHOD**: Verify purchase on server (NOT IMPLEMENTED)
     *
     * **SECURITY - CRITICAL**: This method MUST be implemented before production deployment.
     *
     * Implementation steps:
     *
     * 1. Create a secure backend API endpoint (HTTPS only)
     * 2. Send purchase data to your server:
     *    - purchaseToken (from Google Play)
     *    - signature (from Google Play)
     *    - orderId
     *    - productId
     *    - user identifier (if applicable)
     *
     * 3. On your server:
     *    - Verify the signature using Google's public key
     *    - Call Google Play Developer API to validate the purchase:
     *      GET https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{packageName}/purchases/products/{productId}/tokens/{purchaseToken}
     *    - Check purchase state is PURCHASED (0)
     *    - Verify the purchase hasn't been consumed or refunded
     *    - Store the verification result in your database
     *
     * 4. Return verification result to the app
     * 5. Only unlock features if server returns success
     *
     * **Example API call structure**:
     * ```
     * POST https://your-backend.com/api/verify-purchase
     * {
     *   "purchaseToken": "token_from_google",
     *   "signature": "signature_from_google",
     *   "productId": "remove_ads",
     *   "orderId": "GPA.1234-5678-9012-34567"
     * }
     * ```
     *
     * **Security best practices**:
     * - Use HTTPS only (TLS 1.2+)
     * - Implement rate limiting to prevent abuse
     * - Use Google Play Developer API service account authentication
     * - Log all verification attempts for fraud detection
     * - Implement idempotency to prevent duplicate processing
     * - Set up alerts for failed verifications
     *
     * @param purchaseToken The purchase token from Google Play
     * @param signature The purchase signature from Google Play
     * @param onResult Callback with verification result (true if valid, false if invalid)
     *
     * TODO: Implement this method with your backend API before production deployment
     */
    private fun verifyPurchaseOnServer(
        purchaseToken: String,
        signature: String,
        onResult: (Boolean) -> Unit
    ) {
        // TODO: Implement server-side verification
        // This is a stub method - implementation required for production

        // Example implementation (replace with your actual API):
        // val apiService = RetrofitClient.create()
        // CoroutineScope(Dispatchers.IO).launch {
        //     try {
        //         val response = apiService.verifyPurchase(
        //             purchaseToken = purchaseToken,
        //             signature = signature,
        //             productId = PRODUCT_ID_REMOVE_ADS
        //         )
        //         withContext(Dispatchers.Main) {
        //             onResult(response.isValid)
        //         }
        //     } catch (e: Exception) {
        //         Log.e(TAG, "Server verification failed", e)
        //         withContext(Dispatchers.Main) {
        //             onResult(false) // Fail securely - deny on error
        //         }
        //     }
        // }

        Log.e(TAG, "SECURITY WARNING: verifyPurchaseOnServer() not implemented - using insecure client-side validation")
        onResult(true) // INSECURE - always returns true without verification
    }

    /**
     * Acknowledge a purchase
     * Required for all non-consumable products
     *
     * @param purchase The purchase to acknowledge
     */
    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged successfully")
                // Save purchase status to preferences
                CoroutineScope(Dispatchers.Main).launch {
                    savePurchaseStatus(true)
                }
            } else {
                Log.e(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Save purchase status to PreferencesManager
     * Also notifies the listener about the status change
     *
     * @param purchased true if ad removal was purchased, false otherwise
     */
    private suspend fun savePurchaseStatus(purchased: Boolean) {
        withContext(Dispatchers.IO) {
            preferencesManager.saveAdRemovalPurchased(purchased)
        }

        // Notify listener on main thread
        withContext(Dispatchers.Main) {
            onPurchaseUpdateListener?.invoke(purchased)
        }
    }

    /**
     * Check if ads should be shown
     * Returns true if ads should be shown (user hasn't purchased ad removal)
     *
     * Note: This is a synchronous check. For reactive updates, use PreferencesManager.adRemovalPurchased Flow
     *
     * @return true if ads should be shown, false if user purchased ad removal
     */
    suspend fun shouldShowAds(): Boolean {
        return withContext(Dispatchers.IO) {
            // Check preferences - use first() to get single value instead of collecting indefinitely
            val adsRemoved = preferencesManager.adRemovalPurchased.first()
            !adsRemoved
        }
    }

    /**
     * Check if billing is available
     * Useful for disabling purchase UI if billing is not supported
     *
     * @return true if billing is connected and ready, false otherwise
     */
    fun isBillingAvailable(): Boolean {
        return isConnected && isInitialized
    }

    /**
     * End billing client connection
     * Call this in Activity's onDestroy()
     */
    fun endConnection() {
        billingClient?.endConnection()
        billingClient = null
        isConnected = false
        isInitialized = false
        Log.d(TAG, "Billing client disconnected")
    }
}

# Build and Deployment Guide - Calm Burst

Complete guide for building, testing, and deploying the Calm Burst motivational notification Android app.

---

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Initial Setup](#initial-setup)
3. [Building the App](#building-the-app)
4. [Testing](#testing)
5. [Generating APK](#generating-apk)
6. [Production Deployment](#production-deployment)
7. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software
- **Android Studio**: Arctic Fox (2020.3.1) or later
- **Java Development Kit (JDK)**: JDK 17 or later
- **Android SDK**: API Level 34 (Android 14)
- **Gradle**: 8.5+ (included via wrapper)

### Required Accounts
- **Google Play Console**: For app distribution
- **Google AdMob**: For ad integration ([admob.google.com](https://admob.google.com))
- **Google Cloud Platform**: For server-side purchase verification (optional for beta)

### Hardware
- **Development**: Any modern PC (Windows, Mac, Linux)
- **Testing Device**: Android 8.0+ (API 26+) physical device or emulator

---

## Initial Setup

### 1. Clone the Repository

```bash
git clone https://github.com/JoshtheCasual/Calm-Burst.git
cd Calm-Burst
```

### 2. Configure Android SDK

Update `local.properties` with your Android SDK location:

```properties
sdk.dir=/path/to/Android/sdk
```

**Find SDK Location:**
- **Mac**: `~/Library/Android/sdk`
- **Linux**: `~/Android/Sdk`
- **Windows**: `C:\Users\<username>\AppData\Local\Android\Sdk`

### 3. Configure AdMob

#### For Development (Testing):
```properties
# local.properties
ADMOB_APP_ID=ca-app-pub-3940256099942544~3347511713
ADMOB_BANNER_ID=ca-app-pub-3940256099942544/6300978111
```

#### For Production:
1. Create an AdMob account at [admob.google.com](https://admob.google.com)
2. Register your app
3. Create a banner ad unit
4. Update `local.properties` with real IDs:

```properties
ADMOB_APP_ID=ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy
ADMOB_BANNER_ID=ca-app-pub-xxxxxxxxxxxxxxxx/zzzzzzzzzz
```

### 4. Setup Gradle Wrapper (if missing)

```bash
gradle wrapper --gradle-version 8.5
```

---

## Building the App

### Using Android Studio

1. **Open Project**:
   - File → Open → Select `Calm-Burst` folder
   - Wait for Gradle sync to complete

2. **Build Debug APK**:
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Output: `app/build/outputs/apk/debug/app-debug.apk`

3. **Run on Device**:
   - Run → Run 'app'
   - Select connected device or emulator

### Using Command Line

#### Clean Build
```bash
./gradlew clean
```

#### Build Debug APK
```bash
./gradlew assembleDebug
```
**Output**: `app/build/outputs/apk/debug/app-debug.apk`

#### Build Release APK
```bash
./gradlew assembleRelease
```
**Output**: `app/build/outputs/apk/release/app-release.apk`

#### Install on Connected Device
```bash
./gradlew installDebug
```

---

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests (requires connected device)
```bash
./gradlew connectedAndroidTest
```

### Manual Testing Checklist

#### First Launch
- [ ] App launches successfully
- [ ] POST_NOTIFICATIONS permission requested (Android 13+)
- [ ] Default quote displayed or empty state shown
- [ ] AdMob test banner loads at bottom

#### Notification System
- [ ] Navigate to Settings
- [ ] Select notification interval (test all 4 options)
- [ ] Wait for notification to appear
- [ ] Tap notification → app opens to last quote
- [ ] Verify quote updates in main screen

#### Quiet Hours
- [ ] Set quiet hours (e.g., 22:00 - 08:00)
- [ ] Verify no notifications during quiet hours
- [ ] Verify notifications resume after quiet hours end
- [ ] Test edge cases (midnight crossing)

#### Ad Removal Purchase
- [ ] Click "Remove Ads" in Settings
- [ ] Complete test purchase flow
- [ ] Verify banner ad disappears
- [ ] Restart app → verify ad stays hidden
- [ ] Check "Ads Removed" status in Settings

#### Accessibility
- [ ] Enable TalkBack → verify all elements accessible
- [ ] Increase text size → verify UI remains usable
- [ ] Verify minimum touch targets (48dp)
- [ ] Check color contrast (WCAG AA)

---

## Generating APK

### Debug APK (for testing)

**Build:**
```bash
./gradlew assembleDebug
```

**Install:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Distribution:**
- Share APK file directly
- No signing required
- Not suitable for Play Store

### Release APK (for production)

#### 1. Generate Upload Keystore (first time only)

```bash
keytool -genkey -v -keystore upload-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias calm-burst-upload
```

**Security**: Store keystore file and password securely. **Never commit to git**.

#### 2. Configure Signing

Create `keystore.properties` in project root (git-ignored):

```properties
storeFile=/path/to/upload-keystore.jks
storePassword=your_store_password
keyAlias=calm-burst-upload
keyPassword=your_key_password
```

#### 3. Update build.gradle.kts

```kotlin
// In app/build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... existing config
        }
    }
}
```

#### 4. Build Signed Release APK

```bash
./gradlew assembleRelease
```

**Output**: `app/build/outputs/apk/release/app-release.apk`

#### 5. Verify APK

```bash
# Check signature
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk

# Check APK contents
unzip -l app/build/outputs/apk/release/app-release.apk
```

---

## Production Deployment

### Pre-Production Checklist

#### Code Preparation
- [ ] Update version code and name in `build.gradle.kts`
- [ ] Replace test AdMob IDs with production IDs
- [ ] Implement server-side purchase verification (see BillingHelper.kt)
- [ ] Remove or disable debug logging
- [ ] Update app icon (replace placeholder)
- [ ] Test on multiple devices (min API 26+)

#### Google Play Console Setup
- [ ] Create app listing
- [ ] Upload screenshots (phone, tablet, TV if applicable)
- [ ] Write app description and release notes
- [ ] Set pricing ($0.00 - free with IAP)
- [ ] Configure in-app product: "remove_ads" at $1.00
- [ ] Set content rating
- [ ] Complete privacy policy

#### Security Verification
- [ ] Review SECURITY_AUDIT_REPORT.md
- [ ] Confirm network_security_config.xml enforces HTTPS
- [ ] Verify ProGuard enabled in release build
- [ ] Test XXE protection (quotes.xml parsing)
- [ ] Implement server-side purchase verification
- [ ] Review all permissions (only POST_NOTIFICATIONS, INTERNET)

### Upload to Play Console

#### 1. Generate App Bundle (Recommended)

```bash
./gradlew bundleRelease
```

**Output**: `app/build/outputs/bundle/release/app-release.aab`

#### 2. Upload to Play Console

1. Login to [Google Play Console](https://play.google.com/console)
2. Select your app
3. Production → Create new release
4. Upload `app-release.aab`
5. Add release notes
6. Review and roll out

#### 3. Configure In-App Products

1. Monetize → In-app products → Create product
2. **Product ID**: `remove_ads`
3. **Name**: "Remove Ads"
4. **Description**: "Remove all advertisements and support the creator"
5. **Price**: $1.00 USD
6. **Status**: Active
7. Save and activate

### Beta Testing (Recommended)

**Internal Testing Track:**
1. Create internal testing release
2. Add tester emails
3. Share opt-in link
4. Collect feedback
5. Iterate and fix issues

**Closed/Open Beta:**
1. Create closed/open testing release
2. Invite beta testers or make public
3. Monitor crash reports and feedback
4. Address issues before production launch

---

## Server-Side Purchase Verification

**⚠️ CRITICAL**: The app currently uses client-side purchase validation only. Implement server verification before production.

### Implementation Steps

#### 1. Create Backend API

```python
# Example: Python Flask endpoint
from flask import Flask, request, jsonify
import google.auth
from google.oauth2 import service_account
from googleapiclient.discovery import build

app = Flask(__name__)

@app.route('/verify-purchase', methods=['POST'])
def verify_purchase():
    data = request.json
    token = data['token']
    product_id = data['productId']

    # Verify with Google Play Developer API
    service = build('androidpublisher', 'v3', credentials=credentials)
    result = service.purchases().products().get(
        packageName='com.calmburst',
        productId=product_id,
        token=token
    ).execute()

    # Check purchase state
    if result['purchaseState'] == 0:  # Purchased
        return jsonify({'valid': True})
    else:
        return jsonify({'valid': False})
```

#### 2. Update BillingHelper.kt

See `verifyPurchaseOnServer()` stub method in BillingHelper.kt for implementation guidance.

#### 3. Secure Backend

- Use HTTPS only
- Implement rate limiting
- Validate request signatures
- Store service account credentials securely
- Monitor for fraud attempts

---

## Troubleshooting

### Build Issues

**Problem**: Gradle sync fails
```
Solution:
- Ensure Android SDK installed
- Update local.properties with correct sdk.dir
- Run: ./gradlew clean build
```

**Problem**: Missing Gradle wrapper JAR
```
Solution:
- Run: gradle wrapper --gradle-version 8.5
```

**Problem**: Build config errors (ADMOB_APP_ID)
```
Solution:
- Ensure local.properties exists with AdMob IDs
- Check BuildConfig.ADMOB_APP_ID is generated
```

### Runtime Issues

**Problem**: Notifications not appearing
```
Solution:
- Check POST_NOTIFICATIONS permission granted (Android 13+)
- Verify notification channel created
- Check quiet hours settings
- Review WorkManager status: adb shell dumpsys jobscheduler
```

**Problem**: AdMob banner not loading
```
Solution:
- Verify INTERNET permission in manifest
- Check AdMob app ID in manifest
- Review logcat for AdMob errors
- Ensure test device is not on ad blocker
```

**Problem**: Purchase flow not launching
```
Solution:
- Ensure app signed with correct keystore
- Check Google Play Billing Library initialized
- Verify product ID "remove_ads" exists in Play Console
- Test on device with Google Play Services
```

### Deployment Issues

**Problem**: Upload rejected (duplicate version code)
```
Solution:
- Increment versionCode in build.gradle.kts
- Rebuild and re-upload
```

**Problem**: APK too large
```
Solution:
- Enable ProGuard/R8 (should already be enabled)
- Use App Bundle instead of APK
- Review resource usage
```

---

## Version Management

### Version Code and Name

Update in `app/build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        versionCode = 1      // Increment for each release
        versionName = "1.0.0" // Semantic versioning
    }
}
```

**Versioning Strategy**:
- **versionCode**: Integer, increments with each upload (1, 2, 3...)
- **versionName**: User-facing string (1.0.0, 1.1.0, 2.0.0)
  - **MAJOR**: Breaking changes
  - **MINOR**: New features (backward compatible)
  - **PATCH**: Bug fixes and minor updates

---

## Additional Resources

### Documentation
- [Android Developer Guide](https://developer.android.com)
- [Google Play Console Help](https://support.google.com/googleplay/android-developer)
- [AdMob Documentation](https://developers.google.com/admob)
- [Play Billing Library](https://developer.android.com/google/play/billing)

### Project Files
- **FRAMEWORK.md**: Technical architecture
- **SECURITY_AUDIT_REPORT.md**: Security findings and recommendations
- **CHANGE_NOTES.md**: Version history
- **README.md**: Project overview

### Support
- **Issues**: [GitHub Issues](https://github.com/JoshtheCasual/Calm-Burst/issues)
- **Discussions**: [GitHub Discussions](https://github.com/JoshtheCasual/Calm-Burst/discussions)

---

## Next Steps

1. **Complete Initial Setup** (SDK, AdMob, keystore)
2. **Build Debug APK** and test on device
3. **Implement Server Verification** for purchases
4. **Beta Test** with internal testers
5. **Upload to Play Console** (production track)
6. **Monitor** crash reports and user feedback
7. **Iterate** with updates and improvements

---

**Last Updated**: 2026-01-01
**Project Version**: 1.0.0
**Build Tools**: Gradle 8.5, Android SDK 34

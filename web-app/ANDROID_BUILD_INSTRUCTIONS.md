# Android Build Instructions for Calm Burst v2.0.0

## Prerequisites

### Required Software
- Node.js 18+ and npm
- Android Studio (latest version)
- JDK 11 or later
- Android SDK (API 26+, automatically installed with Android Studio)

### Required Accounts
- Google Play Console account (for distribution)
- Free for development/testing

## Project Setup

### 1. Install Dependencies
```bash
cd web-app
npm install
```

### 2. Build Web Assets
```bash
npm run build
```

### 3. Sync Capacitor
```bash
npx cap sync android
```

## Opening in Android Studio

```bash
npx cap open android
```

This opens the Android project at `android/`

## Configuration in Android Studio

### 1. SDK & Build Tools

1. Go to **File > Project Structure > SDK Location**
2. Ensure Android SDK path is set
3. Go to **SDK Manager** (Tools > SDK Manager)
4. Verify installed:
   - Android SDK Platform 34 (Android 14)
   - Android SDK Build-Tools 34.0.0+
   - Android SDK Platform-Tools

### 2. Gradle Sync

1. Android Studio will prompt to sync Gradle
2. Click **Sync Now**
3. Wait for sync to complete (may download dependencies)

### 3. Verify AndroidManifest.xml

The following permissions are already configured:
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
<uses-permission android:name="android.permission.VIBRATE"/>
```

### 4. App Icons (Optional but Recommended)

Icons are located in:
```
app/src/main/res/
├── mipmap-hdpi/
├── mipmap-mdpi/
├── mipmap-xhdpi/
├── mipmap-xxhdpi/
└── mipmap-xxxhdpi/
```

**Current Status**: Using default Capacitor icons

### 5. Version and Build Numbers

Edit `app/build.gradle`:
```gradle
defaultConfig {
    versionCode 1
    versionName "2.0.0"
}
```

## Building and Testing

### Run on Emulator

1. Create/start an emulator:
   - Tools > Device Manager
   - Create Virtual Device (Pixel 5 or newer recommended)
   - API Level 26+ (Android 8.0+)
2. Click **Run** button (▶) or press `Shift+F10`
3. App will launch in emulator

### Run on Physical Device

1. Enable Developer Options on Android device:
   - Settings > About Phone
   - Tap Build Number 7 times
2. Enable USB Debugging:
   - Settings > Developer Options > USB Debugging
3. Connect device via USB
4. Select device from dropdown
5. Click **Run** button (▶)

## Testing Checklist

- [ ] App launches successfully
- [ ] Home screen displays quote
- [ ] "New Quote" button loads random quotes
- [ ] Navigation to Settings works
- [ ] Settings persist after app restart
- [ ] Notification permission request appears (Android 13+)
- [ ] Notifications schedule correctly
- [ ] Quiet hours respected
- [ ] Notification tap opens app

## Building for Distribution

### Generate Signed APK/AAB

1. Go to **Build > Generate Signed Bundle / APK**
2. Select **Android App Bundle** (AAB) for Play Store
3. Click **Next**

### Create Keystore (First Time Only)

1. Click **Create new...**
2. Fill in details:
   - Key store path: Choose location (save securely!)
   - Password: Create strong password (save securely!)
   - Alias: e.g., "calm-burst-release"
   - Validity: 25 years
   - Certificate info: Fill in your details
3. Click **OK**

**IMPORTANT**: Keep keystore and passwords safe! You cannot update your app without them.

### Sign Release Build

1. Select your keystore
2. Enter passwords
3. Select release variant
4. Click **Finish**

Release AAB will be in: `app/release/app-release.aab`

### Upload to Google Play Console

1. Go to Google Play Console
2. Select your app (or create new)
3. Go to Production > Create new release
4. Upload your AAB file
5. Add release notes
6. Review and rollout

## Google Play Console Setup

### App Information

- App Name: Calm Burst
- Short Description: Motivational quotes for calm living
- Full Description: (200+ characters about the app)
- Category: Health & Fitness or Lifestyle
- Tags: motivational, quotes, mindfulness, calm, wellness

### Content Rating

Complete questionnaire:
- No violence
- No scary content
- No user-generated content
- No sharing personal information
- Age rating will likely be Everyone

### Privacy Policy

Required for Play Store. Must include:
- What data is collected (Settings only, stored locally)
- No data shared with third parties
- No tracking or analytics

### Screenshots

Required sizes (capture from emulator):
- Phone: 320 x 640 minimum, 3840 x 2160 maximum
- 7-inch tablet: 1024 x 600 minimum, 7680 x 4320 maximum
- 10-inch tablet: 1200 x 900 minimum, 7680 x 4320 maximum

Minimum 2 screenshots, maximum 8.

## Troubleshooting

### Gradle Sync Fails

```bash
cd android
./gradlew clean
./gradlew build
```

### Build Errors

1. Clean project: Build > Clean Project
2. Rebuild: Build > Rebuild Project
3. Invalidate caches: File > Invalidate Caches / Restart

### Emulator Issues

- Ensure Intel HAXM or AMD equivalent is installed
- Allocate sufficient RAM (2GB minimum)
- Use x86/x86_64 system images for better performance

### Signing Errors

- Verify keystore path is correct
- Verify passwords are correct
- Ensure keystore has not been modified

## Version Management

- **versionName** (user-facing): 2.0.0
- **versionCode** (incremental): Start at 1, increment for each Play Store upload

Each upload to Play Store must have higher versionCode than previous.

## Post-Submission

- Review time: Usually 1-3 days
- Monitor Play Console for review status
- Respond to any review questions promptly
- Monitor crash reports and ANRs

## Quick Reference

**Build commands:**
```bash
npm run build              # Build web app
npx cap sync android       # Sync to Android
npx cap open android       # Open in Android Studio
```

**Gradle commands (from android/ directory):**
```bash
./gradlew assembleDebug    # Build debug APK
./gradlew assembleRelease  # Build release APK
./gradlew bundleRelease    # Build release AAB
```

## Current Status

✅ Android project fully configured and ready to build
✅ Permissions configured for Android 8+
✅ App name and package ID set (com.calmburst)
✅ Capacitor plugins integrated
✅ Web assets built and synced

**Next step**: Open in Android Studio and build!

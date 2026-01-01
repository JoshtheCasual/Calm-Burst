# How to Build the APK

This guide shows how to build the Calm Burst Android app on your local machine.

## ⚠️ Build Environment Not Available

The development environment where this code was generated doesn't have Android SDK installed, so the APK couldn't be built automatically. However, building is straightforward on any machine with Android development tools.

## Prerequisites

You need:
1. **JDK 17 or later** - [Download here](https://adoptium.net/)
2. **Android SDK** - Easiest via [Android Studio](https://developer.android.com/studio)

## Quick Build Guide

### Option 1: Using Android Studio (Recommended)

1. **Open Project**:
   ```bash
   git clone https://github.com/JoshtheCasual/Calm-Burst.git
   cd Calm-Burst
   ```
   - Open Android Studio
   - File → Open → Select `Calm-Burst` folder
   - Wait for Gradle sync

2. **Configure SDK**:
   - Android Studio will auto-configure `local.properties`
   - Or manually create it:
     ```properties
     sdk.dir=/path/to/Android/sdk
     ADMOB_APP_ID=ca-app-pub-3940256099942544~3347511713
     ADMOB_BANNER_ID=ca-app-pub-3940256099942544/6300978111
     ```

3. **Build APK**:
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Or from terminal: `./gradlew assembleDebug`

4. **Find APK**:
   - Location: `app/build/outputs/apk/debug/app-debug.apk`
   - Install: `adb install app/build/outputs/apk/debug/app-debug.apk`

### Option 2: Using Command Line

1. **Install Android SDK**:
   ```bash
   # Download Android Studio or command-line tools
   # Set environment variable:
   export ANDROID_HOME=/path/to/android-sdk
   export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
   ```

2. **Clone and Build**:
   ```bash
   git clone https://github.com/JoshtheCasual/Calm-Burst.git
   cd Calm-Burst

   # Run the build script
   chmod +x build.sh
   ./build.sh
   ```

3. **Or use Gradle directly**:
   ```bash
   ./gradlew assembleDebug
   ```

### Option 3: Using Our Build Script

We've included `build.sh` which automates the process:

```bash
chmod +x build.sh
./build.sh
```

The script will:
- ✅ Check for Java installation
- ✅ Check for Android SDK
- ✅ Create local.properties automatically
- ✅ Let you choose debug or release build
- ✅ Show APK location when done

## Build Outputs

### Debug APK
- **Path**: `app/build/outputs/apk/debug/app-debug.apk`
- **Use**: Testing and development
- **Signing**: Debug keystore (auto-generated)
- **Install**: `adb install app/build/outputs/apk/debug/app-debug.apk`

### Release APK
- **Path**: `app/build/outputs/apk/release/app-release.apk`
- **Use**: Production deployment
- **Signing**: Requires release keystore (see BUILD_AND_DEPLOY.md)
- **Upload**: To Google Play Console

## Expected APK Size

- **Debug APK**: ~8-12 MB
- **Release APK**: ~4-6 MB (ProGuard enabled, resources optimized)

## Troubleshooting

### "SDK not found"
```bash
# Create local.properties with SDK path
echo "sdk.dir=/path/to/Android/sdk" > local.properties
```

### "Gradle sync failed"
```bash
# Clean and retry
./gradlew clean
./gradlew assembleDebug
```

### "Build tools not installed"
Open Android Studio → SDK Manager → Install:
- Android SDK Build-Tools 34
- Android SDK Platform 34

### "Missing Gradle wrapper JAR"
```bash
gradle wrapper --gradle-version 8.5
```

## For CI/CD Builds

If you want to build in CI/CD (GitHub Actions, etc.), see the example workflow:

```yaml
# .github/workflows/build.yml
name: Build APK

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Setup Android SDK
        uses: android-actions/setup-android@v2

      - name: Build Debug APK
        run: ./gradlew assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```

## Next Steps

After building:

1. **Install on Device**:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Test Features**:
   - See manual testing checklist in BUILD_AND_DEPLOY.md
   - Test notifications, quiet hours, settings
   - Try ad removal purchase (test mode)

3. **For Production**:
   - Generate release keystore
   - Build signed release APK
   - Upload to Google Play Console
   - See BUILD_AND_DEPLOY.md for full guide

## Need Help?

- **Full Documentation**: See BUILD_AND_DEPLOY.md
- **Architecture**: See FRAMEWORK.md
- **Security**: See SECURITY_AUDIT_REPORT.md
- **Issues**: [GitHub Issues](https://github.com/JoshtheCasual/Calm-Burst/issues)

---

**Current Status**: Source code is complete and ready to build. Just needs Android SDK setup on your machine.

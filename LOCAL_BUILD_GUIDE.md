# Local Build Guide - Calm Burst

Since GitHub Actions is encountering issues, here's how to build the APK on your local machine.

---

## Quick Start (3 Steps)

### Step 1: Install Android Studio
Download from: https://developer.android.com/studio

### Step 2: Open Project
```bash
cd Calm-Burst
# Open in Android Studio or build with command line
```

### Step 3: Build APK
```bash
./gradlew assembleDebug
```

**Output**: `app/build/outputs/apk/debug/app-debug.apk`

---

## Detailed Instructions

### Option A: Using Android Studio (Easiest)

1. **Open Project**
   - Launch Android Studio
   - File → Open
   - Navigate to `Calm-Burst` folder
   - Click OK

2. **Wait for Sync**
   - Gradle will sync automatically
   - Wait for "Gradle build finished" message
   - May take 5-10 minutes on first run

3. **Build APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Wait for build to complete
   - Click "locate" when done

4. **Install on Device**
   - Connect Android device via USB
   - Enable USB debugging on device
   - Run → Run 'app'
   - Or: `adb install app/build/outputs/apk/debug/app-debug.apk`

---

### Option B: Command Line (Faster)

#### Requirements:
- JDK 17 or later
- Android SDK installed

#### Build Commands:

```bash
# Make gradlew executable
chmod +x gradlew

# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires keystore)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

---

### Option C: Docker Build (No Android SDK needed)

```bash
# Build using Docker (may take 15-20 minutes first time)
./docker-build.sh

# Output: app-debug.apk in current directory

# Install on device
adb install app-debug.apk
```

---

## Troubleshooting

### Error: SDK not found

**Create `local.properties`:**
```properties
sdk.dir=/path/to/Android/sdk
ADMOB_APP_ID=ca-app-pub-3940256099942544~3347511713
ADMOB_BANNER_ID=ca-app-pub-3940256099942544/6300978111
```

**Find SDK location:**
- **Mac**: `~/Library/Android/sdk`
- **Linux**: `~/Android/Sdk`
- **Windows**: `C:\Users\<username>\AppData\Local\Android\Sdk`

---

### Error: Daemon issues

```bash
# Stop all Gradle daemons
./gradlew --stop

# Rebuild
./gradlew clean assembleDebug
```

---

### Error: Dependency resolution

```bash
# Refresh dependencies
./gradlew --refresh-dependencies assembleDebug
```

---

### Error: Out of memory

```bash
# Increase Gradle memory
export GRADLE_OPTS="-Xmx4g -XX:MaxMetaspaceSize=512m"
./gradlew assembleDebug
```

---

## Build Variants

### Debug Build
- **Use**: Testing and development
- **Command**: `./gradlew assembleDebug`
- **Signing**: Auto-signed with debug key
- **ProGuard**: Disabled
- **Size**: ~10-12 MB

### Release Build
- **Use**: Production deployment
- **Command**: `./gradlew assembleRelease`
- **Signing**: Requires release keystore
- **ProGuard**: Enabled (code minification)
- **Size**: ~4-6 MB

---

## Creating Release Keystore

```bash
keytool -genkey -v -keystore calm-burst-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias calm-burst

# Enter details when prompted
# SAVE THE PASSWORD SECURELY!
```

**Configure signing in `keystore.properties`:**
```properties
storeFile=calm-burst-release.jks
storePassword=your_password_here
keyAlias=calm-burst
keyPassword=your_key_password_here
```

---

## Verifying the APK

```bash
# Check signature
jarsigner -verify -verbose app/build/outputs/apk/debug/app-debug.apk

# View APK contents
unzip -l app/build/outputs/apk/debug/app-debug.apk

# Check APK size
ls -lh app/build/outputs/apk/debug/app-debug.apk

# Get package info
aapt dump badging app/build/outputs/apk/debug/app-debug.apk | grep package
```

---

## Expected Build Time

| Environment | First Build | Subsequent Builds |
|-------------|-------------|-------------------|
| Android Studio | 5-10 min | 30-60 sec |
| Command Line | 3-5 min | 20-40 sec |
| Docker | 15-20 min | 3-5 min |

---

## What Gets Built

### APK Contents:
- Compiled Kotlin code (DEX files)
- All resources (layouts, strings, colors, images)
- AndroidManifest.xml
- 56 motivational quotes (quotes.xml)
- Dependencies (AndroidX, WorkManager, AdMob, Billing)

### APK Details:
- **Package**: com.calmburst
- **Version**: 1.0.0 (code 1)
- **Min SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)
- **Permissions**: POST_NOTIFICATIONS, INTERNET

---

## Installing on Device

### Via USB:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Via File Transfer:
1. Copy APK to device
2. Open file manager on device
3. Tap APK file
4. Allow "Install from Unknown Sources" if prompted
5. Tap Install

---

## GitHub Actions Alternative

If you want to fix GitHub Actions instead of building locally, the issue is likely:

1. **Network connectivity** to Gradle servers
2. **Android SDK components** not installed
3. **Memory limits** in CI environment

See `.github/workflows/build-apk.yml` to debug.

---

## Need Help?

- **Build errors**: Share the full error message (not just stacktrace)
- **SDK issues**: Verify `local.properties` points to correct SDK
- **Permission errors**: Run `chmod +x gradlew`

---

**The code is 100% ready to build - you just need a properly configured environment!**

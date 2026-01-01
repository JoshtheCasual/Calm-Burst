# Calm Burst - Android Project Structure

## Overview
Complete Android project structure for the Calm Burst motivational notification app, built with Kotlin and following MVVM architecture.

## Project Configuration

### Build System
- **Gradle Version**: 8.5
- **Android Gradle Plugin**: 8.2.0
- **Kotlin Version**: 1.9.20
- **Build Tool**: Gradle with Kotlin DSL (.kts files)

### SDK Configuration
- **Min SDK**: 26 (Android 8.0) - Required for notification channels
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

### Version Information
- **Version Code**: 1
- **Version Name**: 1.0.0

## Directory Structure

```
Calm-Burst/
├── .gitignore                           # Git ignore configuration
├── build.gradle.kts                     # Root build file
├── settings.gradle.kts                  # Project settings
├── gradle.properties                    # Gradle configuration
├── local.properties.template            # Template for API keys (create local.properties)
├── gradlew                              # Gradle wrapper script (Unix/Linux/Mac)
├── gradlew.bat                          # Gradle wrapper script (Windows)
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties    # Gradle wrapper configuration
│
├── app/
│   ├── build.gradle.kts                # App module build configuration
│   ├── proguard-rules.pro              # ProGuard rules for release builds
│   │
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml     # App manifest
│       │   │
│       │   ├── java/com/calmburst/
│       │   │   ├── MainActivity.kt     # Main activity (single-activity architecture)
│       │   │   │
│       │   │   ├── data/               # Data layer
│       │   │   │   ├── Quote.kt        # Quote data class
│       │   │   │   ├── QuoteRepository.kt      # Quote data management
│       │   │   │   └── PreferencesManager.kt   # DataStore preferences
│       │   │   │
│       │   │   ├── ui/                 # UI layer
│       │   │   │   ├── HomeFragment.kt         # Home screen fragment
│       │   │   │   └── SettingsFragment.kt     # Settings screen fragment
│       │   │   │
│       │   │   ├── worker/             # Background work
│       │   │   │   ├── NotificationWorker.kt   # WorkManager worker
│       │   │   │   └── NotificationScheduler.kt # Scheduling logic
│       │   │   │
│       │   │   └── util/               # Utilities
│       │   │       ├── NotificationHelper.kt   # Notification management
│       │   │       └── BillingHelper.kt        # Google Play Billing
│       │   │
│       │   └── res/                    # Resources
│       │       ├── layout/
│       │       │   └── activity_main.xml       # Main activity layout
│       │       │
│       │       ├── values/
│       │       │   ├── strings.xml     # String resources
│       │       │   ├── colors.xml      # Color palette (earthy browns)
│       │       │   └── themes.xml      # Material theme configuration
│       │       │
│       │       ├── raw/
│       │       │   └── quotes.xml      # 56 motivational quotes
│       │       │
│       │       ├── xml/
│       │       │   ├── backup_rules.xml        # Backup configuration
│       │       │   └── data_extraction_rules.xml # Data extraction rules
│       │       │
│       │       └── mipmap-*/           # Launcher icons (placeholder README files)
│       │
│       ├── test/java/com/calmburst/    # Unit tests
│       └── androidTest/java/com/calmburst/  # Instrumented tests
│
├── FRAMEWORK.md                         # Technical specification
├── CLAUDE.md                            # AI agent instructions
├── CHANGE_NOTES.md                      # Change history
├── README.md                            # User documentation
└── MILESTONES.md                        # Project milestones
```

## Dependencies Added

### AndroidX Core & UI
- `androidx.core:core-ktx:1.12.0` - Kotlin extensions
- `androidx.appcompat:appcompat:1.6.1` - AppCompat library
- `androidx.constraintlayout:constraintlayout:2.1.4` - Constraint layouts
- `com.google.android.material:material:1.11.0` - Material Design components
- `androidx.fragment:fragment-ktx:1.6.2` - Fragment Kotlin extensions

### Lifecycle Components
- `androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0` - ViewModel
- `androidx.lifecycle:lifecycle-livedata-ktx:2.7.0` - LiveData
- `androidx.lifecycle:lifecycle-runtime-ktx:2.7.0` - Lifecycle runtime

### Background Work & Storage
- `androidx.work:work-runtime-ktx:2.9.0` - WorkManager for notifications
- `androidx.datastore:datastore-preferences:1.0.0` - DataStore for preferences

### Monetization
- `com.google.android.gms:play-services-ads:22.6.0` - Google AdMob SDK
- `com.android.billingclient:billing-ktx:6.1.0` - Google Play Billing Library

### Testing
- `junit:junit:4.13.2` - JUnit testing
- `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3` - Coroutines testing
- `androidx.test.ext:junit:1.1.5` - Android JUnit extensions
- `androidx.test.espresso:espresso-core:3.5.1` - Espresso UI testing
- `androidx.work:work-testing:2.9.0` - WorkManager testing

## Key Features

### Build Features Enabled
- **ViewBinding**: Enabled for type-safe view access
- **BuildConfig**: Enabled for AdMob API key injection

### Build Types
- **Debug**: No code shrinking, default signing
- **Release**: ProGuard enabled with optimization, resource shrinking

### Permissions Required
- `POST_NOTIFICATIONS` - For Android 13+ notification permission
- `INTERNET` - For AdMob ads and billing verification

### Color Palette (Earthy/Accessible)
- **Primary**: #5D4037 (brown 700)
- **Secondary**: #8D6E63 (brown 300)
- **Accent**: #A1887F (brown 200)
- **Background**: #EFEBE9 (brown 50)
- **Text**: #3E2723 (brown 900)
- **WCAG AA Compliant**: Minimum 4.5:1 contrast ratio

## Configuration Notes

### 1. Local Properties Setup
Before building the project, create `local.properties` from the template:

```properties
# Copy local.properties.template to local.properties
# Add your SDK path and AdMob keys

sdk.dir=/path/to/Android/sdk

# AdMob Configuration (get from https://admob.google.com)
ADMOB_APP_ID=ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy
ADMOB_BANNER_ID=ca-app-pub-xxxxxxxxxxxxxxxx/zzzzzzzzzz

# For testing, use Google's test IDs (already set as fallback):
# ADMOB_APP_ID=ca-app-pub-3940256099942544~3347511713
# ADMOB_BANNER_ID=ca-app-pub-3940256099942544/6300978111
```

### 2. Launcher Icons
The project references launcher icons that need to be created:
- Check `app/src/main/res/mipmap-*/README.md` for icon requirements
- Use Android Asset Studio or similar tool to generate icons
- Replace placeholder README files with actual PNG icons

### 3. Gradle Sync
After creating `local.properties`, sync the project in Android Studio:
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Resolve any SDK/dependency issues

### 4. Quote Data
The project includes **56 motivational quotes** in `app/src/main/res/raw/quotes.xml`:
- Format: XML with text, author, year, context for each quote
- AI contribution guide included in XML comments
- Proper XML escaping applied (&apos;, &quot;, &amp;, etc.)

## Build Verification

### Project Structure Correctness
✅ All required directories created
✅ All package structures follow `com.calmburst.*` naming
✅ Gradle files use Kotlin DSL (.kts)
✅ AndroidManifest.xml properly configured
✅ All dependencies properly declared
✅ ProGuard rules configured for release
✅ Resource files (strings, colors, themes) created
✅ Backup and data extraction rules configured

### Build Readiness
The project structure is complete and ready to build, pending:
1. Creation of `local.properties` from template
2. Installation of Android SDK (as specified in local.properties)
3. Generation of launcher icons (currently placeholder READMEs)

### Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## Next Development Steps

### Stub Files to Implement
All Kotlin files are currently stubs with TODO comments indicating required functionality:

1. **Data Layer**:
   - Implement XML parsing in `QuoteRepository.kt`
   - Implement DataStore operations in `PreferencesManager.kt`

2. **UI Layer**:
   - Create fragment layouts and ViewModels for `HomeFragment` and `SettingsFragment`
   - Implement fragment navigation

3. **Worker Layer**:
   - Implement notification scheduling in `NotificationScheduler.kt`
   - Implement quote notification in `NotificationWorker.kt`

4. **Utilities**:
   - Implement notification channel creation and display in `NotificationHelper.kt`
   - Implement billing flow in `BillingHelper.kt`

5. **MainActivity**:
   - Initialize fragments
   - Setup AdMob banner (conditional on ad removal status)
   - Request notification permission for Android 13+

## Package Organization

### `com.calmburst.data`
Data layer containing Quote model, repository, and preferences management

### `com.calmburst.ui`
UI layer with fragments and (future) ViewModels

### `com.calmburst.worker`
Background work layer for notification scheduling and delivery

### `com.calmburst.util`
Utility classes for notifications and billing

## Architecture Compliance

✅ **MVVM Pattern**: Structure supports ViewModel pattern
✅ **Single Activity**: MainActivity with fragment-based navigation
✅ **Separation of Concerns**: Clear package separation by layer
✅ **Dependency Injection Ready**: Structure supports DI if needed
✅ **Testability**: Test directories created for unit and instrumented tests

## Security & Privacy

✅ **API Keys**: Stored in git-ignored `local.properties`
✅ **ProGuard**: Configured for release builds
✅ **Backup Rules**: Excludes sensitive preferences from backup
✅ **Permissions**: Only necessary permissions requested
✅ **No Network Data**: Quotes bundled, no external API calls

---

**Project Status**: Structure complete, ready for implementation of business logic and UI components.

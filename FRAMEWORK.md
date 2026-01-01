# Technical Framework - Motivational Notification App

## Project Overview
Android app delivering motivational quotes via timed notifications with configurable scheduling and ad-supported monetization.

## Technology Stack
- **Platform**: Android (Kotlin)
- **Min SDK**: 26 (Android 8.0) - for notification channels
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle with Kotlin DSL
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependencies**:
  - AndroidX Core, AppCompat, Material Design
  - WorkManager (notification scheduling)
  - DataStore (preferences storage)
  - Google AdMob SDK (ads)
  - Google Play Billing Library (in-app purchase)

## Core Components

### 1. Quote System
**File**: `quotes.xml` in `res/raw/`
```xml
<!-- AI Contribution Guide:
  Add quotes with: text, author, year, context
  Validate: no duplicates, proper escaping
  Categories: motivation, inspiration, perseverance
-->
<quotes>
  <quote>
    <text>...</text>
    <author>...</author>
    <year>YYYY</year>
    <context>Where/when said</context>
  </quote>
</quotes>
```

**QuoteRepository**: Parses XML, provides random selection

### 2. Notification System
**NotificationScheduler**: Uses WorkManager with:
- PeriodicWorkRequest with configurable intervals
- Constraints: battery not low, quiet hours check
- Random delay within interval window

**Intervals**:
- 1-2 hours (avg 1.5h)
- 1-6 hours (avg 3.5h)
- 1-12 hours (avg 6.5h)
- 1-24 hours (avg 12.5h)

**Quiet Hours**: User-defined start/end time (default 22:00-07:00)

### 3. UI Components
**MainActivity**: Single activity with fragments
- **HomeFragment**: Displays last shown quote
- **SettingsFragment**: Interval, quiet hours, ad removal

**Color Palette (Earthy/Accessible)**:
- Primary: #5D4037 (brown 700)
- Secondary: #8D6E63 (brown 300)
- Accent: #A1887F (brown 200)
- Background: #EFEBE9 (brown 50)
- Text: #3E2723 (brown 900)
- Minimum contrast ratio: 4.5:1 (WCAG AA)

**Accessibility**:
- Large touch targets (48dp minimum)
- Content descriptions for all interactive elements
- Support for TalkBack
- Scalable text (sp units)

### 4. Monetization
**AdMob Banner**: Bottom of MainActivity (320x50dp)
**In-App Purchase**: One-time $1.00 purchase
- Product ID: "remove_ads"
- On purchase: hide ad, persist state in DataStore

### 5. Data Persistence
**DataStore** (replacing SharedPreferences):
- Last shown quote
- Notification interval setting
- Quiet hours (start/end)
- Ad removal purchase status

## Build Outputs
1. Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
2. Release APK: Signed with upload keystore `app/build/outputs/apk/release/app-release.apk`

## Testing Strategy
- Unit tests: QuoteRepository, scheduling logic
- Instrumented tests: Database, notifications
- Manual testing: UI flows, accessibility

## Security Considerations
- No sensitive data storage
- AdMob/Billing keys in local.properties (git-ignored)
- ProGuard enabled for release builds
- Permission justification: POST_NOTIFICATIONS (Android 13+)

## Simplicity Principles
- No over-engineering: single activity, minimal fragments
- No network calls: quotes bundled in APK
- No complex state: DataStore for simple key-value pairs
- Clear code structure: one responsibility per class

# Calm Burst v2.0.0 - Cross-Platform Edition

## Overview

Calm Burst is a cross-platform motivational quotes application available on iOS and Android. Built with modern web technologies (React, TypeScript) and deployed natively using Capacitor, Calm Burst delivers inspiring quotes through intelligent notifications while maintaining a lightweight, accessible design.

**Key Innovation**: Single React codebase with ~90% code sharing across iOS and Android, eliminating the maintenance burden of separate native apps.

---

## Technology Stack

### Web Framework & Tools
- **React** 18.x - UI component library with hooks
- **TypeScript** 5.x - Type-safe JavaScript
- **Vite** 5.x - Next-generation build tool (fast dev server, optimized bundles)
- **Tailwind CSS** 3.x - Utility-first CSS framework
- **React Context API** - State management (no external dependency)
- **Capacitor** 6.x - Native bridge for iOS/Android APIs

### Native Platforms
- **iOS**: 13.0+ (iPhone, iPad)
- **Android**: 8.0+ (API 26+)

### Capacitor Plugins
- `@capacitor/local-notifications` - Background notification scheduling
- `@capacitor/preferences` - Persistent storage (iOS UserDefaults, Android SharedPreferences)
- `@capacitor-community/admob` - Banner ads and ad management
- `@capacitor/app` - App lifecycle management
- `@capacitor/splash-screen` - Launch screen control

### Development Tools
- **ESLint** - Code quality and security linting
- **Prettier** - Code formatting
- **Jest** - Unit testing
- **TypeScript Compiler** - Strict type checking

---

## Architecture

### Web + Native Bridge Model

Calm Burst uses a **Web App + Native Bridge** architecture:

1. **Web Layer** (React/TypeScript)
   - All business logic, UI components, and state management
   - Runs in a WebView on iOS/Android
   - Platform-agnostic code for maximum reusability

2. **Native Bridge** (Capacitor)
   - Connects web app to native APIs (notifications, storage, billing)
   - Provides JavaScript interfaces to native functionality
   - Handles platform-specific implementations transparently

3. **Native Layer** (iOS/Android)
   - Minimal custom native code (configuration-driven)
   - Uses Capacitor plugins for all platform integrations
   - Configured via `capacitor.config.ts`

### Benefits
- **Single Codebase**: 90% shared logic and UI between iOS/Android
- **Fast Development**: Instant reload in dev mode, rapid iteration
- **Type Safety**: Full TypeScript coverage end-to-end
- **Native Capabilities**: Full access to platform notifications, storage, and monetization
- **Minimal Maintenance**: Avoid maintaining two separate native codebases

---

## Project Structure

```
Calm-Burst/
├── web-app/                          # React web application
│   ├── src/
│   │   ├── components/               # Reusable UI components
│   │   │   ├── Layout.tsx            # App header, main content area, footer
│   │   │   ├── QuoteCard.tsx         # Styled quote display
│   │   │   ├── IntervalSelector.tsx  # Notification interval picker
│   │   │   ├── TimePickerInput.tsx   # Quiet hours time inputs
│   │   │   └── AdBanner.tsx          # Monetization ad display
│   │   │
│   │   ├── views/                    # Page-level components
│   │   │   ├── HomeView.tsx          # Main quote display
│   │   │   └── SettingsView.tsx      # User preferences
│   │   │
│   │   ├── hooks/                    # Custom React hooks
│   │   │   ├── useQuotes.ts          # Quote management
│   │   │   ├── useSettings.ts        # Settings persistence
│   │   │   ├── useNotifications.ts   # Notification scheduling
│   │   │   └── useAds.ts             # Ad management
│   │   │
│   │   ├── services/                 # Business logic layer
│   │   │   ├── quoteService.ts       # Quote loading, randomization
│   │   │   ├── storageService.ts     # localStorage/Preferences wrapper
│   │   │   ├── notificationService.ts # Scheduling, quiet hours
│   │   │   ├── adService.ts          # AdMob integration
│   │   │   └── nativePlugins.ts      # Capacitor plugin wrappers
│   │   │
│   │   ├── context/                  # React Context providers
│   │   │   └── AppContext.tsx        # Global app state
│   │   │
│   │   ├── types/                    # TypeScript type definitions
│   │   │   ├── Quote.ts              # Quote data structure
│   │   │   ├── Settings.ts           # User settings interface
│   │   │   └── index.ts              # Type exports
│   │   │
│   │   ├── data/                     # Static data
│   │   │   └── quotes.json           # 56 motivational quotes (migrated from XML)
│   │   │
│   │   ├── App.tsx                   # Root component
│   │   ├── main.tsx                  # Application entry point
│   │   └── styles/                   # Global styles
│   │       ├── globals.css           # Tailwind directives
│   │       └── variables.css         # CSS custom properties
│   │
│   ├── public/                       # Static assets
│   │   ├── icons/                    # App icons (all sizes)
│   │   └── splash/                   # Splash screen images
│   │
│   ├── package.json                  # Web dependencies
│   ├── tsconfig.json                 # TypeScript configuration
│   ├── vite.config.ts                # Vite build configuration
│   ├── tailwind.config.js            # Tailwind CSS theme
│   ├── capacitor.config.ts           # Capacitor configuration
│   └── .eslintrc.json                # ESLint rules
│
├── ios/                              # iOS native project (generated by Capacitor)
│   ├── App/
│   │   ├── Info.plist               # App metadata, permissions
│   │   ├── App.entitlements         # Code signing entitlements
│   │   ├── Podfile                  # CocoaPods dependencies
│   │   └── ...                      # iOS-specific config
│   └── Calm Burst.xcworkspace       # Xcode workspace
│
├── android/                          # Android native project (generated by Capacitor)
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── AndroidManifest.xml  # App permissions
│   │   │   ├── java/com/calmburst/  # Minimal native code
│   │   │   └── res/                 # Icons, layouts, strings
│   │   └── build.gradle             # Android build config
│   └── ...
│
├── FRAMEWORK.md                      # Technical specification (v1.x)
├── CAPACITOR_MIGRATION_PLAN.md       # v2.0.0 migration plan (15 milestones)
├── CHANGE_NOTES.md                   # Version history
├── CLAUDE.md                         # AI agent instructions
├── README.md                         # Original v1.x documentation
├── README_V2.md                      # This file
└── .gitignore                        # Git exclusions
```

---

## Development Setup

### Prerequisites

Before setting up the project, ensure you have installed:

#### All Platforms
- **Node.js** 18.0+ ([download](https://nodejs.org/))
- **npm** 9.0+ (included with Node.js)
- **Git** 2.0+ (for version control)

#### macOS (for iOS Development)
- **Xcode** 13.0+ ([App Store](https://apps.apple.com/us/app/xcode/id497799835?mt=12))
- **Xcode Command Line Tools**: `xcode-select --install`
- **CocoaPods**: `sudo gem install cocoapods`

#### Windows/Mac/Linux (for Android Development)
- **Android Studio** 2021.1+ ([download](https://developer.android.com/studio))
- **Android SDK** 34 (Android 14)
- **Java Development Kit** 11+ (included with Android Studio)
- **ANDROID_HOME** environment variable set

### Installation Steps

#### 1. Clone the Repository
```bash
git clone https://github.com/JoshtheCasual/Calm-Burst.git
cd Calm-Burst
```

#### 2. Install Web Dependencies
```bash
cd web-app
npm install
```

#### 3. Setup Environment Variables
Create a `.env.local` file (not tracked by git):
```properties
# Optional: AdMob app IDs (for production builds)
# Get these from AdMob console: https://admob.google.com
VITE_ADMOB_APP_ID_IOS=ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy
VITE_ADMOB_APP_ID_ANDROID=ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy
```

---

## Running Locally

### Web Development Server

Start the development server with hot module replacement:

```bash
cd web-app
npm run dev
```

The app will be available at `http://localhost:5173` with instant reload on file changes.

### Building the Web App

Create an optimized production bundle:

```bash
cd web-app
npm run build
```

Output is generated in `web-app/dist/` directory.

---

## Building for iOS

### Sync Web Build to iOS Project

After building the web app, sync it to the iOS project:

```bash
cd web-app
npx cap sync ios
```

This copies the web bundle and installs any updated Capacitor plugins.

### Build in Xcode

Open the iOS workspace:
```bash
open web-app/ios/Calm\ Burst.xcworkspace
```

Then in Xcode:
1. Select target device or simulator (top left)
2. Product → Build (`Cmd + B`)
3. Product → Run (`Cmd + R`)

### Build for iOS App Store

Create a release build:
```bash
open web-app/ios/Calm\ Burst.xcworkspace
```

Then in Xcode:
1. Select "Any iOS Device (arm64)" as destination
2. Product → Archive
3. Organizer → Distribute App

See [iOS Build Guide](/iOS_BUILD_GUIDE.md) for detailed instructions.

---

## Building for Android

### Sync Web Build to Android Project

After building the web app, sync it to the Android project:

```bash
cd web-app
npx cap sync android
```

### Build in Android Studio

Open the Android project:
```bash
open -a "Android Studio" web-app/android
```

Then in Android Studio:
1. Select device or emulator (top toolbar)
2. Build → Make Project
3. Run → Run 'app'

### Build for Google Play

Create a release bundle (Android App Bundle):
```bash
cd web-app/android
./gradlew bundleRelease
```

Output: `web-app/android/app/build/outputs/bundle/release/app-release.aab`

See [Android Build Guide](/ANDROID_BUILD_GUIDE.md) for detailed instructions.

---

## Testing

### Run Unit Tests

```bash
cd web-app
npm run test
```

### Run ESLint

Check code quality:
```bash
cd web-app
npm run lint
```

Fix automatically:
```bash
cd web-app
npm run lint:fix
```

### Manual Testing Checklist

- [ ] App launches on iOS simulator
- [ ] App launches on Android emulator
- [ ] Quote displays on Home screen
- [ ] Settings view loads and displays current settings
- [ ] Notification scheduling works (check native notifications)
- [ ] Quiet hours enforcement prevents notifications
- [ ] Settings persist after app restart
- [ ] Ad banner displays (if ad removal not purchased)
- [ ] Ad removal purchase works
- [ ] Responsive design works on multiple screen sizes (320px-768px+)
- [ ] Accessibility: VoiceOver (iOS) and TalkBack (Android) work

---

## Features

### Core Features (v2.0.0)

#### 1. Random Motivational Quotes
- Database of 56 curated motivational quotes
- Metadata: Author name, year, context/attribution
- Random selection with no duplicates (intelligent caching)

#### 2. Configurable Notification Intervals
- 1-2 hours (average 1.5h)
- 1-6 hours (average 3.5h)
- 1-12 hours (average 6.5h)
- 1-24 hours (average 12.5h)
- Random delay within selected window

#### 3. Quiet Hours (Do-Not-Disturb)
- User-defined start and end times (e.g., 10 PM to 7 AM)
- Notifications suppressed during quiet hours
- Configurable in Settings

#### 4. Ad-Supported Model
- AdMob banner ads (bottom of screen)
- One-time in-app purchase ($1.00) to remove ads
- Purchase state persists across sessions

#### 5. Quote History
- View the most recently shown quote
- Tap to see full quote details (author, year, context)

#### 6. Accessible Design
- **WCAG AA Compliance**: 4.5:1 contrast ratio minimum
- **Touch Targets**: 48pt minimum (mobile-friendly)
- **Text Scaling**: Respects system font sizes
- **Accessibility Labels**: All interactive elements described
- **Screen Reader Support**: VoiceOver (iOS), TalkBack (Android)

#### 7. Responsive Mobile Design
- Mobile-first layout optimized for 320px-768px viewports
- Tablet layouts (iPad, Android tablets) with optimal spacing
- Portrait and landscape orientation support

### Planned Features (Future Releases)

- [ ] Quote search and filtering
- [ ] Custom quote submission
- [ ] Sharing quotes to social media
- [ ] Dark mode theme
- [ ] Multiple language support

---

## Migration from v1.x (Native Android)

### For Users
If you're upgrading from Calm Burst v1.0.5 (native Android):

1. **Install v2.0.0** from Google Play
2. **Your settings are NOT automatically transferred** (separate app with web storage)
3. **Manual setup required**:
   - Re-configure your preferred notification interval
   - Re-set quiet hours if desired
   - Repurchase ad removal if you had purchased it (within 30 days of release, contact support for refund)

### For Developers
The codebase has migrated from:
- **v1.x**: Kotlin/Android/Gradle → **v2.0.0**: React/TypeScript/Vite/Capacitor

Key changes:
- Quote source changed: `quotes.xml` (Android resources) → `quotes.json` (web assets)
- Storage backend: Android DataStore → Capacitor Preferences (abstracts platform differences)
- Notification system: Android WorkManager → Capacitor Local Notifications
- Monetization: Google Play Billing → Capacitor AdMob + custom IAP handling
- Build system: Gradle → npm/Vite for web, Gradle via Capacitor for platforms

The v1.x native Android source is preserved in the `v1-legacy` branch for reference.

---

## Build Instructions Quick Reference

### Web Development
```bash
cd web-app
npm install        # One-time: install dependencies
npm run dev        # Start dev server (http://localhost:5173)
npm run build      # Create production bundle
npm run test       # Run unit tests
npm run lint       # Check code quality
```

### iOS Build
```bash
cd web-app
npm run build             # Build web app
npx cap sync ios          # Sync to iOS
open ios/Calm\ Burst.xcworkspace
# In Xcode: Product → Run (Cmd + R)
```

### Android Build
```bash
cd web-app
npm run build             # Build web app
npx cap sync android      # Sync to Android
open -a Android\ Studio android
# In Android Studio: Run → Run 'app'
```

### Release Builds
```bash
# iOS: In Xcode, Product → Archive
# Android: cd web-app/android && ./gradlew bundleRelease
```

---

## Troubleshooting

### Common Issues

**Dev server won't start**
```bash
# Clear cache and reinstall
rm -rf web-app/node_modules web-app/package-lock.json
cd web-app && npm install
npm run dev
```

**iOS build fails with CocoaPods error**
```bash
cd web-app/ios && pod install --repo-update
```

**Android emulator shows blank screen**
```bash
# Clear Capacitor cache
cd web-app
npx cap sync android --live
```

**Notifications not working on physical device**
- Verify notification permissions are granted (Settings → App Permissions)
- Check if app is in Quiet Hours (disable to test)
- Ensure background app refresh is enabled (iOS Settings → General → Background App Refresh)

**Build size is too large**
```bash
# Analyze bundle
cd web-app && npm run build
npm run build-analyze  # If script is configured
```

### Getting Help

- **GitHub Issues**: [Calm-Burst/issues](https://github.com/JoshtheCasual/Calm-Burst/issues)
- **TypeScript Errors**: Check `web-app/tsconfig.json` and run `npx tsc --noEmit`
- **Build Errors**: Check Xcode or Android Studio logs for specific native errors

---

## Performance Considerations

### App Size
- **iOS**: ~20-30 MB (varies by SDK size)
- **Android**: ~15-25 MB (AAB format)

### Launch Time
- **Target**: < 2 seconds (iOS), < 3 seconds (Android)
- **Metric**: Time from icon tap to Home view rendered

### Memory Usage
- **Typical**: 40-60 MB (web content + plugins)
- **Max**: < 100 MB under normal use

### Battery Impact
- **Background**: Notification scheduling every 30+ minutes uses minimal battery
- **Optimization**: Uses platform-native notification APIs (not polling)

---

## Security & Privacy

### Data Handling
- **No Personal Data Collected**: App uses local storage only
- **Quote Data**: Bundled in app, no server calls
- **User Settings**: Stored locally (Preferences/UserDefaults)
- **Ads**: AdMob handles targeting (see [Google privacy policy](https://policies.google.com/privacy))

### Code Security
- **TypeScript**: Strict mode enabled, no `any` types in production
- **ESLint**: Security plugin rules enforced
- **Dependencies**: Regular audits (`npm audit`), no high/critical vulnerabilities

### Platform Security
- **iOS**: Adheres to App Store security guidelines
- **Android**: Adheres to Google Play security requirements
- **No Hardcoded Secrets**: API keys from environment variables

---

## Contributing

Contributions are welcome! Please see [Contributing Guidelines](CONTRIBUTING.md).

---

## License

Calm Burst is released under the [MIT License](LICENSE).

---

## Credits

**Motivational Quotes**: Curated from public domain sources with author attribution
**Original v1.0 Developer**: Josh (JoshtheCasual)
**Icon Design**: Calm Burst branding
**Capacitor Framework**: [Ionic Team](https://capacitorjs.com/)

---

## Version History

**v2.0.0** (2026-01-XX)
- Major architectural migration to React + Capacitor
- New iOS platform support
- Feature parity with v1.0.5 (Android)
- Improved build performance and development experience

**v1.0.5** (2026-01-02)
- Last native Android release
- See [CHANGE_NOTES.md](CHANGE_NOTES.md) for full history

---

## Support

For issues, feature requests, or questions:
- **GitHub Issues**: [Calm-Burst/issues](https://github.com/JoshtheCasual/Calm-Burst/issues)
- **Documentation**: See [FRAMEWORK.md](FRAMEWORK.md) for technical details
- **Build Help**: See [CAPACITOR_MIGRATION_PLAN.md](CAPACITOR_MIGRATION_PLAN.md) for migration details

---

**Last Updated**: January 4, 2026
**Status**: v2.0.0 Development (Milestone 1 - Foundation)

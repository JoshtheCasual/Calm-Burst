# Capacitor Migration Plan - iOS Support (v2.0.0)

## Overview
Migration from native Android (Kotlin) to Capacitor-based cross-platform app supporting both iOS and Android.

**Version**: v2.0.0 (MAJOR - Architectural change)
**Current Version**: v1.0.5 (Native Android)
**Target Platforms**: iOS (new), Android (migrated)

---

## Architecture Decision

### Technology Stack
- **Framework**: Capacitor 6.x
- **Web Layer**: React 18.x + TypeScript
- **Build Tools**: Vite (fast builds)
- **State Management**: React Context + Hooks
- **Styling**: Tailwind CSS (responsive, accessible)
- **Native Platforms**: iOS (new), Android (migrated from native)

### Why React + Capacitor?
1. **Capacitor**: Excellent native API support (notifications, local storage, purchases)
2. **React**: Component reusability, strong ecosystem, TypeScript support
3. **Vite**: Fast development builds, optimized production bundles
4. **Tailwind**: Rapid UI development, maintains earthy color palette
5. **Shared Codebase**: ~90% code sharing between iOS/Android

---

## Migration Strategy

### Phase 1: Web App Foundation
Build React web app with all features, no native integration yet.

### Phase 2: Capacitor Integration
Add Capacitor, configure iOS/Android platforms, integrate native APIs.

### Phase 3: Platform-Specific Features
Handle platform differences (permissions, notifications, billing).

### Phase 4: Testing & Optimization
Test on physical devices, optimize performance, security scan.

---

## Milestone Breakdown

## Milestone 1: Project Structure & Foundation
**Version**: v2.0.0-alpha.1
**Approach**: Parallel Development (deploy specialized agents)

### Tasks
- [ ] Create new Capacitor project structure (React + TypeScript + Vite)
- [ ] Configure package.json with dependencies (React, Capacitor, Tailwind)
- [ ] Setup TypeScript configuration (strict mode, path aliases)
- [ ] Setup Vite configuration (dev server, build optimization)
- [ ] Configure Tailwind CSS with earthy color palette (brown theme)
- [ ] Create project folder structure (components, hooks, services, types)
- [ ] Setup ESLint + Prettier (code quality, consistent formatting)
- [ ] Migrate quotes.xml → quotes.json (56 quotes with metadata)
- [ ] Create README_V2.md documenting new architecture

### Deliverables
- Buildable React + TypeScript + Vite project
- Configuration files (tsconfig.json, vite.config.ts, tailwind.config.js)
- quotes.json data file
- Clean development environment

### Verification
- TypeScript: PASS (no errors)
- ESLint: PASS (no warnings)
- Build: PASS (vite build succeeds)
- Dev Server: PASS (vite dev runs)

---

## Milestone 2: Core UI Components
**Version**: v2.0.0-alpha.2
**Approach**: Parallel Development (deploy specialized agents)

### Tasks
- [ ] Create Layout component (header, main content, bottom nav/ad area)
- [ ] Create HomeView component (quote display with text, author, year, context)
- [ ] Create SettingsView component (interval picker, quiet hours pickers)
- [ ] Create QuoteCard component (styled quote display)
- [ ] Create IntervalSelector component (1-2h, 1-6h, 1-12h, 1-24h options)
- [ ] Create TimePickerInput component (quiet hours start/end)
- [ ] Implement navigation routing (React Router or simple state-based)
- [ ] Apply Tailwind styling (earthy palette, responsive, accessible)
- [ ] Ensure WCAG AA compliance (contrast, touch targets, aria labels)
- [ ] Mobile-first responsive design (320px - 768px+ viewports)

### Deliverables
- Complete UI component library
- Responsive layouts for mobile (iOS/Android)
- Accessible design (TalkBack/VoiceOver support)
- Working navigation between Home and Settings

### Verification
- TypeScript: PASS
- ESLint: PASS
- Build: PASS
- Accessibility: PASS (manual TalkBack/VoiceOver testing)
- Responsive: PASS (test at 320px, 375px, 414px, 768px)

---

## Milestone 3: State Management & Business Logic
**Version**: v2.0.0-alpha.3
**Approach**: Parallel Development (deploy specialized agents)

### Tasks
- [ ] Create QuoteService (load quotes.json, random selection)
- [ ] Create StorageService interface (localStorage wrapper for preferences)
- [ ] Create NotificationScheduler service (scheduling logic, quiet hours check)
- [ ] Create AppContext (React Context for global state)
- [ ] Create useQuotes hook (fetch and display quotes)
- [ ] Create useSettings hook (interval, quiet hours, ad removal state)
- [ ] Create useNotifications hook (schedule, cancel notifications)
- [ ] Implement quiet hours logic (check current time against user settings)
- [ ] Implement random delay within interval window (match Android behavior)
- [ ] Add loading states and error handling

### Deliverables
- Complete business logic layer
- State management with React Context
- Custom hooks for all features
- Error handling and loading states

### Verification
- TypeScript: PASS
- ESLint: PASS
- Build: PASS
- Unit Tests: PASS (Jest tests for services)
- Logic: PASS (quiet hours, random delay calculations)

---

## Milestone 4: Capacitor Platform Setup
**Version**: v2.0.0-alpha.4
**Approach**: Parallel Development (deploy specialized agents)

### Tasks
- [ ] Install Capacitor CLI and core packages (@capacitor/core, @capacitor/cli)
- [ ] Run `npx cap init` (app name: "Calm Burst", app id: "com.calmburst")
- [ ] Add iOS platform (`npx cap add ios`)
- [ ] Add Android platform (`npx cap add android`)
- [ ] Configure capacitor.config.ts (iOS/Android settings)
- [ ] Install Capacitor plugins:
  - [ ] @capacitor/local-notifications (notifications)
  - [ ] @capacitor/preferences (storage)
  - [ ] @capacitor-community/admob (ads)
  - [ ] @capacitor/app (lifecycle)
  - [ ] @capacitor/splash-screen (splash screen)
- [ ] Configure iOS project (Xcode settings, signing, capabilities)
- [ ] Configure Android project (gradle, signing, permissions)
- [ ] Setup icon and splash screen assets (iOS and Android)

### Deliverables
- iOS project in `ios/` directory
- Android project in `android/` directory
- Capacitor configuration
- All required plugins installed

### Verification
- Build: PASS (web build succeeds)
- iOS Sync: PASS (`npx cap sync ios`)
- Android Sync: PASS (`npx cap sync android`)
- Xcode: PASS (iOS project opens without errors)
- Android Studio: PASS (Android project opens without errors)

---

## Milestone 5: Native API Integration - Notifications
**Version**: v2.0.0-alpha.5
**Approach**: Parallel Development (deploy specialized agents)

### Tasks
- [ ] Integrate @capacitor/local-notifications plugin
- [ ] Request notification permissions (iOS/Android)
- [ ] Create NotificationPlugin service wrapper
- [ ] Implement schedule notification function (with quote text)
- [ ] Implement cancel all notifications function
- [ ] Handle notification tap actions (open app to Home view)
- [ ] Test notification scheduling on iOS simulator
- [ ] Test notification scheduling on Android emulator
- [ ] Handle iOS notification categories (if needed)
- [ ] Handle Android notification channels (match native app)
- [ ] Implement quiet hours enforcement (skip notifications during quiet hours)
- [ ] Implement random delay within interval window

### Deliverables
- Working local notifications on iOS
- Working local notifications on Android
- Notification permission handling
- Quiet hours enforcement

### Verification
- TypeScript: PASS
- Build: PASS
- iOS Notifications: PASS (schedule, receive, tap)
- Android Notifications: PASS (schedule, receive, tap)
- Quiet Hours: PASS (no notifications during quiet hours)

---

## Milestone 6: Native API Integration - Storage
**Version**: v2.0.0-alpha.6
**Approach**: Parallel Development (deploy specialized agents)

### Tasks
- [ ] Integrate @capacitor/preferences plugin
- [ ] Create PreferencesPlugin service wrapper
- [ ] Implement save/load notification interval
- [ ] Implement save/load quiet hours (start/end times)
- [ ] Implement save/load last shown quote
- [ ] Implement save/load ad removal status
- [ ] Migrate localStorage calls to Preferences plugin
- [ ] Test data persistence on iOS
- [ ] Test data persistence on Android
- [ ] Handle migration from web localStorage (if needed)

### Deliverables
- Persistent storage on iOS (using native Preferences)
- Persistent storage on Android (using native SharedPreferences)
- Settings persistence across app restarts

### Verification
- TypeScript: PASS
- Build: PASS
- iOS Storage: PASS (save/load works, persists after restart)
- Android Storage: PASS (save/load works, persists after restart)

---

## Milestone 7: Monetization Integration
**Version**: v2.0.0-alpha.7
**Approach**: Parallel Development (deploy specialized agents)

### Tasks
- [ ] Integrate @capacitor-community/admob plugin
- [ ] Configure AdMob app IDs (iOS and Android in capacitor.config.ts)
- [ ] Create AdMobService wrapper
- [ ] Implement banner ad display (bottom of screen)
- [ ] Implement ad initialization on app startup
- [ ] Implement ad hiding when ad removal purchased
- [ ] Handle iOS App Tracking Transparency (ATT) prompt
- [ ] Handle Android ad consent (if in EU region)
- [ ] Test ads on iOS (test ad unit IDs)
- [ ] Test ads on Android (test ad unit IDs)
- [ ] Research iOS in-app purchase solution (StoreKit via Capacitor plugin)
- [ ] Research Android in-app purchase solution (Google Play Billing)
- [ ] Implement in-app purchase flow (both platforms)
- [ ] Implement purchase restoration (iOS requirement)
- [ ] Test purchase flow on iOS TestFlight
- [ ] Test purchase flow on Android beta track

### Deliverables
- Working AdMob banner ads (iOS/Android)
- In-app purchase implementation (remove ads)
- Purchase restoration functionality
- Ad consent handling

### Verification
- TypeScript: PASS
- Build: PASS
- iOS Ads: PASS (test banner displays)
- Android Ads: PASS (test banner displays)
- iOS Purchase: PASS (test purchase works)
- Android Purchase: PASS (test purchase works)
- Restore Purchase: PASS (iOS)

---

## Milestone 8: iOS-Specific Configuration
**Version**: v2.0.0-alpha.8
**Approach**: Parallel Development (deploy specialized agents)

### Tasks
- [ ] Configure Info.plist (notification permissions, ATT description)
- [ ] Setup iOS app icons (all required sizes: 20pt-1024pt)
- [ ] Setup iOS launch screen (splash screen)
- [ ] Configure iOS capabilities (Push Notifications, In-App Purchase)
- [ ] Setup iOS signing (development and distribution certificates)
- [ ] Configure iOS deployment target (minimum iOS 13.0)
- [ ] Setup iOS privacy manifest (if required for App Store)
- [ ] Configure iOS app category (Lifestyle or Health & Fitness)
- [ ] Setup iOS screenshots for App Store (required sizes)
- [ ] Create iOS App Store Connect listing (metadata, description)
- [ ] Test on physical iOS device (iPhone)
- [ ] Test on multiple iOS versions (iOS 13, 14, 15, 16, 17)

### Deliverables
- Complete iOS configuration
- App icons and splash screens
- Signing certificates configured
- Ready for TestFlight beta

### Verification
- Xcode: PASS (no build errors)
- iOS Build: PASS (archive succeeds)
- Physical Device: PASS (installs and runs)
- Multi-Version: PASS (tested on iOS 13-17)

---

## Milestone 9: Android Migration & Configuration
**Version**: v2.0.0-alpha.9
**Approach**: Parallel Development (deploy specialized agents)

### Tasks
- [ ] Update AndroidManifest.xml (permissions, notification channels)
- [ ] Configure android/app/build.gradle (match native app settings)
- [ ] Setup Android app icons (all densities: mdpi-xxxhdpi)
- [ ] Setup Android splash screen
- [ ] Configure Android permissions (POST_NOTIFICATIONS for Android 13+)
- [ ] Setup Android signing (debug and release keystores)
- [ ] Configure Android build variants (debug, release)
- [ ] Setup ProGuard rules (if needed)
- [ ] Test on physical Android device
- [ ] Test on multiple Android versions (Android 8-14)
- [ ] Verify notification channels (match native app behavior)
- [ ] Test background notification scheduling
- [ ] Compare with native Android app (feature parity check)

### Deliverables
- Complete Android configuration
- App icons and splash screens
- Signing configured (reuse existing keystore if possible)
- Feature parity with native v1.0.5

### Verification
- Gradle: PASS (build succeeds)
- Android Build: PASS (APK generated)
- Physical Device: PASS (installs and runs)
- Multi-Version: PASS (tested on Android 8-14)
- Feature Parity: PASS (matches native v1.0.5 behavior)

---

## Milestone 10: Reconcile & Integration Testing
**Version**: v2.0.0-beta.1
**Approach**: Integration testing across all platforms

### Tasks
- [ ] Test complete notification flow (schedule → receive → tap) on iOS
- [ ] Test complete notification flow on Android
- [ ] Test quiet hours enforcement (iOS/Android)
- [ ] Test settings persistence across app restarts (iOS/Android)
- [ ] Test ad display and removal (iOS/Android)
- [ ] Test in-app purchase flow (iOS/Android)
- [ ] Test purchase restoration (iOS)
- [ ] Test responsive UI on multiple device sizes
- [ ] Test accessibility (VoiceOver on iOS, TalkBack on Android)
- [ ] Test app lifecycle (background, foreground, termination)
- [ ] Test quote randomization (56 quotes, no duplicates in short term)
- [ ] Performance testing (app launch time, notification scheduling speed)
- [ ] Memory leak testing (long-running app)
- [ ] Battery impact testing (background notifications)
- [ ] Cross-platform behavior consistency check

### Deliverables
- Complete integration test report
- All features working on both platforms
- Performance benchmarks
- Bug fixes for identified issues

### Verification
- iOS Integration: PASS (all features work)
- Android Integration: PASS (all features work)
- Cross-Platform: PASS (consistent behavior)
- Performance: PASS (meets benchmarks)
- Accessibility: PASS (VoiceOver/TalkBack)

---

## Milestone 11: Security Scan
**Version**: v2.0.0-beta.2
**Approach**: Automated security analysis

### Tasks
- [ ] Run npm audit (fix all high/critical vulnerabilities)
- [ ] Run TypeScript compiler in strict mode (no any types)
- [ ] Run ESLint with security rules (eslint-plugin-security)
- [ ] Scan for hardcoded secrets (API keys should be in environment)
- [ ] Review Info.plist permissions (iOS - justify all permissions)
- [ ] Review AndroidManifest.xml permissions (justify all permissions)
- [ ] Review Capacitor plugin permissions (minimize permissions)
- [ ] Check for insecure data storage (no sensitive data in localStorage)
- [ ] Validate HTTPS-only network requests (if any)
- [ ] Review third-party dependencies (AdMob, billing plugins)
- [ ] iOS security review (App Transport Security, keychain usage)
- [ ] Android security review (ProGuard, obfuscation)
- [ ] Test SSL pinning (if making network requests)
- [ ] Review App Store security guidelines (iOS)
- [ ] Review Google Play security guidelines (Android)

### Deliverables
- Clean npm audit report (0 vulnerabilities)
- Security scan report
- All vulnerabilities addressed
- Compliance with platform security guidelines

### Verification
- npm audit: PASS (0 vulnerabilities)
- TypeScript: PASS (strict mode, no errors)
- ESLint: PASS (security rules)
- iOS Security: PASS (Apple guidelines)
- Android Security: PASS (Google guidelines)

---

## Milestone 12: Remediation
**Version**: v2.0.0-rc.1
**Approach**: Fix ALL identified issues

### Tasks
- [ ] Fix all security vulnerabilities from scan
- [ ] Fix all TypeScript errors and warnings
- [ ] Fix all ESLint warnings
- [ ] Fix all accessibility issues (WCAG AA compliance)
- [ ] Fix all performance issues (slow renders, memory leaks)
- [ ] Fix all cross-platform inconsistencies
- [ ] Address all beta tester feedback
- [ ] Update documentation (README, FRAMEWORK.md)
- [ ] Re-run all tests (unit, integration, accessibility)
- [ ] Re-verify on physical devices (iOS/Android)
- [ ] Final code review (code quality, comments, cleanup)

### Deliverables
- Zero critical/high security issues
- Zero TypeScript/ESLint errors
- All tests passing
- Production-ready code

### Verification
- Security: PASS (0 critical/high issues)
- TypeScript: PASS (0 errors)
- ESLint: PASS (0 errors)
- Tests: PASS (all tests pass)
- Devices: PASS (iOS/Android physical devices)

---

## Milestone 13: iOS App Store Submission
**Version**: v2.0.0
**Approach**: Production iOS build

### Tasks
- [ ] Create production signing certificates (iOS Distribution)
- [ ] Create provisioning profiles (App Store distribution)
- [ ] Build iOS release archive (Product → Archive in Xcode)
- [ ] Upload to App Store Connect (Xcode Organizer)
- [ ] Complete App Store listing (description, keywords, screenshots)
- [ ] Set app category (Lifestyle or Health & Fitness)
- [ ] Upload required screenshots (all device sizes)
- [ ] Set pricing (free with IAP)
- [ ] Configure in-app purchase product (remove_ads, $1.00)
- [ ] Complete privacy details (data collection disclosure)
- [ ] Submit for App Review
- [ ] Address App Review feedback (if any)
- [ ] Release to App Store (after approval)

### Deliverables
- iOS app live on App Store
- App Store listing complete
- In-app purchase configured

### Verification
- Archive: PASS (iOS release build succeeds)
- Upload: PASS (submitted to App Store Connect)
- App Review: PASS (approved by Apple)
- Live: PASS (app available on App Store)

---

## Milestone 14: Android Google Play Submission
**Version**: v2.0.0
**Approach**: Production Android build

### Tasks
- [ ] Build Android release AAB (Android App Bundle)
- [ ] Sign AAB with release keystore
- [ ] Upload to Google Play Console (production track)
- [ ] Complete Play Store listing (description, keywords, screenshots)
- [ ] Set app category (Lifestyle or Health & Fitness)
- [ ] Upload required screenshots and feature graphic
- [ ] Set pricing (free with IAP)
- [ ] Configure in-app purchase product (remove_ads, $1.00)
- [ ] Complete privacy policy and data safety form
- [ ] Complete content rating questionnaire
- [ ] Submit for review
- [ ] Address Play Store review feedback (if any)
- [ ] Release to production (after approval)

### Deliverables
- Android app live on Google Play
- Play Store listing complete
- In-app purchase configured

### Verification
- Build: PASS (release AAB generated)
- Upload: PASS (submitted to Play Console)
- Review: PASS (approved by Google)
- Live: PASS (app available on Google Play)

---

## Milestone 15: Documentation & Handoff
**Version**: v2.0.0
**Approach**: Finalize documentation

### Tasks
- [ ] Update CHANGE_NOTES.md with v2.0.0 details
- [ ] Update README.md (new architecture, build instructions)
- [ ] Update FRAMEWORK.md (Capacitor architecture)
- [ ] Create BUILD_GUIDE_V2.md (React + Capacitor build instructions)
- [ ] Create IOS_BUILD_GUIDE.md (Xcode build instructions)
- [ ] Create ANDROID_BUILD_GUIDE.md (Android Studio build instructions)
- [ ] Document migration from v1.0.5 to v2.0.0
- [ ] Create deprecation notice for native Android v1.x
- [ ] Update MILESTONES.md (mark v2.0.0 complete)
- [ ] Archive native Android source (create v1-legacy branch)
- [ ] Tag v2.0.0 release in git
- [ ] Create GitHub release notes

### Deliverables
- Complete documentation for v2.0.0
- Migration guide
- Build guides for iOS and Android
- Git tags and releases

### Verification
- Documentation: PASS (all docs updated)
- Build Guides: PASS (tested by following guides)
- Git: PASS (tagged and released)

---

## Workflow Summary

Each milestone (1-12) follows this pattern:
1. **Parallel Development**: Deploy specialized coding agents for tasks
2. **Reconcile & Test**: Integration verification
3. **Security Scan**: Automated analysis
4. **Remediation**: Fix ALL issues immediately
5. **Commit & Push**: Update CHANGE_NOTES.md, commit with notes as message, push to branch

---

## Version Strategy

- **v2.0.0-alpha.x**: Development milestones (features being built)
- **v2.0.0-beta.x**: Integration and testing (feature-complete, testing)
- **v2.0.0-rc.x**: Release candidate (remediation, final fixes)
- **v2.0.0**: Production release (iOS + Android live on app stores)

---

## Risk Mitigation

### Technical Risks
1. **Notification Reliability**: Capacitor local-notifications plugin may have platform differences
   - **Mitigation**: Thorough testing on physical devices, fallback strategies
2. **In-App Purchase Complexity**: Different implementations for iOS/Android
   - **Mitigation**: Use well-maintained plugins, test extensively on TestFlight/Beta
3. **Performance**: Web-based app may be slower than native
   - **Mitigation**: Optimize bundle size, lazy loading, performance profiling
4. **Battery Impact**: Background notifications may drain battery
   - **Mitigation**: Efficient scheduling, respect platform best practices

### Business Risks
1. **App Store Rejection**: iOS review may reject app
   - **Mitigation**: Follow Apple guidelines strictly, prepare clear responses
2. **User Migration**: Existing Android users need to migrate
   - **Mitigation**: Clear communication, data migration guide
3. **Feature Parity**: Capacitor may not support all native features
   - **Mitigation**: Research plugins upfront, identify gaps early

---

## Success Criteria

### Technical Success
- [ ] iOS app builds and runs on iOS 13+
- [ ] Android app maintains feature parity with v1.0.5
- [ ] Notifications work reliably on both platforms
- [ ] Settings persist across app restarts
- [ ] In-app purchase works on both platforms
- [ ] Ads display correctly (when not removed)
- [ ] WCAG AA accessibility compliance
- [ ] Zero critical security vulnerabilities

### Business Success
- [ ] iOS app approved and live on App Store
- [ ] Android v2.0.0 live on Google Play
- [ ] Positive user reviews (4+ stars)
- [ ] Smooth migration from v1.0.5 (Android users)

---

## Timeline Estimate (Developer Effort)

- **Milestones 1-3** (Foundation, UI, Logic): 3-5 days
- **Milestones 4-7** (Capacitor, Native APIs, Monetization): 4-6 days
- **Milestones 8-9** (iOS/Android Config): 2-3 days
- **Milestones 10-12** (Testing, Security, Remediation): 3-4 days
- **Milestones 13-15** (App Store Submission, Docs): 2-3 days

**Total**: ~14-21 days (developer effort, not calendar time)

**Note**: App Store review (iOS) typically takes 1-3 days. Google Play review typically takes 1-2 days.

---

## Next Steps

**Status**: ⏸️ AWAITING APPROVAL

This plan requires user approval before proceeding. Once approved:

1. Create branch `claude/capacitor-migration-v2.0.0-[session-id]`
2. Begin Milestone 1: Project Structure & Foundation
3. Deploy parallel development agents for initial setup
4. Follow milestone workflow for each subsequent milestone
5. Update CHANGE_NOTES.md after each milestone completion
6. Push commits to branch after each milestone

**Ready to proceed when instructed.**

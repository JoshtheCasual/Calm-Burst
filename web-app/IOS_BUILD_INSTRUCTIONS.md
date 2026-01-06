# iOS Build Instructions for Calm Burst v2.0.0

## Prerequisites

### Required Software (macOS only)
- macOS 12.0 or later
- Xcode 13.0 or later
- Node.js 18+ and npm
- CocoaPods (install via: `sudo gem install cocoapods`)

### Required Accounts
- Apple Developer account (for distribution builds and App Store)
- Free Apple ID works for development/testing

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
npx cap sync ios
```

## Opening in Xcode

```bash
npx cap open ios
```

This opens the Xcode project at `ios/App/App.xcworkspace`

## Configuration in Xcode

### 1. Signing & Capabilities

1. Select the **App** target in project navigator
2. Go to **Signing & Capabilities** tab
3. Select your **Team** from dropdown
4. Xcode will automatically create a provisioning profile

### 2. Verify Capabilities

Ensure these capabilities are enabled:
- ✅ **Push Notifications** (for local notifications)

### 3. Verify Info.plist

The following permissions are already configured:
- `NSUserNotificationsUsageDescription` - "Calm Burst sends you motivational quotes at intervals you choose to help you stay calm and focused throughout your day."

### 4. App Icons (Optional but Recommended)

1. Navigate to **Assets.xcassets > AppIcon**
2. Drag and drop your app icons for each required size
3. Required sizes: 20pt, 29pt, 40pt, 58pt, 60pt, 76pt, 80pt, 87pt, 120pt, 152pt, 167pt, 180pt, 1024pt

**Current Status**: Using default Capacitor icons

### 5. Version and Build Numbers

1. Select **App** target
2. Go to **General** tab
3. Set **Version**: 2.0.0
4. Set **Build**: 1

## Building and Testing

### Run on Simulator

1. Select a simulator from device dropdown (e.g., iPhone 15)
2. Click **Run** button (▶) or press `Cmd+R`
3. App will launch in iOS Simulator

### Run on Physical Device

1. Connect iPhone via USB
2. Select your device from device dropdown
3. Click **Run** button (▶) or press `Cmd+R`
4. If prompted, trust the developer certificate on your device

**Note**: For first-time device deployment, you may need to:
- Enable Developer Mode on iOS device (Settings > Privacy & Security > Developer Mode)
- Trust the developer certificate (Settings > General > VPN & Device Management)

## Testing Checklist

- [ ] App launches successfully
- [ ] Home screen displays quote
- [ ] "New Quote" button loads random quotes
- [ ] Navigation to Settings works
- [ ] Settings persist after app restart
- [ ] Notification permission request appears
- [ ] Notifications schedule correctly
- [ ] Quiet hours respected
- [ ] Notification tap opens app

## Building for Distribution

### Create Archive

1. Select **Any iOS Device** from device dropdown
2. Go to **Product > Archive**
3. Wait for archive to complete
4. Xcode Organizer will open

### Upload to App Store Connect

1. In Organizer, select your archive
2. Click **Distribute App**
3. Select **App Store Connect**
4. Follow wizard to upload

### TestFlight Beta Testing

1. After upload processing completes (~10-15 minutes)
2. Go to App Store Connect > TestFlight
3. Add internal testers
4. Submit for external testing (requires App Review)

### App Store Submission

1. Complete app information in App Store Connect
2. Add screenshots (required sizes)
3. Set pricing (Free with IAP - if implementing monetization)
4. Submit for review

## Troubleshooting

### Pod Install Errors

If you encounter CocoaPods errors:
```bash
cd ios/App
pod deintegrate
pod install
```

### Signing Errors

- Ensure you're signed in to Xcode with your Apple ID (Xcode > Settings > Accounts)
- Try automatic signing first before manual signing
- For distribution, ensure you have a valid Distribution certificate

### Build Errors

- Clean build folder: Product > Clean Build Folder (`Cmd+Shift+K`)
- Delete Derived Data: Xcode > Settings > Locations > Derived Data > Click arrow > Delete
- Re-sync Capacitor: `npx cap sync ios`

### Simulator Not Launching

- Quit Simulator app completely
- Delete simulator: Xcode > Window > Devices and Simulators > Simulators > Right-click > Delete
- Create new simulator of same type

## App Store Requirements

### Required Assets

- [ ] App Icon (1024x1024px)
- [ ] Screenshots for all required device sizes:
  - 6.7" Display (iPhone 15 Pro Max): 1290 x 2796 pixels
  - 6.5" Display (iPhone 14 Plus): 1284 x 2778 pixels
  - 5.5" Display (iPhone 8 Plus): 1242 x 2208 pixels
  - 12.9" iPad Pro: 2048 x 2732 pixels

### App Information

- App Name: Calm Burst
- Subtitle: Motivational Quotes for Calm Living
- Category: Health & Fitness or Lifestyle
- Keywords: motivational, quotes, mindfulness, calm, notifications
- Privacy Policy URL: (Required - must create)
- Support URL: (Required - must create)

### Privacy Details

The app collects:
- No user data
- No tracking
- Local storage only (quotes, settings)

### Age Rating

- 4+ (No objectionable content)

## Version Management

- **Marketing Version** (user-facing): 2.0.0
- **Build Number** (incremental): Start at 1, increment for each upload

## Post-Submission

- Average review time: 24-48 hours
- Be ready to respond to App Review questions
- Monitor crash reports in Xcode Organizer
- Respond to user reviews

## Support Contacts

For Capacitor issues:
- https://capacitorjs.com/docs/ios
- https://ionic.link/discord

For iOS/Xcode issues:
- https://developer.apple.com/forums

## Quick Reference

**Build commands:**
```bash
npm run build              # Build web app
npx cap sync ios           # Sync to iOS
npx cap open ios           # Open in Xcode
```

**Xcode shortcuts:**
- `Cmd+R` - Run
- `Cmd+.` - Stop
- `Cmd+Shift+K` - Clean build
- `Cmd+B` - Build

## Current Status

✅ iOS project fully configured and ready to build
✅ Notification permissions configured
✅ App name and bundle ID set
✅ Capacitor plugins integrated
✅ Web assets built and synced

**Next step**: Open in Xcode and build!

# Calm Burst - Motivational Notification App

A simple Android app that delivers motivational quotes through notifications at configurable random intervals.

## Features

- **Random Motivational Notifications**: Receive inspiring quotes at intervals you control
- **Configurable Scheduling**: Choose from 4 interval windows (1-2h, 1-6h, 1-12h, 1-24h)
- **Quiet Hours**: Set do-not-disturb times (e.g., 10pm - 7am)
- **Quote History**: View your most recent motivational quote
- **Clean, Accessible Design**: Earthy color palette with WCAG AA compliance
- **Ad-Supported**: Free with ads, or support the creator for $1 to remove them

## Installation

Download and install the APK from the releases section.

**Requirements**: Android 8.0 (API 26) or higher

## Usage

1. **First Launch**: Grant notification permissions
2. **Settings**: Configure your preferred notification interval and quiet hours
3. **Enjoy**: Receive motivational quotes throughout your day
4. **Optional**: Remove ads with a one-time $1 purchase

## Quote Collection

The app includes a curated collection of motivational quotes with full attribution (author, year, context).

To contribute quotes, see the XML structure in `app/src/main/res/raw/quotes.xml`

## Technical Stack

- **Language**: Kotlin
- **Architecture**: MVVM with AndroidX components
- **Scheduling**: WorkManager for reliable background notifications
- **Storage**: DataStore for preferences
- **Monetization**: Google AdMob + Play Billing

## Building from Source

```bash
# Clone the repository
git clone https://github.com/JoshtheCasual/Calm-Burst.git
cd Calm-Burst

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing config)
./gradlew assembleRelease
```

## Project Structure

```
Calm-Burst/
├── app/
│   ├── src/main/
│   │   ├── java/com/calmburst/
│   │   │   ├── data/          # Quote repository, DataStore
│   │   │   ├── ui/            # Activities, Fragments, ViewModels
│   │   │   ├── worker/        # Notification scheduling
│   │   │   └── util/          # Helpers
│   │   ├── res/
│   │   │   ├── raw/           # quotes.xml
│   │   │   ├── layout/        # UI layouts
│   │   │   └── values/        # Colors, strings, themes
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── FRAMEWORK.md               # Technical specification
├── CLAUDE.md                  # AI agent instructions
├── CHANGE_NOTES.md            # Version history
└── README.md                  # This file
```

## License

See LICENSE file for details.

## Support

For issues or feature requests, please open an issue on GitHub.

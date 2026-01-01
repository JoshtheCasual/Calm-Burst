# Building Calm Burst on Android Phone

You can build this APK directly on your Android device using Termux.

## Requirements

- **Android 7.0+** (API 24+)
- **4GB+ RAM** recommended (2GB minimum)
- **2GB free storage** for build tools and dependencies
- **Termux app** (install from F-Droid, NOT Google Play - Play Store version is outdated)

## Quick Start (5 Steps)

### Step 1: Install Termux

Download from **F-Droid**: https://f-droid.org/en/packages/com.termux/

⚠️ **Important**: Do NOT install from Google Play Store - use F-Droid version only.

### Step 2: Setup Build Environment

Open Termux and run these commands:

```bash
# Update package lists
pkg update && pkg upgrade -y

# Install required packages (this takes 5-10 minutes)
pkg install -y git openjdk-17 wget unzip

# Install Gradle
wget https://services.gradle.org/distributions/gradle-8.5-bin.zip
unzip gradle-8.5-bin.zip -d $HOME
rm gradle-8.5-bin.zip
echo 'export PATH=$HOME/gradle-8.5/bin:$PATH' >> $HOME/.bashrc
source $HOME/.bashrc

# Verify installations
java -version    # Should show OpenJDK 17
gradle -v        # Should show Gradle 8.5
```

### Step 3: Install Android SDK (Minimal)

```bash
# Create Android SDK directory
mkdir -p $HOME/android-sdk
cd $HOME/android-sdk

# Download Android command-line tools
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-11076708_latest.zip
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/ 2>/dev/null || true
rm commandlinetools-linux-11076708_latest.zip

# Set environment variables
echo 'export ANDROID_HOME=$HOME/android-sdk' >> $HOME/.bashrc
echo 'export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$PATH' >> $HOME/.bashrc
source $HOME/.bashrc

# Accept licenses and install build tools
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

### Step 4: Clone Repository and Build

```bash
# Navigate to home directory
cd $HOME

# Clone the repository
git clone https://github.com/JoshtheCasual/Calm-Burst.git
cd Calm-Burst

# Create local.properties with SDK path
echo "sdk.dir=$HOME/android-sdk" > local.properties

# Build the APK (this takes 5-15 minutes on first run)
./gradlew assembleDebug
```

### Step 5: Install the APK

```bash
# The APK is located at:
# app/build/outputs/apk/debug/app-debug.apk

# Copy to Download folder so you can install it
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/CalmBurst.apk

# You can now:
# 1. Open your file manager
# 2. Navigate to Downloads folder
# 3. Tap CalmBurst.apk to install
```

## Troubleshooting

### "Out of memory" errors

If Gradle runs out of memory, create/edit `gradle.properties`:

```bash
echo "org.gradle.jvmargs=-Xmx1536m -XX:MaxMetaspaceSize=512m" >> gradle.properties
```

### Build is too slow

Disable parallel builds to reduce memory usage:

```bash
echo "org.gradle.parallel=false" >> gradle.properties
```

### "AAPT not found" error

Install build tools explicitly:

```bash
sdkmanager "build-tools;34.0.0"
```

### Storage space issues

Clean build cache to free space:

```bash
./gradlew clean
rm -rf ~/.gradle/caches/
```

## Alternative: Using AIDE

If Termux is too complex, try **AIDE** (Android IDE Development Environment):

1. Install **AIDE** from Google Play Store (paid app ~$5)
2. Open AIDE and create new project
3. Copy all source files from this repo into AIDE project
4. Build and run directly in AIDE

**Note**: AIDE has better UI but costs money. Termux is free and more powerful.

## Resource Requirements

**Minimum**:
- 2GB RAM
- 1.5GB free storage
- Android 7.0+

**Recommended**:
- 4GB+ RAM
- 3GB+ free storage
- Android 9.0+
- Device with cooling (builds generate heat)

## Tips for Success

1. **Keep device plugged in** - Building uses significant battery
2. **Close other apps** - Free up RAM for Gradle
3. **Use WiFi** - Downloading dependencies uses data
4. **Be patient** - First build takes 10-20 minutes
5. **Keep screen on** - Prevent build interruption (Termux settings → "Acquire wakelock")

## Build Time Estimates

- **First build**: 15-30 minutes (downloading dependencies)
- **Subsequent builds**: 2-5 minutes
- **Clean build**: 5-10 minutes

## Storage Breakdown

- Termux packages (JDK, Git, Wget): ~500MB
- Android SDK (minimal): ~800MB
- Gradle dependencies: ~300MB
- Project source code: ~50MB
- **Total**: ~1.6GB minimum

---

**Enjoy building Android apps on your phone!** 📱

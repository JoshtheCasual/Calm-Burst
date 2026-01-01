# Dockerfile for building Calm Burst Android APK
# This allows building the APK in a containerized environment without installing Android SDK locally

FROM ubuntu:22.04

# Avoid interactive prompts during build
ENV DEBIAN_FRONTEND=noninteractive

# Install dependencies
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    wget \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Set up environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Download and install Android Command Line Tools
RUN mkdir -p $ANDROID_HOME/cmdline-tools && \
    cd $ANDROID_HOME/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip commandlinetools-linux-9477386_latest.zip && \
    rm commandlinetools-linux-9477386_latest.zip && \
    mv cmdline-tools latest

# Accept licenses and install required SDK packages
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Create local.properties with test AdMob IDs
RUN echo "sdk.dir=$ANDROID_HOME" > local.properties && \
    echo "ADMOB_APP_ID=ca-app-pub-3940256099942544~3347511713" >> local.properties && \
    echo "ADMOB_BANNER_ID=ca-app-pub-3940256099942544/6300978111" >> local.properties

# Make gradlew executable
RUN chmod +x gradlew

# Build the APK
RUN ./gradlew assembleDebug --no-daemon

# The APK will be in app/build/outputs/apk/debug/app-debug.apk
# To extract it from the container:
# docker build -t calm-burst-builder .
# docker create --name temp calm-burst-builder
# docker cp temp:/app/app/build/outputs/apk/debug/app-debug.apk .
# docker rm temp

CMD ["echo", "APK built successfully! Extract with: docker cp container:/app/app/build/outputs/apk/debug/app-debug.apk ."]

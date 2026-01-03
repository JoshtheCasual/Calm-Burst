plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.calmburst"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.calmburst"
        minSdk = 26
        targetSdk = 34
        versionCode = 6
        versionName = "1.0.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")

    // Fragment and Navigation
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // WorkManager for notification scheduling
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // DataStore for preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Kotlin Coroutines (explicit for Flow operators like first())
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // XML Parsing (included in Android SDK)
    // No additional dependency needed for basic XML parsing

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Android Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.work:work-testing:2.9.0")
}

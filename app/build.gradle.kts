plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Add this
}

android {
    namespace = "com.example.qtrace"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.qtrace"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
// Standard Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase (BOM ensures compatible versions)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-firestore")

    // Map: osmdroid (The Native "Leaflet")
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // Image Loading: Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
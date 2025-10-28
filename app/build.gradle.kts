plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.androidusbcamapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.androidusbcamapp"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = false
    }

    packagingOptions {
        pickFirst("lib/arm64-v8a/libjpeg-turbo.so")
        pickFirst("lib/arm64-v8a/libusb1.0.so")
        pickFirst("lib/arm64-v8a/libuvc.so")
        pickFirst("lib/armeabi-v7a/libjpeg-turbo.so")
        pickFirst("lib/armeabi-v7a/libusb1.0.so")
        pickFirst("lib/armeabi-v7a/libuvc.so")
        pickFirst("lib/x86/libjpeg-turbo.so")
        pickFirst("lib/x86/libusb1.0.so")
        pickFirst("lib/x86/libuvc.so")
        pickFirst("lib/x86_64/libjpeg-turbo.so")
        pickFirst("lib/x86_64/libusb1.0.so")
        pickFirst("lib/x86_64/libuvc.so")
    }
}

dependencies {
    // Dependensi Inti (Core)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Menggunakan versi yang sedikit lebih tua untuk menghindari kemungkinan build yang rusak di JitPack
    implementation("com.github.saki4510t:UVCCamera:v2.13.3")

    // Dependensi untuk Unit Testing (Lokal)
    testImplementation("junit:junit:4.13.2")

    // Dependensi untuk Android Instrumentation Testing (di perangkat/emulator)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

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

        // Menambahkan runner untuk instrumentation test
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

    // Mengatur kompatibilitas Java ke versi 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // Diperlukan jika Anda menggunakan View Binding
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Dependensi Inti (Core)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Sering digunakan untuk layout

    // Dependensi CameraX
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")

    // Dependensi USB Serial (jika Anda masih menggunakannya)
    implementation("com.github.mik3y:usb-serial-for-android:3.9.0")

    // Dependensi untuk Unit Testing (Lokal)
    testImplementation("junit:junit:4.13.2")

    // Dependensi untuk Android Instrumentation Testing (di perangkat/emulator)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

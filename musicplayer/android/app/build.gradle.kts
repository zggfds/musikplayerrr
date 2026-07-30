plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "com.example.musicplayer"
    compileSdk = 36 // Оставляем 36, как требовали плагины

    // Современный способ задать версию Java для всего приложения
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17) // ЭТО ВАЖНО: заставляет всё использовать Java 17
    }

    defaultConfig {
        applicationId = "com.example.musicplayer"
        minSdk = 21
        targetSdk = 34
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

flutter {
    source = "../.."
}
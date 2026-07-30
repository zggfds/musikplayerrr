plugins {
    id("com.android.application")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "com.example.musicplayer"
    compileSdk = 36  // Ставим 36, как просил лог

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        applicationId = "com.example.musicplayer"
        minSdk = 21
        targetSdk = 34 // Можно оставить 34 или поставить 36
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }
    
    // ДОБАВЬ ИЛИ ИСПРАВЬ ЭТОТ БЛОК:
    kotlinOptions {
        jvmTarget = "17" 
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

flutter {
    source = "../.."
}

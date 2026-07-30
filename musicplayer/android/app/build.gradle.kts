plugins {
    id("com.android.application")
    id("kotlin-android")
    // Плагин Flutter должен быть последним
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    // Твой уникальный ID приложения
    namespace = "com.example.musicplayer"
    
    // Требуется версия 36 для новых плагинов (just_audio, sqflite)
    compileSdk = 36

    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Настройка Kotlin на современный лад (без предупреждений)
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    defaultConfig {
        applicationId = "com.example.musicplayer"
        
        // Минимум 21 для работы on_audio_query
        minSdk = 21
        targetSdk = 34
        
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    buildTypes {
        release {
            // Пока используем отладочный ключ, чтобы APK собрался без лишних настроек подписей
            signingConfig = signingConfigs.getByName("debug")
            
            // Оптимизация (можно выключить, если будут ошибки, но для релиза это хорошо)
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
}

flutter {
    source = "../.."
}

dependencies {
    // Здесь обычно ничего не нужно менять, Flutter сам добавит зависимости
}
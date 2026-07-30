allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

// Настройка путей сборки (стандарт для Flutter)
val newBuildDir: Directory = rootProject.layout.buildDirectory.dir("../../build").get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}

// Мы убрали блок evaluationDependsOn(":app"), так как он мешает сборке

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

// Умный фикс для Namespace (работает и для Groovy, и для Kotlin плагинов)
subprojects {
    val setupProject = {
        val android = extensions.findByName("android") as? com.android.build.gradle.BaseExtension
        android?.apply {
            // Исправляем Namespace
            if (namespace == null) {
                namespace = project.group.toString()
            }
            
            // ПРИНУДИТЕЛЬНО ставим Java 17 для всех плагинов
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
        
        // ПРИНУДИТЕЛЬНО ставим Kotlin JVM 17 для всех плагинов
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    if (state.executed) {
        setupProject()
    } else {
        afterEvaluate { setupProject() }
    }
}
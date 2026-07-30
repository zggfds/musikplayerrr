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
    val setupNamespace = {
        val android = extensions.findByName("android") as? com.android.build.gradle.BaseExtension
        android?.apply {
            if (namespace == null) {
                // Если namespace не задан в плагине, берем его из ID проекта
                namespace = project.group.toString()
            }
        }
    }

    // Если проект уже "вычислен", применяем сразу. Если нет - ждем.
    if (state.executed) {
        setupNamespace()
    } else {
        afterEvaluate { setupNamespace() }
    }
}
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory = rootProject.layout.buildDirectory.dir("../../build").get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

// Современный фикс для всех плагинов (библиотек)
subprojects {
    // 1. Форсируем Java 17 для всех Java-задач
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    // 2. Форсируем JVM 17 для всех Kotlin-задач (используем новый DSL)
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    // 3. Исправляем Namespace без использования устаревшего BaseExtension
    afterEvaluate {
        if (extensions.findByName("android") != null) {
            val android = extensions.getByName("android")
            // Используем рефлексию, чтобы не зависеть от конкретного класса Extension
            try {
                val getNamespace = android.javaClass.getMethod("getNamespace")
                val setNamespace = android.javaClass.getMethod("setNamespace", String::class.java)
                
                if (getNamespace.invoke(android) == null) {
                    setNamespace.invoke(android, project.group.toString())
                }
            } catch (e: Exception) {
                // Если не получилось, значит плагин сам справится или это не Android-плагин
            }
        }
    }
}
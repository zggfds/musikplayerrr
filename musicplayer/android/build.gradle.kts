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

// ПРИНУДИТЕЛЬНОЕ ПЕРЕОПРЕДЕЛЕНИЕ ДЛЯ ВСЕХ ПОДПРОЕКТОВ И ПЛАГИНОВ
subprojects {
    // 1. Форсируем Java 17 для всех задач JavaCompile (решает проблему 11 vs 17)
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.release.set(17)
    }

    // 2. Форсируем JVM 17 для всех задач KotlinCompile
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    // 3. Исправляем Namespace (на случай, если плагин его не указал)
    afterEvaluate {
        if (extensions.findByName("android") != null) {
            val android = extensions.getByName("android")
            try {
                val setNamespace = android.javaClass.getMethod("setNamespace", String::class.java)
                val getNamespace = android.javaClass.getMethod("getNamespace")
                if (getNamespace.invoke(android) == null) {
                    setNamespace.invoke(android, project.group.toString())
                }
            } catch (e: Exception) { /* Пропускаем, если метод не найден */ }
        }
    }
}
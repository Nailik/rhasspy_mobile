import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())

    implementation("com.android.tools.build:gradle:_")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:_")
    implementation(kotlin("gradle-plugin", "_"))
    implementation(kotlin("android-extensions"))
}

gradlePlugin {
    plugins {
        register("base-gradle") {
            id = "base-gradle"
            implementationClass = "BaseGradle"
        }
    }
}

allprojects {
    tasks.withType(KotlinCompile::class.java).all {
        kotlinOptions {
            freeCompilerArgs += listOf("-P", "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=1.9.0")
        }
    }
}
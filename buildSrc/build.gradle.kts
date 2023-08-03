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
import de.fayard.refreshVersions.core.versionFor
buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        //classpath dependencies cannot be loaded from buildSrc
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
        @Suppress("GradlePluginVersion")
        classpath("com.android.tools.build:gradle:_")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
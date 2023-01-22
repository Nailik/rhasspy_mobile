pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.51.0"
}

rootProject.name = "Rhasspy_Mobile"
include(":androidApp")
include(":shared")
include(":shared-ui")
include(":shared-viewmodel")

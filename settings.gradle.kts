pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.40.1"
////                            # available:"0.40.2"
}

rootProject.name = "Rhasspy_Mobile"
include(":androidApp")
include(":MultiPlatformLibrary")
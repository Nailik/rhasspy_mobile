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
include(":iosApp")
include(":shared")
include(":ui")
include(":viewmodel")
include(":logic")
include(":resources")
include(":platformspecific")
include(":data")
include(":settings")
include(":widget")

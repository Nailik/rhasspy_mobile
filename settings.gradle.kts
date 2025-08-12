pluginManagement {
    includeBuild("./gradle/conventions")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Rhasspy_Mobile"
includeBuild("./gradle/conventions")
include(":androidApp")
include(":app")

include(":overlay")

include(":ui")
include(":widget")

include(":viewmodel")
include(":logic")
include(":platformspecific")
include(":data")
include(":settings")
include(":resources")



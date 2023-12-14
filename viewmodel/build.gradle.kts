@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

plugins {
    id("org.kodein.mock.mockmp")
    kotlin("multiplatform")
    id("com.android.library")
    id("base-gradle")
    id("org.gradle.test-retry")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":data"))
                implementation(project(":platformspecific"))
                implementation(project(":logic"))
                implementation(project(":resources"))
                implementation(project(":settings"))
                implementation(Touchlab.kermit)
                implementation(Jetbrains.Kotlinx.coroutines)
                implementation(Jetbrains.Kotlinx.immutable)
                implementation(Jetbrains.Kotlinx.serialization)
                implementation(Jetbrains.Kotlinx.dateTime)
                implementation(Jetbrains.Compose.foundation)
                implementation(Jetbrains.Compose.foundation)
                implementation(Jetbrains.Compose.materialIconsExtended)
                implementation(Koin.core)
                implementation(Icerock.Mvvm.core)
                implementation(Ktor2.Client.core)
                implementation(Icerock.Resources.resourcesCompose)
                implementation(Square.okio)
                implementation(Mikepenz.aboutLibrariesCore)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":testutils"))
                implementation(Koin.test)
                implementation(Kotlin.test)
                implementation(KotlinX.Coroutines.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.multidex)
                implementation(Square.okio)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(AndroidX.archCore.testing)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies {
                implementation(Square.okio)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating
    }
}

android {
    namespace = "org.rhasspy.mobile.viewmodel"

    testOptions {
        unitTests.isReturnDefaultValues = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
}

mockmp {
    usesHelper = true
    installWorkaround()
}
mockmp.installWorkaround()

tasks.withType<Test>().configureEach {
    retry {
        ignoreFailures = true
        maxRetries.set(20)
        maxFailures.set(20)
        failOnPassedAfterRetry.set(false)
    }
}

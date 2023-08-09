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
                implementation(Jetbrains.Compose.materialIconsExtended)
                implementation(Koin.core)
                implementation(Icerock.Mvvm.core)
                implementation(Ktor2.Client.core)
                implementation(Icerock.Resources.resourcesCompose)
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.2.1")
                implementation(Mikepenz.aboutLibrariesCore)
                implementation(Russhwolf.multiplatformSettingsNoArg)
            }
        }
        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(project(":app"))
                implementation(Koin.test)
                implementation(Kotlin.test)
                implementation(KotlinX.Coroutines.test)
                implementation(Russhwolf.multiplatformSettingsTest)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.multidex)
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.2.1")
            }
        }
        val androidUnitTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation(AndroidX.archCore.testing)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.2.1")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
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
}

tasks.withType<Test>().configureEach {
    retry {
        ignoreFailures = true
        maxRetries.set(20)
        maxFailures.set(20)
        failOnPassedAfterRetry.set(false)
    }
}

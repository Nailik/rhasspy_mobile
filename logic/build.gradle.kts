@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

plugins {
    id("org.kodein.mock.mockmp")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
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
                implementation(project(":resources"))
                implementation(project(":settings"))
                implementation(Jetbrains.Kotlinx.coroutines)
                implementation(Touchlab.kermit)
                implementation(Jetbrains.Kotlinx.serialization)
                implementation(Jetbrains.Kotlinx.immutable)
                implementation(Jetbrains.Kotlinx.dateTime)
                implementation(Icerock.Resources.resourcesCompose)
                implementation(Ktor2.Server.core)
                implementation(Ktor2.Server.cors)
                implementation(Ktor2.Server.cio)
                implementation(Ktor2.Server.dataConversion)
                implementation(Ktor2.Client.cio)
                implementation(Ktor2.Server.statusPages)
                implementation(Ktor2.Plugins.network)
                implementation(Ktor2.Server.core)
                implementation(Benasher.uuid)
                implementation(Square.okio)
                implementation(Koin.core)
            }
        }
        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(project(":platformspecific"))
                implementation(Russhwolf.multiplatformSettingsTest)
                implementation(Russhwolf.multiplatformSettingsNoArg)
                implementation(Kotlin.test)
                implementation(Koin.test)
                implementation(KotlinX.Coroutines.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.multidex)
                implementation(Picovoice.porcupineAndroid)
            }
        }
        val androidUnitTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation(project(":androidApp"))
                implementation(project(":app"))
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
    namespace = "org.rhasspy.mobile.logic"

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
        maxRetries.set(2)
        maxFailures.set(20)
        failOnPassedAfterRetry.set(false)
    }
}
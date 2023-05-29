@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("base-gradle")
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
                implementation(Jetbrains.Kotlinx.dateTime)
                implementation(Jetbrains.Compose.foundation)
                implementation(Koin.core)
                implementation(Icerock.Mvvm.core)
                implementation(Ktor2.Client.core)
                implementation(Icerock.Resources.resourcesCompose)
                implementation(Square.okio)
                implementation(Mikepenz.aboutLibrariesCore)
                implementation(Russhwolf.multiplatformSettingsNoArg)
            }
        }
        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Kotlin.test)
                implementation(KotlinX.Coroutines.test)
                implementation(Russhwolf.multiplatformSettingsTest)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.multidex)
                implementation(Square.okio)
            }
        }
        val androidUnitTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation(project(":androidApp"))
                implementation(project(":shared"))
                implementation(project(":platformspecific"))
                implementation(Koin.test)
                implementation(Kotlin.test)
                implementation("io.mockk:mockk:_")
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
                implementation(Square.okio)
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
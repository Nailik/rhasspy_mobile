@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("base-gradle")
}

version = Version.toString()

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":ui"))
                implementation(project(":viewmodel"))
                implementation(project(":platformspecific"))
                implementation(Touchlab.kermit)
                implementation(Koin.core)
                implementation(Icerock.Mvvm.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.multidex)
                implementation(Picovoice.porcupineAndroid)
                implementation(AndroidX.window)
                implementation(AndroidX.appCompat)
                implementation(AndroidX.Compose.ui)
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xexpect-actual-classes"
}

android {
    namespace = "org.rhasspy.mobile.overlay"
}